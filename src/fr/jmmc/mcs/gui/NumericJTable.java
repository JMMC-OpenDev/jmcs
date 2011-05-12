/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

/**
 * Extends the swing jtable and fixes the default behaviour
 * to provide ergonomic scientific softwares.
 * Most tables should use this class as custom creation code.
 *
 */
public class NumericJTable extends javax.swing.JTable {

  /** default serial UID for Serializable interface */
  private static final long serialVersionUID = 1;

  /**
   * Overriden constructor to change default behaviour (default editor, single selection)
   */
  public NumericJTable() {
    super();

    // set one click edition on following table and show all decimals in numerical values
    ((DefaultCellEditor) getDefaultEditor(Double.class)).setClickCountToStart(1);

    // single table selection :
    getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Fix lost focus issues on JTable :
    putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
  }

  @Override
  public Component prepareEditor(TableCellEditor editor, int row, int column) {
    final Component c = super.prepareEditor(editor, row, column);

    if (c instanceof JTextComponent) {
      /* use invokeLater because of mouse events default behavior (caret ...) */
      SwingUtilities.invokeLater(new Runnable() {

        public void run() {
          ((JTextComponent) c).selectAll();
        }
      });
    }
    return c;
  }
}
