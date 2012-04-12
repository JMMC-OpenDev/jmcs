/**
 * *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 * ****************************************************************************
 */
package fr.jmmc.jmcs.data.preference;

import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.BadLocationException;

/**
 * Associate one string to a preference entry. This class should be associated
 * to Text widgets that change a string preference. After setModel call, the
 * preference will be automatically changed according user events and UI will be
 * automatically updated according preference change. Moreover actions should be
 * associated to implement application behaviour associated to user events.
 *
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class PreferencedDocument extends javax.swing.text.PlainDocument
        implements Observer, DocumentListener {

    /**
     * default serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1;
    /**
     * Class logger
     */
    private final static Logger _logger = LoggerFactory.getLogger(PreferencedDocument.class.getName());
    /**
     * Store PreferencedDocument instances for a given preference name
     */
    private static Map<String, PreferencedDocument> _instanceMap = Collections.synchronizedMap(new HashMap<String, PreferencedDocument>(8));
    /*
     * members
     */
    /**
     * Shared instance
     */
    private final Preferences _preferences;
    /**
     * Preference property
     */
    private final String _preferenceProperty;
    /**
     * Tells if preference must be saved automatically or not (default) Caution:
     * the whole preference list associated in the preference will also be saved
     * ...
     */
    private final boolean _autosave;
    
    /**
     * PreferencedButtonModel constructor
     *
     * @param preferences the preference that lists every entries
     * @param preferenceProperty the preference name
     * @param autosave Tells if preference must be saved automatically or not
     * (default)
     */
    private PreferencedDocument(final Preferences preferences,
            final String preferenceProperty, final boolean autosave) {

        // Store the Preference shared instance of the main application
        _preferences = preferences;

        // Store the property name for later use
        _preferenceProperty = preferenceProperty;

        // store beavior flag
        _autosave = autosave;

        // Retrieve the property value and set the widget accordinaly
        setMyText(_preferences.getPreference(_preferenceProperty));

        // Register the object as its handler of any modification of its widget        
        addDocumentListener(this);

        // Register the object as the observer of any property value change
        _preferences.addObserver(this);
    }

    /**
     * Return one shared instance associated to the preference property name.
     *
     * @param preferences the preference that lists every entries
     * @param preferenceProperty the preference name
     * @param autosave Tells if preference must be saved automatically or not
     * (default)
     *
     * @return the PreferencedDocument singleton
     */
    public static PreferencedDocument getInstance(final Preferences preferences,
            final String preferenceProperty, final boolean autosave) {

        PreferencedDocument d = _instanceMap.get(preferenceProperty);

        if (d == null) {
            d = new PreferencedDocument(preferences, preferenceProperty, autosave);
            _instanceMap.put(preferenceProperty, d);
        }

        return d;
    }

    /**
     * Return one shared instance associated to the preference property name
     * which preference is not saved automatically (autosave = false)
     *
     * @param preferences the preference that lists every entries
     * @param preferenceProperty the preference name
     * @return the PreferencedDocument singleton
     */
    public static PreferencedDocument getInstance(final Preferences preferences,
            final String preferenceProperty) {
        return getInstance(preferences, preferenceProperty, false);
    }

    /**
     * Get the widget content.
     *
     * @return the widget content.
     */
    public String getMyText() {
        String content = "Error";
        try {
            content = this.getText(0, getLength());
        } catch (BadLocationException ex) {
            throw new IllegalStateException("Can't read data for preference " + _preferenceProperty, ex);
        }

        return content;
    }

    /**
     * Change the value of the widget.
     *
     * @param newValue new value to be written into the widget.
     */
    public void setMyText(final String newValue) {
        _logger.debug("setting new content to: {}", newValue);
        try {
            replace(0, getLength(), newValue, null);
        } catch (BadLocationException ex) {
            throw new IllegalStateException("Can't set value with preference " + _preferenceProperty);
        }
    }

    /**
     * Set new preference value.
     *
     * @param newValue new string value.
     */
    private void setPrefValue(final String newValue) {
            try {
                _preferences.setPreference(_preferenceProperty, newValue);
                if (_autosave) {
                    _preferences.saveToFile();
                }
            } catch (PreferencesException ex) {
                throw new IllegalStateException("Can't set value for preference " + _preferenceProperty);
            }        
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    @Override
    public void changedUpdate(final DocumentEvent evt) {
        // this event is not used
        _logger.trace("changeUpdate:\n event: {}\n text: {}", evt, getMyText());
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    @Override
    public void insertUpdate(final DocumentEvent evt) {
        // Gives notification that there was an insert into the document.        
        _logger.trace("insertUpdate:\n event: {}\n text: {}", evt, getMyText());      
        setPrefValue(getMyText());
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    @Override
    public void removeUpdate(final DocumentEvent evt) {
        // Gives notification that a portion of the document has been removed.        
        _logger.trace("removeUpdate:\n event: {}\n text: {}", evt, getMyText());
        setPrefValue(getMyText());
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(final Observable o, final Object arg) {  
        String currentValue = getMyText();
        // Update the widget view according property value changed if value changed
        final String nextValue = _preferences.getPreference(_preferenceProperty);
        if (currentValue.equals(nextValue)){
            return;
        }
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("Setting '{}' from {} to {}", new Object[]{_preferenceProperty, currentValue, nextValue});
        }        
        setMyText(nextValue);
    }
}
/*
 * ___oOo___
 */
