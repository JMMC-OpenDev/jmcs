/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Preferences;
import fr.jmmc.mcs.util.PreferencesException;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 * Provides a custom message pane with a check box to hide that kind of message definitely.
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public final class DismissableMessagePane {

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
   * @param message Message to display
   * @param preferences Reference to the dedicated Preferences singleton
   * @param preferenceName Name of the preference related to this message
   */
  public final static void show(final String message,
                                final Preferences preferences, final String preferenceName) {

    final String dontShowPreferenceName = "MCSGUI.DismissableMessagePane."
            + preferenceName + ".dontShow";
    try {
      boolean dontShow = preferences.getPreferenceAsBoolean(dontShowPreferenceName);

      if (!dontShow) {
        final JCheckBox checkbox = new JCheckBox("Do not show this message again.");
        final Object[] params = {message, checkbox};

        JOptionPane.showMessageDialog(App.getFrame(), params);

        dontShow = checkbox.isSelected();

        if (dontShow) {
          preferences.setPreference(dontShowPreferenceName, dontShow);
        }
      }
    } catch (PreferencesException pe) {
      // Show the feedback report :
      new FeedbackReport(pe);
    }
  }
}
