/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.5 2006-04-07 11:04:46 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/04/07 08:24:33  mella
 * Make pref filename protected
 *
 * Revision 1.3  2006/04/06 14:44:07  mella
 * Add feature to restore to default preferences
 *
 * Revision 1.2  2006/03/31 08:52:29  mella
 * Add color handling
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.mcs.util;

import java.awt.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;


/**
 * Preferences can be managed using String values with this class.
 * Reuse can be done just extending this class and using setShortPreferenceFilename
 * and setDefaultProperties methods.
 * Application must take care of multiple instances that shares the same
 * preference file (RW conflict...).
 */
public class Preferences extends Observable
{
    /**
     * Store shortPreferenceFilename.
     * Class that herits from this one should overload this variable to return specific file name.
     * It must not include any file separator.
     */
    protected static String _shortPreferenceFilename = "preferences.properties";

    // Preferences are saved using Properties object.

    /**
     * Internal storage of preferences.
     */
    private Properties myProperties = new Properties();

    /**
     * Store default properties.
     */
    private Properties _defaultProperties = new Properties();

    /**
     * Creates a new Preferences object.
     */
    public Preferences()
    {
        loadFromFile();
    }

    /**
     * Creates a new Preferences object using a specific preference filename.
     * @param shortPreferenceFilename short preference filename ( no file
     * separator included).
     */
    public Preferences(String shortPreferenceFilename)
    {
        _shortPreferenceFilename = shortPreferenceFilename;
        loadFromFile();
    }

    /* Set short preference filename.
     * @param shortPreferenceFilename the filename to store on disk this preference.
     */

    /**
     * DOCUMENT ME!
     *
     * @param shortPreferenceFilename DOCUMENT ME!
     */
    public static void setShortPreferenceFilename(
        String shortPreferenceFilename)
    {
        _shortPreferenceFilename = shortPreferenceFilename;
    }

    /**
     * Save preferences into preferences  file.
     *
     * @throws PreferencesException indicates a problem during save process.
     */
    final public void saveToFile() throws PreferencesException
    {
        try
        {
            String           cfgName    = getPreferenceFilename();
            FileOutputStream outputFile = new FileOutputStream(cfgName);
            myProperties.store(outputFile, "SCALIB GUI PROPERTIES...");
            outputFile.close();
        }
        catch (Exception e)
        {
            throw new PreferencesException("Can't store preferences to file", e);
        }
    }

    /**
     * Load preferences from file if any or reset to default values.
     */
    public void loadFromFile()
    {
        String cfgName = getPreferenceFilename();

        try
        {
            resetToDefaultPreferences();
            myProperties.load(new FileInputStream(cfgName));
            // Notify all preferences listener of maybe new values coming from file.
            setChanged();
            notifyObservers();
        }
        catch (IOException e)
        {
            // Do nothing just default values will be into the preferences.
        }
    }

    /**
     * Returns the name of file to store preferences into.
     * Try to return an absolute pathName.
     *
     * @return $USER/getShortPreferenceFilename()
     */
    public String getPreferenceFilename()
    {
        // TODO : must be specialized in order to properly retrieved each
        // specifc path for the different platforms (mac, linux, win).
        String userHome = System.getProperty("user.home");
        String cfgName  = userHome + File.separator + _shortPreferenceFilename;

        return cfgName;
    }

    /**
     * Set a preference.
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName,
        String preferenceValue)
    {
        myProperties.setProperty(preferenceName, preferenceValue);
        myProperties.put("content", "user");
        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * Get a preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return the preference value.
     */
    final public String getPreference(String preferenceName)
    {
        return myProperties.getProperty(preferenceName);
    }

    /**
     * Get a preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return the preference value.
     */
    final public Color getPreferenceAsColor(String preferenceName)
        throws PreferencesException
    {
        Color c;

        try
        {
            c = Color.decode(myProperties.getProperty(preferenceName));
        }
        catch (Exception e)
        {
            throw new PreferencesException("Can't convert preference value to color",
                e);
        }

        return c;
    }

    /**
     * Returns an Enumeration of preference names which start with given
     * string. One given empty string make all preference entries returned.
     * object.
     * @return Enumeration a string enumeration of preference names
     */
    public Enumeration getPreferences(String prefix)
    {
        Vector      v = new Vector();
        Enumeration e = myProperties.propertyNames();

        while (e.hasMoreElements())
        {
            String propertyName = (String) e.nextElement();

            if (propertyName.startsWith(prefix))
            {
                v.add(propertyName);
            }
        }

        return v.elements();
    }

    /**
     * String representation. Print filename and preferences.
     *
     * @return the representation.
     */
    public String toString()
    {
        return "Preferences stored into [" + getPreferenceFilename() + "] : " +
        myProperties;
    }

    /**
     * Restore default values to preferences. Use save method to store default
     * values into the preferences file.
     */
    public void resetToDefaultPreferences()
    {
        myProperties = (Properties) _defaultProperties.clone();
        myProperties.put("content", "default");
        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * Set the default properties used to reset default preferences.
     * This method should be used to adjust specific application preferences.
     *
     * @defaultProperties the default properties to set for this application.
     */
    protected void setDefaultPreferences(Properties defaultProperties)
    {
        _defaultProperties = defaultProperties;
    }

    /**
     * main method used to test this class.
     *
     * @param args command line arguments
     *
     */
    public static void main(String[] args)
    {
        // Do simple tests
        try
        {
            Preferences p = new Preferences();

            System.out.println("You can mv your configuration file" +
                p.getPreferenceFilename() + " to see different behaviour");

            System.out.println("Preferences are :" + p);
            p.setPreference("color.c1", "#123456");
            p.setPreference("color.c2", "#234567");
            System.out.println("Preferences are :" + p);

            // Try to store preferences into the preference file
            p.saveToFile();

            // Play with a second preference instance that share common prefs
            Preferences p2 = new Preferences();
            System.out.println("Preferences 2 are :" + p2);

            p2.resetToDefaultPreferences();
            System.out.println("Default preferences 2 are :" + p2);
        }
        catch (PreferencesException e)
        {
            e.printStackTrace();
        }
    }
}
