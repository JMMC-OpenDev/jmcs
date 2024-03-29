/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.jmcs.util.ObjectUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CommonPreferences editor
 */
public final class CommonPreferencesView extends javax.swing.JPanel implements Observer {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CommonPreferencesView.class.getName());
    // Members:
    /** preference singleton */
    private final CommonPreferences myPreferences = CommonPreferences.getInstance();

    /** browser selector action */
    private final Action browserSelectorAction;

    /** laf infos (class name == name) */
    private final Map<String, String> lafNames = new HashMap<String, String>(8);

    /** laf infos (name == class name) */
    private final Map<String, String> lafClassNames = new HashMap<String, String>(8);

    /** Creates new form CommonPreferencesView */
    public CommonPreferencesView() {
        initComponents();

        this.browserSelectorAction = BrowserLauncher.getBrowserSelectorAction(this);
        this.jButtonBrowserSelector.setEnabled(this.browserSelectorAction.isEnabled());

        // register this instance as a Preference Observer :
        this.myPreferences.addObserver(this);

        // update GUI
        update(null, null);

        jFieldUiScale.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                jFieldUiScaleValuePropertyChanged(evt);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupFileChooser = new javax.swing.ButtonGroup();
        jPanelMisc = new javax.swing.JPanel();
        jLabelBrowser = new javax.swing.JLabel();
        jTextFieldBrowser = new javax.swing.JTextField();
        jButtonBrowserSelector = new javax.swing.JButton();
        jLabelScaleUI = new javax.swing.JLabel();
        jComboBoxLAF = new javax.swing.JComboBox();
        jLabelLAF = new javax.swing.JLabel();
        jFieldUiScale = new javax.swing.JFormattedTextField();
        jButtonRefreshUI = new javax.swing.JButton();
        jLabelFileChooser = new javax.swing.JLabel();
        jRadioButtonNativeFileChooserYes = new javax.swing.JRadioButton();
        jRadioButtonNativeFileChooserNo = new javax.swing.JRadioButton();
        filler = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        jPanelMisc.setBorder(javax.swing.BorderFactory.createTitledBorder("Miscellaneous"));
        jPanelMisc.setLayout(new java.awt.GridBagLayout());

        jLabelBrowser.setText("Web browser:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jLabelBrowser, gridBagConstraints);

        jTextFieldBrowser.setEditable(false);
        jTextFieldBrowser.setText("Web Browser");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jTextFieldBrowser, gridBagConstraints);

        jButtonBrowserSelector.setText("Select...");
        jButtonBrowserSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowserSelectorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jButtonBrowserSelector, gridBagConstraints);

        jLabelScaleUI.setText("UI scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jLabelScaleUI, gridBagConstraints);

        jComboBoxLAF.setModel(new DefaultComboBoxModel(generateLAF()));
        jComboBoxLAF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLAFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jComboBoxLAF, gridBagConstraints);

        jLabelLAF.setText("Look & Feel:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jLabelLAF, gridBagConstraints);

        jFieldUiScale.setColumns(6);
        jFieldUiScale.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jFieldUiScale, gridBagConstraints);

        jButtonRefreshUI.setText("Update UI");
        jButtonRefreshUI.setToolTipText("<html>\nClick 'Update' to change both the Look and Feel & the UI scale<br>\n<b> but it is recommended to restart the application.</b>");
        jButtonRefreshUI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshUIActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelMisc.add(jButtonRefreshUI, gridBagConstraints);

        jLabelFileChooser.setText("Native file chooser:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        jPanelMisc.add(jLabelFileChooser, gridBagConstraints);

        buttonGroupFileChooser.add(jRadioButtonNativeFileChooserYes);
        jRadioButtonNativeFileChooserYes.setText("yes");
        jRadioButtonNativeFileChooserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonNativeFileChooserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMisc.add(jRadioButtonNativeFileChooserYes, gridBagConstraints);

        buttonGroupFileChooser.add(jRadioButtonNativeFileChooserNo);
        jRadioButtonNativeFileChooserNo.setText("no");
        jRadioButtonNativeFileChooserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonNativeFileChooserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMisc.add(jRadioButtonNativeFileChooserNo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanelMisc, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowserSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowserSelectorActionPerformed
        browserSelectorAction.actionPerformed(evt);
    }//GEN-LAST:event_jButtonBrowserSelectorActionPerformed

    private void jButtonRefreshUIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshUIActionPerformed
        // TODO: confirm popup
        final String lafName = (String) jComboBoxLAF.getSelectedItem();
        if (lafName != null) {
            final String className = lafClassNames.get(lafName);
            if (className != null) {
                SwingSettings.setLookAndFeel(className, true);
            }
        }
    }//GEN-LAST:event_jButtonRefreshUIActionPerformed

    private void jComboBoxLAFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLAFActionPerformed
        final String lafName = (String) jComboBoxLAF.getSelectedItem();
        if (lafName != null) {
            try {
                final String className = lafClassNames.get(lafName);
                if (className != null) {
                    myPreferences.setPreference(CommonPreferences.UI_LAF_CLASSNAME, className);
                }
            } catch (PreferencesException pe) {
                _logger.error("property failure : ", pe);
            }
        }
    }//GEN-LAST:event_jComboBoxLAFActionPerformed

    private void jRadioButtonNativeFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonNativeFileChooserActionPerformed
        try {
            // will fire triggerObserversNotification so update() will be called
            this.myPreferences.setPreference(CommonPreferences.FILECHOOSER_NATIVE, Boolean.valueOf(this.jRadioButtonNativeFileChooserYes.isSelected()));
        } catch (PreferencesException pe) {
            _logger.error("property failure : ", pe);
        }
    }//GEN-LAST:event_jRadioButtonNativeFileChooserActionPerformed

    private void jFieldUiScaleValuePropertyChanged(PropertyChangeEvent evt) {
        if (!ObjectUtils.areEquals(evt.getNewValue(), evt.getOldValue())) {
            final double uiScaleNew = ((Number) jFieldUiScale.getValue()).doubleValue();
            if (uiScaleNew < 1.0 || uiScaleNew > 10.0) {
                // invalid value :
                jFieldUiScale.setValue(1.0);
                return;
            }
            try {
                myPreferences.setPreference(CommonPreferences.UI_SCALE, Double.valueOf(uiScaleNew));
            } catch (PreferencesException pe) {
                _logger.error("property failure : ", pe);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupFileChooser;
    private javax.swing.Box.Filler filler;
    private javax.swing.JButton jButtonBrowserSelector;
    private javax.swing.JButton jButtonRefreshUI;
    private javax.swing.JComboBox jComboBoxLAF;
    private javax.swing.JFormattedTextField jFieldUiScale;
    private javax.swing.JLabel jLabelBrowser;
    private javax.swing.JLabel jLabelFileChooser;
    private javax.swing.JLabel jLabelLAF;
    private javax.swing.JLabel jLabelScaleUI;
    private javax.swing.JPanel jPanelMisc;
    private javax.swing.JRadioButton jRadioButtonNativeFileChooserNo;
    private javax.swing.JRadioButton jRadioButtonNativeFileChooserYes;
    private javax.swing.JTextField jTextFieldBrowser;
    // End of variables declaration//GEN-END:variables

    /**
     * Listen to preferences changes
     * @param o Preferences
     * @param arg unused
     */
    @Override
    public void update(final Observable o, final Object arg) {
        _logger.debug("Preferences updated on : {}", this);

        jTextFieldBrowser.setText(myPreferences.getPreference(CommonPreferences.WEB_BROWSER));

        final String lafClassName = myPreferences.getPreference(CommonPreferences.UI_LAF_CLASSNAME);
        if (lafClassName.isEmpty()) {
            jComboBoxLAF.setSelectedItem(UIManager.getLookAndFeel().getName());
        } else {
            jComboBoxLAF.setSelectedItem(lafNames.get(lafClassName));
        }

        jFieldUiScale.setValue(myPreferences.getUIScale());

        final boolean useNativeFileChooser = this.myPreferences.getPreferenceAsBoolean(CommonPreferences.FILECHOOSER_NATIVE);
        this.jRadioButtonNativeFileChooserYes.setSelected(useNativeFileChooser);
        this.jRadioButtonNativeFileChooserNo.setSelected(!useNativeFileChooser);
    }

    Vector<String> generateLAF() {
        final UIManager.LookAndFeelInfo[] lafInfos = UIManager.getInstalledLookAndFeels();

        final Vector<String> model = new Vector<String>(lafInfos.length + 1);

        for (UIManager.LookAndFeelInfo lookAndFeelInfo : lafInfos) {
            model.add(lookAndFeelInfo.getName());
            addLaf(lookAndFeelInfo.getName(), lookAndFeelInfo.getClassName());
        }

        // Always add current LAF:
        final LookAndFeel laf = UIManager.getLookAndFeel();
        if (!model.contains(laf.getName())) {
            model.add(0, laf.getName());
            addLaf(laf.getName(), laf.getClass().getName());
        }
        return model;
    }

    private void addLaf(final String name, final String className) {
        lafNames.put(className, name);
        lafClassNames.put(name, className);
    }
}
