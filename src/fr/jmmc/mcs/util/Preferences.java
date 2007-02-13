/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.13 2007-02-13 13:48:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2006/11/30 14:53:34  lafrasse
 * Added a method to really trigger all observers updates.
 *
 * Revision 1.11  2006/10/16 14:29:49  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.10  2006/10/03 14:10:35  lafrasse
 * Corrected a bug in getPreferenceOrder() that prevented initial preferences
 * set.
 *
 * Revision 1.9  2006/09/28 15:22:25  lafrasse
 * Added ordered properties support.
 * Added error management with exceptionx.
 *
 * Revision 1.8  2006/09/15 14:14:55  lafrasse
 * Added Double value support.
 * Documentation refinments.
 *
 * Revision 1.7  2006/06/08 11:41:18  mella
 * Add Boolean preference handling
 *
 * Revision 1.6  2006/04/12 12:30:02  lafrasse
 * Updated some Doxygen tags to fix previous documentation generation errors
 *
 * Revision 1.5  2006/04/07 11:04:46  mella
 * *** empty log message ***
 *
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
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.log.MCSLogger;

import java.awt.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
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

    /**
     * Store hidden properties index prefix.
     */
    protected static String _indexPrefix = "MCSPropertyIndexes.";

    /**
     * Internal storage of preferences.
     */
    private Properties _currentProperties = new Properties();

    /**
     * Default propertiy values.
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
        MCSLogger.trace();

        _shortPreferenceFilename = shortPreferenceFilename;
        loadFromFile();
    }

    /**
     * Truly notifies all registered Observers.
     */
    public void trulyNotifyObservers()
    {
        MCSLogger.trace();

        // Notify all preferences listener of maybe new values coming from file.
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the name of file to store preferences into.
     * Try to return an absolute pathName.
     *
     * @return $USER/getShortPreferenceFilename()
     */
    public String getPreferenceFilename()
    {
        MCSLogger.trace();

        // TODO : must be specialized in order to properly retrieved each
        // specifc path for the different platforms (mac, linux, win).
        String userHome = System.getProperty("user.home");
        String cfgName  = userHome + File.separator + _shortPreferenceFilename;

        return cfgName;
    }

    /**
     * Set short preference filename.
     *
     * @param shortPreferenceFilename the filename to store on disk this
     * preference.
     */
    public static void setShortPreferenceFilename(
        String shortPreferenceFilename)
    {
        MCSLogger.trace();

        _shortPreferenceFilename = shortPreferenceFilename;
    }

    /**
     * Load preferences from file if any or reset to default values.
     */
    public void loadFromFile()
    {
        MCSLogger.trace();

        String cfgName = getPreferenceFilename();

        try
        {
            resetToDefaultPreferences();
            _currentProperties.load(new FileInputStream(cfgName));
        }
        catch (IOException e)
        {
            // Do nothing just default values will be into the preferences.
        }

        // Notify all preferences listener of maybe new values coming from file.
        setChanged();
        notifyObservers();
    }

    /**
     * Save preferences into preferences file.
     *
     * @throws PreferencesException indicates a problem during save process.
     */
    final public void saveToFile() throws PreferencesException
    {
        MCSLogger.trace();

        try
        {
            String           cfgName    = getPreferenceFilename();
            FileOutputStream outputFile = new FileOutputStream(cfgName);
            _currentProperties.store(outputFile, "SCALIB GUI PROPERTIES...");
            outputFile.close();
        }
        catch (Exception e)
        {
            throw new PreferencesException("Can't store preferences to file", e);
        }
    }

    /**
     * Set a preference.
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName,
        Object preferenceValue) throws PreferencesException
    {
        MCSLogger.trace();

        // Wiil automatically get -1 for a yet undefined preference
        int order = getPreferenceOrder(preferenceName);

        try
        {
            setPreference(preferenceName, order, preferenceValue);
        }
        catch (PreferencesException e)
        {
            throw e;
        }
    }

    /**
     * Set a preference.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName, int preferenceIndex,
        Object preferenceValue) throws PreferencesException
    {
        MCSLogger.trace();

        // If the constraint is a String object
        if (preferenceValue.getClass() == java.lang.String.class)
        {
            _currentProperties.setProperty(preferenceName,
                (String) preferenceValue);
        }

        // Else if the constraint is a Boolean object
        else if (preferenceValue.getClass() == java.lang.Boolean.class)
        {
            _currentProperties.setProperty(preferenceName,
                ((Boolean) preferenceValue).toString());
        }

        // Else if the constraint is a Double object
        else if (preferenceValue.getClass() == java.lang.Double.class)
        {
            _currentProperties.setProperty(preferenceName,
                ((Double) preferenceValue).toString());
        }

        // Else if the constraint is a Color object
        else if (preferenceValue.getClass() == java.awt.Color.class)
        {
            _currentProperties.setProperty(preferenceName,
                fr.jmmc.mcs.util.ColorEncoder.encode((Color) preferenceValue));
        }

        // Otherwise we don't know how to handle the given object type
        else
        {
            throw new PreferencesException(
                "Can't handle the given preference value.");
        }

        // Add property index for order if needed
        setPreferenceOrder(preferenceName, preferenceIndex);

        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * Set a preference order.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     */
    final public void setPreferenceOrder(String preferenceName,
        int preferenceIndex)
    {
        MCSLogger.trace();

        // Add property index for order if needed
        if (preferenceIndex > -1)
        {
            _currentProperties.setProperty(_indexPrefix + preferenceName,
                Integer.toString(preferenceIndex));
        }
        else
        {
            _currentProperties.setProperty(_indexPrefix + preferenceName,
                Integer.toString(-1));
        }

        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * Get a preference order.
     *
     * @param preferenceName the preference name.
     *
     * @return the order number for the property (-1 for no order).
     */
    final public int getPreferenceOrder(String preferenceName)
    {
        MCSLogger.trace();

        // -1 is the flag value for no order found, so it is the default value.
        int result = -1;

        // If the asked order is NOT about an internal MCS index property
        if (preferenceName.startsWith(_indexPrefix) == false)
        {
            // Get the corresponding order as a String
            String orderString = _currentProperties.getProperty(_indexPrefix +
                    preferenceName);

            // If an order token was found
            if (orderString != null)
            {
                // Convert the String in an int
                Integer orderInteger = Integer.valueOf(orderString);
                result = orderInteger.intValue();
            }

            // Otherwise the default -1 value will be returned
        }

        return result;
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
        MCSLogger.trace();

        return _currentProperties.getProperty(preferenceName);
    }

    /**
     * Get a boolean preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one boolean representing the preference value.
     */
    final public boolean getPreferenceAsBoolean(String preferenceName)
    {
        MCSLogger.trace();

        String value = _currentProperties.getProperty(preferenceName);

        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * Get a double preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one double representing the preference value.
     */
    final public double getPreferenceAsDouble(String preferenceName)
    {
        MCSLogger.trace();

        String value = _currentProperties.getProperty(preferenceName);

        return Double.valueOf(value).doubleValue();
    }

    /**
     * Get a color preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one Color object representing the preference value.
     */
    final public Color getPreferenceAsColor(String preferenceName)
        throws PreferencesException
    {
        MCSLogger.trace();

        String stringValue = _currentProperties.getProperty(preferenceName);
        Color  colorValue;

        try
        {
            colorValue     = Color.decode(stringValue);
        }
        catch (Exception e)
        {
            throw new PreferencesException("Can't convert preference '" +
                preferenceName + "'value '" + stringValue + "' to a Color.", e);
        }

        return colorValue;
    }

    /**
     * Returns an Enumeration (ordered if possible) of preference names which
     * start with given string. One given empty string make all preference
     * entries returned.
     *
     * @return Enumeration a string enumeration of preference names
     */
    public Enumeration getPreferences(String prefix)
    {
        MCSLogger.trace();

        int         size               = 0;
        Enumeration e                  = _currentProperties.propertyNames();
        Vector      shuffledProperties = new Vector();

        // Count the number of properties for the given index and store them
        while (e.hasMoreElements())
        {
            String propertyName = (String) e.nextElement();

            if (propertyName.startsWith(prefix))
            {
                size++;
                shuffledProperties.add(propertyName);
            }
        }

        String[] orderedProperties = new String[size];
        e = shuffledProperties.elements();

        // Order the stored properties if needed
        while (e.hasMoreElements())
        {
            String propertyName  = (String) e.nextElement();

            int    propertyOrder = getPreferenceOrder(propertyName);

            // If the property is ordered
            if (propertyOrder > -1)
            {
                // Store it at the right position
                orderedProperties[propertyOrder] = propertyName;
            }
            else
            {
                // Break and return the shuffled enumeration
                return shuffledProperties.elements();
            }
        }

        // Get an enumaration by converting the array -> List -> Vector -> Enumeration
        List        orderedList        = Arrays.asList(orderedProperties);
        Vector      orderedVector      = new Vector(orderedList);
        Enumeration orderedEnumeration = orderedVector.elements();

        return orderedEnumeration;
    }

    /**
     * Restore default values to preferences. Use save method to store default
     * values into the preferences file.
     */
    public void resetToDefaultPreferences()
    {
        MCSLogger.trace();

        _currentProperties = (Properties) _defaultProperties.clone();

        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * Set the default properties used to reset default preferences.
     * This method should be used to adjust specific application preferences.
     *
     * @param defaultProperties the default properties to set for this application.
     */
    protected void setDefaultPreferences(Preferences defaults)
    {
        MCSLogger.trace();

        _defaultProperties = (Properties) defaults._currentProperties.clone();

        // Notify all preferences listener.
        setChanged();
        notifyObservers();
    }

    /**
     * String representation. Print filename and preferences.
     *
     * @return the representation.
     */
    public String toString()
    {
        MCSLogger.trace();

        return "Preferences stored into [" + getPreferenceFilename() + "] : " +
        _currentProperties;
    }

    /**
     * main method used to test this class.
     *
     * @param args command line arguments
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
