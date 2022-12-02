/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmcs.model;

import fr.jmmc.jmcs.gui.component.BasicTableColumnModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This abstract class deals with the ColumnDesc list describing the table columns
 */
public abstract class ColumnDescTableModel extends AbstractTableModel implements BasicTableColumnModel {

    /* members */
    /** list of ColumnDesc. Uniques by getName() */
    protected final List<ColumnDesc> listColumnDesc;
    /** temporary buffer */
    protected final StringBuilder sbTmp = new StringBuilder(256);

    @SuppressWarnings("CollectionWithoutInitialCapacity")
    public ColumnDescTableModel() {
        super();
        listColumnDesc = new ArrayList<>();
    }

    /** @return list of ColumnDesc */
    public final List<ColumnDesc> getColumnDescList() {
        return listColumnDesc;
    }

    public final List<String> getColumnNames() {
        final List<String> columnNames = new ArrayList<>(listColumnDesc.size());
        listColumnDesc.forEach(columnDesc -> columnNames.add(columnDesc.getName()));
        return columnNames;
    }

    public final ColumnDesc getColumnDesc(final int columnIndex) {
        return this.listColumnDesc.get(columnIndex);
    }

    /* TableModel interface implementation */
    @Override
    public final int getColumnCount() {
        return listColumnDesc.size();
    }

    @Override
    public final String getColumnName(int columnIndex) {
        return getColumnDesc(columnIndex).getName();
    }

    @Override
    public final Class<?> getColumnClass(int columnIndex) {
        return getColumnDesc(columnIndex).getDataClass();
    }

    /* BasicTableColumnModel interface implementation */
    @Override
    public final String getColumnLabel(final int columnIndex) {
        return getColumnDesc(columnIndex).getLabel();
    }

    @Override
    public final String getColumnTooltipText(final int columnIndex) {
        final ColumnDesc columnDesc = getColumnDesc(columnIndex);
        sbTmp.setLength(0);
        if (columnDesc.getLabel() != null) {
            sbTmp.append(columnDesc.getLabel()).append(' ');
        }
        sbTmp.append('[').append(columnDesc.getName()).append("] ");
        if (columnDesc.getDescription() != null) {
            sbTmp.append(columnDesc.getDescription());
        }
        return sbTmp.toString();
    }

    /**
     * Sets the value in the cell at
     * <code>columnIndex</code> and
     * <code>rowIndex</code> to
     * <code>aValue</code>.
     *
     * @param	aValue	the new value
     * @param	rowIndex	the row whose value is to be changed
     * @param	columnIndex the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // no-op
    }
}
