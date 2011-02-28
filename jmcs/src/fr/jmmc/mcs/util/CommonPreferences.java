package fr.jmmc.mcs.util;

/**
 * Singleton object which handles common preferences.
 */
public class CommonPreferences extends Preferences {

    /** Store the filename of the common preference file */
    public static final String PREFERENCES_FILENAME = "fr.jmmc.jmcs.properties";

    /**  Name of the preference which stores the user email in the feedback report */
    public static final String FEEDBACK_REPORT_USER_EMAIL = "feedback_report.user_email";
    /** Name of the preference which stores the flag to display or not the splashscreen */
    public static final String SHOW_STARTUP_SPLASHSCREEN = "startup.splashscreen.show";
    /** Store the singleton instance */
    private static CommonPreferences _instance = null;

    private CommonPreferences() {
        super();
    }

    public static CommonPreferences getInstance() {
        if (_instance == null) {
            _instance = new CommonPreferences();
        }
        return _instance;
    }

    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        // Display the splash screen during app launch.
        setDefaultPreference(SHOW_STARTUP_SPLASHSCREEN, true);
    }

    @Override
    protected String getPreferenceFilename() {
        return PREFERENCES_FILENAME;
    }

    @Override
    protected int getPreferencesVersionNumber() {
        return 1;
    }

    /**
     *  run this program to generate the common preference file.
     * @param args NC
     */
    public static void main(String[] args) {
        try {
            CommonPreferences.getInstance().saveToFile();
        } catch (PreferencesException exc) {
            exc.printStackTrace();
        }
    }
}
