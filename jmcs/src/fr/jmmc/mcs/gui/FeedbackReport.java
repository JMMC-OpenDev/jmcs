/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReport.java,v 1.13 2008-06-25 12:05:21 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * This class opens a new feedback report window. It uses the model
 * called <b>FeedbackReportModel</b> to take the user informations,
 * the user system informations and the application logs and send all
 * using a HTTP POST request.
 */
public class FeedbackReport extends JDialog implements Observer
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReport.class.getName());

    /** Load bar */
    private static JProgressBar _loadBar = new JProgressBar();

    /** Kind of error/bug */
    private static JComboBox _typeComboBox = new JComboBox();

    /** User bug description */
    private static JTextArea _description = new JTextArea();

    /** User mail */
    private static JTextField _mail = new JTextField();

    /** Cancel button */
    private static JButton _cancelButton = new JButton();

    /** Submit button */
    private static JButton _submitButton = new JButton();

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

    /** Exception */
    private Exception _exception = null;

    /** Exit the application? */
    private boolean _exit = false;

    /** Creates a new FeedbackReport object */
    public FeedbackReport()
    {
        this(null, false);
    }

    /**
     * Creates a new FeedbackReport object
     * Set the parent frame.
     *
     * @param frame parent frame
     */
    public FeedbackReport(Frame frame)
    {
        this(frame, false);
    }

    /**
     * Creates a new FeedbackReport object
     * Set the parent frame and specify if this dialog is modal or not.
     *
     * @param frame parent frame
     * @param modal if true, this dialog is modal
     */
    public FeedbackReport(Frame frame, boolean modal)
    {
        this(frame, modal, null);
    }

    /**
     * Creates a new FeedbackReport object
     * Set the parent frame and specify if this dialog is modal or not.
     *
     * @param frame parent frame
     * @param modal if true, this dialog is modal
     * @param exception exception
     */
    public FeedbackReport(Frame frame, boolean modal, Exception exception)
    {
        this(frame, modal, exception, false);
    }

    /**
     * Creates a new FeedbackReport object
     * Set the parent frame and specify if this dialog is modal or not.
     *
     * @param frame parent frame
     * @param modal if true, this dialog is modal
     * @param exception exception
     * @param exit if true, exit the application
     */
    public FeedbackReport(Frame frame, boolean modal, Exception exception,
        boolean exit)
    {
        super(frame, modal);

        _exception               = exception;
        _exit                    = exit;

        // Create the model and add the observer
        _feedbackReportModel     = new FeedbackReportModel();
        _feedbackReportModel.addObserver(this);

        // Launch the model as thread
        _feedbackReportThread = new Thread(_feedbackReportModel);
        _feedbackReportThread.start();

        // Draw the widgets
        setSplitsProperties();
        setMailProperties();
        setTypeProperties();
        setDescriptionProperties();
        setButtonsProperties();
        setTabbedProperties();
        setFrameProperties();
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
        systemScrollPane.setBorder(BorderFactory.createTitledBorder(
                "System properties :"));
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
        exceptionScrollPane.setBorder(BorderFactory.createTitledBorder(
                "Exception message :"));
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
        _mailSplit.setDividerLocation(50);
        _mailSplit.setDividerSize(0);

        _mailLabel.setText("Mail :");
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
        _typeSplit.setDividerLocation(50);
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
                    _feedbackReportThread.stop();

                    // Exit or not the application
                    exit();

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

    /** Update progress bar according to report sending completion state */
    public void update(Observable observable, Object object)
    {
        activateLoadBarProperties();

        _loadBar.setIndeterminate(false);

        FeedbackReportModel feedbackReportModel = (FeedbackReportModel) object;

        if (feedbackReportModel.isReportSend())
        {
            _loadBar.setString("Thank you for your feedback.");

            // Wait before closing
            int delay = 2000;

            try
            {
                Thread.sleep(delay);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING, "Cannot wait " + delay + "ms", ex);
            }

            _logger.info("Feedback report sent");

            _submitButton.setEnabled(true);
            dispose();

            // Stop the thread in background
            _feedbackReportThread.stop();

            // Exit or not the application
            exit();
        }
        else
        {
            String errorMessage = "Feedback Report message has not been sent.\nPlease check your internet connection.";
            JOptionPane.showMessageDialog(null, errorMessage,
                "Feedback Report Failed", JOptionPane.ERROR_MESSAGE);

            _submitButton.setEnabled(true);
            _loadBar.setString("Error during report sending.");
        }
    }

    /**
     * Return the mail value
     *
     * @return mail value
     */
    public static String getMail()
    {
        return _mail.getText();
    }

    /**
     * Return the default combo box model
     *
     * @return default combo box model
     */
    public static DefaultComboBoxModel getDefaultComboBoxModel()
    {
        return (DefaultComboBoxModel) _typeComboBox.getModel();
    }

    /**
     * Return the description value
     *
     * @return description value
     */
    public static String getDescription()
    {
        return _description.getText();
    }

    /**
     * Return exception trace as a string
     *
     * @return exception trace
     */
    private String getExceptionTrace()
    {
        String exceptionTrace = "None";

        // Check if the exception is not null
        if (_exception != null)
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter  printWriter  = new PrintWriter(stringWriter);

            _exception.printStackTrace(printWriter);

            exceptionTrace = stringWriter.toString();

            //_exception.printStackTrace();
        }

        return exceptionTrace;
    }

    /** Exit the application if there was a fatal error */
    private void exit()
    {
        // Exit or not the application
        if (_exit)
        {
            System.exit(-1);
        }
    }
}
/*___oOo___*/
