/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReportModel.java,v 1.1 2008-04-22 09:15:56 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;

import java.io.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;


/** Model of FeedbackView class */
public class FeedbackReportModel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReportModel.class.getName());

    /** URL of the PHP script that handles form parameters */
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";

    /** ApplicationData model */
    public static ApplicationDataModel _applicationDataModel = new ApplicationDataModel();

    /** Feedback report type definition array */
    private static String[] _types = new String[]
        {
            "Bug", "Evolution", "Documentation", "Support"
        };

    /** Program version */
    private String _programVersion = "None";

    /** Program name */
    private String _programName = "None";

    /** User system configuration */
    private String _systemConfig = "None";

    /** Application logs */
    private String _applicationLog = "None";

    /** User mail */
    private String _mail = "None";

    /** The default combo box model */
    private DefaultComboBoxModel _typeDataModel;

    /** The user bug description */
    private String _description = "None";

    /** Application-specific information */
    private String _applicationSpecificInformation = "None";

    /** Creates a new FeedbackReportModel object */
    public FeedbackReportModel()
    {
        _typeDataModel = new DefaultComboBoxModel(_types);
        _logger.fine("TypeDataModel constructed");

        // Informations to send with the report
        _programVersion     = _applicationDataModel.getProgramVersion();
        _programName        = _applicationDataModel.getProgramName();
        _systemConfig       = getSystemConfig();
        _applicationLog     = App.getLogOutput();
        _logger.fine("All generated report informations have been collected");
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
     * Set value of description
     *
     * @param description value of description
     */
    public void setDescription(String description)
    {
        _description = description;
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
        return _typeDataModel;
    }

    /**
     * Set the default combo box model
     *
     * @param typeDataModel default combo box model
     */
    public void setTypeDataModel(DefaultComboBoxModel typeDataModel)
    {
        _typeDataModel = typeDataModel;
        _logger.fine("Type data model value has been set");
    }

    /** Send the report peer mail */
    public void sendReport()
    {
        try
        {
            // Create an HTTP client to send report information to our PHP script
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(_phpScriptURL);
            _logger.fine("Http client and post method have been created");

            // Compose HTML form parameters
            method.addParameter("programName", _programName);
            method.addParameter("programVersion", _programVersion);
            method.addParameter("systemConfig", _systemConfig);
            method.addParameter("applicationLog", _applicationLog);
            method.addParameter("userEmail", _mail);

            String type = (String) _typeDataModel.getSelectedItem();
            method.addParameter("reportClass", type);
            method.addParameter("description", _description);
            method.addParameter("applicationSpecificInformation",
                _applicationSpecificInformation);
            _logger.fine("All post parameters have been set");

            // Send feedback report to PHP script
            client.executeMethod(method);
            _logger.fine("The report mail has been send");

            // Get PHP script result (either SUCCESS or FAILURE)
            String response = method.getResponseBodyAsString();

            if (response.contains("FAILED"))
            {
                _logger.fine("The PHP response is FAILURE");

                String errorMessage = "Feedback Report message has not been sent.\nPlease check your internet connection.";
                JOptionPane.showMessageDialog(null, errorMessage,
                    "Feedback Report Failed", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                _logger.fine("The PHP response is SUCCESS");
            }
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Cannot send feedback report", ex);
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

        try
        {
            // For each system property, we make a string like "{name} : {value}"
            while (hostPropertiesEnumeration.hasMoreElements())
            {
                String propertyName  = String.valueOf(hostPropertiesEnumeration.nextElement());
                String propertyValue = System.getProperty(propertyName);

                allHostProperties += (propertyName + " : " + propertyValue +
                "\n");
            }
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot get system configuration", ex);
        }

        return allHostProperties;
    }
}
/*___oOo___*/
