/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReport.java,v 1.32 2011-02-04 17:04:35 lafrasse Exp $"
 *
 */
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.gui.task.JmcsTaskRegistry;
import fr.jmmc.mcs.gui.task.TaskSwingWorker;
import fr.jmmc.mcs.gui.task.TaskSwingWorkerExecutor;
import fr.jmmc.mcs.util.CommonPreferences;
import fr.jmmc.mcs.util.Http;
import fr.jmmc.mcs.util.PreferencedDocument;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * This class opens a new feedback report window. It uses the model
 * called <b>FeedbackReportModel</b> to take the user informations,
 * the user system informations and the application logs and send all
 * using a HTTP POST request.
 *
 * TODO : september 2010 : handle properly thread associated to FeedBackReportModel (start/stop/notify) ...
 *
 */
public class FeedbackReport extends javax.swing.JDialog implements KeyListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReport.class.getName());
    /** Any Throwable (Exception, RuntimeException and Error) */
    private Throwable _exception = null;
    /** Feedback report type definition array */
    private static final String[] _feedbackTypes = new String[]{
        "Bug Report", "Documentation Typo", "Evolution Request",
        "Support Request"
    };

    /* Swing components */
    /** The default combo box model */
    private DefaultComboBoxModel _feedbackTypeDataModel = null;

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

        // TODO supprimer une fois que ce bout de code est deplacé dans une partie commune
        // Initialize tasks and the task executor :
        TaskSwingWorkerExecutor.create(JmcsTaskRegistry.getInstance());


        _feedbackTypeDataModel = new DefaultComboBoxModel(_feedbackTypes);

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
        this.setMinimumSize(new Dimension(600, 600));
        this.setPreferredSize(new Dimension(600, 600));

        WindowCenterer.centerOnMainScreen(this);

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                // Just use dispose() as it is overriden to :
                // - stop the thread in background
                // - exit if needed
                dispose();
            }
        });

        if (_exception != null) {
            descriptionTextArea.append("Following exception occured:\n"
                    + ((_exception.getMessage() != null) ? _exception.getMessage() : "no message")
                    + "\n\n--\n");

            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE, "An exception was given to the feedback report", _exception);
            }
        }

        // Listen to key event to ensure
        // that send button is enable only if desc or summary is not null
        descriptionTextArea.addKeyListener(this);
        summaryTextField.addKeyListener(this);

        // Associate email to common preference
        emailTextField.setDocument(PreferencedDocument.getInstance(CommonPreferences.getInstance(), CommonPreferences.FEEDBACK_REPORT_USER_EMAIL, true));

        // and update ui
        keyReleased(null);

        headerLabel.setText("<html><body>"
                + "<center>"
                + "<big>Welcome to the JMMC Feedback Report</big><br/>"
                + "We are eager to get your feedback, questions or comments !<br/>"
                + "So please do not hesitate to use this form.<br/>"
                + "</center>"
                + "<br/><br/>"
                + "Moreover, we encourage you to provide us with your e-mail address, so we can :"
                + "<ul>"
                + "<li>keep you up to date on the status of your request;</li>"
                + "<li>ask you more information if needed.</li>"
                + "</ul>"
                + "</body></html>");

        typeComboBox.setModel(_feedbackTypeDataModel);
        systemTextArea.setText(getSystemConfig());
        logTextArea.setText(getApplicationLog());
        exceptionTextArea.setText(getExceptionTrace());

        setVisible(true);
    }

    /**
     * Close the dialog box if everything was correct or let the user retry.
     * This method is called by the worker using EDT :

     * @param boolean done
     */
    public void shouldDispose(boolean sent) {
        // 
        loadProgressBar.setIndeterminate(false);

        if (sent) {
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

        // do not kill the associated worker task to let the started job end properly
        // else we would hav called:
        // TaskSwingWorkerExecutor.cancel(JmcsTaskRegistry.TASK_FEEDBACK_REPORT);

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
     * Return the summary value
     *
     * @return summary value
     */
    public final String getSummary() {
        return summaryTextField.getText();
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

    /**
     * Enable submit button according desc and summary fields.
     * @param e event thrown by description or summary updates.
     */
    public final void keyReleased(KeyEvent e) {
        boolean hasDesc = descriptionTextArea.getText().length() > 0;
        boolean hasSummary = summaryTextField.getText().length() > 0;
        submitButton.setEnabled(hasDesc && hasSummary);
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
        summaryTextField = new javax.swing.JTextField();
        summaryLabel = new javax.swing.JLabel();
        detailPanel = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        exceptionScrollPane = new javax.swing.JScrollPane();
        exceptionTextArea = new javax.swing.JTextArea();
        systemScrollPane = new javax.swing.JScrollPane();
        systemTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JMMC Feedback Report ");

        sendReportPanel.setLayout(new java.awt.GridBagLayout());

        emailLabel.setText("E-Mail :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(emailLabel, gridBagConstraints);

        typeLabel.setText("Type :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(typeLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(emailTextField, gridBagConstraints);

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(typeComboBox, gridBagConstraints);

        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerLabel.setText("<html>headerLabel<br> changed  by code</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(headerLabel, gridBagConstraints);

        descriptionScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Description :"));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        sendReportPanel.add(descriptionScrollPane, gridBagConstraints);

        cancelButton.setText("Cancel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(cancelButton, gridBagConstraints);

        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(submitButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(loadProgressBar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sendReportPanel.add(summaryTextField, gridBagConstraints);

        summaryLabel.setText("Summary :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        sendReportPanel.add(summaryLabel, gridBagConstraints);

        jTabbedPane1.addTab("Send report", sendReportPanel);

        detailPanel.setLayout(new java.awt.GridBagLayout());

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setRows(5);
        logScrollPane.setViewportView(logTextArea);

        jTabbedPane2.addTab("Log content", logScrollPane);

        exceptionTextArea.setColumns(20);
        exceptionTextArea.setEditable(false);
        exceptionTextArea.setRows(5);
        exceptionScrollPane.setViewportView(exceptionTextArea);

        jTabbedPane2.addTab("Exception message", exceptionScrollPane);

        systemTextArea.setColumns(20);
        systemTextArea.setEditable(false);
        systemTextArea.setRows(5);
        systemScrollPane.setViewportView(systemTextArea);

        jTabbedPane2.addTab("System properties", systemScrollPane);

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
        // update swing widgets
        loadProgressBar.setStringPainted(true);
        loadProgressBar.setIndeterminate(true);
        loadProgressBar.setString("Sending report...");
        submitButton.setEnabled(false);

        // launch a new worker
        FeedbackReportWorker worker = new FeedbackReportWorker(this,
                getSystemConfig(),
                getApplicationLog(),
                getExceptionTrace(),
                (String) _feedbackTypeDataModel.getSelectedItem(),
                getMail(),
                getSummary(),
                getDescription());
        TaskSwingWorkerExecutor.executeTask(worker);
    }//GEN-LAST:event_submitButtonActionPerformed

    /**
     * Returns system configuration
     *
     * @return sorted list of system properties
     */
    public final String getSystemConfig() {
        // Get all informations about the system running the application
        final Properties hostProperties = System.getProperties();
        String[] keys = new String[hostProperties.size()];
        hostProperties.keySet().toArray(keys);
        java.util.Arrays.sort(keys);

        final StringBuilder sb = new StringBuilder(2048);
        // For each system property, we make a string like "{name} : {value}"
        for (String key : keys) {
            sb.append(key).append(" : ").append(System.getProperty(key)).append("\n");
        }

        return sb.toString();
    }

    private String getApplicationLog() {
        final String logOutput = App.getLogOutput();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("logOutput length = " + logOutput.length());
        }

        return (logOutput.length() > 0) ? logOutput : "None";
    }

    /**
     *
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
    private javax.swing.JLabel summaryLabel;
    private javax.swing.JTextField summaryTextField;
    private javax.swing.JScrollPane systemScrollPane;
    private javax.swing.JTextArea systemTextArea;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * This worker aims to send the feedback mail in background.
     * It replaces the old FeedbackReportModel which was runnable.
     */
    private static class FeedbackReportWorker extends TaskSwingWorker<Boolean> {

        /** URL of the PHP script that handles form parameters */
        private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";
        // developpers can use the fake script that do not store incomming reports
        //private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedbackFake.php";
        // Following members store the elements to send to remote scripts
        private final FeedbackReport feedbackReport;
        private final String config;
        private final String log;
        private final String stackTrace;
        private final String type;
        private final String mail;
        private final String summary;
        private final String comments;

        public FeedbackReportWorker(FeedbackReport feedbackReport, String config, String log, String stackTrace,
                String type, String mail, String summary, String comments) {
            super(JmcsTaskRegistry.TASK_FEEDBACK_REPORT, "send feed back report");
            this.feedbackReport = feedbackReport;
            this.config = config;
            this.log = log;
            this.stackTrace = stackTrace;
            this.type = type;
            this.mail = mail;
            this.summary = summary;
            this.comments = comments;
        }

        @Override
        public Boolean computeInBackground() {

            boolean statusFlag = false;
            // Create an HTTP client to send report information to our PHP script
            final HttpClient client = Http.getHttpClient(false);

            final PostMethod method = new PostMethod(_phpScriptURL);

            try {
                _logger.fine("Http client and post method have been created");

                final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
                String applicationName;
                String applicationVersion;
                // Compose HTML form parameters
                // Get informations to send with the report
                if (applicationDataModel != null) {
                    applicationName = applicationDataModel.getProgramName();
                    applicationVersion = applicationDataModel.getProgramVersion();
                } else {
                    applicationName = "Unknown";
                    applicationVersion = "Unknown";
                }

                method.addParameter("applicationName", applicationName);
                method.addParameter("applicationVersion", applicationVersion);
                method.addParameter("systemConfig", config);
                method.addParameter("applicationLog", log);
                method.addParameter("applicationSpecificInformation", stackTrace);

                // Get information from swing elements
                method.addParameter("userEmail", mail);
                method.addParameter("feedbackType", type);
                method.addParameter("comments", comments);
                method.addParameter("summary", summary);

                _logger.fine("All post parameters have been set");

                // Send feedback report to PHP script
                client.executeMethod(method);

                _logger.fine("The report mail has been send");

                // Get PHP script result (either SUCCESS or FAILURE)
                final String response = method.getResponseBodyAsString();

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("HTTP response : " + response);
                }

                statusFlag = (!response.contains("FAILED")) && (method.isRequestSent());

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("Report sent : " + (statusFlag ? "YES" : "NO"));
                }

            } catch (Exception e) {
                _logger.log(Level.SEVERE, "Cannot send feedback report", e);
            } finally {
                // Release the connection.
                method.releaseConnection();
            }

            _logger.fine("Set ready to send to false");

            return (statusFlag == true ? Boolean.TRUE : Boolean.FALSE);
        }

        @Override
        public void refreshUI(Boolean sent) {
            feedbackReport.shouldDispose(sent);
        }
    }
}
