/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.awt.event.*;

import java.util.*;

import javax.swing.JCheckBoxMenuItem;


/**
 * Menu item with a check box representing a MCS preference boolean property
 * state.
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA.
 */
public class PreferencedCheckBoxMenuItem extends JCheckBoxMenuItem
    implements Observer, ActionListener
{
    /** Menu item corresponding preference property */
    private String _preferenceProperty;

    /** Shared instance */
    private Preferences _preferences;

    /**
     * PreferencedCheckBoxMenuItem constructor
     *
     * title a string containing the label to be displayed in the menu
     * preferenceProperty a string containing the reference to the boolean property to handle
     */
    public PreferencedCheckBoxMenuItem(String title, Preferences preferences,
        String preferenceProperty)
    {
        // Set the label of the Menu Item widget
        super(title);

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
     * Triggerd if the menu item has been clicked.
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If the widget changed, update the property value
        try
        {
            _preferences.setPreference(_preferenceProperty, isSelected());
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
        // Update the widget status if the property value changed
        setSelected(_preferences.getPreferenceAsBoolean(_preferenceProperty));
    }
}
/*___oOo___*/
