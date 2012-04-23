/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.awt.Image;
import java.net.URL;
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
    private static final Logger _logger = LoggerFactory.getLogger(ImageUtils.class.getName());

    /**
     * Forbidden constructor
     */
    private ImageUtils() {
        // no-op
    }

    /**
     * Try to load the given resource path as ImageIcon.
     *
     * @param url the image icon resource path
     *
     * @return the retrieved image icon if found, null otherwise.
     */
    public static ImageIcon loadResourceIcon(final String url) {
        if (url == null) {
            return null;
        }
        final URL imageUrl = ImageUtils.class.getResource(url);
        if (imageUrl == null) {
            return null;
        }
        return new ImageIcon(imageUrl);
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
        }
        if (maxWidth > 0) {
            newWidth = Math.min(iconWidth, maxWidth);
            newHeight = (int) Math.floor((double) iconHeight * ((double) newWidth / (double) iconWidth));
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Scaling image from {} x {} to {} x {}.",
                    new Object[]{iconWidth, iconHeight, newWidth, newHeight});
        }

        final Image image = imageIcon.getImage();
        final Image scaledImage = image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
