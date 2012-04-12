/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic panel to display check boxes associated with boolean preferences.
 * @author Sylvain LAFRASSE
 */
public class BooleanPreferencesView extends JPanel implements Observer, ChangeListener {

    /** Logger - get from given class name */
    private static final Logger _logger = LoggerFactory.getLogger(BooleanPreferencesView.class.getName());
    private final Preferences _preferences;
    private final Map<Object, JCheckBox> _booleanPreferencesHashMap;
    private boolean _programaticUpdateUnderway = false;

    /**
     * Constructor.
     * @param preferences the PReferences instance to work on.
     * @param booleanPreferencesHashMap the ordered map linking preference key to its check box label.
     */
    public BooleanPreferencesView(Preferences preferences, LinkedHashMap<Object, String> booleanPreferencesHashMap) {

        super();

        if ((preferences == null) || (booleanPreferencesHashMap == null) || (booleanPreferencesHashMap.size() < 1)) {
            throw new IllegalArgumentException();
        }

        _preferences = preferences;

        _booleanPreferencesHashMap = new LinkedHashMap<Object, JCheckBox>();
        for (Map.Entry<Object, String> entry : booleanPreferencesHashMap.entrySet()) {
            final Object preferenceKey = entry.getKey();
            final String checkBoxLabel = entry.getValue();
            _booleanPreferencesHashMap.put(preferenceKey, new JCheckBox(checkBoxLabel));
        }
    }

    /** MANDATORY call after construction. */
    public void init() {

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);

        // Layout management
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;

        // Initialize all checkboxes
        for (JCheckBox checkBox : _booleanPreferencesHashMap.values()) {
            topPanel.add(checkBox, gridBagConstraints);
            checkBox.addChangeListener(this);
            gridBagConstraints.gridy++;
        }
        add(topPanel);

        update(null, null);
    }

    @Override
    public void update(Observable observable, Object parameter) {

        _programaticUpdateUnderway = true;

        for (Map.Entry<Object, JCheckBox> entry : _booleanPreferencesHashMap.entrySet()) {

            final JCheckBox currentCheckBox = entry.getValue();
            final String currentCheckBoxName = currentCheckBox.getText();
            final boolean currentCheckBoxState = currentCheckBox.isSelected();
            final Object currentPreferenceKey = entry.getKey();
            final boolean currentPreferenceState = _preferences.getPreferenceAsBoolean(currentPreferenceKey);

            _logger.debug("Set checkbox '" + currentCheckBoxName + "' to '" + currentPreferenceState + "' (was '" + currentCheckBoxState + "').");
            currentCheckBox.setSelected(currentPreferenceState);
        }

        _programaticUpdateUnderway = false;
    }

    /**
     * Update preferences according buttons change
     * @param ev 
     */
    @Override
    public void stateChanged(ChangeEvent ev) {

        JCheckBox clickedCheckBox = (JCheckBox) ev.getSource();
        if (clickedCheckBox == null) {
            _logger.error("Could not retrieve event source : " + ev);
            return;
        }

        final String clickedCheckBoxName = clickedCheckBox.getText();
        _logger.debug("Checkbox '" + clickedCheckBoxName + "' state changed:");

        if (_programaticUpdateUnderway) {
            _logger.trace("Programatic update underway, SKIPPING.");
            return;
        }

        for (Map.Entry<Object, JCheckBox> entry : _booleanPreferencesHashMap.entrySet()) {

            final JCheckBox currentCheckBox = entry.getValue();
            if (!clickedCheckBox.equals(currentCheckBox)) {
                continue;
            }

            final Object currentPreferenceKey = entry.getKey();
            final boolean currentPreferenceState = _preferences.getPreferenceAsBoolean(currentPreferenceKey);
            final boolean clickedCheckBoxState = currentCheckBox.isSelected();

            if (clickedCheckBoxState == currentPreferenceState) {
                _logger.trace("State did not trully changed (" + clickedCheckBoxState + " == " + currentPreferenceState + "), SKIPPING.");
                return;
            }

            try {
                _logger.debug("State did changed (" + currentPreferenceState + " -> " + clickedCheckBoxState + "), WRITING.");
                _preferences.setPreference(currentPreferenceKey, clickedCheckBoxState);
            } catch (PreferencesException ex) {
                _logger.warn("Could not set preference : " + ex);
            }

            return;
        }
    }

    public static void main(String[] args) {

        CommonPreferences preferences = CommonPreferences.getInstance();

        LinkedHashMap<Object, String> booleanSettings = new LinkedHashMap<Object, String>();
        booleanSettings.put(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN, "Show splashscreen at startup");
        // And so on...

        final BooleanPreferencesView generalSettingsView = new BooleanPreferencesView(preferences, booleanSettings);
        generalSettingsView.init();

        JFrame frame = new JFrame();
        frame.add(generalSettingsView);
        frame.pack();
        frame.setVisible(true);
    }
}
