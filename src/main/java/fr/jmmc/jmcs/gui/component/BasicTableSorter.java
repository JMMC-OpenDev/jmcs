package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.StringUtils;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TableSorter is a decorator for TableModels; adding sorting
 * functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains
 * a map from the row indexes of the view to the row indexes of the
 * model. As requests are made of the sorter (like getValueAt(row, col))
 * they are passed to the underlying model after the row numbers
 * have been translated via the internal mapping array. This way,
 * the TableSorter appears to hold another copy of the table
 * with the rows in a different order.
 * TableSorter registers itself as a listener to the underlying model,
 * just as the JTable itself would. Events recieved from the model
 * are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorter's listeners (typically the JTable).
 * If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the
 * rows the next time a value is requested.
 * When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the
 * table header may be used as a complete UI for TableSorter.
 * The default renderer of the tableHeader is decorated with a renderer
 * that indicates the sorting status of each column. In addition,
 * a mouse listener is installed with the following behavior:
 * <ul>
 * <li>
 * Mouse-click: Clears the sorting status of all other columns
 * and advances the sorting status of that column through three
 * values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to
 * NOT_SORTED again).
 * <li>
 * SHIFT-mouse-click: Clears the sorting status of all other columns
 * and cycles the sorting status of the column through the same
 * three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li>
 * CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except
 * that the changes to the column do not cancel the statuses of columns
 * that are already sorting - giving a way to initiate a compound
 * sort.
 * </ul>
 * This is a long overdue rewrite of a class of the same name that
 * first appeared in the swing table demos in 1997.
 *
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 */
public final class BasicTableSorter extends AbstractTableModel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(BasicTableSorter.class.getName());
    /** Ascending Sort */
    public static final int ASCENDING = 1;
    /** Not Sorted */
    public static final int NOT_SORTED = 0;
    /** Descending Sort */
    public static final int DESCENDING = -1;
    /** empty sort directive*/
    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED, null, null);
    /**
     * comparison based on Comparable interface
     */
    public static final Comparator<Object> COMPARABLE_COMPARATOR = new Comparator<Object>() {
        @Override
        @SuppressWarnings("unchecked")
        public int compare(final Object o1, final Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };
    /**
     * Lexical comparison ie compare String values ignoring case.
     */
    public static final Comparator<Object> LEXICAL_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(final Object o1, final Object o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }
    };

    /* members */
    /** source data model */
    private TableModel tableModel;
    /** sorted rows mapping the view to the source model */
    private Row[] viewToModel;
    /** mapping from the source model index to the view index */
    private int[] modelToView;
    /** table header */
    private JTableHeader tableHeader;
    private MouseHandler mouseListener = null;
    private DragColumnHandler dragHandler = null;
    private final TableModelHandler tableModelListener;
    private final List<Directive> sortingColumns = new ArrayList<Directive>(4);
    /** optional table header customizer */
    private BasicTableColumnModel tableHeaderCustomizer = null;
    /** optional visible column names (ordered) */
    private List<String> visibleColumnNames = null;
    /** unique column names that are missing (avoid repeated messages in logs) */
    private final HashSet<String> _ignoreMissingColumns = new HashSet<String>(32);
    /** optional table column moved listener */
    private BasicTableColumnMovedListener tableColumnMovedListener = null;

    /**
     * Indirection array.
     *
     * Contains the model column for any given displayed column.
     * modelColumn = _viewIndex[viewColumn];
     */
    private int[] _viewIndex;

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel
     * @param tableHeader
     */
    public BasicTableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this(tableModel, tableHeader, null);
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel
     * @param tableHeader
     * @param tableHeaderCustomizer
     */
    public BasicTableSorter(TableModel tableModel, JTableHeader tableHeader, BasicTableColumnModel tableHeaderCustomizer) {
        this.tableModelListener = new TableModelHandler();
        if (tableModel instanceof BasicTableColumnModel) {
            this.tableHeaderCustomizer = (BasicTableColumnModel) tableModel;
        } else {
            this.tableHeaderCustomizer = tableHeaderCustomizer;
        }

        if (tableModel != null) {
            tableModel.addTableModelListener(tableModelListener);
        }
        setTableHeader(tableHeader);
        setTableModel(tableModel);

        computeColumnsIndirectionArray();
    }

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    /**
     * @return the source table model
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * Defines the source data model
     * @param tableModel source data model
     */
    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;

        // Multiple table model listeners => ordering side effects !!!
        clearSortingState();
        fireTableStructureChanged();
    }

    private int findModelColumnByName(final String columnName) {
        for (int i = 0, len = tableModel.getColumnCount(); i < len; i++) {
            if (columnName.equals(tableModel.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return table header
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Define the table header (to customize)
     * @param tableHeader table header
     */
    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            if (mouseListener != null) {
                this.tableHeader.removeMouseListener(mouseListener);
            }
            if (this.dragHandler != null) {
                this.tableHeader.getColumnModel().removeColumnModelListener(dragHandler);
            }
        }
        this.tableHeader = tableHeader;

        if (this.tableHeader != null) {
            final TableColumnModel columnModel = tableHeader.getColumnModel();

            this.dragHandler = new DragColumnHandler(columnModel);
            this.mouseListener = new MouseHandler(dragHandler);

            columnModel.addColumnModelListener(dragHandler);

            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(
                    new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer(), this.tableHeaderCustomizer));
        }
    }

    /**
     * @return optional table header customizer
     */
    public BasicTableColumnModel getTableHeaderCustomizer() {
        return tableHeaderCustomizer;
    }

    /**
     * Define the optional table header customizer
     * @param headerCustomizer optional table header customizer
     */
    public void setTableHeaderCustomizer(final BasicTableColumnModel headerCustomizer) {
        this.tableHeaderCustomizer = headerCustomizer;
        setTableHeader(getTableHeader());
    }

    /**
     * @return true if sorting is enabled
     */
    public boolean isSorting() {
        return !sortingColumns.isEmpty();
    }

    private Directive getDirective(final int column) {
        for (Directive directive : sortingColumns) {
            if (directive.column == column) {
                return directive;
            }
        }

        return EMPTY_DIRECTIVE;
    }

    /**
     * @param column column index
     * @return integer value in (ASCENDING = 1, NOT_SORTED = 0, DESCENDING = -1)
     */
    public int getSortingStatus(int column) {
        return getDirective(columnModelIndex(column)).direction;
    }

    /**
     * Defines the column sorting
    @param column column index
    @param status integer value in (ASCENDING = 1, NOT_SORTED = 0, DESCENDING = -1)
     */
    public void setSortingStatus(final int column, final int status) {
        final int modelColumn = columnModelIndex(column);

        final Directive directive = getDirective(modelColumn);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(createDirective(modelColumn, status));
        }

        _logger.debug("setSortingStatus: {}", sortingColumns);

        sortingStatusChanged();
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();

        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    Icon getHeaderRendererIcon(final int column, final int size) {
        final Directive directive = getDirective(columnModelIndex(column));

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new ArrowIcon(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    private void checkSortingState() {
        for (ListIterator<Directive> it = sortingColumns.listIterator(); it.hasNext();) {
            Directive directive = it.next();

            // Get the current column index given its name:
            // TODO: check view or model ?
            final int columnId = findModelColumnByName(directive.colName);

            // check column indexes:
            if (columnId != directive.column) {
                if (columnId != -1) {
                    // update directive:
                    directive = createDirective(columnId, directive.direction);
                    _logger.debug("update sorting directive: {}", directive);
                    it.set(directive);
                } else {
                    // missing column:
                    _logger.debug("remove sorting directive: {}", directive);
                    it.remove();
                }
            }
        }
        clearSortingState();
    }

    private Directive createDirective(final int realColumn, final int direction) {
        // note: may return a virtual column name (AA, AB, etc.) if realColumn > column count
        // see AbstractTableModel.getColumnName(int)
        final String colName = tableModel.getColumnName(realColumn);

        return new Directive(realColumn, direction, getComparator(realColumn), colName);
    }

    private Comparator<Object> getComparator(final int realColumn) {
        final Class<?> columnType = tableModel.getColumnClass(realColumn);

        if (_logger.isDebugEnabled()) {
            _logger.debug("getComparator({}): {}", tableModel.getColumnName(realColumn), columnType);
        }

        if (String.class == columnType) {
            return LEXICAL_COMPARATOR;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {

            final int tableModelRowCount = tableModel.getRowCount();
            final Row[] newModel = new Row[tableModelRowCount];

            final Row.RowState state = new Row.RowState(tableModel, sortingColumns);

            for (int row = 0; row < tableModelRowCount; row++) {
                newModel[row] = new Row(state, row);
            }

            if (isSorting()) {
                final long start = System.nanoTime();

                Arrays.sort(newModel);

                if (_logger.isDebugEnabled()) {
                    _logger.debug("sort ({} rows) processed in {} ms.", tableModelRowCount, 1e-6d * (System.nanoTime() - start));
                }
            }

            // update model once:
            viewToModel = newModel;
        }

        return viewToModel;
    }

    /**
     * @param viewIndex row index in the view
     * @return row index in the source model
     */
    public int modelIndex(final int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            final Row[] viewModel = getViewToModel();
            final int len = viewModel.length;

            modelToView = new int[len];

            for (int i = 0; i < len; i++) {
                modelToView[viewModel[i].modelIndex] = i;
            }
        }

        return modelToView;
    }

    /**
     * @param modelIndex row index in the source model
     * @return row index in the view
     */
    public int viewIndex(final int modelIndex) {
        return getModelToView()[modelIndex];
    }

    /**
     * @param column column index in the view
     * @return model column index in the source model
     */
    public int columnModelIndex(final int column) {
        return _viewIndex[column];
    }

    /**
     * Find the column index in the view
     * @param modelColumn model column index
     * @return view index of this column or -1 if not found
     */
    public int findColumnViewIndex(final int modelColumn) {
        if (modelColumn != -1) {
            for (int i = 0, len = _viewIndex.length; i < len; i++) {
                if (_viewIndex[i] == modelColumn) {
                    return i;
                }
            }
        }
        return -1;
    }

    // TableModel interface methods
    @Override
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        int nbOfColumns = 0;

        if (tableModel != null) {
            // If the table is empty, should show NO columns at all.
            nbOfColumns = Math.min(tableModel.getColumnCount(), _viewIndex.length);
        }

        return nbOfColumns;
    }

    @Override
    public String getColumnName(final int column) {
        return tableModel.getColumnName(columnModelIndex(column));
    }

    @Override
    public Class<?> getColumnClass(final int column) {
        return tableModel.getColumnClass(columnModelIndex(column));
    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return tableModel.isCellEditable(modelIndex(row), columnModelIndex(column));
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        return tableModel.getValueAt(modelIndex(row), columnModelIndex(column));
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        tableModel.setValueAt(aValue, modelIndex(row), columnModelIndex(column));
    }

    /**
     * @return visible column names (ordered)
     */
    public List<String> getVisibleColumnNames() {
        return visibleColumnNames;
    }

    /**
     * Defines the visible column names (ordered)
     * @param visibleColumnNames list of column names
     */
    public void setVisibleColumnNames(final List<String> visibleColumnNames) {
        this.visibleColumnNames = visibleColumnNames;

        computeColumnsIndirectionArray();

        fireTableStructureChanged();
    }

    void handleDraggedColumnMoved(final TableColumnModel columnModel) {
        // ColumnModel corresponds to the JTable view (over its view model):
        final int len = columnModel.getColumnCount();
        final List<String> viewColumnNames = new ArrayList<String>(len);

        for (int i = 0; i < len; i++) {
            final TableColumn tc = columnModel.getColumn(i);
            viewColumnNames.add(tc.getIdentifier().toString());
        }
        _logger.debug("handleDraggedColumnMoved: columns: {}", viewColumnNames);

        setVisibleColumnNames(viewColumnNames);

        // invoke to the optional listener:
        if (tableColumnMovedListener != null) {
            tableColumnMovedListener.tableColumnMoved(this);
        }
    }

    /**
     * @return optional table column moved listener
     */
    public BasicTableColumnMovedListener getTableHeaderChangeListener() {
        return tableColumnMovedListener;
    }

    /**
     * Defines the optional table column moved listener
     * @param tableHeaderChangeListener optional table column moved listener
     */
    public void setTableHeaderChangeListener(final BasicTableColumnMovedListener tableHeaderChangeListener) {
        this.tableColumnMovedListener = tableHeaderChangeListener;
    }

    /**
     * Automatically called whenever the observed model changed
     */
    void computeColumnsIndirectionArray() {
        // Get column count in the model:
        final int nbOfModelColumns = tableModel.getColumnCount();

        // Either simple or detailed views
        if (visibleColumnNames == null) {
            // Full view, with all columns

            // allocate corresponding memory for the indirection array
            _viewIndex = new int[nbOfModelColumns];

            // Generate a 'one to one' indirection array to show every single column
            for (int i = 0; i < nbOfModelColumns; i++) {
                _viewIndex[i] = i;
            }
        } else if (nbOfModelColumns == 0 || visibleColumnNames.isEmpty()) {
            // empty view
            _viewIndex = new int[0];
        } else {
            _logger.debug("Columns = {}", visibleColumnNames);

            // Get the selected ordered column name table
            final int nbOfColumns = visibleColumnNames.size();

            // Use list to keep only valid columns:
            final List<Integer> viewIndex = new ArrayList<Integer>(nbOfColumns);

            for (String columnName : visibleColumnNames) {
                if (!StringUtils.isEmpty(columnName)) {
                    // Get the current column index given its name:
                    final int columnId = findModelColumnByName(columnName);

                    // If no column Id was found for the given column name
                    if (columnId == -1) {
                        if (_ignoreMissingColumns.add(columnName)) {
                            _logger.debug("No column named '{}'.", columnName);
                        }
                    } else {
                        viewIndex.add(NumberUtils.valueOf(columnId));
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("viewIndex[{}] = '{}' -> '{}'.", (viewIndex.size() - 1), columnId, columnName);
                        }
                    }
                }
            }

            // Create a new array of this with the right size
            final int rightSize = viewIndex.size();
            _viewIndex = new int[rightSize];

            // Copy back all the meaningfull result in the rightly sized array
            for (int i = 0; i < rightSize; i++) {
                _viewIndex[i] = viewIndex.get(i).intValue();
            }
        }
    }

    // Helper classes
    private final class TableModelHandler implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {

            computeColumnsIndirectionArray();

            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);

                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                // 2017: maintain sorting enabled when the user enable/disable filters or dynamic columns are updated (vis2 / dist)
                checkSortingState();
                fireTableChanged(e);

                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();

            if ((e.getFirstRow() == e.getLastRow())
                    && (column != TableModelEvent.ALL_COLUMNS)
                    && (getSortingStatus(column) == NOT_SORTED)
                    && (modelToView != null)) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(BasicTableSorter.this,
                        viewIndex, viewIndex, column, e.getType()));

                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();
        }
    }

    private final class MouseHandler extends MouseAdapter {

        private final DragColumnHandler dragHandler;

        MouseHandler(DragColumnHandler dragHandler) {
            this.dragHandler = dragHandler;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            final JTableHeader h = (JTableHeader) e.getSource();
            final TableColumnModel columnModel = h.getColumnModel();
            final int viewColumn = columnModel.getColumnIndexAtX(e.getX());

            if (viewColumn != -1) {
                final int column = columnModel.getColumn(viewColumn).getModelIndex();

                if (column != -1) {
                    int status = getSortingStatus(column);

                    if (!e.isControlDown()) {
                        cancelSorting();
                    }

                    // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                    // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                    status += (e.isShiftDown() ? (-1) : 1);
                    status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                    setSortingStatus(column, status);
                }
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            // enable column dragging monitoring:
            this.dragHandler.setEnabled(true);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            // handle potential event:
            this.dragHandler.handleChanged();
            // disable column dragging monitoring:
            this.dragHandler.setEnabled(false);
        }
    }

    private final class SortableHeaderRenderer implements TableCellRenderer {

        /* members */
        /** parent table cell header renderer */
        final TableCellRenderer tableCellRenderer;
        /** optional header customizer */
        private BasicTableColumnModel tableHeaderCustomizer = null;
        /** text icon gap scaled */
        private final int textSpacing = SwingUtils.adjustUISizeCeil(4);

        /**
         * Protected constructor
         * @param tableCellRenderer parent  table cell header renderer
         * @param tableHeaderCustomizer
         */
        SortableHeaderRenderer(final TableCellRenderer tableCellRenderer, final BasicTableColumnModel tableHeaderCustomizer) {
            this.tableCellRenderer = tableCellRenderer;
            this.tableHeaderCustomizer = tableHeaderCustomizer;
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                       final boolean isSelected, final boolean hasFocus,
                                                       final int row, final int column) {

            final Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                final JLabel jLabel = (JLabel) c;
                jLabel.setHorizontalTextPosition(JLabel.LEFT);
                jLabel.setHorizontalAlignment(JLabel.CENTER);
                jLabel.setIconTextGap(textSpacing);

                final int colIndex = table.convertColumnIndexToModel(column);

                if (colIndex != -1) {
                    jLabel.setIcon(getHeaderRendererIcon(colIndex, table.getRowHeight()));

                    if (tableHeaderCustomizer != null) {
                        // Set the column header tooltip (with unit if any)
                        final int modelColumn = columnModelIndex(colIndex);

                        // use the column header customizer to get such information
                        String label = tableHeaderCustomizer.getColumnLabel(modelColumn);
                        if (!StringUtils.isEmpty(label)) {
                            jLabel.setText(label);
                        }
                        String tooltip = tableHeaderCustomizer.getColumnTooltipText(modelColumn);
                        if (!StringUtils.isEmpty(tooltip)) {
                            jLabel.setToolTipText(tooltip);
                        }
                    }
                }
            }

            // Return the component
            return c;
        }
    }

    private final class DragColumnHandler implements TableColumnModelListener {

        /* members */
        private final TableColumnModel columnModel;
        private int lastFrom;
        private int lastTo;
        private boolean enabled = false;
        private boolean changed = false;

        DragColumnHandler(final TableColumnModel columnModel) {
            this.columnModel = columnModel;
        }

        private void reset() {
            lastFrom = -1;
            lastTo = -1;
            changed = false;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (_logger.isDebugEnabled()) {
                _logger.debug("setEnabled: {}", enabled);
            }
            if (!enabled) {
                reset();
            }
        }

        private void setChanged(boolean changed) {
            this.changed = changed;
        }

        public void handleChanged() {
            if (_logger.isDebugEnabled()) {
                _logger.debug("handleChanged: {}", changed);
            }
            if (changed) {
                changed = false;
                handleDraggedColumnMoved(columnModel);
            }
        }

        private void verifyChange(int from, int to) {
            // ignore repeated events:
            if ((from != to) && ((from != lastFrom) || (to != lastTo))) {
                lastFrom = from;
                lastTo = to;

                // mark changed state:
                setChanged(true);
            }
        }

        @Override
        public void columnMoved(final TableColumnModelEvent e) {
            if (enabled) {
                verifyChange(e.getFromIndex(), e.getToIndex());
            }
        }

        @Override
        public void columnAdded(TableColumnModelEvent e) {
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
        }
    }

}
