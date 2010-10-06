/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ImageUtils.java,v 1.3 2010-02-09 16:50:07 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/02/03 09:30:31  bourgesl
 * better float value to color mapping
 *
 * Revision 1.1  2010/01/29 15:50:07  bourgesl
 * Simple Image utilities to create an image from a float data array and use simple color models (lut)
 *
 */
package fr.jmmc.mcs.image;

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
 * @author bourgesl
 */
public class ImageUtils {

  /** Class Name */
  private static final String className_ = "fr.jmmc.mcs.image.ImageUtils";
  /** Class logger */
  protected static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          className_);
          /** alpha integer mask */
  private final static int ALPHA_MASK = 0xff << 24;
  /** flag to use RGB color interpolation */
  public final static boolean USE_RGB_INTERPOLATION = true;


  /**
   * Forbidden constructor
   */
  private ImageUtils() {
    // no-op
  }

  /**
   * Return the float value to pixel coefficient
   * @param min data min value
   * @param max data max value
   * @param iMaxColor maximum number of colors to use
   * @return float value to pixel coefficient
   */
  public static float computeScalingFactor(final float min, final float max, final int iMaxColor) {
    final int iMin = 0;
    final int iMax = iMaxColor - 1;

    float c = (iMax - iMin) / (max - min);

    if (c == 0) {
      c = 1;
    }

    return c;
  }

  /*
   * Create an Image from the given data array using the specified Color Model
   *
   * @param width image width
   * @param height image height
   * @param array data array
   * @param min lower data value (lower threshold)
   * @param max upper data value (upper threshold)
   * @param colorModel color model
   * @param iMaxColor maximum number of colors
   * @return new BufferedImage
   */
  public static BufferedImage createImage(final int width, final int height,
          final float[] array, final float min, final float max,
          final IndexColorModel colorModel) {

    final float scalingFactor = ImageUtils.computeScalingFactor(min, max, colorModel.getMapSize());

    return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor);
  }

  /*
   * Create an Image from the given data array using the specified Color Model
   *
   * @param width image width
   * @param height image height
   * @param array data array
   * @param min lower data value (lower threshold)
   * @param colorModel color model
   * @param iMaxColor maximum number of colors
   * @param scalingFactor float value to pixel coefficient
   * @return new BufferedImage
   */
  public static BufferedImage createImage(final int width, final int height,
          final float[] array, final float min,
          final IndexColorModel colorModel, final float scalingFactor) {

    if (logger.isLoggable(Level.FINEST)) {
      logger.finest("createImage: using array of size  " + width + "x" + height +
              " array : nb of point is " + array.length);
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
        dataBuffer.setElem(i, getRGB(colorModel, iMaxColor, (array[i] - min) * c));
      }
      return new BufferedImage(rgbColorModel, imageRaster, false, null);
      
    } else {
      final WritableRaster imageRaster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, width, height, new int[]{0xFF},
              new Point(0, 0));

      final DataBuffer dataBuffer = imageRaster.getDataBuffer();

      // init raster pixels
      for (int i = 0, v = 0, size = array.length; i < size; i++) {
        v = Math.round(((array[i] - min) * c));

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

  private static final int getRGB(final IndexColorModel colorModel, final int iMaxColor, final float value) {

      int minColorIdx = (int)Math.floor(value);

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

      return ALPHA_MASK | (r << 16) | (g << 8) | b;
  }
}
