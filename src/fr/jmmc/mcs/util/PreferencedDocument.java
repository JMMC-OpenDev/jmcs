/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencedDocument.java,v 1.1 2007-02-14 10:14:38 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.awt.event.*;

import java.util.*;

import javax.swing.event.*;


/**
 * Associate one string to a preference entry.
 * This class should be associated to Text widgets that change
 * a string preference. After setModel call, the preference will be
 * automatically changed according user events and UI will be automatically
 * updated according preference change. Moreover actions should be associated to
 * implement application behaviour associated to user events.
 */
public class PreferencedDocument extends javax.swing.text.PlainDocument
    implements Observer, DocumentListener {
    /** Store PreferencedButtonModel instances for a given preference name */
    protected static Hashtable _instancesHashtable = new Hashtable();

    /** Preference property */
    private String _preferenceProperty;

    /** Shared instance */
    private Preferences _preferences;
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mcs.util.PreferencedDocument");

    /**
     * PreferencedButtonModel constructor
     *
     * title a string containing the label to be displayed in the menu
     * preferenceProperty a string containing the reference to the boolean property to handle
     */
    protected PreferencedDocument(Preferences preferences,
        String preferenceProperty) {
        // Store the Preference shared instance of the main application
        _preferences = preferences;

        // Store the property name for later use
        _preferenceProperty = preferenceProperty;
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
     * @param preferences the preference that list every entries
     * @param preferenceProperty the preference name
     *
     * @return the PreferencedDocument singleton 
     */
    public static PreferencedDocument getInstance(Preferences preferences,
        String preferenceProperty) {
        PreferencedDocument d;

        if (_instancesHashtable.containsKey(preferenceProperty)) {
            d = (PreferencedDocument) _instancesHashtable.get(preferenceProperty);
        } else {
            d = new PreferencedDocument(preferences, preferenceProperty);
            _instancesHashtable.put(preferenceProperty, d);
        }

        return d;
    }

    public String getMyText() {
        String content = "Error";

        try {
            content = this.getText(0, getLength());
        } catch (Exception e) {
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }

        return content;
    }

    public void setMyText(String newValue) {
        logger.fine("setting new content to " + newValue);

        try {
            replace(0, getLength(), newValue, null);
        } catch (IllegalStateException e) {
            // @todo do nothing except change some code???
        } catch (Exception e) {
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }

    public void setPrefValue(String newValue) {
        try {
            _preferences.setPreference(_preferenceProperty, newValue);
        } catch (Exception e) {
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }

    /**
     * Triggerd if the text has been changed.
     */
    public void changedUpdate(DocumentEvent evt) {
        // Gives notification that an attribute or set of attributes changed.                
        logger.finest("changedUpdate event :");

        // If the widget changed is not due to user action,
        // return not to enter into an infinite loop
        /*if (evt.)
        {
            if (evt.getActionCommand().equals("internalUpdate"))
            {
                logger.info("This event is due to a preference update");
                return;
            }
        }
        //*/

        //setMyText(getMyText());        
    }

    public void insertUpdate(DocumentEvent evt) {
        // Gives notification that there was an insert into the document.        
        logger.finest("insertUpdate event:" + getMyText());
        setPrefValue(getMyText());
    }

    public void removeUpdate(DocumentEvent evt) {
        // Gives notification that a portion of the document has been removed.        
        logger.finest("removeUpdate event:" + getMyText());
        setPrefValue(getMyText());
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     */
    public void update(Observable o, Object arg) {
        // Notify event Listener (telling this that it is an internal update)
        logger.fine("Fire action listeners ");

        // fireActionPerformed(new ActionEvent(this, SELECTED, "internalUpdate"));

        // Update the widget view according property value changed
        String nextValue = _preferences.getPreference(_preferenceProperty);
        logger.fine("Setting " + _preferenceProperty + " to " + nextValue);
        setMyText(nextValue);

        //setSelected(nextValue);
    }
}
/*___oOo___*/
