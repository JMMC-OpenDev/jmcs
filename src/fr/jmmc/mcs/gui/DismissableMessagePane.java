/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DismissableMessagePane.java,v 1.2 2010-09-23 19:38:16 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/09/01 14:43:51  mella
 * First revision
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Preferences;
import fr.jmmc.mcs.util.PreferencesException;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 * Provides display methods.
 *
 */
public class DismissableMessagePane {

  /**
   * Forbidden constructor
   */
  private DismissableMessagePane() {
    super();
  }


    /**
     * Show a message dialog until the user choose to hide this kind
     * of message permanently.
     *
     * @param parentComponent Parent component or null
     * @param message Message to display
     * @param preferences Reference to the dedicated Preferences singleton
     * @param preferenceName Name of the preference related to this message
     */
    public static void show(Component parentComponent, String message,
            Preferences preferences, String preferenceName) {
        String dontShowPreferenceName = "MCSGUI.DismissableMessagePane." +
                preferenceName + ".dontShow";
        try {
            boolean dontShow = preferences.getPreferenceAsBoolean(dontShowPreferenceName);

            if (dontShow == false) {
                JCheckBox checkbox = new JCheckBox("Do not show this message again.");
                Object[] params = {message, checkbox};
                JOptionPane.showMessageDialog(parentComponent, params);
                dontShow = checkbox.isSelected();

                if (dontShow) {
                    preferences.setPreference(dontShowPreferenceName, dontShow);
                }
            }
        } catch (PreferencesException ex) {
            // Show feedback report (not modal and do exit on close) :
            new FeedbackReport(ex);
        }

    }
}
