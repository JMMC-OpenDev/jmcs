/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmcs.model;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.component.ComponentResizeAdapter;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Panel to be added in a dialog box to edit the display of the table
 *
 * @author martin
 */
public class TableEditorPanel extends javax.swing.JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /**
     * Display the table editor using the given target name as the initial selected target
     * @param prevAllColumns all available columns (visible + hidden)
     * @param prevVisibleColumns visible columns
     * @param defaultAllColumns default all columns (reset button)
     * @param defaultVisibleColumns default visible columns (reset button)
     * @param newAllColumns empty list that will store updated allColumns.
     * @param newVisibleColumns empty list that will store updated visibleColumns
     * @param dialogSizePref optional preference key to restore dialog size
     * @return void, the return values are in params newAllColumns & newVisibleColumns.
     */
    public static void showEditor(
            final List<String> prevAllColumns,
            final List<String> prevVisibleColumns,
            final List<String> defaultAllColumns,
            final List<String> defaultVisibleColumns,
            final List<String> newAllColumns,
            final List<String> newVisibleColumns,
            final String dialogSizePref) {

        // 1. Create the dialog (modal):
        final JDialog dialog = new JDialog(App.getFrame(), "Edit table columns", true);

        final Dimension dim = new Dimension(600, 500);
        dialog.setMinimumSize(dim);
        dialog.addComponentListener(new ComponentResizeAdapter(dim));

        // 2. Optional: What happens when the dialog closes ?
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 3. Create components and put them in the dialog
        final TableEditorPanel tableEditorPanel = 
                new TableEditorPanel(dialog, prevAllColumns, prevVisibleColumns, 
                        defaultAllColumns, defaultVisibleColumns);
        dialog.add(tableEditorPanel);

        // 4. Size the dialog.
        WindowUtils.setClosingKeyboardShortcuts(dialog);
        dialog.pack();

        // Restore, then automatically save window size changes:
        WindowUtils.rememberWindowSize(dialog, dialogSizePref);

        // Center it :
        dialog.setLocationRelativeTo(dialog.getOwner());

        // 5. Show it and waits until dialog is not visible or disposed :
        dialog.setResizable(true);
        dialog.setVisible(true);

        // when dialog returns OK, set the chosen columns
        if (tableEditorPanel.isResult()) {
            newAllColumns.addAll(tableEditorPanel.getNewAllColumns());
            newVisibleColumns.addAll(tableEditorPanel.getNewVisibleColumns());
        }
        else {
            // return the initial lists
            newAllColumns.addAll(prevAllColumns);
            newVisibleColumns.addAll(prevVisibleColumns);
        }
    }

    /* members */
    // Model and view for the list
    private final DefaultListModel<String> modelHidden = new DefaultListModel<>();
    private final DefaultListModel<String> modelVisible = new DefaultListModel<>();

    // Reference to the parent dialog box to handle its events
    private final JDialog dialog;

    /** editor result = true if the user validates the inputs */
    private boolean result = false;
    
     /** default all columns for reset button. */
    private final List<String> defaultAllColumns;
    
     /** default visible columns for reset button. */
    private final List<String> defaultVisibleColumns;
    

    /**
     * Constructor
     * @param dialog Reference to the parent dialog box to handle its events
     * @param initialAllColumns all available columns (visible + hidden)
     * @param initialVisibleColumns visible columns
     * @param defaultAllColumns default all columns (reset button)
     * @param defaultVisibleColumns default visible columns (reset button)
     */
    private TableEditorPanel(final JDialog dialog, 
            final List<String> initialAllColumns, final List<String> initialVisibleColumns,
            final List<String> defaultAllColumns, final List<String> defaultVisibleColumns) {
        initComponents();
        this.dialog = dialog;
        
        this.defaultAllColumns = defaultAllColumns;
        this.defaultVisibleColumns = defaultVisibleColumns;

        // Fill with available columns, but remove the ones already displayed
        initialAllColumns.forEach(modelHidden::addElement);
        initialVisibleColumns.forEach(modelHidden::removeElement);
        initialVisibleColumns.forEach(modelVisible::addElement);

        jListHidden.setModel(modelHidden);
        jListVisible.setModel(modelVisible);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButtonAdd = new javax.swing.JButton();
        jLabelDisplayed = new javax.swing.JLabel();
        jButtonOk = new javax.swing.JButton();
        jLabelHidden = new javax.swing.JLabel();
        jButtonCancel = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jScrollPaneHidden = new javax.swing.JScrollPane();
        jListHidden = new javax.swing.JList<>();
        jScrollPaneDisplayed = new javax.swing.JScrollPane();
        jListVisible = new javax.swing.JList<>();
        jButtonReset = new javax.swing.JButton();
        jButtonUp = new javax.swing.JButton();
        jButtonDown = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(400, 250));
        setLayout(new java.awt.GridBagLayout());

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonAdd, gridBagConstraints);

        jLabelDisplayed.setText("Visible columns");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabelDisplayed, gridBagConstraints);

        jButtonOk.setText("Ok");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonOk, gridBagConstraints);

        jLabelHidden.setText("Hidden columns");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabelHidden, gridBagConstraints);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonCancel, gridBagConstraints);

        jButtonRemove.setText("Remove");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonRemove, gridBagConstraints);

        jListHidden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jListHiddenFocusGained(evt);
            }
        });
        jScrollPaneHidden.setViewportView(jListHidden);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(jScrollPaneHidden, gridBagConstraints);

        jListVisible.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jListVisibleFocusGained(evt);
            }
        });
        jScrollPaneDisplayed.setViewportView(jListVisible);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(jScrollPaneDisplayed, gridBagConstraints);

        jButtonReset.setText("Reset");
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonReset, gridBagConstraints);

        jButtonUp.setText("Up");
        jButtonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonUp, gridBagConstraints);

        jButtonDown.setText("Down");
        jButtonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jButtonDown, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        jListVisible.getSelectedValuesList().forEach(column -> {
            modelHidden.addElement(column);
            modelVisible.removeElement(column);
        });
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.result = true;
        dialog.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dialog.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        modelHidden.clear();
        modelVisible.clear();
        defaultAllColumns.forEach(modelHidden::addElement);
        defaultVisibleColumns.forEach(modelHidden::removeElement);
        defaultVisibleColumns.forEach(modelVisible::addElement);
    }//GEN-LAST:event_jButtonResetActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        jListHidden.getSelectedValuesList().forEach(column -> {
            modelVisible.addElement(column);
            modelHidden.removeElement(column);
        });
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed

        int[] indexes = jListVisible.getSelectedIndices();

        // - 1 so the index 0 cannot be moved up
        int lastIndex = -1;

        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];

            // we cannot move up if the last index is immediately above
            if (lastIndex < index - 1) {
                // we switch the values
                String a = modelVisible.getElementAt(index - 1);
                String b = modelVisible.getElementAt(index);
                modelVisible.setElementAt(b, index - 1);
                modelVisible.setElementAt(a, index);
                // we decrement the index
                index--;
                // also in the indexes table (to keep selection GUI correct)
                indexes[i]--;
            }

            lastIndex = index;
        }

        // we update the selection GUI because the values have moved but not the selection indexes
        jListVisible.setSelectedIndices(indexes);
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed

        int[] indexes = jListVisible.getSelectedIndices();

        // size() so the index (size - 1) cannot be moved down
        int lastIndex = modelVisible.getSize();

        for (int i = indexes.length - 1; i >= 0; i--) {
            int index = indexes[i];

            // we cannot move down if the last index is immediately below
            if (lastIndex > index + 1) {
                // we switch the values
                String a = modelVisible.getElementAt(index);
                String b = modelVisible.getElementAt(index + 1);
                modelVisible.setElementAt(b, index);
                modelVisible.setElementAt(a, index + 1);
                // we increment the index
                index++;
                // also in the indexes table (to keep selection GUI correct)
                indexes[i]++;
            }

            lastIndex = index;
        }

        // we update the selection GUI because the values have moved but not the selection indexes
        jListVisible.setSelectedIndices(indexes);
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jListHiddenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jListHiddenFocusGained
        jListVisible.clearSelection();
    }//GEN-LAST:event_jListHiddenFocusGained

    private void jListVisibleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jListVisibleFocusGained
        jListHidden.clearSelection();
    }//GEN-LAST:event_jListVisibleFocusGained

    List<String> getNewAllColumns() {
        // getting all visibles
        List<String> newAllColumns = getNewVisibleColumns();
        // adding all hiddens
        for (Enumeration<String> e = this.modelHidden.elements(); e.hasMoreElements();) {
            newAllColumns.add(e.nextElement());
        }
        return newAllColumns;
    }
    
    List<String> getNewVisibleColumns() {
        List<String> availableColumns = new ArrayList<>(modelVisible.getSize());
        for (Enumeration<String> e = modelVisible.elements(); e.hasMoreElements();) {
            availableColumns.add(e.nextElement());
        }
        return availableColumns;
    }

    /**
     * Return the editor result
     * @return true if the user validated (ok button)
     */
    protected boolean isResult() {
        return result;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JLabel jLabelDisplayed;
    private javax.swing.JLabel jLabelHidden;
    private javax.swing.JList<String> jListHidden;
    private javax.swing.JList<String> jListVisible;
    private javax.swing.JScrollPane jScrollPaneDisplayed;
    private javax.swing.JScrollPane jScrollPaneHidden;
    // End of variables declaration//GEN-END:variables

}
