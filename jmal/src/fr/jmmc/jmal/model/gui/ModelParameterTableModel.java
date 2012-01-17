/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.gui;

import fr.jmmc.jmal.model.ModelDefinition;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.model.targetmodel.Parameter;
import fr.jmmc.jmal.model.targetmodel.ParameterLink;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a specific table model (JTable) to display and edit the model parameters
 *
 * @author Laurent BOURGES, Guillaume MELLA.
 */
public final class ModelParameterTableModel extends AbstractTableModel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ModelParameterTableModel.class.getName());

    /** Table mode enumeration (LITpro or ASPRO) */
    public enum Mode {

        /** LITPRO data model */
        LITPRO,
        /** ASPRO data model (less fields) */
        ASPRO;
    }

    /** Table edit mode enumeration (X_Y or RHO_THETA) */
    public enum EditMode {

        /** coordinates in carthesian (x/y) format */
        X_Y,
        /** coordinates in polar (rho/theta) format */
        RHO_THETA;
    }

    /** Column definition enum */
    public enum ColumnDef {

        /** model name */
        MODEL("Model", String.class, false),
        /** parameter name */
        NAME("Name", String.class, false),
        /** parameter type */
        TYPE("Type", String.class, false),
        /** shared parameter flag */
        SHARED("Shared", Boolean.class, false),
        /** parameter units */
        UNITS("Units", String.class, false),
        /** parameter value */
        VALUE("Value", Double.class, true),
        /** parameter minimum value */
        MIN_VALUE("MinValue", Double.class, true),
        /** parameter maximum value */
        MAX_VALUE("MaxValue", Double.class, true),
        /** parameter scale value */
        SCALE("Scale", Double.class, true),
        /** fixed parameter value flag */
        FIXED_VALUE("HasFixedValue", Boolean.class, true);

        /**
         * Custom constructor
         *
         * @param name name of the column
         * @param type class type of the column value
         * @param editable flag to indicate if column values are editable
         */
        private ColumnDef(final String name, final Class<?> type, final boolean editable) {
            this.name = name;
            this.type = type;
            this.editable = editable;
        }
        /** column name */
        private final String name;
        /** class type */
        private final Class<?> type;
        /** editable flag */
        private final boolean editable;

        /**
         * Return the name of the column
         *
         * @return name of the column
         */
        public String getName() {
            return name;
        }

        /**
         * Return the class type of the column value
         *
         * @return class type of the column value
         */
        public Class<?> getType() {
            return type;
        }

        /**
         * Return the flag to indicate if column values are editable
         *
         * @return flag to indicate if column values are editable
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         * Return the name of the column
         *
         * @return name of the column
         */
        @Override
        public String toString() {
            return name;
        }
    }
    /** LITpro Columns (all) */
    private static final ColumnDef[] LITPRO_COLUMNS = ColumnDef.values();
    /** ASPRO Columns */
    private static final ColumnDef[] ASPRO_COLUMNS = {ColumnDef.MODEL, ColumnDef.NAME, ColumnDef.UNITS, ColumnDef.VALUE};

    /* members */
    /** edit mode */
    private EditMode editMode = EditMode.X_Y;
    /** column count */
    private int columnCount;
    /** column definitions */
    private ColumnDef[] columnDefs;
    /** first model reference to handle special behaviour */
    private Model firstModel = null;
    /** list of parameters (row) present in the table */
    private final List<Editable> parameterList = new ArrayList<Editable>();

    /**
     * Public constructor
     *
     * @param mode table mode (LITpro or ASPRO)
     */
    public ModelParameterTableModel(final Mode mode) {
        super();
        this.initColumns(mode);
    }

    /**
     * Define the table columns according to the given mode
     *
     * @param mode table mode (LITpro or ASPRO)
     */
    private void initColumns(final Mode mode) {
        switch (mode) {
            case LITPRO:
                this.columnDefs = LITPRO_COLUMNS;
                this.columnCount = this.columnDefs.length;
                break;
            case ASPRO:
                this.columnDefs = ASPRO_COLUMNS;
                this.columnCount = this.columnDefs.length;
                break;
            default:
                this.columnCount = 0;
        }
    }

    /**
     * Define the data to use in this table model for a single target model
     *
     * @param model target model
     * @param editMode edition mode
     */
    public void setData(final Model model, final EditMode editMode) {
        if (logger.isDebugEnabled()) {
            logger.debug("setData[{}]: {}", editMode, model);
        }
        setEditMode(editMode);
        resetData();
        processData(model);

        // fire the table data changed event :
        fireTableDataChanged();
    }

    /**
     * Define the data to use in this table model for a list of target models
     *
     * @param models list of target models
     * @param editMode edition mode
     */
    public void setData(final List<Model> models, final EditMode editMode) {
        if (logger.isDebugEnabled()) {
            logger.debug("setData[{}]: {}", editMode, models);
        }
        setEditMode(editMode);
        resetData();
        if (models != null) {
            for (Model model : models) {
                processData(model);
            }
        }

        // fire the table data changed event :
        fireTableDataChanged();
    }

    /**
     * Reset the table data members
     */
    private void resetData() {
        this.firstModel = null;
        this.parameterList.clear();
    }

    /**
     * Fill the table data members recursively with the given model
     *
     * @param model model to process
     */
    private void processData(final Model model) {
        // define the first model if undefined :
        if (this.firstModel == null) {
            this.firstModel = model;
        }

        // First add model parameters :
        final List<Parameter> parameters = model.getParameters();

        for (Parameter parameter : parameters) {
            addParameter(model, parameter, false);
        }

        // Second add model linked parameters :
        final List<ParameterLink> parameterLinks = model.getParameterLinks();

        for (ParameterLink parameterLink : parameterLinks) {
            addParameter(model, parameterLink.getParameterRef(), true);
        }

        // Finally traverse the model hierarchy :
        final List<Model> children = model.getModels();

        for (Model child : children) {
            processData(child);
        }
    }

    /**
     * Add the given parameter
     *
     * @param model the model containing the given parameter
     * @param parameter parameter to add
     * @param shared shared parameter flag
     */
    private void addParameter(final Model model, final Parameter parameter, final boolean shared) {
        switch (getEditMode()) {
            default:
            case X_Y:
                this.parameterList.add(new EditableParameter(model, parameter, shared));
                break;

            case RHO_THETA:

                // First Model Rules :

                // For the first model keep X/Y fixed parameters :

                if (model == this.firstModel) {
                    this.parameterList.add(new EditableParameter(model, parameter, shared));
                } else {

                    // Intercept and check for X/Y parameters :

                    if (ModelDefinition.PARAM_X.equals(parameter.getType())) {
                        this.parameterList.add(new EditableRhoThetaParameter(model, EditableRhoThetaParameter.Type.RHO));
                    } else if (ModelDefinition.PARAM_Y.equals(parameter.getType())) {
                        this.parameterList.add(new EditableRhoThetaParameter(model, EditableRhoThetaParameter.Type.THETA));
                    } else {
                        this.parameterList.add(new EditableParameter(model, parameter, shared));
                    }
                }
        }
    }

    /* TableModel interface implementation */
    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Returns the name of the column at
     * <code>columnIndex</code>. This is used
     * to initialize the table's column header name. Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of the column
     * @return the name of the column
     */
    @Override
    public String getColumnName(final int columnIndex) {
        return this.columnDefs[columnIndex].getName();
    }

    /**
     * Returns the most specific superclass for all the cell values
     * in the column. This is used by the
     * <code>JTable</code> to set up a
     * default renderer and editor for the column.
     *
     * @param columnIndex the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return this.columnDefs[columnIndex].getType();
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
        boolean editable = false;
        final boolean columnEditable = this.columnDefs[columnIndex].isEditable();
        if (columnEditable) {
            editable = true;

            // check if the row is editable ?

            final Editable parameter = this.parameterList.get(rowIndex);

            // First Model Rules :
            if (getModelAt(rowIndex) == this.firstModel) {

                // if the parameter is a position (X/Y or RHO/THETA) => not editable :
                editable = !parameter.isPosition();
            }

            // Custom position parameter = only value is editable :
            if (editable && parameter instanceof EditableRhoThetaParameter) {
                editable = (this.columnDefs[columnIndex] == ColumnDef.VALUE);
            }
        }
        return editable;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display. This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return this.parameterList.size();
    }

    /**
     * Returns the value for the cell at
     * <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Editable parameter = this.parameterList.get(rowIndex);

        if (parameter != null) {
            switch (this.columnDefs[columnIndex]) {
                case MODEL:
                    return this.getModelAt(rowIndex).getName();
                case NAME:
                    return parameter.getName();
                case SHARED:
                    return parameter.isShared();
                case TYPE:
                    return parameter.getType();
                case UNITS:
                    return parameter.getUnits();
                case VALUE:
                    return parameter.getValue();
                case MIN_VALUE:
                    return parameter.getMinValue();
                case MAX_VALUE:
                    return parameter.getMaxValue();
                case SCALE:
                    return parameter.getScale();
                case FIXED_VALUE:
                    return parameter.isHasFixedValue();
                default:
            }
        }

        return null;
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
        final Editable parameter = this.parameterList.get(rowIndex);

        if (parameter != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("setValueAt: parameter {} old: {} new: {} ({})",
                        new Object[]{parameter.getName(), getValueAt(rowIndex, columnIndex), aValue,
                                     ((aValue != null) ? aValue.getClass() : "")});
            }
            boolean modified = false;

            switch (this.columnDefs[columnIndex]) {
                case VALUE:
                    final double dValue = (aValue != null) ? ((Double) aValue).doubleValue() : 0D;
                    parameter.setValue(dValue);
                    modified = true;
                    break;
                case MIN_VALUE:
                    parameter.setMinValue((Double) aValue);
                    modified = true;
                    break;
                case MAX_VALUE:
                    parameter.setMaxValue((Double) aValue);
                    modified = true;
                    break;
                case SCALE:
                    parameter.setScale((Double) aValue);
                    modified = true;
                    break;
                case FIXED_VALUE:
                    final boolean bValue = (aValue != null) ? ((Boolean) aValue).booleanValue() : false;
                    parameter.setHasFixedValue(bValue);
                    modified = true;
                    break;
                default:
            }

            if (modified) {
                if (parameter instanceof EditableRhoThetaParameter) {
                    fireTableDataChanged();
                } else {
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
            }
        }
    }

    /* custom */
    /**
     * Return the model corresponding to the row at
     * <code>rowIndex</code>
     *
     * @param	rowIndex	the row whose value is to be queried
     * @return model
     */
    public Model getModelAt(final int rowIndex) {
        return this.parameterList.get(rowIndex).getModel();
    }

    /**
     * Return the edit mode
     *
     * @return edit mode
     */
    public EditMode getEditMode() {
        return editMode;
    }

    /**
     * Define the edit mode (X_Y or RHO_THETA)
     *
     * @param editMode edit mode
     */
    public void setEditMode(final EditMode editMode) {
        this.editMode = editMode;
    }
}
