/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencedButtonModel.java,v 1.5 2007-02-13 13:48:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/10/16 14:29:49  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.3  2006/09/28 15:23:20  lafrasse
 * Updated to handle jmmc.util.Preferences API modifications.
 *
 * Revision 1.2  2006/07/28 08:41:20  mella
 * factory one shared model per preference
 *
 * Revision 1.1  2006/07/07 09:16:23  mella
 * First revision
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.log.MCSLogger;

import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.DefaultButtonModel;


/**
 * Menu item with a check box representing a MCS preference boolean property
 * state. This class should be associated to AbstractButton widgets that change
 * a boolean preference. After setModel call, the preference will be
 * automatically changed according user events and UI will be automatically
 * updated according preference change. Moreover actions should be associated to
 * implement application behaviour associated to user events.
 */
public class PreferencedButtonModel extends DefaultButtonModel
    implements Observer, ActionListener
{
    /** Store PreferencedButtonModel instances for a given preference name */
    protected static Hashtable _instancesHashtable = new Hashtable();

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
    protected PreferencedButtonModel(Preferences preferences,
        String preferenceProperty)
    {
        // Store the Preference shared instance of the main application
        _preferences            = preferences;

        // Store the property name for later use
        _preferenceProperty     = preferenceProperty;
        // Retrieve the property boolean value and set the widget accordinaly
        setSelected(_preferences.getPreferenceAsBoolean(_preferenceProperty));

        // Register the object as its handler of any modification of its widget
        addActionListener(this);
        // Register the object as the observer of any property value change
        _preferences.addObserver(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param preferences DOCUMENT ME!
     * @param preferenceProperty DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static PreferencedButtonModel getInstance(Preferences preferences,
        String preferenceProperty)
    {
        PreferencedButtonModel bm;

        if (_instancesHashtable.containsKey(preferenceProperty))
        {
            bm = (PreferencedButtonModel) _instancesHashtable.get(preferenceProperty);
        }
        else
        {
            bm = new PreferencedButtonModel(preferences, preferenceProperty);
            _instancesHashtable.put(preferenceProperty, bm);
        }

        return bm;
    }

    /**
     * Triggerd if the button has been clicked.
     */
    public void actionPerformed(ActionEvent evt)
    {
        // Because actionPerformed is called before selected flag change :
        // invert isSelected returned value
        boolean nextValue = ! isSelected();

        // If the widget changed due to user action,
        // update the property value
        if (evt.getActionCommand() != null)
        {
            if (evt.getActionCommand().equals("internalUpdate"))
            {
                MCSLogger.info("This event is due to a preference update");

                return;

                /*MCSLogger.info("Setting preference '" + _preferenceProperty + "' to " +
                   nextValue);
                   _requireSetSelected=false;
                   _preferences.setPreference(_preferenceProperty, nextValue);
                   MCSLogger.info("This is a internal update");
                   _requireSetSelected=true;
                 */
            }
        }

        MCSLogger.info("This event is due to a user interaction");
        MCSLogger.info("Setting preference '" + _preferenceProperty + "' to " +
            nextValue);

        try
        {
            _preferences.setPreference(_preferenceProperty, nextValue);
        }
        catch (Exception e)
        {
            // @TODO
        }
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     */
    public void update(Observable o, Object arg)
    {
        // Notify event Listener (telling this that it is an internal update)
        MCSLogger.info("Fire action listeners ");

        fireActionPerformed(new ActionEvent(this, SELECTED, "internalUpdate"));

        // Update the widget view according property value changed
        boolean nextValue = _preferences.getPreferenceAsBoolean(_preferenceProperty);
        MCSLogger.info("Setting selected to " + nextValue);
        setSelected(nextValue);
    }
}
/*___oOo___*/
