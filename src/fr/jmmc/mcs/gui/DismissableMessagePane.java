/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DismissableMessagePane.java,v 1.5 2011-03-08 12:53:45 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2010/09/30 13:28:57  bourgesl
 * comments
 *
 * Revision 1.3  2010/09/24 15:45:38  bourgesl
 * format
 *
 * Revision 1.2  2010/09/23 19:38:16  bourgesl
 * comments when calling FeedBackReport
 *
 * Revision 1.1  2010/09/01 14:43:51  mella
 * First revision
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Preferences;
import fr.jmmc.mcs.util.PreferencesException;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 * Provides a custom message pane with a check box to hide that kind of message definitely.
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
