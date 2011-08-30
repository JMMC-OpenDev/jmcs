/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import java.util.logging.Level;

/**
 * Singleton object which handles common preferences.
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public final class CommonPreferences extends Preferences
{

    /** Singleton instance */
    private static CommonPreferences _singleton = null;
    /** Class Name */
    private final static String className_ = "fr.jmmc.mcs.util.CommonPreferences";
    /** Logger */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className_);
    /* Preferences */
    /** Store the filename of the common preference file */
    public static final String PREFERENCES_FILENAME = "fr.jmmc.jmcs.properties";
    /**  Name of the preference which stores the user email in the feedback report */
    public static final String FEEDBACK_REPORT_USER_EMAIL = "feedback_report.user_email";
    /** Name of the preference which stores the flag to display or not the splashscreen */
    public static final String SHOW_STARTUP_SPLASHSCREEN = "startup.splashscreen.show";

    /* Proxy settings */
    /** HTTP proxy host */
    public static final String HTTP_PROXY_HOST = "http.proxyHost";
    /** HTTP proxy port */
    public static final String HTTP_PROXY_PORT = "http.proxyPort";

    /**
     * Private constructor that must be empty.
     */
    private CommonPreferences()
    {
        super();
    }

    /**
     * Return the singleton instance of CommonPreferences.
     *
     * @return the singleton preference instance
     */
    public static CommonPreferences getInstance()
    {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null) {
            logger.fine("CommonPreferences.getInstance()");

            _singleton = new CommonPreferences();
        }

        return _singleton;
    }

    /**
     * Define the default properties used to reset default preferences.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    @Override
    protected void setDefaultPreferences() throws PreferencesException
    {
        // Display the splash screen during app launch.
        setDefaultPreference(SHOW_STARTUP_SPLASHSCREEN, true);
        setDefaultPreference(HTTP_PROXY_HOST, "");
        setDefaultPreference(HTTP_PROXY_PORT, "");
    }

    /**
     * Return the preference filename.
     *
     * @return preference filename.
     */
    @Override
    protected String getPreferenceFilename()
    {
        return PREFERENCES_FILENAME;
    }

    /**
     *  Return preference version number.
     *
     * @return preference version number.
     */
    @Override
    protected int getPreferencesVersionNumber()
    {
        return 1;
    }

    /**
     * Run this program to generate the common preference file.
     * @param args NC
     */
    public static void main(String[] args)
    {
        try {
            CommonPreferences.getInstance().saveToFile();
        } catch (PreferencesException pe) {
            logger.log(Level.SEVERE, "property failure : ", pe);
        }
    }
}
