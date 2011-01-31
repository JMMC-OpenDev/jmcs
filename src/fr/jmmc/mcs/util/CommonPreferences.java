package fr.jmmc.mcs.util;

/**
 * Handle common properties.
 */
public class CommonPreferences extends Preferences{
    public static String FEEDBACK_EMAIL = "feedback.email";

    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        // Do not display the splash screen during app launch.
        setDefaultPreference("splash.screen.show", false);
    }

    @Override
    protected String getPreferenceFilename() {
        return "fr.jmmc.mcs.properties";
    }

    @Override
    protected int getPreferencesVersionNumber() {
        return 1;
    }

}
