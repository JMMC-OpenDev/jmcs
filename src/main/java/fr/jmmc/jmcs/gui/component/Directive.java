/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import java.util.Comparator;

/**
 * Sorting directive used by BasicTableSorter
 * @author bourgesl
 */
public final class Directive {

    public final int column;
    /* column name used to ensure consistency */
    public final String colName;
    public final int direction;
    public final Comparator<Object> comparator;

    public Directive(final int column, final int direction, final Comparator<Object> comparator, final String colName) {
        this.column = column;
        this.colName = colName;
        this.direction = direction;
        this.comparator = comparator;
    }

    @Override
    public String toString() {
        return "Directive{" + "column=" + column + ", colName=" + colName + ", direction=" + direction + '}';
    }
}
