/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RegisteredPreferencedBooleanAction.java,v 1.1 2008-09-18 20:59:13 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;


/**
 * RegisteredAction class customized to be bound to a preferenced boolean.
 */
public class RegisteredPreferencedBooleanAction extends RegisteredAction
    implements Observer
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.util.RegisteredPreferencedBooleanAction");

    /** Monitored Preference object */
    private Preferences _preferences;

    /** Name of the bound preference */
    private String _preferenceName;

    /** List of buttons bound to this action */
    private Vector<AbstractButton> _boundButtons;

    /**
     * Constructor.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param actionName the name of the action.
     * @param preferences the Preferences object to monitor.
     * @param preferenceName the preference name of the value to get/set.
     */
    public RegisteredPreferencedBooleanAction(String classPath,
        String fieldName, String actionName, Preferences preferences,
        String preferenceName)
    {
        super(classPath, fieldName, actionName);

        _boundButtons       = new Vector<AbstractButton>();

        _preferenceName     = preferenceName;

        // Store the application preferences and register against it
        _preferences        = preferences;
        _preferences.addObserver(this);
    }

    /**
     * Register the given button as one to update when observed preferences change.
     *
     * @param button the button to register.
     */
    public void addBoundButton(AbstractButton button)
    {
        _logger.entering("RegisteredPreferencedBooleanAction",
            "rememberBoundButton");

        _boundButtons.add(button);
    }

    /**
     * Automatically called whenever the observed Preferences object changed.
     */
    public void update(Observable o, Object arg)
    {
        _logger.entering("RegisteredPreferencedBooleanAction", "update");

        boolean state = _preferences.getPreferenceAsBoolean(_preferenceName);

        for (AbstractButton button : _boundButtons)
        {
            button.setSelected(state);
        }
    }

    /**
     * Automatically called whenever any bound button is clicked.
     */
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        _logger.entering("RegisteredPreferencedBooleanAction", "actionPerformed");

        if (e.getSource() instanceof AbstractButton)
        {
            AbstractButton button     = (AbstractButton) e.getSource();
            boolean        isSelected = button.isSelected();

            try
            {
                _preferences.setPreference(_preferenceName, isSelected);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Cannot set preference '" + _preferenceName + "' to '" +
                    isSelected + "'.", ex);
            }
        }
    }
}
/*___oOo___*/
