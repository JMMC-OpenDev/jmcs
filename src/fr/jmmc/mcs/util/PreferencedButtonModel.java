/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencedButtonModel.java,v 1.1 2006-07-07 09:16:23 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package jmmc.mcs.util;

import jmmc.mcs.log.MCSLogger;

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
    /** Menu item corresponding preference property */
    private String _preferenceProperty;

    /** Shared instance */
    private Preferences _preferences;

    /** Shared logger */
    private Logger _logger = MCSLogger.getLogger();

    /**
     * PreferencedButtonModel constructor
     *
     * title a string containing the label to be displayed in the menu
     * preferenceProperty a string containing the reference to the boolean property to handle
     */
    public PreferencedButtonModel(Preferences preferences,
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
     * Triggerd if the button has been clicked.
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If the widget changed, update the property value

        // Because actionPerformed is called before selectin flag change :
        // invert isSelected returned value
        boolean nextValue = ! isSelected();
        _logger.fine("Setting preference '" + _preferenceProperty + "' to " +
            nextValue);
        _preferences.setPreference(_preferenceProperty, nextValue);
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     */
    public void update(Observable o, Object arg)
    {
        // Update the widget status if the property value changed
        setSelected(_preferences.getPreferenceAsBoolean(_preferenceProperty));
    }
}
/*___oOo___*/
