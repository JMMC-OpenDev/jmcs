/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.model;

/**
 * This abstract class extends ColumnDescTableModel to support columns giving URLs
 */
public abstract class ColumnDescURLTableModel extends ColumnDescTableModel {

    /**
     * Return whether the column has an URL.
     *
     * @param column
     * @return true if a URL is set, false otherwise.
     */
    public boolean hasURL(final int column) {
        return false;
    }

    /**
     * Return the URL defined for the given column or null if undefined
     * @param column
     * @return URL defined for the given column or null if undefined
     */
    public String getURL(final int column, final int row) {
        return null;
    }

    /**
     * Returns true if the cell at
     * <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable. Otherwise,
     * <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     *
     * @param	rowIndex	the row whose value to be queried
     * @param	columnIndex	the column whose value to be queried
     * @return	true if the cell is editable
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return hasURL(columnIndex);
    }

}
