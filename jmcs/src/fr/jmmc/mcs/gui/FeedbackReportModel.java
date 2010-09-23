/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReportModel.java,v 1.15 2010-09-23 19:40:40 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.14  2010/09/17 14:18:58  mella
 * Do also set mail widget not static so that it is always shown.
 *
 * Revision 1.13  2010/09/17 14:04:37  mella
 * Do not share static widget between multiple feedback reports so that user as to acknowledge each report
 *
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

import fr.jmmc.mcs.util.MCSObservable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;


/**
 * This class takes the informations from the view called
 * <b>FeedbackReport</b>, the user system informations and
 * the application logs and send all by a HTTP POST request
 * to the jmmc team via a PHP script.
 */
public class FeedbackReportModel extends MCSObservable implements Runnable
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReportModel.class.getName());

    /** URL of the PHP script that handles form parameters */
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";

    /* TEST URL of the PHP script that handles form parameters */
/*
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedbackLB.php";
*/
    /** Feedback report type definition array */
    private static final String[] _feedbackTypes = new String[]
        {
            "Bug Report", "Documentation Typo", "Evolution Request",
            "Support Request"
        };

    /* members */

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

    /** The user bug description */
    private String _comments = "";

    /** Application-specific information */
    private String _applicationSpecificInformation = null;

    /** Report send? */
    private boolean _send = false;

    /** Ready to send report? */
    private boolean _readyToSend = false;

    /* Swing components */
    /** The default combo box model */
    private DefaultComboBoxModel _feedbackTypeDataModel;

    /** Component that store user's input*/
    private final FeedbackReport _feedbackReport;


    /** 
     * Creates a new FeedbackReportModel object
     */
    public FeedbackReportModel()
    {
        this(null);
    }

    /**
     * Creates a new FeedbackReportModel object
     * with the possibility to define a specific information
     * @param feedbackReport feedback report dialog or null if unknow
     */
    public FeedbackReportModel(final FeedbackReport feedbackReport)
    {
        super();
        
        _feedbackReport = feedbackReport;

        _applicationSpecificInformation = "None";

        if (_feedbackReport != null)
        {
            _applicationSpecificInformation = feedbackReport.getExceptionTrace();
            setMail(feedbackReport.getMail());
        }       
        _logger.fine("Specific information has been set");

        final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();

        _feedbackTypeDataModel     = new DefaultComboBoxModel(_feedbackTypes);
        _logger.fine("TypeDataModel constructed");

        // Get informations to send with the report
        if (applicationDataModel != null)
        {
            _applicationVersion     = applicationDataModel.getProgramVersion();
            _applicationName        = applicationDataModel.getProgramName();
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
    public final void setReadyToSend(final boolean ready)
    {
        _readyToSend = ready;
    }

    /**
     * Set value of mail
     *
     * @param mail value of mail
     */
    public final void setMail(final String mail)
    {
        _mail = mail;
        _logger.fine("Mail value has been set");
    }

    /**
     * Set value of feedback report description
     *
     * @param comments value of feedback report description
     */
    public final void setDescription(final String comments)
    {
        _comments = comments;
        _logger.fine("Description value has been set");
    }

    /**
     * Set value of Application-Specific Information
     *
     * @param information value of Application-Specific Information
     */
    public final void setApplicationSpecificInformation(final String information)
    {
        _applicationSpecificInformation = information;
        _logger.fine("Application-Specific Information value has been set");
    }

    /**
     * Return the default combo box model to the view
     *
     * @return default combo box model
     */
    public final DefaultComboBoxModel getTypeDataModel()
    {
        return _feedbackTypeDataModel;
    }

    /**
     * Set the default combo box model
     *
     * @param typeDataModel default combo box model
     */
    public final void setTypeDataModel(final DefaultComboBoxModel typeDataModel)
    {
        if (typeDataModel != null)
        {
            _feedbackTypeDataModel = typeDataModel;
            _logger.fine("Type data model value has been set");
        }
    }

    /** 
     * Send the report per mail
     */
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException ie)
            {
            }

            if (_readyToSend)
            {
                _logger.fine("Ready to send is true");

                setMail(_mail);
                if (_feedbackReport != null) {
                    setTypeDataModel(_feedbackReport.getDefaultComboBoxModel());
                    setDescription(_feedbackReport.getDescription());
                }

                try
                {
                    // Create an HTTP client to send report information to our PHP script
                    final HttpClient client = new HttpClient();
                    final PostMethod method = new PostMethod(_phpScriptURL);

                    _logger.fine("Http client and post method have been created");

                    // Compose HTML form parameters
                    method.addParameter("applicationName", _applicationName);
                    method.addParameter("applicationVersion", _applicationVersion);
                    method.addParameter("systemConfig", _systemConfig);
                    method.addParameter("applicationLog", _applicationLog);
                    method.addParameter("userEmail", _mail);

                    final String feedbackType = (String) _feedbackTypeDataModel.getSelectedItem();
                    method.addParameter("feedbackType", feedbackType);
                    method.addParameter("comments", _comments);
                    method.addParameter("applicationSpecificInformation", _applicationSpecificInformation);

                    _logger.fine("All post parameters have been set");

                    // Send feedback report to PHP script
                    client.executeMethod(method);

                    _logger.fine("The report mail has been send");

                    // Get PHP script result (either SUCCESS or FAILURE)
                    final String response = method.getResponseBodyAsString();

                    if (_logger.isLoggable(Level.FINE)) {
                      _logger.fine("HTTP response : " + response);
                    }

                    _send = (! response.contains("FAILED")) && (method.isRequestSent());

                    if (_logger.isLoggable(Level.FINE)) {
                      _logger.fine("Report sent : " + (_send ? "YES" : "NO"));
                    }

                    // Set state to changed
                    setChanged();

                    _logger.fine("The model has changed");

                    // Notify feedback report using EDT :
                    fireNotification(null);
                }
                catch (Exception e)
                {
                    _logger.log(Level.SEVERE, "Cannot send feedback report", e);
                }

                _readyToSend = false;
                
                _logger.fine("Set ready to send to false");
            }
        }
    }


    /**
     * Fires the notification to the registered observers using the EDT thread
     * @param arg optional argument
     */
    private void fireNotification(final Object arg) {
      // notify observers (swing components) within EDT :

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          notifyObservers(arg);

          _logger.fine("Observers have been notified that the model has changed");
        }
      });
    }

    /**
     * Return if report has been send
     * @return true if report has been send 
     */
    public final boolean isReportSend()
    {
        return _send;
    }

    /**
     * Returns system configuration
     *
     * @return system configuration
     */
    public final String getSystemConfig()
    {
        // Get all informations about the system running the application
        final Properties  hostProperties            = System.getProperties();
        final Enumeration<?> hostPropertiesEnumeration = hostProperties.propertyNames();

        final StringBuilder sb = new StringBuilder(2048);

        String propertyName, propertyValue;

        // For each system property, we make a string like "{name} : {value}"
        while (hostPropertiesEnumeration.hasMoreElements())
        {
            propertyName  = String.valueOf(hostPropertiesEnumeration.nextElement());
            propertyValue = System.getProperty(propertyName);

            sb.append(propertyName).append(" : ").append(propertyValue).append("\n");
        }

        return sb.toString();
    }
}
/*___oOo___*/
