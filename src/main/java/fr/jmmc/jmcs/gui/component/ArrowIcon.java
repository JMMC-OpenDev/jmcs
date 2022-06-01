/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Arrow as Icon
 * @author bourgesl
 */
public final class ArrowIcon implements Icon {

    private final boolean descending;
    private final int size;
    private final int priority;

    public ArrowIcon(final boolean descending, final int size, final int priority) {
        this.descending = descending;
        this.size = size;
        this.priority = priority;
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, int y) {
        final Color color = (c == null) ? Color.red : c.getBackground();

        // In a compound sort, make each succesive triangle 20%
        // smaller than the previous one.
        final int dx = (int) (size / 2d * Math.pow(0.8d, priority));
        final int dy = descending ? dx : (-dx);

        // Align icon (roughly) with font baseline.
        final int bl = y + ((5 * size) / 6) + (descending ? (-dy) : 0);

        final int shift = descending ? 1 : (-1);
        g.translate(x, bl);

        // Right diagonal.
        g.setColor(color.darker());
        g.drawLine(dx / 2, dy, 0, 0);
        g.drawLine(dx / 2, dy + shift, 0, shift);

        // Left diagonal.
        g.setColor(color.brighter());
        g.drawLine(dx / 2, dy, dx, 0);
        g.drawLine(dx / 2, dy + shift, dx, shift);

        // Horizontal line.
        if (descending) {
            g.setColor(color.darker().darker());
        } else {
            g.setColor(color.brighter().brighter());
        }

        g.drawLine(dx, 0, 0, 0);

        g.setColor(color);
        g.translate(-x, -bl);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
