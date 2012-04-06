/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @TODO handle close button correctly
// @TODO add deleteObserver(this) to dispose() to dereference each subview properly
/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends JFrame implements ActionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(PreferencesView.class.getName());
    /** Data model */
    Preferences _preferences = null;
    /** Preferences... action */
    public final ShowPreferencesAction _showPreferencesAction;
    /** "Restore to Default Settings" button */
    protected JButton _restoreDefaultButton = null;
    /** "Save Modifications" button */
    protected JButton _saveModificationButton = null;

    /**
     * Constructor.
     * @param preferences your application Preferences instance.
     * @param panels a map of tab title (string) -> panel (JPanel).
     */
    public PreferencesView(Preferences preferences, LinkedHashMap<String, JPanel> panels) {

        super("Preferences");

        // Check arguments validity
        if ((preferences == null) || (panels == null) || (panels.isEmpty())) {
            throw new IllegalArgumentException();
        }

        // Window size
        setSize(600, 400);
        setResizable(false);

        // Get and listen to data model modifications
        _preferences = preferences;

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Skip tabbed pane if only one panel to display
        if (panels.size() == 1) {
            final JPanel firstPanel = panels.values().iterator().next();
            contentPane.add(firstPanel);
        } else {
            // Build the tabbed pane
            JTabbedPane tabbedPane = new JTabbedPane();
            contentPane.add(tabbedPane);

            // Add each preferences pane
            for (Map.Entry<String, JPanel> entry : panels.entrySet()) {

                final String panelName = entry.getKey();
                final JPanel panel = entry.getValue();

                tabbedPane.add(panelName, panel);

                _logger.debug("Added '{}' panel to PreferenceView tabbed pane.", panelName);
            }
        }

        // Add the restore and sace buttons
        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore Default Settings");
        buttonsPanel.add(_restoreDefaultButton);
        _saveModificationButton = new JButton("Save Modifications");
        buttonsPanel.add(_saveModificationButton);
        contentPane.add(buttonsPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();

        _showPreferencesAction = new ShowPreferencesAction(getClass().getName(), "_showPreferencesAction");
    }

    public void init() {

        WindowUtils.centerOnMainScreen(this);
        WindowUtils.setClosingKeyboardShortcuts(rootPane, this);

        _restoreDefaultButton.addActionListener(this);
        _saveModificationButton.addActionListener(this);
    }

    /**
     * actionPerformed  -  Listener
     *
     * @param evt ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        _logger.trace("PreferencesView.actionPerformed");

        // If the "Restore to default settings" button has been pressed
        if (evt.getSource().equals(_restoreDefaultButton)) {
            _preferences.resetToDefaultPreferences();
        }

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton)) {
            try {
                _preferences.saveToFile();
            } catch (PreferencesException pe) {
                _logger.warn("Could not save preferences.", pe);
            }
        }
    }

    /**
     * Called to show the preferences window.
     */
    protected class ShowPreferencesAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        ShowPreferencesAction(String classPath, String fieldName) {
            super(classPath, fieldName);
            flagAsPreferenceAction();
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.trace("ShowPreferencesAction.actionPerformed");

            // Show the Preferences window
            setVisible(true);
        }
    }
}
