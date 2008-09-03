/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.21 2008-09-03 16:19:59 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.20  2008/08/28 12:34:06  lafrasse
 * Changed preference file name API.
 * Added Preference file version differences handling APIs.
 * Enhanced documentation.
 * Removed test main() as class became abstract.
 *
 * Revision 1.19  2008/07/25 14:12:02  lafrasse
 * Corrected Mac OS X prefrerence file path generation.
 *
 * Revision 1.18  2008/06/27 11:22:32  bcolucci
 * Improve again the way to get the properties file path according
 * to the user OS.
 *
 * Revision 1.17  2008/06/25 12:04:18  bcolucci
 * Fix properties file name bug.
 *
 * Revision 1.16  2008/06/25 08:20:54  bcolucci
 * Add a surcharge method of saveToFile in order to permit to specify
 * a comment.
 *
 * Revision 1.15  2008/06/25 08:11:51  bcolucci
 * Improve the way to get the properties file in function of
 * the OS.
 *
 * Revision 1.14  2008/06/23 07:49:30  bcolucci
 * Use SystemUtils class from apache common lang library
 * instead of user.home property in order to know
 * the default user home directory according to the OS.
 *
 * Revision 1.13  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
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

import org.apache.commons.lang.SystemUtils;

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
public abstract class Preferences extends Observable
{
    /**
     * Store hidden preference version number name.
     */
    private static String _preferencesVersionNumberName = "preferences.version";

    /**
     * Store hidden properties index prefix.
     */
    private static String _indexPrefix = "MCSPropertyIndexes.";

    /**
     * Internal storage of preferences.
     */
    private Properties _currentProperties = new Properties();

    /**
     * Default propertiy values.
     */
    protected Properties _defaultProperties = new Properties();

    /*
       {
           setDefaultPreference("key", "value");
       }
     */

    /**
     * Creates a new Preferences object.
     *
     * This will try to load the preference file, if any.
     */
    public Preferences()
    {
        try
        {
            setDefaultPreferences();
            loadFromFile();
        }
        catch (Exception ex)
        {
            MCSLogger.error("Default preference values creation FAILED." + ex);
        }
    }

    /**
     * Return the preference filename.
     *
     * @warning Classes that herits from Preferences class MUST overload this
     * method to return specific file name.
     *
     * @return the preference filename, without any file separator.
     */
    protected abstract String getPreferenceFilename();

    /**
     * Return the version of the structure of the preference file.
     *
     * @warning Classes that herits from Preferences class MUST overload this
     * method to return specific file name.
     *
     * @return the preference current version.
     */
    protected abstract int getPreferencesVersionNumber();

    /**
     * Hook to handle update of older preference file version.
     *
     * The default implementation triggers default values load.
     *
     * @warning This method should be overriden to process older files. In its
     * default behavior, default values will be loaded instead.
     *
     * @param loadedVersionNumber the version of the loaded preference file.
     *
     * @return should return true if the update went fine, false otherwise to
     * automaticcaly trigger default values load.
     */
    protected boolean updatePreferencesVersion(int loadedVersionNumber)
    {
        MCSLogger.trace();

        return false;
    }

    /**
     * Set the default properties used to reset default preferences.
     * This method should be used to adjust specific application preferences.
     *
     * @warning Classes that herits from Preferences MUST overload this method
     * to set default preferences.
     */
    protected abstract void setDefaultPreferences() throws PreferencesException;

    /**
     * Returns the path of file containing preferences values, as this varies
     * accross different execution platforms.
     *
     * @return a string containing the full file path to the preference file,
     * according to execution platform.
     */
    public String getPreferenceFilepath()
    {
        MCSLogger.trace();

        // [USER_HOME]/
        String cfgName = SystemUtils.USER_HOME + File.separator;

        // Under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX)
        {
            // [USER_HOME]/Library/Preferences/
            cfgName += ("Library" + File.separator + "Preferences" +
            File.separator);
        }

        // Under Windows
        else if (SystemUtils.IS_OS_WINDOWS)
        {
            // [USER_HOME]/Local Settings/Application Data/
            cfgName += ("Local Settings" + File.separator + "Application Data" +
            File.separator);
        }

        // Under Linux, and anything else
        else
        {
            // [USER_HOME]/.
            cfgName += ".";
        }

        // Windows : [USER_HOME]/Local Settings/Application Data/fr.jmmc...properties
        // UNIX : [USER_HOME]/.fr.jmmc...properties
        // MAC OS X : [USER_HOME]/Library/Preferences/fr.jmmc...properties
        cfgName += getPreferenceFilename();

        return cfgName;
    }

    /**
     * Load preferences from file if any or reset to default values.
     */
    public void loadFromFile()
    {
        MCSLogger.trace();

        String cfgName = getPreferenceFilepath();

        resetToDefaultPreferences();

        try
        {
            System.out.println("Loading '" + cfgName + "' preference file.");
            MCSLogger.info("Loading '" + cfgName + "' preference file.");

            // Laoding preference file
            FileInputStream inputFile = new FileInputStream(cfgName);
            _currentProperties.loadFromXML(inputFile);

            // Getting laoded preference file version number
            int loadedPreferenceVersion  = getPreferenceAsInt(_preferencesVersionNumberName);
            int preferencesVersionNumber = getPreferencesVersionNumber();

            // If the preference file version is older the the current default version
            if (loadedPreferenceVersion < preferencesVersionNumber)
            {
                MCSLogger.warning(
                    "Loaded an 'anterior to current version' preference file, so try to update preference file.");

                // Handle version differences
                boolean status = updatePreferencesVersion(loadedPreferenceVersion);

                if (status == false)
                {
                    resetToDefaultPreferences();
                }
            }

            // If the preference file version is newer the the current default version
            if (loadedPreferenceVersion > preferencesVersionNumber)
            {
                MCSLogger.warning(
                    "Loaded a 'posterior to current version' preference file, so fall back to default values instead.");

                // Use current default values instead
                resetToDefaultPreferences();
            }
        }
        catch (IOException e)
        {
            // Do nothing just default values will be into the preferences.
            MCSLogger.warning(
                "Failed loading preference file, so fall back to default values instead.");
            resetToDefaultPreferences();
        }

        // Notify all preferences listener of maybe new values coming from file.
        triggerObserversNotification();
    }

    /**
     * Save preferences into preferences file.
     *
     * @throws PreferencesException DOCUMENT ME!
     */
    final public void saveToFile() throws PreferencesException
    {
        MCSLogger.trace();

        saveToFile(null);
    }

    /**
     * Save preferences into preferences file.
     *
     * @param comment comment to b included in the preference file
     *
     * @throws PreferencesException DOCUMENT ME!
     */
    final public void saveToFile(String comment) throws PreferencesException
    {
        MCSLogger.trace();

        try
        {
            String           cfgName    = getPreferenceFilepath();
            FileOutputStream outputFile = new FileOutputStream(cfgName);
            _currentProperties.storeToXML(outputFile, comment);
            outputFile.close();
        }
        catch (Exception e)
        {
            throw new PreferencesException("Can't store preferences to file", e);
        }
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
        triggerObserversNotification();
    }

    /**
     * Set a preference in the given properties set.
     *
     * @param properties the properties set to modify.
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setPreferenceToProperties(Properties properties,
        String preferenceName, Object preferenceValue)
        throws PreferencesException
    {
        MCSLogger.trace();

        // Wiil automatically get -1 for a yet undefined preference
        int order = getPreferenceOrder(preferenceName);

        setPreferenceToProperties(properties, preferenceName, order,
            preferenceValue);
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

        setPreferenceToProperties(_currentProperties, preferenceName,
            preferenceValue);
    }

    /**
     * Set a preference.
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setDefaultPreference(String preferenceName,
        Object preferenceValue) throws PreferencesException
    {
        MCSLogger.trace();

        setPreferenceToProperties(_defaultProperties, preferenceName,
            preferenceValue);
    }

    /**
     * Set a preference in the given properties set.
     *
     * @param properties the properties set to modify.
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     */
    final private void setPreferenceToProperties(Properties properties,
        String preferenceName, int preferenceIndex, Object preferenceValue)
        throws PreferencesException
    {
        MCSLogger.trace();

        // If the constraint is a String object
        if (preferenceValue.getClass() == java.lang.String.class)
        {
            properties.setProperty(preferenceName, (String) preferenceValue);
        }

        // Else if the constraint is a Boolean object
        else if (preferenceValue.getClass() == java.lang.Boolean.class)
        {
            properties.setProperty(preferenceName,
                ((Boolean) preferenceValue).toString());
        }

        // Else if the constraint is an Integer object
        else if (preferenceValue.getClass() == java.lang.Integer.class)
        {
            properties.setProperty(preferenceName,
                ((Integer) preferenceValue).toString());
        }

        // Else if the constraint is a Double object
        else if (preferenceValue.getClass() == java.lang.Double.class)
        {
            properties.setProperty(preferenceName,
                ((Double) preferenceValue).toString());
        }

        // Else if the constraint is a Color object
        else if (preferenceValue.getClass() == java.awt.Color.class)
        {
            properties.setProperty(preferenceName,
                fr.jmmc.mcs.util.ColorEncoder.encode((Color) preferenceValue));
        }

        // Otherwise we don't know how to handle the given object type
        else
        {
            throw new PreferencesException(
                "Can't handle the given preference value.");
        }

        // Add property index for order if needed
        setPreferenceOrderToProperties(properties, preferenceName,
            preferenceIndex);

        // Notify all preferences listener.
        triggerObserversNotification();
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

        setPreferenceToProperties(_currentProperties, preferenceName,
            preferenceIndex, preferenceValue);
    }

    /**
     * Set a preference default value.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     */
    final public void setDefaultPreference(String preferenceName,
        int preferenceIndex, Object preferenceValue)
        throws PreferencesException
    {
        MCSLogger.trace();

        setPreferenceToProperties(_defaultProperties, preferenceName,
            preferenceIndex, preferenceValue);
    }

    /**
     * Set a preference order in the given properties set.
     *
     * @param properties the properties set to modify.
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     */
    final private void setPreferenceOrderToProperties(Properties properties,
        String preferenceName, int preferenceIndex)
    {
        MCSLogger.trace();

        // Add property index for order if needed
        if (preferenceIndex > -1)
        {
            properties.setProperty(_indexPrefix + preferenceName,
                Integer.toString(preferenceIndex));
        }
        else
        {
            properties.setProperty(_indexPrefix + preferenceName,
                Integer.toString(-1));
        }

        // Notify all preferences listener.
        triggerObserversNotification();
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

        setPreferenceOrderToProperties(_currentProperties, preferenceName,
            preferenceIndex);
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
     * Get an integer preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one integer representing the preference value.
     */
    final public int getPreferenceAsInt(String preferenceName)
    {
        MCSLogger.trace();

        String value = _currentProperties.getProperty(preferenceName);

        return Integer.valueOf(value).intValue();
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
     * Trigger a notification of change to all registered Observers.
     */
    public void triggerObserversNotification()
    {
        MCSLogger.trace();

        // Notify all preferences listener of maybe new values coming from file.
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

        return "Preferences file " + getPreferenceFilepath() + " contains :\n" +
        _currentProperties;
    }
}
