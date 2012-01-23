/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.awt.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.ImageIcon;

/**
 * Several Image utility methods
 * 
 * @author Sylvain LAFRASSE.
 */
public final class ImageUtils {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class.getName());

    /**
     * Forbidden constructor
     */
    private ImageUtils() {
        // no-op
    }

    /**
     * Scales a given image to given maximum width and height.
     *
     * @param imageIcon the image to scale
     * @param maxHeight the maximum height of the scaled image, or automatic proportional scaling if less than or equal to 0
     * @param maxWidth the maximum width of the scaled image, or automatic proportional scaling if less than or equal to 0
     *
     * @return the scaled image
     */
    public static ImageIcon getScaledImageIcon(final ImageIcon imageIcon, int maxHeight, int maxWidth) {

        // Give up if params messed up
        if ((maxHeight == 0) && (maxWidth == 0)) {
            return imageIcon;
        }

        final int iconHeight = imageIcon.getIconHeight();
        final int iconWidth = imageIcon.getIconWidth();

        // If no resizing required
        if ((maxHeight == iconHeight) && (maxWidth == iconWidth)) {
            // Return early
            return imageIcon;
        }

        int newHeight = iconHeight;
        int newWidth = iconWidth;

        if (maxHeight > 0) {
            newHeight = Math.min(iconHeight, maxHeight);
            newWidth = (int) Math.floor((double) iconWidth * ((double) newHeight / (double) iconHeight));
        } else if (maxWidth > 0) {
            newWidth = Math.min(iconWidth, maxWidth);
            newHeight = (int) Math.floor((double) iconHeight * ((double) newWidth / (double) iconWidth));
        }

        final Image image = imageIcon.getImage();
        final Image scaledImage = image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
