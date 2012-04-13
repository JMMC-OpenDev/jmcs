/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

    // Constants
    public static final String SAVE_AND_RESTART_MESSAGE = "Please save modifications and restart the application to apply changes.";
    /** Logger - get from given class name */
    private static final Logger _logger = LoggerFactory.getLogger(BooleanPreferencesView.class.getName());
    private final Preferences _preferences;
    private final Map<Object, JCheckBox> _booleanPreferencesHashMap;
    private boolean _programaticUpdateUnderway = false;
    private final String _message;

    /**
     * Constructor.
     * @param preferences the PReferences instance to work on.
     * @param booleanPreferencesHashMap the ordered map linking preference key to its check box label.
     */
    public BooleanPreferencesView(Preferences preferences, LinkedHashMap<Object, String> booleanPreferencesHashMap) {
        this(preferences, booleanPreferencesHashMap, null);
    }

    /**
     * Constructor.
     * @param preferences the PReferences instance to work on.
     * @param booleanPreferencesHashMap the ordered map linking preference key to its check box label.
     * @param message (optional) string added at the bottom of the pane, null otherwise.
     */
    public BooleanPreferencesView(Preferences preferences, LinkedHashMap<Object, String> booleanPreferencesHashMap, String message) {

        super();

        // Check arguments validity
        if ((preferences == null) || (booleanPreferencesHashMap == null) || (booleanPreferencesHashMap.size() < 1)) {
            throw new IllegalArgumentException();
        }

        // Decipher message availability
        if ((message == null) || (message.length() == 0)) {
            _message = null;
        } else {
            _message = message;
        }

        _preferences = preferences;

        _booleanPreferencesHashMap = new LinkedHashMap<Object, JCheckBox>();
        for (Map.Entry<Object, String> entry : booleanPreferencesHashMap.entrySet()) {

            final Object preferenceKey = entry.getKey();
            final String checkBoxLabel = entry.getValue();

            final JCheckBox newCheckBox = new JCheckBox(checkBoxLabel);
            _booleanPreferencesHashMap.put(preferenceKey, newCheckBox);
        }
    }

    /** MANDATORY call after construction. */
    public void init() {

        _preferences.addObserver(this);

        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setOpaque(false);

        // Layout management
        checkBoxesPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;

        // Layout each checkbox
        int biggestMinimumCheckBoxesWidth = 0;
        for (JCheckBox checkBox : _booleanPreferencesHashMap.values()) {

            checkBoxesPanel.add(checkBox, gridBagConstraints);
            gridBagConstraints.gridy++;

            final int currentMinimumCheckBoxWidth = checkBox.getMinimumSize().width;
            biggestMinimumCheckBoxesWidth = Math.max(biggestMinimumCheckBoxesWidth, currentMinimumCheckBoxWidth);

            checkBox.addChangeListener(this);
        }

        // Set checkboxes panel width to center properly
        final Dimension dimension = new Dimension(biggestMinimumCheckBoxesWidth, 0);
        checkBoxesPanel.setMaximumSize(dimension);

        // Layout the checkboxes panel centered at the top
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(checkBoxesPanel);
        add(Box.createVerticalGlue());

        // Add the centered bottom label (if any)
        if (_message != null) {
            final JLabel label = new JLabel(_message);
            label.setAlignmentX(CENTER_ALIGNMENT);
            add(label);
        }

        // Synchronize checkboxes state with their associated preference values
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

        final BooleanPreferencesView generalSettingsView = new BooleanPreferencesView(preferences, booleanSettings, "For testing purpose only !");
        generalSettingsView.init();

        JFrame frame = new JFrame();
        frame.add(generalSettingsView);
        frame.pack();
        frame.setVisible(true);
    }
}
