/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.1 2008-07-01 08:58:13 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.modjava;


/** Test application default preferences */
public class Preferences extends fr.jmmc.mcs.util.Preferences
{
    /** Preference file name */
    static String _shortPreferenceFilename = "fr.jmmc.test.properties";

    /** Singleton instance */
    private static Preferences _singleton = null;

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public static Preferences getInstance()
    {
        if (_singleton == null)
        {
            // On charge les préférences contenues dans le fichier
            _singleton = new Preferences();
            _singleton.setShortPreferenceFilename(_shortPreferenceFilename);
            _singleton.loadFromFile();

            // On crée des préférences particulières pour l'application
            Preferences defaults = new Preferences();

            // Default preferences
            try
            {
                defaults.setPreference("DEFAULT", "default");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            // On remplace les valeurs par défaut si possible
            _singleton.setDefaultPreferences(defaults);
            _singleton.loadFromFile();
        }

        return _singleton;
    }
}
