/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: UVMapData.java,v 1.2 2011-02-04 16:41:33 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/02/04 14:43:36  bourgesl
 * added UVMapData bean to keep several data related to uv map in order to conserve the value / color mapping and uv area while zooming on uv map
 *
 */
package fr.jmmc.mcs.model;

import fr.jmmc.mcs.model.ModelUVMapService.ImageMode;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * This class contains the results of the ModelUVMap service
 * @author bourgesl
 */
public final class UVMapData {

  /** number of pixels for both width and height of the generated image */
  private final int imageSize;
  /** image mode (amplitude or phase) */
  private final ImageMode mode;
  /** image color model */
  private final IndexColorModel colorModel;
  /** uv frequency area */
  private final Rectangle.Double uvRect;
  /** visibility minimum value (amplitude or phase) */
  private final Float min;
  /** visibility maximum value (amplitude or phase) */
  private final Float max;
  /** uv map image */
  private final BufferedImage uvMap;

  /**
   * Constuctor
   * @param mode image mode (amplitude or phase)
   * @param imageSize number of pixels for both width and height of the generated image
   * @param colorModel color model to use
   * @param uvRect UV frequency area in rad-1
   * @param min visibility minimum value (amplitude or phase)
   * @param max visibility maximum value (amplitude or phase)
   * @param uvMap uv map image
   */
  public UVMapData(final ImageMode mode, final int imageSize, final IndexColorModel colorModel,
                   final Rectangle.Double uvRect, final Float min, final Float max,
                   final BufferedImage uvMap) {
    this.mode = mode;
    this.imageSize = imageSize;
    this.colorModel = colorModel;
    this.uvRect = uvRect;
    this.min = min;
    this.max = max;
    this.uvMap = uvMap;
  }

  /**
   * Return the number of pixels for both width and height of the generated image
   * @return number of pixels for both width and height of the generated image
   */
  public int getImageSize() {
    return imageSize;
  }

  /**
   * Return the image mode (amplitude or phase)
   * @return image mode (amplitude or phase)
   */
  public ImageMode getImageMode() {
    return mode;
  }

  /**
   * Return the image color model
   * @return image color model
   */
  public IndexColorModel getColorModel() {
    return colorModel;
  }

  /**
   * Return the uv frequency area
   * @return uv frequency area
   */
  public Rectangle.Double getUvRect() {
    return uvRect;
  }

  /**
   * Return the visibility minimum value (amplitude or phase)
   * @return visibility minimum value (amplitude or phase)
   */
  public Float getMin() {
    return min;
  }

  /**
   * Return the visibility maximum value (amplitude or phase)
   * @return visibility maximum value (amplitude or phase)
   */
  public Float getMax() {
    return max;
  }

  /**
   * Return the uv map image
   * @return uv map image
   */
  public BufferedImage getUvMap() {
    return uvMap;
  }
}
