/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import fr.jmmc.jmcs.data.preference.Preferences;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;

/**
 * RegisteredAction class customized to be bound to a preferenced boolean.
 * 
 * @author Sylvain LAFRASSE.
 */
public class RegisteredPreferencedBooleanAction extends RegisteredAction
        implements Observer, ItemListener {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(RegisteredPreferencedBooleanAction.class.getName());
    private static final long serialVersionUID = 1L;
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
            String preferenceName) {
        super(classPath, fieldName, actionName);

        _boundButtons = new Vector<AbstractButton>();

        _preferenceName = preferenceName;

        // Store the application preferences and register against it
        _preferences = preferences;
        _preferences.addObserver(this);
    }

    /**
     * Register the given button as one to update when observed preferences change.
     *
     * @param button the button to register.
     */
    public void addBoundButton(AbstractButton button) {
        _logger.entering("RegisteredPreferencedBooleanAction",
                "rememberBoundButton");

        _boundButtons.add(button);
        button.addItemListener(this);
    }

    /**
     * Automatically called whenever the observed Preferences object changed.
     * @param o
     * @param arg  
     */
    @Override
    public void update(Observable o, Object arg) {
        _logger.entering("RegisteredPreferencedBooleanAction", "update");

        boolean state = _preferences.getPreferenceAsBoolean(_preferenceName);

        _logger.finest(_preferenceName + " value changed to become '" + state
                + "'.");

        for (AbstractButton button : _boundButtons) {
            button.setSelected(state);
        }
    }

    /**
     * Automatically called whenever any bound button is clicked.
     * @param e 
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        _logger.entering("RegisteredPreferencedBooleanAction", "actionPerformed");

        if (e.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) e.getSource();
            boolean isSelected = button.isSelected();

            try {
                _logger.finest(_preferenceName
                        + " value was updated with new external state of '"
                        + isSelected + "'.");

                _preferences.setPreference(_preferenceName, isSelected);
            } catch (Exception ex) {
                _logger.log(Level.WARNING,
                        "Cannot set preference '" + _preferenceName + "' to '"
                        + isSelected + "'.", ex);
            }
        }
    }

    /**
     * Automatically called whenever any bound button state change.
     *
     * Added as it is the only reliable way to deal with ButtonGroup and JRadioButton.
     * ButtonGroups don't handle "UNSELECT" ActionEvent, whereas ItemEvent do !!!
     * @param e 
     * @sa http://forums.sun.com/thread.jspa?forumID=257&threadID=173201
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        _logger.entering("RegisteredPreferencedBooleanAction",
                "itemStateChanged");

        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);

        try {
            _logger.finest(_preferenceName
                    + " value was updated with new internal state of '" + isSelected
                    + "'.");

            _preferences.setPreference(_preferenceName, isSelected);
        } catch (Exception ex) {
            _logger.log(Level.WARNING,
                    "Cannot set preference '" + _preferenceName + "' to '"
                    + isSelected + "'.", ex);
        }
    }
}
/*___oOo___*/
