/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.gui.FeedbackReport;
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
    implements Observer, DocumentListener
{
    /** Store PreferencedButtonModel instances for a given preference name */
    protected static Hashtable _instancesHashtable = new Hashtable();

    /** Class name */
    private final static String _className = "fr.jmmc.mcs.util.PreferencedDocument";

    /** Class logger */
    private final static java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(_className);

    /** Preference property */
    private String _preferenceProperty;

    /** Shared instance */
    private Preferences _preferences;

    /** 
     * Tells if preference must be saved automatically or not (default)
     * Caution: the whole preference list associated in the preference 
     * will also be saved ...
     */
     private boolean _autosave=false;
     
    /**
     * PreferencedButtonModel constructor
     *
     * title a string containing the label to be displayed in the menu
     * preferenceProperty a string containing the reference to the boolean property to handle
     */
    protected PreferencedDocument(Preferences preferences,
        String preferenceProperty, boolean autosave)
    {
        // Store the Preference shared instance of the main application
        _preferences            = preferences;

        // Store the property name for later use
        _preferenceProperty     = preferenceProperty;
        // Retrieve the property value and set the widget accordinaly
        setMyText(_preferences.getPreference(_preferenceProperty));

        // Register the object as its handler of any modification of its widget        
        addDocumentListener(this);

        // Register the object as the observer of any property value change
        _preferences.addObserver(this);

        // store beavior flag
        _autosave=autosave;

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
        String preferenceProperty, boolean autosave)
    {
        PreferencedDocument d;

        if (_instancesHashtable.containsKey(preferenceProperty))
        {
            d = (PreferencedDocument) _instancesHashtable.get(preferenceProperty);
        }
        else
        {
            d = new PreferencedDocument(preferences, preferenceProperty, autosave);
            _instancesHashtable.put(preferenceProperty, d);
        }

        return d;
    }

    public static PreferencedDocument getInstance(Preferences preferences,
        String preferenceProperty)
    {
        return getInstance(preferences,preferenceProperty,false);
    }
    
    /**
     * Get the widget content.
     *
     * @return the widget content.
     */
    public String getMyText()
    {
        String content = "Error";

        try
        {
            content = this.getText(0, getLength());
        }
        catch (Exception e)
        {
          // Show the feedback report (modal) :
          new FeedbackReport(true, e);
        }

        return content;
    }

    /**
     * Change the value of the widget.
     *
     * @param newValue new value to be written into the widget.
     */
    public void setMyText(String newValue)
    {
        _logger.fine("setting new content to " + newValue);

        try
        {
            replace(0, getLength(), newValue, null);
        }
        catch (IllegalStateException e)
        {
            // @todo do nothing except change some code???
        }
        catch (Exception e)
        {
          // Show the feedback report (modal) :
          new FeedbackReport(true, e);
        }
    }

    /**
     * Sett new preference value.
     *
     * @param newValue new string value.
     */
    private void setPrefValue(String newValue)
    {
        try
        {
            _preferences.setPreference(_preferenceProperty, newValue);
            if(_autosave){
                _preferences.saveToFile();
            }            
        }
        catch (Exception e)
        {
          // Show the feedback report (modal) :
          new FeedbackReport(true, e);
        }
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    public void changedUpdate(DocumentEvent evt)
    {
        // this event is not used
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    public void insertUpdate(DocumentEvent evt)
    {
        // Gives notification that there was an insert into the document.        
        _logger.finest("insertUpdate event:" + getMyText());
        setPrefValue(getMyText());
    }

    /**
     * Handle event.
     *
     * @param evt document event.
     */
    public void removeUpdate(DocumentEvent evt)
    {
        // Gives notification that a portion of the document has been removed.        
        _logger.finest("removeUpdate event:" + getMyText());
        setPrefValue(getMyText());
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     */
    public void update(Observable o, Object arg)
    {
        // Notify event Listener (telling this that it is an internal update)
        _logger.fine("Fire action listeners ");

        // Update the widget view according property value changed
        String nextValue = _preferences.getPreference(_preferenceProperty);
        _logger.fine("Setting " + _preferenceProperty + " to " + nextValue);
        setMyText(nextValue);
    }
}
/*___oOo___*/
