/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

/**
 * This interface provides custom label & tooltips on column header
 */
public interface BasicTableColumnModel {

    /**
     * Return an optional label for the given column index (model)
     *
     * To be overriden, by default returns null
     *
     * @param	columnIndex	the index of the column (model)
     * @return the label of the column
     */
    public String getColumnLabel(final int columnIndex);

    /**
     * Return an optional tooltip text for the given column index (model)
     *
     * To be overriden, by default returns null
     *
     * @param	columnIndex	the index of the column (model)
     * @return the label of the column
     */
    public String getColumnTooltipText(final int columnIndex);

}
