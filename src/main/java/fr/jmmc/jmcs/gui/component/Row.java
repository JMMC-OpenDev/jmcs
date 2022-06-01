/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import static fr.jmmc.jmcs.gui.component.BasicTableSorter.ASCENDING;
import java.util.List;
import javax.swing.table.TableModel;

/**
 * Smallest wrapper on a Row
 */
public final class Row implements Comparable<Row> {

    /* members */
    private final RowState state;
    public final int modelIndex;

    public Row(final RowState state, final int index) {
        this.state = state;
        this.modelIndex = index;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final Row other) {
        final TableModel tableModel = state.tableModel;
        final List<Directive> sortingColumns = state.sortingColumns;

        final int row1 = modelIndex;
        final int row2 = other.modelIndex;

        for (int i = 0, len = sortingColumns.size(); i < len; i++) {
            final Directive directive = sortingColumns.get(i);
            final int column = directive.column;

            final Object o1 = tableModel.getValueAt(row1, column);
            final Object o2 = tableModel.getValueAt(row2, column);

            // Define null greater than everything, except null. 
            if ((o1 == null) && (o2 == null)) {
                continue;
            }
            if (o1 == null) {
                // o1 == null vs o2 != null
                return 1; // o1 greater than o2
            }
            if (o2 == null) {
                // o1 != null vs o2 == null
                return -1; // o1 less than o2
            }

            // o1 != null and o2 != null:
            final int cmp = directive.comparator.compare(o1, o2);

            if (cmp != 0) {
                return (directive.direction == ASCENDING) ? cmp : -cmp;
            }
        }
        return 0;
    }

    public final static class RowState {

        final TableModel tableModel;
        final List<Directive> sortingColumns;

        public RowState(final TableModel tableModel, final List<Directive> sortingColumns) {
            this.tableModel = tableModel;
            this.sortingColumns = sortingColumns;
        }
    }
}
