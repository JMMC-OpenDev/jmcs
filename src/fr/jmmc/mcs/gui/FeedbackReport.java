/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReport.java,v 1.27 2010-09-26 12:40:18 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.26  2010/09/25 13:38:35  bourgesl
 * removed deprecated / unused constructors
 * exit flag is automatically set (application is not ready or not visible)
 *
 * Revision 1.25  2010/09/24 15:45:14  bourgesl
 * use use MessagePane
 *
 * Revision 1.24  2010/09/23 19:43:56  bourgesl
 * better EDT handling (refresh) but the mail report thread must be corrected later
 * automatic Frame (Dialog) association with the application main Frame / new constructor without frame argument
 * Free resources (thread ...) when the window is closed
 *
 * Revision 1.23  2010/09/17 14:18:58  mella
 * Do also set mail widget not static so that it is always shown.
 *
 * Revision 1.22  2010/09/17 14:04:37  mella
 * Do not share static widget between multiple feedback reports so that user as to acknowledge each report
 *
 * Revision 1.21  2009/03/31 07:19:15  mella
 * Fix layout
 *
 * Revision 1.20  2009/03/31 07:11:32  mella
 * Change Email label
 *
 * Revision 1.19  2009/01/19 11:06:28  lafrasse
 * Jalopization.
 *
 * Revision 1.18  2009/01/14 14:26:52  mella
 * Add enw constructor and set new level to log when exception is given
 *
 * Revision 1.17  2008/11/28 12:55:30  mella
 * Enable submit button only if text is description is not null
 *
 * Revision 1.16  2008/11/18 09:13:54  lafrasse
 * Jalopization.
 *
 * Revision 1.15  2008/11/06 13:44:44  mella
 * Add exception to log trace
 *
 * Revision 1.14  2008/10/07 13:42:43  mella
 * Use tip to return stacktrace
 *
 * Revision 1.13  2008/06/25 12:05:21  bcolucci
 * Add a surcharge of the constructor with a boolean
 * in order to specify that the application have to
 * be closed after that the report has been sent.
 *
 * Revision 1.12  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.11  2008/06/19 13:10:50  bcolucci
 * Fix the height of the exception textarea.
 *
 * Revision 1.10  2008/06/17 13:03:17  bcolucci
 * Create a function which returns an exception trace as a string.
 *
 * Revision 1.9  2008/06/17 12:37:40  bcolucci
 * Improve tabbed component conception.
 * Add the possibility to put an exception in the constructor.
 *
 * Revision 1.8  2008/06/17 11:13:05  bcolucci
 * Add tabbed pane in the window and the fact that the exception trace
 * is put in a textarea.
 *
 * Revision 1.7  2008/06/17 07:53:30  bcolucci
 * Extend from JDialog instead of JFrame in order to set it modal.
 * Reload progress bar after that a report has been sent.
 * Set the dialog visible after that it has been centered.
 *
 * Revision 1.6  2008/05/27 12:06:48  bcolucci
 * Moving the JOptionPane to view from model.
 * Stopping the report thread in background.
 * Reactivating the submit button after a report.
 *
 * Revision 1.5  2008/05/20 08:52:16  bcolucci
 * Changed communication between View and Model to Observer/Observable pattern.
 *
 * Revision 1.4  2008/05/19 14:56:21  lafrasse
 * Updated according to FeedbackReportModel() API changes.
 *
 * Revision 1.3  2008/05/16 13:01:34  bcolucci
 * Removed unecessary try/catch, and added argument checks.
 * Threaded it.
 *
 * Revision 1.2  2008/04/24 15:55:57  mella
 * Added applicationDataModel to constructor.
 *
 * Revision 1.1  2008/04/22 09:15:56  bcolucci
 * Created FeedbackReport.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.BorderLayout;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
public class FeedbackReport extends JDialog implements Observer, KeyListener
{

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReport.class.getName());

    /** User mail */
    private JTextField _mail = new JTextField();

    /** Load bar */
    private  JProgressBar _loadBar = new JProgressBar();

    /** Kind of error/bug */
    private JComboBox _typeComboBox = new JComboBox();

    /** User bug description */
    private JTextArea _description = new JTextArea();

    /** Cancel button */
    private JButton _cancelButton = new JButton();

    /** Submit button */
    private JButton _submitButton = new JButton();

    /** Model of the feedback report box */
    private FeedbackReportModel _feedbackReportModel = null;

    /** Mail and description split */
    private JSplitPane _mailAndDescriptionSplit = new JSplitPane();

    /** Mail and space split */
    private JSplitPane _mailAndSpaceSplit = new JSplitPane();

    /** Mail and type split */
    private JSplitPane _mailAndTypeSplit = new JSplitPane();

    /** Mail label */
    private JLabel _mailLabel = new JLabel();

    /** Mail split */
    private JSplitPane _mailSplit = new JSplitPane();

    /** Space and mail split */
    private JSplitPane _spaceAndMailSplit = new JSplitPane();

    /** Type label */
    private JLabel _typeLabel = new JLabel();

    /** Type panel */
    private JPanel _typePanel = new JPanel();

    /** Type split */
    private JSplitPane _typeSplit = new JSplitPane();

    /** Description and buttons split */
    private JSplitPane _descriptionAndButtonsSplit = new JSplitPane();

    /** Description label */
    private JLabel _descriptionLabel = new JLabel();

    /** Description pane */
    private JScrollPane _descriptionPane = new JScrollPane();

    /** Description split */
    private JSplitPane _descriptionSplit = new JSplitPane();

    /** Cancel and Submit buttons panel */
    private JPanel _buttonsPanel = new JPanel();

    /** Feedback report thread */
    private Thread _feedbackReportThread = null;

    /** Tabbed pane */
    private JTabbedPane _tabbedPane = null;

    /** Any Throwable (Exception, RuntimeException and Error) */
    private Throwable _exception = null;
    
    /** 
     * Creates a new FeedbackReport object (not modal).
     * Do not exit on close.
     */
    public FeedbackReport()
    {
        this(null, false, null);
    }

    /**
     * Creates a new FeedbackReport object (not modal).
     * Do not exit on close.
     * @param exception exception
     */
    public FeedbackReport(final Throwable exception)
    {
        this(null, false, exception);
    }

    /**
     * Creates a new FeedbackReport object.
     * Do not exit on close.
     *
     * @param modal if true, this dialog is modal
     * @param exception exception
     */
    public FeedbackReport(final boolean modal, final Throwable exception)
    {
        this(null, modal, exception);
    }

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
    public FeedbackReport(final Frame frame, final boolean modal, final Throwable exception)
    {
        super(MessagePane.getOwner(frame), modal);

        // Force to dispose when the dialog closes :
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        _exception               = exception;

        // Create the model and add the observer
        // GM has haccked api to temporary shortcup process and force presence of exception if any
        _feedbackReportModel     = new FeedbackReportModel(this);

        if (_exception != null)
        {
            _description.append("Following exception occured:\n" +
                ((_exception.getMessage() != null) ? _exception.getMessage() : "no message") +
                "\n\n--\n");

            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE, "An exception was given to the feedback report", _exception);
            }
        }

        _feedbackReportModel.addObserver(this);

        // Launch the model as thread
        _feedbackReportThread = new Thread(_feedbackReportModel);

        // LAURENT : TODO CLEAN : the thread should only start when the report must be sent ...
        _feedbackReportThread.start();

        // Draw the widgets
        setSplitsProperties();
        setMailProperties();
        setTypeProperties();
        setDescriptionProperties();
        setButtonsProperties();
        setTabbedProperties();
        setFrameProperties();

        // Listen to key event to ensure
        // that send button is enable only if desc is not null
        _description.addKeyListener(this);
        // and update ui
        keyReleased(null);

        _logger.fine("All feedback report properties have been set");
    }
    
    /** Set tabbed pane properties */
    private void setTabbedProperties()
    {
        // Get exception trace
        String exceptionTrace = getExceptionTrace();

        // Create tabbed pane
        _tabbedPane = new JTabbedPane();

        // Add send report tab
        _tabbedPane.addTab("Send report", _mailAndTypeSplit);

        // Create panel for scroll panes
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create scoll pane and textarea for the system properties
        JScrollPane systemScrollPane = new JScrollPane();
        JTextArea   systemTextArea   = new JTextArea();

        systemScrollPane.setAutoscrolls(true);
        systemScrollPane.setBorder(BorderFactory.createTitledBorder("System properties :"));
        systemTextArea.setEditable(false);
        systemTextArea.setLineWrap(true);
        systemTextArea.setRows(10);
        systemTextArea.setText(_feedbackReportModel.getSystemConfig());
        systemScrollPane.setViewportView(systemTextArea);

        // Add scroll pane in panel
        panel.add(systemScrollPane, BorderLayout.PAGE_START);

        // Create scoll pane and textarea for the exception message
        JScrollPane exceptionScrollPane = new JScrollPane();
        JTextArea   exceptionTextArea   = new JTextArea();

        exceptionScrollPane.setAutoscrolls(true);
        exceptionScrollPane.setBorder(BorderFactory.createTitledBorder("Exception message :"));
        exceptionTextArea.setEditable(false);
        exceptionTextArea.setRows(10);
        exceptionTextArea.setLineWrap(true);
        exceptionTextArea.setText(exceptionTrace);
        exceptionScrollPane.setViewportView(exceptionTextArea);

        // Add scroll pane in panel
        panel.add(exceptionScrollPane, BorderLayout.CENTER);

        // Add panel to the tabbed pane
        _tabbedPane.addTab("Details", panel);
    }

    /** Set frame properties */
    private void setFrameProperties()
    {
        // Finish window configuration and draw it
        getContentPane().add(_tabbedPane, BorderLayout.CENTER);
        setTitle("Feedback Report");
        setResizable(false);
        pack();
        WindowCenterer.centerOnMainScreen(this);
        setVisible(true);
        _logger.fine("Frame properties have been set");
    }

    /** Draw mail-related widgets */
    private void setMailProperties()
    {
        _mailSplit.setBorder(null);
        _mailSplit.setDividerLocation(80);
        _mailSplit.setDividerSize(0);

        _mailLabel.setText("Your Email :");
        _mailSplit.setLeftComponent(_mailLabel);
        _mailSplit.setRightComponent(_mail);
        _logger.fine("Mail properties have been set");
    }

    /** Draw report type-related widgets */
    private void setTypeProperties()
    {
        _typePanel.setLayout(new BorderLayout());

        _typeComboBox.setModel(_feedbackReportModel.getTypeDataModel());
        _typePanel.add(_typeComboBox, BorderLayout.LINE_START);

        _typeSplit.setBorder(null);
        _typeSplit.setDividerLocation(80);
        _typeSplit.setDividerSize(0);
        _typeSplit.setEnabled(false);

        _typeLabel.setText("Type :");
        _typeSplit.setLeftComponent(_typeLabel);

        _typeSplit.setRightComponent(_typePanel);
        _logger.fine("Type properties have been set");
    }

    /** Draw description-related widgets */
    private void setDescriptionProperties()
    {
        _description.setColumns(40);
        _description.setLineWrap(true);
        _description.setRows(15);
        _descriptionPane.setViewportView(_description);
        _descriptionSplit.setBorder(null);
        _descriptionSplit.setDividerSize(0);
        _descriptionSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _descriptionSplit.setEnabled(false);

        _descriptionLabel.setText("Description :");
        _descriptionSplit.setRightComponent(_descriptionLabel);

        _descriptionSplit.setTopComponent(new JLabel(" "));
        _logger.fine("Description properties have been set");
    }

    /** Set Cancel and Submit buttons properties and actions */
    private void setButtonsProperties()
    {
        _buttonsPanel.setLayout(new BorderLayout());

        _cancelButton.setText("Cancel");
        // Actions performed when there was a click on "Cancel" button
        _cancelButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    // Just use dispose() as it is overriden to :
                    // - stop the thread in background
                    // - exit if needed
                    dispose();
                }
            });
        _buttonsPanel.add(_cancelButton, BorderLayout.LINE_START);

        _submitButton.setText("Submit");
        // Actions performed when there was a click on "Submit" button
        _submitButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    activateLoadBarProperties();

                    // Sends report
                    _feedbackReportModel.setReadyToSend(true);

                    _submitButton.setEnabled(false);
                }
            });
        _buttonsPanel.add(_submitButton, BorderLayout.LINE_END);
    }

    /** Set split properties */
    private void setSplitsProperties()
    {
        _mailAndTypeSplit.setBorder(null);
        _mailAndTypeSplit.setDividerSize(0);
        _mailAndTypeSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _descriptionAndButtonsSplit.setBorder(null);
        _descriptionAndButtonsSplit.setDividerSize(0);
        _descriptionAndButtonsSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _descriptionAndButtonsSplit.setLeftComponent(_descriptionPane);
        _descriptionAndButtonsSplit.setRightComponent(_buttonsPanel);
        _buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        _mailAndTypeSplit.setBottomComponent(_descriptionAndButtonsSplit);

        _mailAndDescriptionSplit.setBorder(null);
        _mailAndDescriptionSplit.setDividerSize(0);
        _mailAndDescriptionSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _spaceAndMailSplit.setBorder(null);
        _spaceAndMailSplit.setDividerSize(0);
        _spaceAndMailSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _spaceAndMailSplit.setBottomComponent(_typeSplit);

        _mailAndSpaceSplit.setBorder(null);
        _mailAndSpaceSplit.setDividerSize(0);
        _mailAndSpaceSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _mailAndSpaceSplit.setTopComponent(new JLabel(" "));
        _mailAndSpaceSplit.setRightComponent(_mailSplit);

        _spaceAndMailSplit.setLeftComponent(_mailAndSpaceSplit);

        _mailAndDescriptionSplit.setTopComponent(_spaceAndMailSplit);
        _mailAndDescriptionSplit.setRightComponent(_descriptionSplit);

        _mailAndTypeSplit.setLeftComponent(_mailAndDescriptionSplit);
        _mailAndTypeSplit.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        _logger.fine("Splits properties have been set");
    }

    /** Set load bar properties */
    private void activateLoadBarProperties()
    {
        _loadBar.setStringPainted(true);
        _loadBar.setIndeterminate(true);
        _loadBar.setString("Sending report...");

        _buttonsPanel.add(_loadBar, BorderLayout.CENTER);
        _buttonsPanel.revalidate();
    }

    /**
     * Update progress bar according to report sending completion state
     * @param observable feedbackReportModel instance
     * @param object unused argument
     */
    public void update(final Observable observable, final Object object)
    {

        // This method is called by the feedbackReportModel using EDT :

        activateLoadBarProperties();

        _loadBar.setIndeterminate(false);

        final FeedbackReportModel feedbackReportModel = (FeedbackReportModel) observable;

        if (feedbackReportModel.isReportSend())
        {
            _logger.info("Feedback report sent");

            _loadBar.setString("Thank you for your feedback.");

            // Wait before closing
            // Use invokeLater to avoid blocking EDT to repaint current changes :
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    final int delay = 2000;
                    try
                    {
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException ie)
                    {
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
        }
        else
        {
            MessagePane.showErrorMessage(
                  "Feedback Report message has not been sent.\nPlease check your internet connection.",
                  "Feedback Report Failed");

            _submitButton.setEnabled(true);
            _loadBar.setString("Error during report sending.");
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
    public final String getMail()
    {
        return _mail.getText();
    }

    /**
     * Return the default combo box model
     *
     * @return default combo box model
     */
    public final DefaultComboBoxModel getDefaultComboBoxModel()
    {
        return (DefaultComboBoxModel) _typeComboBox.getModel();
    }

    /**
     * Return the description value
     *
     * @return description value
     */
    public final String getDescription()
    {
        return _description.getText();
    }

    /**
     * Append the given message to the description value
     * @param message to add
     * @return complete description value
     */
    public final String addDescription(final String message)
    {
        _description.append(message);
        return _description.getText();
    }

    /**
     * Return exception trace as a string
     *
     * @return exception trace
     */
    public final String getExceptionTrace()
    {
        String exceptionTrace = "No stack trace";

        // Check if the exception is not null
        if (_exception != null)
        {
            final StringWriter stringWriter = new StringWriter();
            _exception.printStackTrace(new PrintWriter(stringWriter));
            exceptionTrace = stringWriter.toString();
        }

        return exceptionTrace;
    }

    /** Exit the application if there was a fatal error */
    private final void exit()
    {

        // If the application is not ready, exit now :
        final boolean ready = App.isReady();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Application is ready : " + ready);
        }

        final boolean exit = !ready || !App.getFrame().isVisible();

        // Exit or not the application
        if (exit)
        {
            _logger.fine("exiting ...");
            System.exit(-1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public final void keyTyped(KeyEvent e)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public final void keyPressed(KeyEvent e)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public final void keyReleased(KeyEvent e)
    {
        _submitButton.setEnabled(_description.getText().length() > 0);
    }
}
/*___oOo___*/
