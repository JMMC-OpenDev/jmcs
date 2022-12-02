/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        // TODO: use icon cache ?

        // In a compound sort, make each succesive triangle 15%
        // smaller than the previous one.
        final int dx = (int) Math.round((size / 2.0) * Math.pow(0.85, priority));
        final int dy = (descending) ? dx : (-dx);

        // Align icon (roughly) with font baseline.
        final int bl = (int) Math.round((size * 3) / 4.0) + ((dy > 0) ? (-dy) : 0);

        final int shift = (dy >= 0) ? 1 : (-1);

        final Graphics2D g2d = (Graphics2D) g;

        g2d.translate(x, (y + bl));

        final Object savedAAHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Color savedColor = g2d.getColor();

        final Color color = (c == null) ? Color.red : c.getBackground();
        final Color colorDarker = color.darker();
        final Color colorDarker2 = colorDarker.darker();
        final Color colorBrighter = color.brighter();

        // force antialiasing Off:
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int[] xPoints = new int[]{0, dx / 2, dx};
        final int[] yPoints = new int[]{0, dy, 0};

        g2d.setColor(colorDarker);
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        // force antialiasing On:
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Right diagonal:
        g2d.setColor(colorDarker2);
        g2d.drawLine(dx / 2, dy, 0, 0);
        g2d.drawLine(dx / 2, dy + shift, 0, shift);

        // Left diagonal:
        g2d.setColor(colorBrighter);
        g2d.drawLine(dx / 2, dy, dx, 0);
        g2d.drawLine(dx / 2, dy + shift, dx, shift);

        // Horizontal line.
        if (descending) {
            g2d.setColor(colorDarker2.darker());
        } else {
            g2d.setColor(colorBrighter.brighter());
        }
        g2d.drawLine(dx, 0, 0, 0);

        // restore g2d state:
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedAAHint);
        g2d.setColor(savedColor);
        g2d.translate(-x, -(y + bl));
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
