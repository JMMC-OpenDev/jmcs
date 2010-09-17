/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReportModel.java,v 1.13 2010-09-17 14:04:37 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2008/10/15 14:01:20  mella
 * improved handling when applicationDataModel is null
 *
 * Revision 1.11  2008/06/20 08:42:25  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.10  2008/06/17 07:55:05  bcolucci
 * Add more logs.
 *
 * Revision 1.9  2008/06/13 08:17:49  bcolucci
 * Remove unused specific information settor.
 *
 * Revision 1.8  2008/06/12 11:57:55  bcolucci
 * Add a setter for application specific information.
 *
 * Revision 1.7  2008/06/12 11:33:23  bcolucci
 * Add a new constructor which permits to add directly a specific information
 * about the application to the feedback report.
 *
 * Revision 1.6  2008/05/27 12:09:17  bcolucci
 * Updating the way to verify the HTTP response of the feedback.
 *
 * Revision 1.5  2008/05/20 08:52:16  bcolucci
 * Changed communication between View and Model to Observer/Observable pattern.
 *
 * Revision 1.4  2008/05/19 14:55:24  lafrasse
 * Updated field names.
 * Updated default values.
 * Added use of App.getSharedApplicationDataModel() instead of receivng it through
 * constructor parameter.
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;


/**
 * This class takes the informations from the view called
 * <b>FeedbackReport</b>, the user system informations and
 * the application logs and send all by a HTTP POST request
 * to the jmmc team via a PHP script.
 */
public class FeedbackReportModel extends Observable implements Runnable
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReportModel.class.getName());

    /** URL of the PHP script that handles form parameters */
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";

    //private static final String _phpScriptURL = "http://jmmc.fr/~bcolucci/feedback/feedback.php";

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
    private String _applicationLog = "None";

    /** User mail */
    private String _mail = "Unknown";

    /** The default combo box model */
    private DefaultComboBoxModel _feedbackTypeDataModel;

    /** The user bug description */
    private String _comments = "";

    /** Application-specific information */
    private String _applicationSpecificInformation = null;

    /** Report send? */
    private boolean _send = false;

    /** Ready to send report? */
    private boolean _readyToSend = false;

    /** Component that store user's input*/
    FeedbackReport _feedbackReport=null;

    /** Creates a new FeedbackReportModel object */
    public FeedbackReportModel()
    {
        this(null);
    }

    /** Creates a new FeedbackReportModel object
     * with the possibility to define a specific information
     */
    public FeedbackReportModel(FeedbackReport feedbackReport)
    {
        _feedbackReport = feedbackReport;

        _applicationSpecificInformation="None";
        if (_feedbackReport!=null){
            _applicationSpecificInformation = feedbackReport.getExceptionTrace();
        }       
        _logger.fine("Specific information has been set");

        _applicationDataModel      = App.getSharedApplicationDataModel();

        _feedbackTypeDataModel     = new DefaultComboBoxModel(_feedbackTypes);
        _logger.fine("TypeDataModel constructed");

        // Get informations to send with the report
        if (_applicationDataModel != null)
        {
            _applicationVersion     = _applicationDataModel.getProgramVersion();
            _applicationName        = _applicationDataModel.getProgramName();
        }

        _systemConfig = getSystemConfig();
        _logger.fine("system configuration has been saved");

        _applicationLog = App.getLogOutput();
        _logger.fine("All generated report informations have been collected");
    }

    /**
     * Set ready to send to true or false
     *
     * @param ready ready to send report
     */
    public void setReadyToSend(boolean ready)
    {
        _readyToSend = ready;
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
        while (1 == 1)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (Exception ex)
            {
            }

            if (_readyToSend)
            {
                _logger.fine("Ready to send is true");

                setMail(FeedbackReport.getMail());
                if (_feedbackReport != null) {
                    setTypeDataModel(_feedbackReport.getDefaultComboBoxModel());
                    setDescription(_feedbackReport.getDescription());
                }

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

                    _logger.fine("HTTP response : " + response);

                    _send = (! response.contains("FAILED")) &&
                        (method.isRequestSent());

                    _logger.fine("Report sent : " + (_send ? "YES" : "NO"));

                    // Set state to changed
                    setChanged();
                    _logger.fine("The model has changed");

                    // Notify feedback report
                    notifyObservers(this);
                    _logger.fine(
                        "Observers have been notified that the model has changed");
                }
                catch (Exception ex)
                {
                    _logger.log(Level.SEVERE, "Cannot send feedback report", ex);
                }

                _readyToSend = false;
                _logger.fine("Set ready to send to false");
            }
        }
    }

    /** Return if report has been send */
    public boolean isReportSend()
    {
        return _send;
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
