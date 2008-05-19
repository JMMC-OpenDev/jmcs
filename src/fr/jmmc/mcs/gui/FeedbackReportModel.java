/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReportModel.java,v 1.4 2008-05-19 14:55:24 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;

import java.lang.Thread;

import java.util.*;
import java.util.logging.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;


/** Model of FeedbackView class */
public class FeedbackReportModel extends Thread
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReportModel.class.getName());

    /** URL of the PHP script that handles form parameters */
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";
    //private static final String _phpScriptURL = "http://jmmc.fr/~lafrasse/feedback/feedback.php";

    /** ApplicationData model */
    public static ApplicationDataModel _applicationDataModel;

    /** Feedback report type definition array */
    private static String[] _feedbackTypes = new String[]
        {
            "Bug Report", "Documentation Typo", "Evolution Request",
            "Support Request"
        };

    /** Program version */
    private String _applicationVersion = "Unknown";

    /** Program name */
    private String _applicationName = "Unknown";

    /** User system configuration */
    private String _systemConfig = "Unknown";

    /** Application logs */
    private String _applicationLog = "Unknown";

    /** User mail */
    private String _mail = "Unknown";

    /** The default combo box model */
    private DefaultComboBoxModel _feedbackTypeDataModel;

    /** The user bug description */
    private String _comments = "Unknown";

    /** Application-specific information */
    private String _applicationSpecificInformation = "Unknown";

    /**
     * DOCUMENT ME!
     */
    FeedbackReport _feedbackReport = null;

    /**
     * DOCUMENT ME!
     */
    private boolean readyToSend = false;

    /** Creates a new FeedbackReportModel object */
    public FeedbackReportModel(FeedbackReport feedbackReport)
    {
        _feedbackReport           = feedbackReport;
        _applicationDataModel     = App.getSharedApplicationDataModel();

        if (_applicationDataModel != null)
        {
            _feedbackTypeDataModel = new DefaultComboBoxModel(_feedbackTypes);
            _logger.fine("TypeDataModel constructed");

            // Get informations to send with the report
            _applicationVersion     = _applicationDataModel.getProgramVersion();
            _applicationName        = _applicationDataModel.getProgramName();

            _systemConfig           = getSystemConfig();
            _applicationLog         = App.getLogOutput();
            _logger.fine(
                "All generated report informations have been collected");
        }
    }

    /**
     * Creates a new setReadyToSend object.
     *
     * @param ready DOCUMENT ME!
     */
    public void setReadyToSend(boolean ready)
    {
        readyToSend = ready;
    }

    /**
     * Set value of mail
     *
     * @param mail value of mail
     */
    public void setMail(String mail)
    {
        _mail = mail;
        _logger.fine("Mail value has been set");
    }

    /**
     * Set value of feedback report description
     *
     * @param comments value of feedback report description
     */
    public void setDescription(String comments)
    {
        _comments = comments;
        _logger.fine("Description value has been set");
    }

    /**
     * Set value of Application-Specific Information
     *
     * @param information value of Application-Specific Information
     */
    public void setApplicationSpecificInformation(String information)
    {
        _applicationSpecificInformation = information;
        _logger.fine("Application-Specific Information value has been set");
    }

    /**
     * Return the default combo box model to the view
     *
     * @return default combo box model
     */
    public DefaultComboBoxModel getTypeDataModel()
    {
        return _feedbackTypeDataModel;
    }

    /**
     * Set the default combo box model
     *
     * @param typeDataModel default combo box model
     */
    public void setTypeDataModel(DefaultComboBoxModel typeDataModel)
    {
        if (typeDataModel != null)
        {
            _feedbackTypeDataModel = typeDataModel;
            _logger.fine("Type data model value has been set");
        }
    }

    /** Send the report peer mail */
    public void run()
    {
        while (! isInterrupted())
        {
            try
            {
                sleep(10);
            }
            catch (Exception ex)
            {
            }

            if (readyToSend)
            {
                setMail(FeedbackReport.getMail());
                setTypeDataModel(FeedbackReport.getDefaultComboBoxModel());
                setDescription(FeedbackReport.getDescription());

                try
                {
                    // Create an HTTP client to send report information to our PHP script
                    HttpClient client = new HttpClient();
                    PostMethod method = new PostMethod(_phpScriptURL);

                    _logger.fine(
                        "Http client and post method have been created");

                    // Compose HTML form parameters
                    method.addParameter("applicationName", _applicationName);
                    method.addParameter("applicationVersion",
                        _applicationVersion);
                    method.addParameter("systemConfig", _systemConfig);
                    method.addParameter("applicationLog", _applicationLog);
                    method.addParameter("userEmail", _mail);

                    String feedbackType = (String) _feedbackTypeDataModel.getSelectedItem();
                    method.addParameter("feedbackType", feedbackType);
                    method.addParameter("comments", _comments);
                    method.addParameter("applicationSpecificInformation",
                        _applicationSpecificInformation);
                    _logger.fine("All post parameters have been set");

                    // Send feedback report to PHP script
                    client.executeMethod(method);
                    _logger.fine("The report mail has been send");

                    // Get PHP script result (either SUCCESS or FAILURE)
                    String response = method.getResponseBodyAsString();

                    System.out.println(response);

                    if (response.contains(
                                "The requested URL was not found on this server."))
                    {
                        _logger.fine("The PHP response is FAILURE");
                        _feedbackReport.setReportSend(false);

                        String errorMessage = "Feedback Report message has not been sent.\nPlease check your internet connection.";
                        JOptionPane.showMessageDialog(null, errorMessage,
                            "Feedback Report Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        _feedbackReport.setReportSend(true);
                        _logger.fine("The PHP response is SUCCESS");
                    }
                }
                catch (Exception ex)
                {
                    _logger.log(Level.SEVERE, "Cannot send feedback report", ex);
                }

                readyToSend = false;
            }
        }
    }

    /**
     * Returns system configuration
     *
     * @return system configuration
     */
    public String getSystemConfig()
    {
        // Get all informations about the system running the application
        Properties  hostProperties            = System.getProperties();
        Enumeration hostPropertiesEnumeration = hostProperties.propertyNames();
        String      allHostProperties         = "";

        // For each system property, we make a string like "{name} : {value}"
        while (hostPropertiesEnumeration.hasMoreElements())
        {
            String propertyName  = String.valueOf(hostPropertiesEnumeration.nextElement());
            String propertyValue = System.getProperty(propertyName);

            allHostProperties += (propertyName + " : " + propertyValue + "\n");
        }

        return allHostProperties;
    }
}
/*___oOo___*/
