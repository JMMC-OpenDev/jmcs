package fr.jmmc.mcs.util;

/**
 * Handle common properties.
 */
public class CommonPreferences extends Preferences{
    public static final String FEEDBACK_EMAIL = "feedback.email";
    public static final String SPLASH_SCREEN_SHOW = "splash.screen.show";

    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        // Do not display the splash screen during app launch.
        setDefaultPreference(SPLASH_SCREEN_SHOW, false);
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
