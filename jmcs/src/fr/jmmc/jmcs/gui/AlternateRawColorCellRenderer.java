/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Colors the cell background alternatively in white then in light grey blue (as on Mac OS X).
 * 
 * @author Sylvain LAFRASSE.
 */
public class AlternateRawColorCellRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = 1L;
    /** light grey blue (as on Mac OS X) */
    static Color lightGreyBlue = new Color(240, 240, 250);

    /** Constructor */
    public AlternateRawColorCellRenderer() {
        setOpaque(true);
    }

    /**
     * Alternates background colors.
     * 
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Assumes the stuff in the list has a pretty toString
        setText(value.toString());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
        } else {
            // based on the index you set the color.  This produces the every other effect.
            if (index % 2 == 0) {
                setBackground(Color.WHITE);
            } else {
                setBackground(lightGreyBlue);
            }
        }

        return this;
    }
}
