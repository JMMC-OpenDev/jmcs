/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.model.ColumnDescURLTableModel;
import fr.jmmc.jmcs.util.NumberUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to render table cells.
 *
 * @warning: No trace log implemented as this is very often called (performance).
 */
public final class EnhancedTableCellRenderer extends DefaultTableCellRenderer {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(EnhancedTableCellRenderer.class.getName());

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /* members */
    /** table sorter to determine appropriate row/column in model */
    private final BasicTableSorter tableSorter;
    /** table model providing url */
    private final ColumnDescURLTableModel tableModel;
    /** derived italic font */
    private Font _derivedItalicFont = null;
    /** orange border for selected cell */
    private final transient Border _orangeBorder = BorderFactory.createLineBorder(Color.ORANGE, 2);
    /** internal string buffer */
    final StringBuilder _buffer = new StringBuilder(128);

    /**
     * EnhancedTableCellRenderer Constructor
     * @param tableSorter
     */
    public EnhancedTableCellRenderer(final BasicTableSorter tableSorter) {
        super();
        this.tableSorter = tableSorter;

        final TableModel model = tableSorter.getTableModel();
        this.tableModel = (model instanceof ColumnDescURLTableModel) ? (ColumnDescURLTableModel) model : null;
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value  the string value for this cell; if value is
     *          <code>null</code> it sets the text value to an empty string
     * @see JLabel#setText
     *
     */
    @Override
    public void setValue(final Object value) {
        String text = "";
        if (value != null) {
            if (value instanceof Double) {
                final double val = ((Double) value).doubleValue();
                if (!Double.isNaN(val)) {
                    text = NumberUtils.format(val);
                }
            } else if (value instanceof Boolean) {
                text = ((Boolean) value).booleanValue() ? "True" : "False";
            } else {
                text = value.toString();
            }
        }
        setText(text);
    }

    /**
     * getTableCellRendererComponent  -  return the component with renderer (Table)
     * @param table JTable
     * @param value Object
     * @param isSelected boolean
     * @param hasFocus boolean
     * @param row int
     * @param column int
     * @return Component
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                   final boolean isSelected, final boolean hasFocus,
                                                   final int row, final int column) {

        // Set default renderer to the component
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // always use right alignment:
        setHorizontalAlignment(JLabel.RIGHT);

        final int colIndex = table.convertColumnIndexToModel(column);

        if (colIndex == -1) {
            logger.warn("Error searching in the table model while trying to render cell "
                    + "at column {} table.getColumnCount() = {}", column, table.getColumnCount());
            return this;
        }

        // Do not change color if cell is located on a selected row
        if (table.isRowSelected(row)) {
            // Except if it is the selected cell itself (to highlight found tokens)
            if (table.isColumnSelected(column)) {
                setBorder(_orangeBorder);
                setBackground(Color.YELLOW);
                setForeground(Color.BLACK);

                // Put the corresponding row font in italic:
                if (_derivedItalicFont == null) {
                    // cache derived Font:
                    final Font cellFont = getFont();
                    _derivedItalicFont = cellFont.deriveFont(cellFont.getStyle() | Font.ITALIC);
                }
                setFont(_derivedItalicFont);
            }

            return this;
        }

        final StringBuilder sb = _buffer;
        final int modelColumn = tableSorter.columnModelIndex(colIndex);

        // Compose catalog URL
        if ((value != null) && (tableModel != null) && tableModel.hasURL(modelColumn)) {
            setText(sb.append("<html><a href='#empty'>").append(value).append("</a></html>").toString());
            sb.setLength(0); // recycle buffer
        }

        Color foregroundColor = Color.BLACK;
        Color backgroundColor = Color.WHITE;

        // If cell is not selected and not focused 
        if (!(isSelected && hasFocus)) {
            // Apply colors
            setForeground(foregroundColor);
            setBackground(backgroundColor);
            setBorder(noFocusBorder);
        }

        // Return the component
        return this;
    }
}
