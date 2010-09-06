/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: UVMapData.java,v 1.1 2010-02-04 14:43:36 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model;

import fr.jmmc.mcs.model.ModelUVMapService.ImageMode;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;

/**
 * This class contains the results of the ModelUVMap service
 * @author bourgesl
 */
public class UVMapData {

  /** image width / height */
  private int imageSize = 0;
  /** image mode (amplitude or phase) */
  private ImageMode mode;
  /** uv map image */
  private BufferedImage uvMap = null;
  /** uv frequency area */
  private Rectangle.Float uvRect = null;
  /** visibility minimum value (amplitude or phase) */
  private float min;
  /** visibility maximum value (amplitude or phase) */
  private float max;

  /**
   * Constuctor
   */
  public UVMapData() {
    super();
  }

  public int getImageSize() {
    return imageSize;
  }

  public void setImageSize(int imageSize) {
    this.imageSize = imageSize;
  }

  public ImageMode getMode() {
    return mode;
  }

  public void setMode(ImageMode mode) {
    this.mode = mode;
  }

  public BufferedImage getUvMap() {
    return uvMap;
  }

  public void setUvMap(BufferedImage uvMap) {
    this.uvMap = uvMap;
  }

  public Float getUvRect() {
    return uvRect;
  }

  public void setUvRect(Float uvRect) {
    this.uvRect = uvRect;
  }

  public float getMin() {
    return min;
  }

  public void setMin(float min) {
    this.min = min;
  }

  public float getMax() {
    return max;
  }

  public void setMax(float max) {
    this.max = max;
  }
}
