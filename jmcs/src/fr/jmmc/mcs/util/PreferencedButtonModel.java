/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencedButtonModel.java,v 1.9 2010-09-30 13:28:02 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2010/09/23 19:38:16  bourgesl
 * comments when calling FeedBackReport
 *
 * Revision 1.7  2009/08/28 09:01:44  lafrasse
 * Jalopization.
 *
 * Revision 1.6  2009/07/16 09:14:37  mella
 * Add proper logger
 * Handle exception triggering a bugreport dialog
 *
 * Revision 1.5  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
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

import fr.jmmc.mcs.gui.FeedbackReport;
import java.awt.event.*;

import java.util.*;

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

    /** Class name */
    private final static String _className = "fr.jmmc.mcs.util.PreferencedButtonModel";

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
                _logger.fine(
                    "This event is due to a preference update and does nothing");

                return;
            }
        }

        _logger.fine("This event is due to a user interaction");
        _logger.fine("Setting preference '" + _preferenceProperty + "' to " +
            nextValue);

        try
        {
            _preferences.setPreference(_preferenceProperty, nextValue);
        }
        catch (Exception e)
        {
            // Show the feedback report (modal) :
            new FeedbackReport(true, e);
        }
    }

    /**
     * Triggerd if the preference shared instance has been modified.
     */
    public void update(Observable o, Object arg)
    {
        // Notify event Listener (telling this that it is an internal update)
        _logger.fine("Fire action listeners ");

        fireActionPerformed(new ActionEvent(this, SELECTED, "internalUpdate"));

        // Update the widget view according property value changed
        boolean nextValue = _preferences.getPreferenceAsBoolean(_preferenceProperty);
        _logger.fine("Setting selected to " + nextValue);
        setSelected(nextValue);
    }
}
/*___oOo___*/
