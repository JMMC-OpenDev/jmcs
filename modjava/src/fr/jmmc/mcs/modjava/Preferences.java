/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.2 2008-09-22 16:53:50 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/07/01 08:58:13  lafrasse
 * Added jmcs test application from bcolucci.
 *
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.mcs.util.*;

import java.util.logging.*;


/** Test application default preferences */
public class Preferences extends fr.jmmc.mcs.util.Preferences
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.modjava.Preferences");

    /** Singleton instance */
    private static Preferences _instance = null;

    /**
     * Privatized constructor that must be empty.
     */
    private Preferences()
    {
    }

    /**
     * Return the preference filename.
     */
    protected String getPreferenceFilename()
    {
        _logger.entering("Preferences", "getPreferenceFilename");

        return "fr.jmmc.modjava.test.properties";
    }

    /**
     * Return the preference revision number.
     */
    protected int getPreferencesVersionNumber()
    {
        _logger.entering("Preferences", "getPreferencesVersionNumber");

        return 1;
    }

    /** Set preferences default values */
    protected void setDefaultPreferences() throws PreferencesException
    {
        _logger.entering("Preferences", "setDefaultPreferences");

        setDefaultPreference("DEFAULT", "default");
    }

    /** Return the singleton instance */
    public static final synchronized Preferences getInstance()
    {
        // DO NOT MODIFY !!!
        if (_instance == null)
        {
            _instance = new Preferences();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }
}
