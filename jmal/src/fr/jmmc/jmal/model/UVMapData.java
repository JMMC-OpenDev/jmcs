/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.image.ImageUtils.ColorScale;
import fr.jmmc.jmal.model.ModelUVMapService.ImageMode;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * This class contains the results of the ModelUVMap service
 * 
 * @author Laurent BOURGES.
 */
public final class UVMapData {

    /* inputs */
    /** number of pixels for both width and height of the generated image */
    private final int imageSize;
    /** image mode (amplitude or phase) */
    private final ImageMode mode;
    /** image color model */
    private final IndexColorModel colorModel;
    /** image color scale */
    private final ColorScale colorScale;
    /** uv frequency area */
    private final Rectangle.Double uvRect;
    /** target name */
    private String targetName = null;
    /** target model version */
    private int targetVersion = -1;

    /* outputs */
    /** visibility minimum value (amplitude or phase) */
    private final Float min;
    /** visibility maximum value (amplitude or phase) */
    private final Float max;
    /** UV Model image */
    private final float[][] uvData;
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
     * @param uvData UV Model image
     * @param uvMap uv map image
     * @param colorScale color scaling method
     */
    public UVMapData(final ImageMode mode, final int imageSize, final IndexColorModel colorModel,
                     final Rectangle.Double uvRect, final Float min, final Float max,
                     final float[][] uvData, final BufferedImage uvMap, final ColorScale colorScale) {
        this.mode = mode;
        this.imageSize = imageSize;
        this.colorModel = colorModel;
        this.uvRect = uvRect;
        this.min = min;
        this.max = max;
        this.uvData = uvData;
        this.uvMap = uvMap;
        this.colorScale = colorScale;
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
     * Return the target name
     * @return target name
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Define the target name
     * @param targetName target name
     */
    public void setTargetName(final String targetName) {
        this.targetName = targetName;
    }

    /**
     * Return the target version
     * @return target version
     */
    public int getTargetVersion() {
        return targetVersion;
    }

    /**
     * Define the target version
     * @param targetVersion target version
     */
    public void setTargetVersion(final int targetVersion) {
        this.targetVersion = targetVersion;
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
     * Return the UV Model image
     * @return UV Model image
     */
    public float[][] getUvData() {
        return uvData;
    }

    /**
     * Return the uv map image
     * @return uv map image
     */
    public BufferedImage getUvMap() {
        return uvMap;
    }

    /**
     * Return the image color scale
     * @return image color scale
     */
    public ColorScale getColorScale() {
        return colorScale;
    }

    /**
     * Check if this UV Map Data has the same input parameters to reuse its computed model image :
     * @param targetName target name
     * @param targetVersion target version
     * @param uvRect uv frequency area
     * @param mode image mode (amplitude or phase)
     * @param imageSize number of pixels for both width and height of the generated image
     * @param colorModel image color model
     * @return true only if input parameters are equals
     */
    public boolean isValid(final String targetName, final int targetVersion,
                           final Rectangle.Double uvRect,
                           final ImageMode mode,
                           final int imageSize,
                           final IndexColorModel colorModel) {

        if (!targetName.equals(getTargetName())) {
            return false;
        }
        if (targetVersion != getTargetVersion()) {
            return false;
        }
        if (!uvRect.equals(getUvRect())) {
            return false;
        }
        if (mode != getImageMode()) {
            return false;
        }
        if (imageSize != getImageSize()) {
            return false;
        }
        if (colorModel != getColorModel()) {
            return false;
        }

        // TODO: add colorScale:
        return true;
    }
}
