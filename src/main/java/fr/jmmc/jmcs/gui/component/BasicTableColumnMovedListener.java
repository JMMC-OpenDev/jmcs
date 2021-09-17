/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

/**
 * BasicTableColumnMovedListener defines the interface for an object that listens
 to changes on JTable column reordering.
 */
public interface BasicTableColumnMovedListener {

    /**
     * This method is called when the table columns are changed due to JTable column reordering.
     * @param source the BasicTableSorter sending this event
     */
    public void tableColumnMoved(final BasicTableSorter source);

}
