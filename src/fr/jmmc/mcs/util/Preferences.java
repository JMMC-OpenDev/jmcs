/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.39 2011-03-30 09:08:24 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.38  2011/03/02 16:14:37  bourgesl
 * fixed quiet mode
 *
 * Revision 1.37  2011/02/24 17:15:12  bourgesl
 * use quiet flag to hide a logging message
 *
 * Revision 1.36  2011/02/02 13:54:42  mella
 * Fix bug with static member describing filename
 * Do info log during restore and save actions
 * Use parent classname for registering of save and register actions to remove registrar overwriting
 *
 * Revision 1.35  2010/10/15 08:47:06  lafrasse
 * (Hopefully) better exception handling.
 *
 * Revision 1.34  2010/10/14 07:43:38  lafrasse
 * Moved from Vector to ArrayList whenever possible (i.e without API breaks).
 * Fixed comments typo.
 * Reformatted code.
 *
 * Revision 1.33  2010/09/02 09:45:35  mella
 * Declare savePreferences and restoreDefaultPreferences actions
 *
 * Revision 1.32  2010/05/21 13:51:44  mella
 * add javadoc
 *
 * Revision 1.31  2010/05/21 13:48:21  mella
 * Do not notifyObservers anylonger if the preference get the same value
 *
 * Revision 1.30  2010/05/21 13:15:19  mella
 * remove some duplicated calls of notifyObservers
 *
 * Revision 1.29  2009/05/04 12:05:22  lafrasse
 * Added hability to remove (ordered) preference.
 *
 * Revision 1.28  2008/11/18 09:14:51  lafrasse
 * Corrected documentation and log entering in updatePreferencesVersion().
 *
 * Revision 1.27  2008/11/06 13:45:02  mella
 * fix better loging level
 *
 * Revision 1.26  2008/10/16 11:56:30  mella
 * improve logging replacing MCSLogger by common logger
 *
 * Revision 1.25  2008/09/17 21:42:55  lafrasse
 * Enhanced documentation.
 * Updated to handle multiple revision update in a raw on file load.
 * Automated updated file save.
 *
 * Revision 1.24  2008/09/05 16:11:38  lafrasse
 * Typo fix.
 *
 * Revision 1.23  2008/09/05 08:26:33  lafrasse
 * Code, documentation and log enhancement.
 *
 * Revision 1.22  2008/09/04 11:37:33  lafrasse
 * Removed forgotten output trace.
 *
 * Revision 1.21  2008/09/03 16:19:59  lafrasse
 * Enforced default preferences definition.
 *
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

import org.apache.commons.lang.SystemUtils;

import java.awt.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

/**
 * Preferences can be managed using String values with this class.
 * Reuse can be done just extending this class and using setShortPreferenceFilename
 * and setDefaultProperties methods.
 * Application must take care of multiple instances that shares the same
 * preference file (RW conflict...).
 *
 * If instanciated in the App.init(), you can use the instantiated actions which
 * save preference to file or restore preferences that get default values.
 * To use them in
 * &lt;menu label="Preferences"&gt;
 *  &lt;menu label="Save to file" classpath="fr.jmmc.mcs.util.Preferences" action="savePreferences"/&gt;
 *  &lt;menu label="Set default values" classpath="fr.jmmc.mcs.util.Preferences" action="restorePreferences"/&gt;
 * &lt;/menu&gt;
 */
public abstract class Preferences extends Observable
{

    /** Class name */
    private static final String _className = "fr.jmmc.mcs.util.Preferences";
    /** Logger - get from given class name */
    private static final Logger _logger = Logger.getLogger(_className);
    /** Store hidden preference version number name. */
    private static final String PREFERENCES_VERSION_NUMBER_ID = "preferences.version";
    /** Store hidden properties index prefix. */
    private static final String PREFERENCES_ORDER_INDEX_PREFIX = "MCSPropertyIndexes.";
    /* members */
    /** Store preference filename. */
    private String _fullFilepath = null;
    /** Internal storage of preferences. */
    private Properties _currentProperties = new Properties();
    /** Default property. */
    protected final Properties _defaultProperties = new Properties();
    /** Save to file action */
    protected final Action _savePreferences;
    /** Restore preferences that get one default value */
    protected final Action _restoreDefaultPreferences;

    /**
     * Creates a new Preferences object.
     *
     * This will set default preferences values (by invoking user overridden
     * setDefaultPreferences()), then try to load the preference file, if any.
     */
    public Preferences()
    {
        computePreferenceFilepath();

        try {
            setDefaultPreferences();

            loadFromFile();
        } catch (Exception ex) {
            _logger.log(Level.WARNING, "Preference initialization FAILED.", ex);
        }

        // parent class name must be given to register one action per inherited Preference class
        _savePreferences = new SavePrefAction(this.getClass().getName());
        _restoreDefaultPreferences = new RestoreDefaultPrefAction(this.getClass().getName());
    }

    /**
     * MUST set the default properties used to reset default preferences.
     *
     * @warning Classes that inherits from Preferences MUST overload this method
     * to set default preferences.
     */
    protected abstract void setDefaultPreferences() throws PreferencesException;

    /**
     * MUST return the preference filename.
     *
     * Should return the filename to be used for preference load and save. This
     * name should be in the form "fr.jmmc.searchcal.properties".
     *
     * @warning Classes that inherits from Preferences class MUST overload this
     * method in order to properly load preference file.
     *
     * @return the preference filename, without any file separator.
     */
    protected abstract String getPreferenceFilename();

    /**
     * MUST return the revision number of the structure of the preference file.
     *
     * The revision number (a positive integer greater than 0) returned should
     * be incremented each time preference structure changes, in order to
     * automatically trigger updatePreferencesVersion() execution, in order to
     * handle changes when final user update its JMMC software.
     *
     * @warning Classes that inherits from Preferences class MUST overload this
     * method to return specific file name.
     *
     * @return the preference current version.
     */
    protected abstract int getPreferencesVersionNumber();

    /**
     * Hook to handle updates of older preference file version.
     *
     * The default implementation triggers a 'safe' default values load.
     *
     * This method is automatically triggered when the preference file loaded is
     * bound to a previous version of your Preference-derived object. Thus, you
     * have a chance to load previous values and update them if needed.
     *
     * @warning This method SHOULD be overridden in order to process older files.
     * Otherwise, default values will be loaded instead.
     *
     * @warning This method MUST perform one update revision jump at a time, as
     * it will be called as many time as necessary to reach the desired
     * revision state (i.e, if loaded revision is 2 and current revision is 5,
     * this method will be called three times: once to update from rev. 2 to
     * rev. 3; once to update from rev. 3 to rev. 4; and once to update from
     * rev. 4 to rev. 5).
     *
     * @param loadedVersionNumber the version of the loaded preference file.
     *
     * @return should return true if the update went fine and new values should
     * be saved, false otherwise to automatically trigger default values load.
     */
    protected boolean updatePreferencesVersion(int loadedVersionNumber)
    {
        _logger.entering(_className, "updatePreferencesVersion");

        // By default, triggers default values load.
        return false;
    }

    /**
     * Load preferences from file if any, or reset to default values and
     * notify listeners.
     *
     * @warning Any preference value change not yet saved will be LOST.
     */
    final public void loadFromFile()
    {
        _logger.entering(_className, "loadFromFile");

        resetToDefaultPreferences(true);

        try {
            // Loading preference file
            FileInputStream inputFile = null;
            try {
                inputFile = new FileInputStream(_fullFilepath);
            } catch (FileNotFoundException fnfe) {
                _logger.warning("Cannot load '" + _fullFilepath + "' : " + fnfe);
            }
            _logger.info("Loading '" + _fullFilepath + "' preference file.");

            try {
                _currentProperties.loadFromXML(inputFile);
            } catch (InvalidPropertiesFormatException ipfe) {
                _logger.severe("Cannot parse '" + _fullFilepath + "' preference file : " + ipfe);
            } catch (IOException ioe) {
                _logger.warning("Cannot input/ouput to'" + _fullFilepath + "' : " + ioe);
            }

            // Getting loaded preference file version number
            int preferencesVersionNumber = getPreferencesVersionNumber();
            int loadedPreferenceVersion = Integer.MIN_VALUE; // To be sure to be below most preferencesVersionNumber, as Java does not provide unsigned types to garanty positive values from getPreferencesVersionNumber() !!!

            try {
                loadedPreferenceVersion = getPreferenceAsInt(PREFERENCES_VERSION_NUMBER_ID);
            } catch (NumberFormatException nfe) {
                _logger.log(Level.WARNING,
                        "Cannot get loaded preference version number.", nfe);
            }

            _logger.fine("Loaded preference version is '"
                    + loadedPreferenceVersion
                    + "', current preferences version number is '"
                    + preferencesVersionNumber + "'.");

            // If the preference file version is older than the current default version
            if (loadedPreferenceVersion < preferencesVersionNumber) {
                _logger.warning(
                        "Loaded an 'anterior to current version' preference file, will try to update preference file.");

                // Handle version differences
                int currentPreferenceVersion = loadedPreferenceVersion;
                boolean shouldWeContinue = true;

                while ((shouldWeContinue == true)
                        && (currentPreferenceVersion < preferencesVersionNumber)) {
                    _logger.fine(
                            "Trying to update loaded preferences from revision '"
                            + currentPreferenceVersion + "'.");

                    shouldWeContinue = updatePreferencesVersion(currentPreferenceVersion);

                    currentPreferenceVersion++;
                }

                // If update went wrong (or was not handled)
                if (shouldWeContinue == false) {
                    // Use default values instead
                    resetToDefaultPreferences();
                } else {
                    try {
                        // Otherwise save updated values to file
                        saveToFile();
                    } catch (PreferencesException pe) {
                        _logger.log(Level.WARNING,
                                "Cannot save preference to disk: ", pe);
                    }
                }
            }

            // If the preference file version is newer the the current default version
            if (loadedPreferenceVersion > preferencesVersionNumber) {
                _logger.warning(
                        "Loaded a 'posterior to current version' preference file, so fall back to default values instead.");

                // Use current default values instead
                resetToDefaultPreferences();
            }
        } catch (Exception ex) {
            // Do nothing just default values will be into the preferences.
            _logger.log(Level.FINE,
                    "Failed loading preference file, so fall back to default values instead : ",
                    ex);

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
        saveToFile(null);
    }

    /**
     * Save preferences into preferences file.
     *
     * @param comment comment to be included in the preference file
     *
     * @throws PreferencesException DOCUMENT ME!
     */
    final public void saveToFile(String comment) throws PreferencesException
    {
        _logger.entering(_className, "saveToFile");

        // Store current Preference object revision number
        int preferencesVersionNumber = getPreferencesVersionNumber();
        setPreference(PREFERENCES_VERSION_NUMBER_ID, preferencesVersionNumber);

        try {
            FileOutputStream outputFile = new FileOutputStream(_fullFilepath);
            _currentProperties.storeToXML(outputFile, comment);
            _logger.info("Saving '" + _fullFilepath + "' preference file.");
            outputFile.close();
        } catch (Exception e) {
            throw new PreferencesException("Cannot store preferences to file", e);
        }
    }

    /**
     * Restore default values to preferences and notify listeners.
     */
    final public void resetToDefaultPreferences()
    {
        resetToDefaultPreferences(false);
    }

    /**
     * Restore default values to preferences and notify listeners.
     * @param quiet display one info message log if true.
     */
    final public void resetToDefaultPreferences(final boolean quiet)
    {
        _logger.entering(_className, "resetToDefaultPreferences");

        _currentProperties = (Properties) _defaultProperties.clone();

        if (!quiet) {
            _logger.info("Restoring default preferences.");
        }

        // Notify all preferences listener.
        triggerObserversNotification();
    }

    /**
     * Set a preference.
     * The listeners are notified only for preference changes.
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName,
            Object preferenceValue) throws PreferencesException
    {
        _logger.entering(_className, "setPreference");

        setPreferenceToProperties(_currentProperties, preferenceName,
                preferenceValue);
    }

    /**
     * Set a preference.
     * The listeners are notified only for preference changes.
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setDefaultPreference(String preferenceName,
            Object preferenceValue) throws PreferencesException
    {
        _logger.entering(_className, "setDefaultPreference");

        setPreferenceToProperties(_defaultProperties, preferenceName,
                preferenceValue);
    }

    /**
     * Set a preference in the given properties set.
     *
     * @param properties the properties set to modify.
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final private void setPreferenceToProperties(Properties properties,
            String preferenceName, Object preferenceValue)
            throws PreferencesException
    {
        // Will automatically get -1 for a yet undefined preference
        int order = getPreferenceOrder(preferenceName);

        setPreferenceToProperties(properties, preferenceName, order,
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
        _logger.entering(_className, "setPreferenceToProperties");

        String currentValue = properties.getProperty(preferenceName);
        if (currentValue != null && currentValue.equals(preferenceValue.toString())) {
            // nothing to do
            _logger.finest("Preference '" + preferenceName + "' not changed");
            return;
        }

        // If the constraint is a String object
        if (preferenceValue.getClass() == java.lang.String.class) {
            properties.setProperty(preferenceName, (String) preferenceValue);
        } // Else if the constraint is a Boolean object
        else if (preferenceValue.getClass() == java.lang.Boolean.class) {
            properties.setProperty(preferenceName,
                    ((Boolean) preferenceValue).toString());
        } // Else if the constraint is an Integer object
        else if (preferenceValue.getClass() == java.lang.Integer.class) {
            properties.setProperty(preferenceName,
                    ((Integer) preferenceValue).toString());
        } // Else if the constraint is a Double object
        else if (preferenceValue.getClass() == java.lang.Double.class) {
            properties.setProperty(preferenceName,
                    ((Double) preferenceValue).toString());
        } // Else if the constraint is a Color object
        else if (preferenceValue.getClass() == java.awt.Color.class) {
            properties.setProperty(preferenceName,
                    fr.jmmc.mcs.util.ColorEncoder.encode((Color) preferenceValue));
        } // Otherwise we don't know how to handle the given object type
        else {
            throw new PreferencesException(
                    "Cannot handle the given preference value.");
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
        setPreferenceToProperties(_defaultProperties, preferenceName,
                preferenceIndex, preferenceValue);
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
        setPreferenceOrderToProperties(_currentProperties, preferenceName,
                preferenceIndex);
        // Notify all preferences listener.
        triggerObserversNotification();
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
        // Add property index for order if needed
        if (preferenceIndex > -1) {
            properties.setProperty(PREFERENCES_ORDER_INDEX_PREFIX + preferenceName,
                    Integer.toString(preferenceIndex));
        } else {
            properties.setProperty(PREFERENCES_ORDER_INDEX_PREFIX + preferenceName,
                    Integer.toString(-1));
        }
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
        _logger.entering(_className, "getPreferenceOrder");

        // -1 is the flag value for no order found, so it is the default value.
        int result = -1;

        // If the asked order is NOT about an internal MCS index property
        if (preferenceName.startsWith(PREFERENCES_ORDER_INDEX_PREFIX) == false) {
            // Get the corresponding order as a String
            String orderString = _currentProperties.getProperty(PREFERENCES_ORDER_INDEX_PREFIX
                    + preferenceName);

            // If an order token was found
            if (orderString != null) {
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
        _logger.entering(_className, "getPreference");

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
        _logger.entering(_className, "getPreferenceAsBoolean");

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
        _logger.entering(_className, "getPreferenceAsDouble");

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
        _logger.entering(_className, "getPreferenceAsInt");

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
        _logger.entering(_className, "getPreferenceAsColor");

        String stringValue = _currentProperties.getProperty(preferenceName);
        Color colorValue;

        try {
            colorValue = Color.decode(stringValue);
        } catch (Exception e) {
            throw new PreferencesException("Cannot convert preference '"
                    + preferenceName + "'value '" + stringValue + "' to a Color.", e);
        }

        return colorValue;
    }

    /**
     * Remove the given preference.
     *
     * If the given preference belongs to an ordered preferences group,
     * preferences left will be re-ordered down to fullfil the place left empty,
     * as expected. For example, {'LOW', 'MEDIUM', 'HIGH'} - MEDIUM will become
     * {'LOW', 'HIGH'}, not {'LOW, '', 'HIGH'}.
     *
     * @param preferenceName the preference name.
     */
    final public void removePreference(String preferenceName)
    {
        _logger.entering(_className, "removePreference");

        _logger.fine("Removing preference '" + preferenceName + "'.");

        // Get the given preference order, if any
        int preferenceOrder = getPreferenceOrder(preferenceName);

        if (preferenceOrder != -1) {
            // Compute preference group prefix name
            String preferencesPrefix = null;
            int indexOfLastDot = preferenceName.lastIndexOf('.');

            if ((indexOfLastDot > 0)
                    && (indexOfLastDot < (preferenceName.length() - 1))) {
                preferencesPrefix = preferenceName.substring(0, indexOfLastDot);
            }

            _logger.finer("Removing preference from ordered group '"
                    + preferencesPrefix + "'.");

            // For each group preferences
            Enumeration orderedPreferences = getPreferences(preferencesPrefix);

            while (orderedPreferences.hasMoreElements()) {
                String orderedPreferenceName = (String) orderedPreferences.nextElement();

                int preferenceIndex = getPreferenceOrder(orderedPreferenceName);

                if (preferenceIndex > preferenceOrder) {
                    int destinationIndex = preferenceIndex - 1;

                    _logger.finest("Re-ordering preference '"
                            + orderedPreferenceName + "' from index '"
                            + preferenceIndex + "' to index '" + destinationIndex
                            + "'.");

                    setPreferenceOrder(orderedPreferenceName, destinationIndex);
                }
            }
        }

        // Removing the given preference and its ordering index
        _currentProperties.remove(preferenceName);
        _currentProperties.remove(PREFERENCES_ORDER_INDEX_PREFIX + preferenceName);
    }

    /**
     * Returns an Enumeration (ordered if possible) of preference names which
     * start with given string. One given empty string make all preference
     * entries returned.
     *
     * @return Enumeration a string enumeration of preference names
     */
    final public Enumeration getPreferences(String prefix)
    {
        _logger.entering(_className, "getPreferences");

        int size = 0;
        Enumeration e = _currentProperties.propertyNames();
        List shuffledProperties = new ArrayList<String>();

        // Count the number of properties for the given index and store them
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();

            if (propertyName.startsWith(prefix)) {
                size++;
                shuffledProperties.add(propertyName);
            }
        }

        String[] orderedProperties = new String[size];
        Iterator<String> shuffledPropertiesIterator = shuffledProperties.iterator();

        // Order the stored properties if needed
        while (shuffledPropertiesIterator.hasNext()) {
            String propertyName = shuffledPropertiesIterator.next();

            int propertyOrder = getPreferenceOrder(propertyName);

            // If the property is ordered
            if (propertyOrder > -1) {
                // Store it at the right position
                orderedProperties[propertyOrder] = propertyName;
            } else {
                // Break and return the shuffled enumeration
                return Collections.enumeration(shuffledProperties);
            }
        }

        // Get an enumaration by converting the array -> List -> Vector -> Enumeration
        List orderedList = Arrays.asList(orderedProperties);
        Vector orderedVector = new Vector(orderedList);
        Enumeration orderedEnumeration = orderedVector.elements();

        return orderedEnumeration;
    }

    /**
     * Returns the path of file containing preferences values, as this varies
     * accross different execution platforms.
     *
     * @return a string containing the full file path to the preference file,
     * according to execution platform.
     */
    final private String computePreferenceFilepath()
    {
        _logger.entering(_className, "computePreferenceFilepath");

        // [USER_HOME]/
        _fullFilepath = SystemUtils.USER_HOME + File.separator;

        // Under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            // [USER_HOME]/Library/Preferences/
            _fullFilepath += ("Library" + File.separator + "Preferences"
                    + File.separator);
        } // Under Windows
        else if (SystemUtils.IS_OS_WINDOWS) {
            // [USER_HOME]/Local Settings/Application Data/
            _fullFilepath += ("Local Settings" + File.separator
                    + "Application Data" + File.separator);
        } // Under Linux, and anything else
        else {
            // [USER_HOME]/.
            _fullFilepath += ".";
        }

        // Windows : [USER_HOME]/Local Settings/Application Data/fr.jmmc...properties
        // UNIX : [USER_HOME]/.fr.jmmc...properties
        // MAC OS X : [USER_HOME]/Library/Preferences/fr.jmmc..._rev4.properties
        _fullFilepath += getPreferenceFilename();

        _logger.fine("Computed preference file path = '" + _fullFilepath
                + "'.");

        return _fullFilepath;
    }

    /**
     * Trigger a notification of change to all registered Observers.
     */
    final public void triggerObserversNotification()
    {
        _logger.entering(_className, "triggerObserversNotification");

        // Notify all preferences listener of maybe new values coming from file.
        setChanged();
        notifyObservers();
    }

    /**
     * String representation. Print filename and preferences.
     *
     * @return the representation.
     */
    final public String toString()
    {
        return "Preferences file '" + _fullFilepath + "' contains :\n"
                + _currentProperties;
    }

    public Action getSavePreferences()
    {
        return _savePreferences;
    }

    public Action getRestoreDefaultPreferences()
    {
        return _restoreDefaultPreferences;
    }

    // @todo try to move it into the mcs preferences area
    protected class SavePrefAction extends RegisteredAction
    {

        public SavePrefAction(String parentClassName)
        {
            super(parentClassName, "savePreferences");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            try {
                saveToFile();
            } catch (PreferencesException pe) {
                // @todo handle this error at user level
                pe.printStackTrace();
            }
        }
    }

    protected class RestoreDefaultPrefAction extends RegisteredAction
    {

        public RestoreDefaultPrefAction(String parentClassName)
        {
            super(parentClassName, "restorePreferences");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            try {
                resetToDefaultPreferences();
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }

    /**
     * Dump all properties (sorted by keys)
     * @param properties properties to dump
     * @return string representation of properties using the format "{name} : {value}"
     */
    public static final String dumpProperties(final Properties properties)
    {
        if (properties != null) {
            // sort properties :
            final String[] keys = new String[properties.size()];
            properties.keySet().toArray(keys);
            Arrays.sort(keys);

            final StringBuilder sb = new StringBuilder(2048);
            // For each property, we make a string like "{name} : {value}"
            for (String key : keys) {
                sb.append(key).append(" : ").append(properties.getProperty(key)).append("\n");
            }

            return sb.toString();
        }
        return "";
    }
}
