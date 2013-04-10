/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package com.yourcompany.example;

import fr.jmmc.jmcs.data.preference.PreferencesException;

import java.util.logging.*;

/**
 * Test application default preferences class.
 */
public class Preferences extends fr.jmmc.jmcs.data.preference.Preferences {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(Preferences.class.getName());
    /** Singleton instance */
    private static Preferences _instance = null;

    /** Return the preference filename. */
    @Override
    protected String getPreferenceFilename() {
        _logger.entering("Preferences", "getPreferenceFilename");
        return "com.yourcompany.example.test.properties";
    }

    /** Return the preference revision number. */
    @Override
    protected int getPreferencesVersionNumber() {
        _logger.entering("Preferences", "getPreferencesVersionNumber");

        return 1;
    }

    /** Set preferences default values */
    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        _logger.entering("Preferences", "setDefaultPreferences");

        setDefaultPreference("DEFAULT", "default");
    }

    /** Return the singleton instance */
    public static synchronized Preferences getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new Preferences();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /** Privatized constructor that must be empty. */
    private Preferences() {
    }
}
