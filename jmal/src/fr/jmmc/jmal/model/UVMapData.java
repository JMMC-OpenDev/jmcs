/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.image.ColorScale;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * This class contains UV Map results (data, image ...)
 * 
 * @author Laurent BOURGES.
 */
public final class UVMapData {

    /* inputs */
    /** expected uv frequency area */
    private final Rectangle.Double uvRect;
    /** expected number of pixels for both width and height of the generated image */
    private final int imageSize;
    /** image mode (amplitude or phase) */
    private final ImageMode mode;
    /** image color model */
    private final IndexColorModel colorModel;
    /** image color scale */
    private final ColorScale colorScale;
    /** optional Complex visibility Noise Service ready to use to compute noise on model images */
    private final VisNoiseService noiseService;
    /** target name */
    private String targetName = null;
    /** target model version */
    private int targetVersion = -1;

    /* outputs */
    /** minimum value (amplitude or phase) */
    private final Float min;
    /** maximum value (amplitude or phase) */
    private final Float max;
    /** Model complex visiblities */
    private final float[][] visData;
    /** uv map image (amplitude or phase) */
    private final BufferedImage uvMap;
    /** concrete number of pixels for both width and height of the generated image (may be different than imageSize) */
    private final int uvMapSize;
    /** concrete uv frequency area */
    private final Rectangle.Double uvMapRect;

    /**
     * Constuctor
     * @param uvRect expected UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @param min visibility minimum value (amplitude or phase)
     * @param max visibility maximum value (amplitude or phase)
     * @param visData Model complex visiblities
     * @param uvMap uv map image
     * @param uvMapSize concrete number of pixels for both width and height of the generated image
     * @param uvMapRect concrete UV frequency area in rad-1
     * @param noiseService optional Complex visibility Noise Service ready to use to compute noise on model images
     */
    public UVMapData(final Rectangle.Double uvRect,
                     final ImageMode mode, final int imageSize,
                     final IndexColorModel colorModel, final ColorScale colorScale,
                     final Float min, final Float max,
                     final float[][] visData, final BufferedImage uvMap,
                     final int uvMapSize, final Rectangle.Double uvMapRect,
                     final VisNoiseService noiseService) {
        this.uvRect = uvRect;
        this.mode = mode;
        this.imageSize = imageSize;
        this.colorModel = colorModel;
        this.colorScale = colorScale;
        this.min = min;
        this.max = max;
        this.visData = visData;
        this.uvMap = uvMap;
        this.uvMapSize = uvMapSize;
        this.uvMapRect = uvMapRect;
        this.noiseService = noiseService;
    }

    /**
     * Return the expected number of pixels for both width and height of the generated image
     * @return expected number of pixels for both width and height of the generated image
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
     * Return the expected uv frequency area
     * @return expected uv frequency area
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
     * Return the Model complex visiblities
     * @return Model complex visiblities
     */
    public float[][] getVisData() {
        return visData;
    }

    /**
     * Return the uv map image
     * @return uv map image
     */
    public BufferedImage getUvMap() {
        return uvMap;
    }

    /**
     * Return the concrete number of pixels for both width and height of the generated image (may be different than imageSize)
     * @return concrete number of pixels for both width and height of the generated image (may be different than imageSize)
     */
    public int getUvMapSize() {
        return uvMapSize;
    }

    /**
     * Return the concrete uv frequency area
     * @return concrete uv frequency area
     */
    public Rectangle.Double getUvMapRect() {
        return uvMapRect;
    }

    /**
     * Return the image color scale
     * @return image color scale
     */
    public ColorScale getColorScale() {
        return colorScale;
    }

    /**
     * Return the optional Complex visibility Noise Service ready to use to compute noise on model images
     * @return optional Complex visibility Noise Service ready to use to compute noise on model images
     */
    public VisNoiseService getNoiseService() {
        return noiseService;
    }

    /**
     * Check if this UV Map Data has the same input parameters to reuse its computed model image :
     * @param targetName target name
     * @param targetVersion target version
     * @param uvRect uv frequency area
     * @param mode image mode (amplitude or phase)
     * @param imageSize number of pixels for both width and height of the generated image
     * @param colorModel image color model
     * @param colorScale color scaling method
     * @param noiseService optional Complex visibility Noise Service ready to use to compute noise on model images
     * @return true only if input parameters are equals
     */
    public boolean isValid(final String targetName, final int targetVersion,
                           final Rectangle.Double uvRect,
                           final ImageMode mode,
                           final int imageSize,
                           final IndexColorModel colorModel,
                           final ColorScale colorScale,
                           final VisNoiseService noiseService) {

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
        if (colorScale != getColorScale()) {
            return false;
        }
        // TODO: check correctly noise service equality (i.e. check each individual parameters)
        if (noiseService != getNoiseService()) {
            return false;
        }
        return true;
    }

    /**
     * Check if this UV Map Data has the same data parameters to reuse its computed complex visibility data :
     * @param targetName target name
     * @param targetVersion target version
     * @param uvRect uv frequency area
     * @param imageSize number of pixels for both width and height of the generated image
     * @return true only if input parameters are equals
     */
    public boolean isDataValid(final String targetName, final int targetVersion,
                               final Rectangle.Double uvRect,
                               final int imageSize) {

        if (!targetName.equals(getTargetName())) {
            return false;
        }
        if (targetVersion != getTargetVersion()) {
            return false;
        }
        if (!uvRect.equals(getUvRect())) {
            return false;
        }
        if (imageSize != getImageSize()) {
            return false;
        }
        return true;
    }
}
