/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.logging.Level;

/**
 * This class contains several utility methods to produce Image objects from raw data
 * 
 * @author Laurent BOURGES.
 */
public final class ImageUtils
{
    /** Class logger */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ImageUtils.class.getName());
    /** alpha integer mask */
    private final static int ALPHA_MASK = 0xff << 24;
    /** flag to use RGB color interpolation */
    public final static boolean USE_RGB_INTERPOLATION = true;

    /**
     * Forbidden constructor
     */
    private ImageUtils()
    {
        // no-op
    }

    /**
     * Return the value to pixel coefficient
     * @param min data min value
     * @param max data max value
     * @param iMaxColor maximum number of colors to use
     * @return value to pixel coefficient
     */
    public static float computeScalingFactor(final float min, final float max, final int iMaxColor)
    {
        final int iMin = 0;
        final int iMax = iMaxColor - 1;

        float c = (iMax - iMin) / (max - min);

        if (c == 0f) {
            c = 1f;
        }

        return c;
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
            final float[] array, final float min, final float max,
            final IndexColorModel colorModel)
    {

        final float scalingFactor = ImageUtils.computeScalingFactor(min, max, colorModel.getMapSize());

        return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array
     * @param min lower data value (lower threshold)
     * @param colorModel color model
     * @param scalingFactor value to pixel coefficient
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
            final float[] array, final float min,
            final IndexColorModel colorModel, final float scalingFactor)
    {

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("createImage: using array of size  " + width + "x" + height
                    + " array : nb of point is " + array.length);
        }

        // Define image min and image max values. And set coefficient to normalize image values
        final int iMin = 0;
        final int iMaxColor = colorModel.getMapSize() - 1;
        final float c = scalingFactor;

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Image: dims=" + width + "x" + height + ", c=" + c + ", array[0]=" + array[0]);
        }

        if (USE_RGB_INTERPOLATION) {
            final ColorModel rgbColorModel = ColorModel.getRGBdefault();

            final WritableRaster imageRaster = rgbColorModel.createCompatibleWritableRaster(width, height);

            final DataBuffer dataBuffer = imageRaster.getDataBuffer();

            // init raster pixels
            for (int i = 0, size = array.length; i < size; i++) {
                dataBuffer.setElem(i, getRGB(colorModel, iMaxColor, (array[i] - min) * c, ALPHA_MASK));
            }
            return new BufferedImage(rgbColorModel, imageRaster, false, null);

        } else {
            final WritableRaster imageRaster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, width, height, new int[]{0xFF},
                    new Point(0, 0));

            final DataBuffer dataBuffer = imageRaster.getDataBuffer();

            // init raster pixels
            for (int i = 0, v = 0, size = array.length; i < size; i++) {
                v = Math.round((array[i] - min) * c);

                if (v < iMin) {
                    v = iMin;
                } else if (v > iMaxColor) {
                    v = iMaxColor;
                }

                dataBuffer.setElem(i, v);
            }
            return new BufferedImage(colorModel, imageRaster, false, null);
        }
    }
    
    /**
     * Return an RGB color (32bits) using the given color model for the given value (linear scale)
     * @param colorModel color model
     * @param iMaxColor index of the highest color
     * @param value data value to convert
     * @param alphaMask alpha mask (0 - 255) << 24
     * @return RGB color
     */
    public static int getRGB(final IndexColorModel colorModel, final int iMaxColor, final float value, final int alphaMask)
    {

        int minColorIdx = (int) Math.floor(value);

        final float ratio = value - minColorIdx;

        if (minColorIdx < 0) {
            minColorIdx = 0;
        }
        if (minColorIdx > iMaxColor) {
            minColorIdx = iMaxColor;
        }

        int maxColorIdx = minColorIdx + 1;

        if (maxColorIdx > iMaxColor) {
            maxColorIdx = iMaxColor;
        }

        final int minColor = colorModel.getRGB(minColorIdx);
        final int maxColor = colorModel.getRGB(maxColorIdx);

        final int ra = minColor >> 16 & 0xff;
        final int ga = minColor >> 8 & 0xff;
        final int ba = minColor & 0xff;

        final int rb = maxColor >> 16 & 0xff;
        final int gb = maxColor >> 8 & 0xff;
        final int bb = maxColor & 0xff;

        // linear interpolation for color :
        final int r = Math.round(ra + (rb - ra) * ratio);
        final int g = Math.round(ga + (gb - ga) * ratio);
        final int b = Math.round(ba + (bb - ba) * ratio);

        return alphaMask | (r << 16) | (g << 8) | b;
    }
}
