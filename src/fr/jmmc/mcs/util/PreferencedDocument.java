/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencedDocument.java,v 1.6 2010-09-30 13:28:02 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2010/09/23 19:38:16  bourgesl
 * comments when calling FeedBackReport
 *
 * Revision 1.4  2009/08/28 09:01:44  lafrasse
 * Jalopization.
 *
 * Revision 1.3  2009/07/16 09:34:59  mella
 * Improve documentation and clean code
 *
 * Revision 1.2  2007/06/21 07:38:51  lafrasse
 * Jalopization.
 *
 * Revision 1.1  2007/02/14 10:14:38  mella
 * First revision
 *
 *
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
     * PreferencedButtonModel constructor
     *
     * title a string containing the label to be displayed in the menu
     * preferenceProperty a string containing the reference to the boolean property to handle
     */
    protected PreferencedDocument(Preferences preferences,
        String preferenceProperty)
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
        String preferenceProperty)
    {
        PreferencedDocument d;

        if (_instancesHashtable.containsKey(preferenceProperty))
        {
            d = (PreferencedDocument) _instancesHashtable.get(preferenceProperty);
        }
        else
        {
            d = new PreferencedDocument(preferences, preferenceProperty);
            _instancesHashtable.put(preferenceProperty, d);
        }

        return d;
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
