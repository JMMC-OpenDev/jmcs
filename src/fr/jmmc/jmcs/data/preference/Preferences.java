/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.util.FileUtils;
import org.apache.commons.lang.SystemUtils;

import java.awt.Color;
import java.awt.event.ActionEvent;

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
import javax.swing.SwingUtilities;

/**
 * This is the mother class to manage preferences (aka user defaults).
 * 
 * A singleton of yours SHALL derive from it, and implements all abstract methods.
 * Then use the observer/observable pattern to get notifications on value changes.
 *
 * Preferences are stored in key-value pairs. Keys are actually Object instances,
 * but their toString() representation is always used internally to identify each
 * value. SO WE STRONGLY ADVISE OU TO USE RAW STRINGS AS IDENTIFIERS.
 * We had to use this workaround in order to handle use of Enum as key repository
 * (so you don't have to explicitely call toSting() on each call requiring a key),
 * while still handling direct String key use.
 *
 * If your singleton is instanciated in App.init(), you can use the following actions
 * to save preferences to file or restore preferences that get default values:
 * &lt;menu label="Preferences"&gt;
 *  &lt;menu label="Save to file" classpath="fr.jmmc.jmcs.data.preference.Preferences" action="savePreferences"/&gt;
 *  &lt;menu label="Set default values" classpath="fr.jmmc.jmcs.data.preference.Preferences" action="restorePreferences"/&gt;
 * &lt;/menu&gt;
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA, Laurent BOURGES.
 */
public abstract class Preferences extends Observable {

    /** Class name */
    private static final String _className = Preferences.class.getName();
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
    /** flag to enable/disable observer notifications */
    private boolean notify;

    /**
     * Creates a new Preferences object.
     *
     * This will set default preferences values (by invoking user overridden
     * setDefaultPreferences()), then try to load the preference file, if any.
     */
    public Preferences() {
        this(true);
    }

    /**
     * Creates a new Preferences object.
     *
     * This will set default preferences values (by invoking user overridden
     * setDefaultPreferences()), then try to load the preference file, if any.
     *
     * @param notify flag to enable/disable observer notifications
     */
    public Preferences(final boolean notify) {
        setNotify(notify);

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
     * Use this method to define all your default values for each of your preferences,
     * by calling all the 'setDefaultPreference()' method family.
     *
     * @warning Classes that inherits from Preferences MUST overload this method
     * to set default preferences.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    protected abstract void setDefaultPreferences() throws PreferencesException;

    /**
     * Use this method to define your preference file name.
     *
     * Should return the filename to be used for preference load and save. This
     * name should be in the form "com.company.application.properties".
     *
     * @warning Please note that the complete to the preference file is different
     * among the different desktop OS. This handles this for you !
     *
     * @return the preference filename, without any file separator.
     */
    protected abstract String getPreferenceFilename();

    /**
     * Each preference files shall have a version number in it, in order to let you
     * update it when you fix bug or simply change your mind on something stored.
     * You shall use this method to return your most up-to-date version number.
     *
     * The version number (a positive integer greater than 0) returned must
     * be incremented BY YOU each time your preference structure changes.
     *
     * updatePreferencesVersion() execution will then be automatically triggered
     * in case an out-of-date preference file is loaded. You can then perform
     * corresponding updates to offer backward compatibility of your users preferences.
     *
     * @warning Classes that inherits from Preferences class MUST overload this
     * method to return specific file name.
     *
     * @return the most up-to-date preference file version number.
     */
    protected abstract int getPreferencesVersionNumber();

    /**
     * Hook to handle updates of older preference file version.
     *
     * This method is automatically triggered when the preference file loaded is
     * bound to a previous version of your Preference-derived object. Thus, you
     * have a chance to load previous values and update them if needed.
     *
     * @warning This method SHOULD be overridden in order to process older files.
     * Otherwise, default values will be loaded instead, potentially overwriting
     * previous users preferences !
     *
     * @warning This method MUST perform one update revision jump at a time. It
     * will be called as many time as needed to reach the latest revision state
     * (i.e, if loaded revision is 2 and current revision is 5, this method will
     * be called three times: once to update from rev. 2 to rev. 3; once to update
     * from rev. 3 to rev. 4; and once to update from rev. 4 to rev. 5).
     *
     * @param loadedVersionNumber the out-of-date version of the loaded preference file.
     *
     * @return should return true if the update went fine and new values should
     * be saved, false otherwise to automatically trigger default values load.
     */
    protected boolean updatePreferencesVersion(int loadedVersionNumber) {
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
    final public void loadFromFile() {
        _logger.entering(_className, "loadFromFile");

        resetToDefaultPreferences(true);

        try {
            // Loading preference file
            FileInputStream inputFile = null;
            try {
                inputFile = new FileInputStream(_fullFilepath);
            } catch (FileNotFoundException fnfe) {
                if (_logger.isLoggable(Level.WARNING)) {
                    _logger.warning("Cannot load '" + _fullFilepath + "' : " + fnfe);
                }
            }
            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("Loading '" + _fullFilepath + "' preference file.");
            }

            try {
                _currentProperties.loadFromXML(inputFile);
            } catch (InvalidPropertiesFormatException ipfe) {
                _logger.log(Level.SEVERE, "Cannot parse '" + _fullFilepath + "' preference file : ", ipfe);
            } catch (IOException ioe) {
                _logger.log(Level.WARNING, "Cannot input/ouput to'" + _fullFilepath + "' : ", ioe);
            }

            // Getting loaded preference file version number
            int preferencesVersionNumber = getPreferencesVersionNumber();
            int loadedPreferenceVersion = Integer.MIN_VALUE; // To be sure to be below most preferencesVersionNumber, as Java does not provide unsigned types to garanty positive values from getPreferencesVersionNumber() !!!

            try {
                loadedPreferenceVersion = getPreferenceAsInt(PREFERENCES_VERSION_NUMBER_ID);
            } catch (NumberFormatException nfe) {
                _logger.log(Level.WARNING, "Cannot get loaded preference version number.", nfe);
            }

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Loaded preference version is '" + loadedPreferenceVersion
                        + "', current preferences version number is '" + preferencesVersionNumber + "'.");
            }

            // If the preference file version is older than the current default version
            if (loadedPreferenceVersion < preferencesVersionNumber) {
                _logger.warning("Loaded an 'anterior to current version' preference file, will try to update preference file.");

                // Handle version differences
                int currentPreferenceVersion = loadedPreferenceVersion;
                boolean shouldWeContinue = true;

                while (shouldWeContinue && (currentPreferenceVersion < preferencesVersionNumber)) {
                    if (_logger.isLoggable(Level.FINE)) {
                        _logger.fine("Trying to update loaded preferences from revision '"
                                + currentPreferenceVersion + "'.");
                    }

                    shouldWeContinue = updatePreferencesVersion(currentPreferenceVersion);

                    currentPreferenceVersion++;
                }

                // If update went wrong (or was not handled)
                if (!shouldWeContinue) {
                    // Use default values instead
                    resetToDefaultPreferences();
                } else {
                    try {
                        // Otherwise save updated values to file
                        saveToFile();
                    } catch (PreferencesException pe) {
                        _logger.log(Level.WARNING, "Cannot save preference to disk: ", pe);
                    }
                }
            }

            // If the preference file version is newer the the current default version
            if (loadedPreferenceVersion > preferencesVersionNumber) {
                _logger.warning("Loaded a 'posterior to current version' preference file, so fall back to default values instead.");

                // Use current default values instead
                resetToDefaultPreferences();
            }
        } catch (Exception e) {
            // Do nothing just default values will be into the preferences.
            _logger.log(Level.FINE, "Failed loading preference file, so fall back to default values instead : ", e);

            resetToDefaultPreferences();
        }

        // Notify all preferences listener of maybe new values coming from file.
        triggerObserversNotification();
    }

    /**
     * Save the preferences state in memory (i.e for the current session) to file.
     *
     * @throws PreferencesException if the preference file could not be written.
     */
    final public void saveToFile() throws PreferencesException {
        saveToFile(null);
    }

    /**
     * Save the preferences state in memory (i.e for the current session) to file.
     *
     * @param comment comment to be included in the preference file
     *
     * @throws PreferencesException if the preference file could not be written.
     */
    final public void saveToFile(String comment) throws PreferencesException {
        _logger.entering(_className, "saveToFile");

        // Store current Preference object revision number
        int preferencesVersionNumber = getPreferencesVersionNumber();
        setPreference(PREFERENCES_VERSION_NUMBER_ID, preferencesVersionNumber);

        FileOutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(_fullFilepath);
            _currentProperties.storeToXML(outputFile, comment);

            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("Saving '" + _fullFilepath + "' preference file.");
            }

        } catch (Exception e) {
            throw new PreferencesException("Cannot store preferences to file", e);
        } finally {
            FileUtils.closeStream(outputFile);
        }
    }

    /**
     * Restore default values to preferences and notify listeners.
     */
    final public void resetToDefaultPreferences() {
        resetToDefaultPreferences(false);
    }

    /**
     * Restore default values to preferences and notify listeners.
     * @param quiet display one info message log if true (debugging purpose).
     */
    final public void resetToDefaultPreferences(final boolean quiet) {
        _logger.entering(_className, "resetToDefaultPreferences");

        _currentProperties = (Properties) _defaultProperties.clone();

        if (!quiet) {
            _logger.info("Restoring default preferences.");
        }

        // Notify all preferences listener.
        triggerObserversNotification();
    }

    /**
     * Set a preference value.
     * The listeners are notified only for preference changes (multiple calls with
     * the same value does NOT trigger notifications).
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final public void setPreference(Object preferenceName,
            Object preferenceValue) throws PreferencesException {
        _logger.entering(_className, "setPreference");

        setPreferenceToProperties(_currentProperties, preferenceName,
                preferenceValue);
    }

    /**
     * Set a default preference value.
     * The listeners are notified only for preference changes (multiple calls with
     * the same value does NOT trigger notifications).
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final public void setDefaultPreference(Object preferenceName,
            Object preferenceValue) throws PreferencesException {
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
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final private void setPreferenceToProperties(Properties properties,
            Object preferenceName, Object preferenceValue)
            throws PreferencesException {
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
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final private void setPreferenceToProperties(Properties properties,
            Object preferenceName, int preferenceIndex, Object preferenceValue)
            throws PreferencesException {
        _logger.entering(_className, "setPreferenceToProperties");

        String currentValue = properties.getProperty(preferenceName.toString());
        if (currentValue != null && currentValue.equals(preferenceValue.toString())) {
            // nothing to do
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("Preference '" + preferenceName + "' not changed");
            }
            return;
        }

        // If the constraint is a String object
        if (preferenceValue.getClass() == java.lang.String.class) {
            properties.setProperty(preferenceName.toString(), (String) preferenceValue);
        } // Else if the constraint is a Boolean object
        else if (preferenceValue.getClass() == java.lang.Boolean.class) {
            properties.setProperty(preferenceName.toString(),
                    ((Boolean) preferenceValue).toString());
        } // Else if the constraint is an Integer object
        else if (preferenceValue.getClass() == java.lang.Integer.class) {
            properties.setProperty(preferenceName.toString(),
                    ((Integer) preferenceValue).toString());
        } // Else if the constraint is a Double object
        else if (preferenceValue.getClass() == java.lang.Double.class) {
            properties.setProperty(preferenceName.toString(),
                    ((Double) preferenceValue).toString());
        } // Else if the constraint is a Color object
        else if (preferenceValue.getClass() == java.awt.Color.class) {
            properties.setProperty(preferenceName.toString(),
                    fr.jmmc.jmcs.util.ColorEncoder.encode((Color) preferenceValue));
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
     * Set an ordered preference value.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final public void setPreference(String preferenceName, int preferenceIndex,
            Object preferenceValue) throws PreferencesException {
        setPreferenceToProperties(_currentProperties, preferenceName,
                preferenceIndex, preferenceValue);
    }

    /**
     * Set a preference default value.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    final public void setDefaultPreference(String preferenceName,
            int preferenceIndex, Object preferenceValue)
            throws PreferencesException {
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
            int preferenceIndex) {
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
            Object preferenceName, int preferenceIndex) {
        // Add property index for order if needed
        if (preferenceIndex > -1) {
            properties.setProperty(PREFERENCES_ORDER_INDEX_PREFIX + preferenceName.toString(),
                    Integer.toString(preferenceIndex));
        } else {
            properties.setProperty(PREFERENCES_ORDER_INDEX_PREFIX + preferenceName.toString(),
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
    final public int getPreferenceOrder(Object preferenceName) {
        _logger.entering(_className, "getPreferenceOrder");

        // -1 is the flag value for no order found, so it is the default value.
        int result = -1;

        // If the asked order is NOT about an internal MCS index property
        if (!preferenceName.toString().startsWith(PREFERENCES_ORDER_INDEX_PREFIX)) {
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
    final public String getPreference(String preferenceName) {
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
    final public boolean getPreferenceAsBoolean(Object preferenceName) {
        _logger.entering(_className, "getPreferenceAsBoolean");

        String value = _currentProperties.getProperty(preferenceName.toString());

        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * Get a double preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one double representing the preference value.
     */
    final public double getPreferenceAsDouble(Object preferenceName) {
        _logger.entering(_className, "getPreferenceAsDouble");

        String value = _currentProperties.getProperty(preferenceName.toString());

        return Double.valueOf(value).doubleValue();
    }

    /**
     * Get an integer preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one integer representing the preference value.
     */
    final public int getPreferenceAsInt(Object preferenceName) {
        _logger.entering(_className, "getPreferenceAsInt");

        String value = _currentProperties.getProperty(preferenceName.toString());

        return Integer.valueOf(value).intValue();
    }

    /**
     * Get a color preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one Color object representing the preference value.
     */
    final public Color getPreferenceAsColor(Object preferenceName)
            throws PreferencesException {
        _logger.entering(_className, "getPreferenceAsColor");

        String stringValue = _currentProperties.getProperty(preferenceName.toString());
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
    final public void removePreference(Object preference) {
        _logger.entering(_className, "removePreference");

        String preferenceName = preference.toString();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Removing preference '" + preferenceName + "'.");
        }

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

            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Removing preference from ordered group '" + preferencesPrefix + "'.");
            }

            // For each group preferences
            Enumeration orderedPreferences = getPreferences(preferencesPrefix);

            while (orderedPreferences.hasMoreElements()) {
                String orderedPreferenceName = (String) orderedPreferences.nextElement();

                int preferenceIndex = getPreferenceOrder(orderedPreferenceName);

                if (preferenceIndex > preferenceOrder) {
                    int destinationIndex = preferenceIndex - 1;

                    if (_logger.isLoggable(Level.FINEST)) {
                        _logger.finest("Re-ordering preference '"
                                + orderedPreferenceName + "' from index '"
                                + preferenceIndex + "' to index '" + destinationIndex
                                + "'.");
                    }

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
     * @param prefix 
     * @return Enumeration a string enumeration of preference names
     */
    final public Enumeration getPreferences(Object prefix) {
        _logger.entering(_className, "getPreferences");

        int size = 0;
        Enumeration e = _currentProperties.propertyNames();
        List shuffledProperties = new ArrayList<String>();

        // Count the number of properties for the given index and store them
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();

            if (propertyName.startsWith(prefix.toString())) {
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
     * across different execution platforms.
     *
     * @return a string containing the full file path to the preference file,
     * according to execution platform.
     */
    final private String computePreferenceFilepath() {
        _logger.entering(_className, "computePreferenceFilepath");

        // [USER_HOME]/
        _fullFilepath = SystemUtils.USER_HOME + File.separator;

        // Under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            // [USER_HOME]/Library/Preferences/
            _fullFilepath += ("Library" + File.separator + "Preferences" + File.separator);
        } // Under Windows
        else if (SystemUtils.IS_OS_WINDOWS) {
            // [USER_HOME]/Local Settings/Application Data/
            _fullFilepath += ("Local Settings" + File.separator + "Application Data" + File.separator);
        } // Under Linux, and anything else
        else {
            // [USER_HOME]/.
            _fullFilepath += ".";
        }

        // Windows : [USER_HOME]/Local Settings/Application Data/fr.jmmc...properties
        // UNIX : [USER_HOME]/.fr.jmmc...properties
        // MAC OS X : [USER_HOME]/Library/Preferences/fr.jmmc..._rev4.properties
        _fullFilepath += getPreferenceFilename();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Computed preference file path = '" + _fullFilepath + "'.");
        }

        return _fullFilepath;
    }

    /**
     * Trigger a notification of change to all registered Observers.
     */
    final public void triggerObserversNotification() {
        _logger.entering(_className, "triggerObserversNotification");

        if (isNotify()) {
            // Use EDT to ensure that Swing component(s) is updated by EDT :
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // Notify all preferences listener of maybe new values coming from file.
                    setChanged();
                    notifyObservers();
                }
            });
        } else if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("triggerObserversNotification disabled.");
        }
    }

    /**
     * Return the flag to enable/disable observer notifications
     * @return flag to enable/disable observer notifications
     */
    public final boolean isNotify() {
        return notify;
    }

    /**
     * Define the flag to enable/disable observer notifications
     * @param notify flag to enable/disable observer notifications
     */
    public final void setNotify(final boolean notify) {
        this.notify = notify;
    }

    /**
     * String representation. Print filename and preferences.
     *
     * @return the representation.
     */
    @Override
    final public String toString() {
        return "Preferences file '" + _fullFilepath + "' contains :\n"
                + _currentProperties;
    }

    public Action getSavePreferences() {
        return _savePreferences;
    }

    public Action getRestoreDefaultPreferences() {
        return _restoreDefaultPreferences;
    }

    // @todo try to move it into the mcs preferences area
    protected class SavePrefAction extends RegisteredAction {

        private static final long serialVersionUID = 1L;

        public SavePrefAction(String parentClassName) {
            super(parentClassName, "savePreferences");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                saveToFile();
            } catch (PreferencesException pe) {
                // @todo handle this error at user level
                _logger.log(Level.WARNING, "saveToFile failure : ", pe);
            }
        }
    }

    protected class RestoreDefaultPrefAction extends RegisteredAction {

        public RestoreDefaultPrefAction(String parentClassName) {
            super(parentClassName, "restorePreferences");
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            try {
                resetToDefaultPreferences();
            } catch (Exception e) {
                // @todo handle this error at user level
                _logger.log(Level.WARNING, "resetToDefaultPreferences failure : ", e);
            }
        }
    }

    /**
     * Dump all properties (sorted by keys)
     * @param properties properties to dump
     * @return string representation of properties using the format "{name} : {value}"
     */
    public static String dumpProperties(final Properties properties) {
        if (properties == null) {
            return "";
        }

        // Sort properties
        final String[] keys = new String[properties.size()];
        properties.keySet().toArray(keys);
        Arrays.sort(keys);

        // For each property, we make a string like "{name} : {value}"
        final StringBuilder sb = new StringBuilder(2048);
        for (String key : keys) {
            sb.append(key).append(" : ").append(properties.getProperty(key)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Dump current properties (for debugging purposes)
     * @return string representation of properties using the format "{name} : {value}"
     */
    public String dumpCurrentProperties() {
        return dumpProperties(_currentProperties);
    }
}
