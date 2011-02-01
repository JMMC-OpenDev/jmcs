/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReport.java,v 1.29 2011-02-01 16:10:50 mella Exp $"
 *
 */
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.CommonPreferences;
import fr.jmmc.mcs.util.PreferencedDocument;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This class opens a new feedback report window. It uses the model
 * called <b>FeedbackReportModel</b> to take the user informations,
 * the user system informations and the application logs and send all
 * using a HTTP POST request.
 *
 * TODO : september 2010 : handle properly thread associated to FeedBackReportModel (start/stop/notify) ...
 *
 */
public class FeedbackReport extends javax.swing.JDialog implements Observer, KeyListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReport.class.getName());
    /** Model of the feedback report box */
    private FeedbackReportModel _feedbackReportModel = null;
    /** Feedback report thread */
    private Thread _feedbackReportThread = null;
    /** Any Throwable (Exception, RuntimeException and Error) */
    private Throwable _exception = null;

    /**
     * Creates a new FeedbackReport object
     * Set the parent frame and specify if this dialog is modal or not.
     *
     * @param frame parent frame
     * @param modal if true, this dialog is modal
     * @param exception exception
     *
     * @deprecated use new FeedbackReport(final boolean modal, final Throwable exception)
     */
    /** Creates new form FeedbackReport */
    public FeedbackReport(final Frame frame, final boolean modal, final Throwable exception) {

        super(MessagePane.getOwner(frame), modal);

        _exception = exception;

        initComponents();
        postInit();
        // Force to dispose when the dialog closes :
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        _logger.fine("All feedback report properties have been set");
    }

    /**
     * Creates a new FeedbackReport object (not modal).
     * Do not exit on close.
     */
    public FeedbackReport() {
        this(null, false, null);
    }

    /**
     * Creates a new FeedbackReport object (not modal).
     * Do not exit on close.
     * @param exception exception
     */
    public FeedbackReport(final Throwable exception) {
        this(null, false, exception);
    }

    /**
     * Creates a new FeedbackReport object.
     * Do not exit on close.
     *
     * @param modal if true, this dialog is modal
     * @param exception exception
     */
    public FeedbackReport(final boolean modal, final Throwable exception) {
        this(null, modal, exception);
    }

    private void postInit() {
        //setResizable(false);
        this.setMinimumSize(new Dimension(600,600));        
        this.setPreferredSize(new Dimension(600,600));

        WindowCenterer.centerOnMainScreen(this);

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                // Just use dispose() as it is overriden to :
                // - stop the thread in background
                // - exit if needed
                dispose();
            }
        });
        
        // Create the model and add the observer
        // GM has hacked api to temporary shortcut process and force presence of exception if any
        _feedbackReportModel = new FeedbackReportModel(this);

        if (_exception != null) {
            descriptionTextArea.append("Following exception occured:\n"
                    + ((_exception.getMessage() != null) ? _exception.getMessage() : "no message")
                    + "\n\n--\n");

            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE, "An exception was given to the feedback report", _exception);
            }
        }

        _feedbackReportModel.addObserver(this);

        // Launch the model as thread
        _feedbackReportThread = new Thread(_feedbackReportModel);

        // LAURENT : TODO CLEAN : the thread should only start when the report must be sent ...
        _feedbackReportThread.start();

        // Listen to key event to ensure
        // that send button is enable only if desc is not null
        descriptionTextArea.addKeyListener(this);

        // Associate email to common preference
        emailTextField.setDocument(PreferencedDocument.getInstance(CommonPreferences.getInstance(), CommonPreferences.FEEDBACK_EMAIL, true));

        // and update ui
        keyReleased(null);

        headerLabel.setText("<html><center><big>Welcome onto the <em>JMMC</em> feedback report!</big></center><br/>We are pleased to receive some users feedback, so do not hesitate to submit some. Moreover, we recommend to fill your contact email:<ul><li>you will be informed on the status of your request</li><li>we can ask more informations if needed</li></ul><br/> </html>");
        typeComboBox.setModel(_feedbackReportModel.getTypeDataModel());
        systemTextArea.setText(_feedbackReportModel.getSystemConfig());
        logTextArea.setText(_feedbackReportModel.getApplicationLog());
        exceptionTextArea.setText(getExceptionTrace());

        setVisible(true);
    }

    /** Set load bar properties */
    private void activateLoadBarProperties() {
        loadProgressBar.setStringPainted(true);
        loadProgressBar.setIndeterminate(true);
        loadProgressBar.setString("Sending report...");
    }

    /**
     * Update progress bar according to report sending completion state
     * @param observable feedbackReportModel instance
     * @param object unused argument
     */
    public void update(final Observable observable, final Object object) {

        // This method is called by the feedbackReportModel using EDT :

        activateLoadBarProperties();

        loadProgressBar.setIndeterminate(false);

        final FeedbackReportModel feedbackReportModel = (FeedbackReportModel) observable;

        if (feedbackReportModel.isReportSend()) {
            _logger.info("Feedback report sent");

            loadProgressBar.setString("Thank you for your feedback.");

            // Wait before closing
            // Use invokeLater to avoid blocking EDT to repaint current changes :
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    final int delay = 2000;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        if (_logger.isLoggable(Level.WARNING)) {
                            _logger.log(Level.WARNING, "Cannot wait " + delay + "ms", ie);
                        }
                    }

                    // Just use dispose() as it is overriden to :
                    // - stop the thread in background
                    // - exit if needed
                    dispose();
                }
            });
        } else {
            MessagePane.showErrorMessage(
                    "Feedback Report message has not been sent.\nPlease check your internet connection.",
                    "Feedback Report Failed");

            submitButton.setEnabled(true);
            loadProgressBar.setString("Error during report sending.");
        }
    }

    /**
     * Free any ressource or reference to this instance :
     * remove this instance form Preference Observers
     */
    @Override
    public final void dispose() {
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("dispose : " + this);
        }

        _logger.fine("stopping background thread ...");

        // Stop the thread in background
        // LAURENT : TODO CLEAN : ILLEGAL a thread must not be killed like this :
        _feedbackReportThread.stop();

        // Exit or not the application
        exit();

        // dispose Frame :
        super.dispose();
    }

    /**
     * Return the mail value
     *
     * @return mail value
     */
    public final String getMail() {
        return emailTextField.getText();
    }

    /**
     * Return the description value
     *
     * @return description value
     */
    public final String getDescription() {
        return descriptionTextArea.getText();
    }

    /**
     * Append the given message to the description value
     * @param message to add
     * @return complete description value
     */
    public final String addDescription(final String message) {
        descriptionTextArea.append(message);
        return descriptionTextArea.getText();
    }

    /**
     * Return exception trace as a string
     *
     * @return exception trace
     */
    public final String getExceptionTrace() {
        String exceptionTrace = "No stack trace";

        // Check if the exception is not null
        if (_exception != null) {
            final StringWriter stringWriter = new StringWriter();
            _exception.printStackTrace(new PrintWriter(stringWriter));
            exceptionTrace = stringWriter.toString();
        }

        return exceptionTrace;
    }

    /** Exit the application if there was a fatal error */
    private final void exit() {

        // If the application is not ready, exit now :
        final boolean ready = App.isReady();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Application is ready : " + ready);
        }

        final boolean exit = !ready || !App.getFrame().isVisible();

        // Exit or not the application
        if (exit) {
            _logger.fine("exiting ...");
            System.exit(-1);
        }
    }

    /* Implementation of keylistener */
    public final void keyTyped(KeyEvent e) {
    }

    public final void keyPressed(KeyEvent e) {
    }

    public final void keyReleased(KeyEvent e) {
        submitButton.setEnabled(descriptionTextArea.getText().length() > 0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        sendReportPanel = new javax.swing.JPanel();
        emailLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox();
        headerLabel = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();
        loadProgressBar = new javax.swing.JProgressBar();
        detailPanel = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        systemScrollPane = new javax.swing.JScrollPane();
        systemTextArea = new javax.swing.JTextArea();
        exceptionScrollPane = new javax.swing.JScrollPane();
        exceptionTextArea = new javax.swing.JTextArea();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Feedback Report");

        sendReportPanel.setLayout(new java.awt.GridBagLayout());

        emailLabel.setText("Your Email : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        sendReportPanel.add(emailLabel, gridBagConstraints);

        typeLabel.setText("Type : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        sendReportPanel.add(typeLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        sendReportPanel.add(emailTextField, gridBagConstraints);

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sendReportPanel.add(typeComboBox, gridBagConstraints);

        headerLabel.setText("<html>headerLabel<br> changed  by code</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        sendReportPanel.add(headerLabel, gridBagConstraints);

        descriptionScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Description :"));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        sendReportPanel.add(descriptionScrollPane, gridBagConstraints);

        cancelButton.setText("Cancel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        sendReportPanel.add(cancelButton, gridBagConstraints);

        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        sendReportPanel.add(submitButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        sendReportPanel.add(loadProgressBar, gridBagConstraints);

        jTabbedPane1.addTab("Send report", sendReportPanel);

        detailPanel.setLayout(new java.awt.GridBagLayout());

        systemTextArea.setColumns(20);
        systemTextArea.setEditable(false);
        systemTextArea.setRows(5);
        systemScrollPane.setViewportView(systemTextArea);

        jTabbedPane2.addTab("System properties :", systemScrollPane);

        exceptionTextArea.setColumns(20);
        exceptionTextArea.setEditable(false);
        exceptionTextArea.setRows(5);
        exceptionScrollPane.setViewportView(exceptionTextArea);

        jTabbedPane2.addTab("Exception message :", exceptionScrollPane);

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setRows(5);
        logScrollPane.setViewportView(logTextArea);

        jTabbedPane2.addTab("Log content :", logScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        detailPanel.add(jTabbedPane2, gridBagConstraints);

        jTabbedPane1.addTab("Details", detailPanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        activateLoadBarProperties();
        // Sends report
        _feedbackReportModel.setReadyToSend(true);
        submitButton.setEnabled(false);
    }//GEN-LAST:event_submitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FeedbackReport dialog = new FeedbackReport();
                dialog.setVisible(true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JScrollPane exceptionScrollPane;
    private javax.swing.JTextArea exceptionTextArea;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JProgressBar loadProgressBar;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel sendReportPanel;
    private javax.swing.JButton submitButton;
    private javax.swing.JScrollPane systemScrollPane;
    private javax.swing.JTextArea systemTextArea;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
