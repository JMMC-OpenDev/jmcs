/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelParameterTableModel.java,v 1.6 2010-02-19 16:02:52 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2010/02/18 09:59:37  bourgesl
 * new ModelDefinition interface to gather model and parameter types
 *
 * Revision 1.4  2010/02/17 17:05:45  bourgesl
 * first model X/Y parameters are not editable
 *
 * Revision 1.3  2010/02/17 15:10:12  bourgesl
 * added model name + shared parameter flag as columns
 *
 * Revision 1.2  2010/02/15 16:45:47  bourgesl
 * added getModelAt(rowIndex) method
 *
 * Revision 1.1  2010/02/12 15:52:32  bourgesl
 * added cloneable support for target model classes
 * added parameter table model
 *
 */
package fr.jmmc.mcs.model.gui;

import fr.jmmc.mcs.model.ModelDefinition;
import fr.jmmc.mcs.model.targetmodel.Model;
import fr.jmmc.mcs.model.targetmodel.Parameter;
import fr.jmmc.mcs.model.targetmodel.ParameterLink;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.table.AbstractTableModel;

/**
 * This class is a specific table model (JTable) to display and edit the model parameters
 * @author bourgesl
 */
public final class ModelParameterTableModel extends AbstractTableModel {

  /** default serial UID for Serializable interface */
  private static final long serialVersionUID = 1;
  /** Class Name */
  private static final String className_ = "fr.jmmc.mcs.model.gui.ModelParameterTableModel";
  /** Class logger */
  private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          className_);

  /** Table mode enumeration (LITpro or ASPRO) */
  public enum Mode {

    LITPRO, ASPRO;
  }

  /** Table edit mode enumeration (X_Y or RHO_THETA) */
  public enum EditMode {

    X_Y, RHO_THETA;
  }

  /** Column definition enum */
  public enum ColumnDef {

    MODEL("Model", String.class, false),
    NAME("Name", String.class, false),
    TYPE("Type", String.class, false),
    SHARED("Shared", Boolean.class, false),
    UNITS("Units", String.class, false),
    VALUE("Value", Double.class, true),
    MIN_VALUE("MinValue", Double.class, true),
    MAX_VALUE("MaxValue", Double.class, true),
    SCALE("Scale", Double.class, true),
    FIXED_VALUE("HasFixedValue", Boolean.class, true);

    /**
     * Custom constructor
     * @param name name of the column
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

    public String getName() {
      return name;
    }

    public Class<?> getType() {
      return type;
    }

    public boolean isEditable() {
      return editable;
    }

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
  /** list of parameters (row) present in the table */
  private final List<Editable> parameterList = new ArrayList<Editable>();

  /**
   * Public constructor
   * @param mode table mode (LITpro or ASPRO)
   */
  public ModelParameterTableModel(final Mode mode) {
    super();
    this.initColumns(mode);
  }

  /**
   * Define the table columns according to the given mode
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
   * @param model target model
   * @param editMode edition mode
   */
  public void setData(final Model model, final EditMode editMode) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("setData[" + editMode + "] : " + model);
    }
    setEditMode(editMode);
    resetData();
    processData(model);

    // fire the table data changed event :
    fireTableDataChanged();
  }

  /**
   * Define the data to use in this table model for a list of target models
   * @param models list of target models
   * @param editMode edition mode
   */
  public void setData(final List<Model> models, final EditMode editMode) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("setData[" + editMode + "] : " + models);
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
    this.parameterList.clear();
  }

  /**
   * Fill the table data members recursively with the given model
   * @param model model to process
   */
  private void processData(final Model model) {

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
   * Returns the name of the column at <code>columnIndex</code>.  This is used
   * to initialize the table's column header name.  Note: this name does
   * not need to be unique; two columns in a table can have the same name.
   *
   * @param	columnIndex	the index of the column
   * @return  the name of the column
   */
  @Override
  public String getColumnName(final int columnIndex) {
    return this.columnDefs[columnIndex].getName();
  }

  /**
   * Returns the most specific superclass for all the cell values
   * in the column.  This is used by the <code>JTable</code> to set up a
   * default renderer and editor for the column.
   *
   * @param columnIndex  the index of the column
   * @return the common ancestor class of the object values in the model.
   */
  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return this.columnDefs[columnIndex].getType();
  }

  /**
   * Returns true if the cell at <code>rowIndex</code> and
   * <code>columnIndex</code>
   * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
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

      // First Model Rules :
      if (getModelAt(rowIndex) == this.getModelAt(0)) {

        final Editable parameter = this.parameterList.get(rowIndex);

        // if the parameter a position (X/Y or RHO/THETA) => not editable :
        editable = !parameter.isPosition();

        // Custom position parameter = only value is editable :
        if (editable && parameter instanceof EditableRhoThetaParameter) {
          editable = (this.columnDefs[columnIndex] == ColumnDef.VALUE);
        }
      }
    }
    return editable;
  }

  /**
   * Returns the number of rows in the model. A
   * <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it
   * is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return this.parameterList.size();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param	rowIndex	the row whose value is to be queried
   * @param	columnIndex 	the column whose value is to be queried
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
      }
    }

    return null;
  }

  /**
   * Sets the value in the cell at <code>columnIndex</code> and
   * <code>rowIndex</code> to <code>aValue</code>.
   *
   * @param	aValue		 the new value
   * @param	rowIndex	 the row whose value is to be changed
   * @param	columnIndex 	 the column whose value is to be changed
   * @see #getValueAt
   * @see #isCellEditable
   */
  @Override
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
    final Editable parameter = this.parameterList.get(rowIndex);

    if (parameter != null) {
      if (logger.isLoggable(Level.FINE)) {
        logger.fine("parameter " + parameter.getName() + " old:" + getValueAt(rowIndex, columnIndex) + " new:" + aValue + ((aValue != null) ? " (" + aValue.getClass() + ")" : ""));
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
   * Return the model corresponding to the row at <code>rowIndex</code>
   * @param	rowIndex	the row whose value is to be queried
   * @return model
   */
  public Model getModelAt(final int rowIndex) {
    return this.parameterList.get(rowIndex).getModel();
  }

  /**
   * Return the edit mode
   * @return edit mode
   */
  public EditMode getEditMode() {
    return editMode;
  }

  /**
   * Define the edit mode (X_Y or RHO_THETA)
   * @param editMode edit mode
   */
  private void setEditMode(final EditMode editMode) {
    this.editMode = editMode;
  }
}
