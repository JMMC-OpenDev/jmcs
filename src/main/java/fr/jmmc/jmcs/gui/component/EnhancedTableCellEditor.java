/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.model.ColumnDescURLTableModel;
import fr.jmmc.jmcs.service.BrowserLauncher;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom cell editor that handles clicks on URL
 * @author bourgesl
 */
public final class EnhancedTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(EnhancedTableCellEditor.class.getName());
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /* members */
    /** table sorter to determine appropriate row/column in model */
    private final BasicTableSorter tableSorter;
    /** table model providing url */
    private final ColumnDescURLTableModel tableModel;

    /**
     * EnhancedTableCellEditor Constructor
     * @param tableSorter
     */
    public EnhancedTableCellEditor(final BasicTableSorter tableSorter) {
        super();
        this.tableSorter = tableSorter;

        final TableModel model = tableSorter.getTableModel();
        this.tableModel = (model instanceof ColumnDescURLTableModel) ? (ColumnDescURLTableModel) model : null;
    }

    // This method is called when a cell value is edited by the user.
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
                                                 final int row, final int column) {
        // If the cell is empty
        if (value == null) {
            return null; // Exit
        }

        if (tableModel != null) {
            // Retrieve clicked cell informations
            final int colIndex = table.convertColumnIndexToModel(column);

            if (colIndex == -1) {
                logger.warn("Error searching in the table model while trying to render cell "
                        + "at column {} table.getColumnCount() = {}", column, table.getColumnCount());
                return null;
            }

            final int modelColumn = tableSorter.columnModelIndex(colIndex);

            if (tableModel.hasURL(modelColumn)) {
                // use row sorter (filter):
                final int rowIndex = table.convertRowIndexToModel(row);
                // use table sorter:
                final int modelRow = tableSorter.modelIndex(rowIndex);
                final String url = tableModel.getURL(modelColumn, modelRow);

                if (url != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("User clicked, will open '{}' in default browser.", url);
                    }

                    // Open web browser with the computed URL
                    BrowserLauncher.openURL(url);
                }
            }
        }
        // Return null to "cancel" editing
        return null;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    @Override
    public Object getCellEditorValue() {
        // Should not be called
        logger.error("TableCellColorsEditor.getCellEditorValue() should have not been called.");
        return null;
    }
}
