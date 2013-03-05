/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Component adapter to force a resize-able component to have a minimal dimension.
 * @author Laurent BOURGES.
 */
public final class ComponentResizeAdapter extends ComponentAdapter {

    /** minimal dimension to respect */
    private final Dimension _dimension;

    /**
     * Constructor with a given minimal dimension
     * @param dim minimal dimension
     */
    public ComponentResizeAdapter(final Dimension dim) {
        this._dimension = dim;
    }

    /**
     * Invoked when the component's size changes.
     * This override method checks that the new size is greater than the minimal dimension.
     * @param e event to process
     */
    @Override
    public void componentResized(final ComponentEvent e) {
        final Component c = e.getComponent();
        final Dimension d = c.getSize();
        final int w = d.width;
        final int h = d.height;

        final int nw = (w < _dimension.width) ? _dimension.width : w;
        final int nh = (h < _dimension.height) ? _dimension.height : h;

        if (nw != w || nh != h) {
            c.setSize(nw, nh);
        }
    }
}
