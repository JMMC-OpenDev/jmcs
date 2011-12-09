/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import fr.jmmc.jmcs.data.preference.Preferences;
import java.awt.event.*;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * RegisteredAction class customized to be bound to a preferenced boolean.
 * 
 * @author Sylvain LAFRASSE.
 */
public class RegisteredPreferencedBooleanAction extends RegisteredAction
        implements Observer, ItemListener {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(RegisteredPreferencedBooleanAction.class.getName());
    /** default serial UID for Serializable interface */
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
            Object preferenceName) {
        super(classPath, fieldName, actionName);

        _boundButtons = new Vector<AbstractButton>();

        _preferenceName = preferenceName.toString();

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
        boolean state = _preferences.getPreferenceAsBoolean(_preferenceName);

        if (_logger.isTraceEnabled()) {
            _logger.trace(_preferenceName + " value changed to become '" + state + "'.");
        }

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
        if (e.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) e.getSource();
            boolean isSelected = button.isSelected();

            if (_logger.isTraceEnabled()) {
                _logger.trace(_preferenceName + " value was updated with new external state of '" + isSelected + "'.");
            }
            try {

                _preferences.setPreference(_preferenceName, isSelected);
            } catch (Exception ex) {
                _logger.warn("Cannot set preference '" + _preferenceName + "' to '" + isSelected + "'.", ex);
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
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);

        if (_logger.isTraceEnabled()) {
            _logger.trace(_preferenceName + " value was updated with new internal state of '" + isSelected + "'.");
        }
        try {

            _preferences.setPreference(_preferenceName, isSelected);
        } catch (Exception ex) {
            _logger.warn("Cannot set preference '" + _preferenceName + "' to '" + isSelected + "'.", ex);
        }
    }
}
/*___oOo___*/
