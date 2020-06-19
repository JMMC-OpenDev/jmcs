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
    /** minimum value (standard range) */
    private final Float min;
    /** maximum value (standard range) */
    private final Float max;
    /** minimum value in data set */
    private final Float dataMin;
    /** maximum value in data set */
    private final Float dataMax;
    /** Model complex visiblities */
    private final float[][] data;
    /** flag indicating if the data are reused to compute other images (avoid recycling) */
    private volatile boolean dataReused = false;
    /** uv map image (amplitude or phase) */
    private final BufferedImage uvMap;
    /** concrete number of pixels for both width and height of the generated image (may be different than imageSize) */
    private final int uvMapSize;
    /** concrete uv frequency area */
    private final Rectangle.Double uvMapRect;
    /** image index used (user model only) */
    private int imageIndex = -1;
    /** image count (user model only) */
    private int imageCount = -1;
    /** image wavelength in meters (user model only) */
    private Double wavelength = null;
    /** user model (user model only) */
    private Object userModel = null;
    /** (optional) airy radius of the (first) user model data (apodization) */
    private double airyRadius;

    /**
     * Constuctor
     * @param uvRect expected UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @param min minimum value (standard range) 
     * @param max maximum value (standard range)
     * @param dataMin minimum value in data set 
     * @param dataMax maximum value in data set
     * @param data amplitude/phase/square amplitude data
     * @param uvMap uv map image
     * @param uvMapSize concrete number of pixels for both width and height of the generated image
     * @param uvMapRect concrete UV frequency area in rad-1
     * @param noiseService optional Complex visibility Noise Service ready to use to compute noise on model images
     */
    public UVMapData(final Rectangle.Double uvRect,
                     final ImageMode mode, final int imageSize,
                     final IndexColorModel colorModel, final ColorScale colorScale,
                     final Float min, final Float max,
                     final Float dataMin, final Float dataMax,
                     final float[][] data, final BufferedImage uvMap,
                     final int uvMapSize, final Rectangle.Double uvMapRect,
                     final VisNoiseService noiseService) {
        this.uvRect = uvRect;
        this.mode = mode;
        this.imageSize = imageSize;
        this.colorModel = colorModel;
        this.colorScale = colorScale;
        this.min = min;
        this.max = max;
        this.dataMin = dataMin;
        this.dataMax = dataMax;
        this.data = data;
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
     * Return the minimum value (standard range)
     * @return minimum value (standard range)
     */
    public Float getMin() {
        return min;
    }

    /**
     * Return the maximum value (standard range)
     * @return maximum value (standard range)
     */
    public Float getMax() {
        return max;
    }

    /**
     * Return the minimum value in data set
     * @return minimum value in data set
     */
    public Float getDataMin() {
        return dataMin;
    }

    /**
     * Return the maximum value in data set
     * @return maximum value in data set
     */
    public Float getDataMax() {
        return dataMax;
    }

    /**
     * Return the amplitude/phase/square amplitude data
     * @return amplitude/phase/square amplitude data
     */
    public float[][] getData() {
        return data;
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
     * Return the image index used (user model only)
     * @return image index used (user model only)
     */
    public int getImageIndex() {
        return imageIndex;
    }

    /**
     * Define the image index used (user model only)
     * @param imageIndex image index used (user model only)
     */
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    /**
     * Return the image count (user model only)
     * @return image count (user model only)
     */
    public int getImageCount() {
        return imageCount;
    }

    /**
     * Define the image count (user model only)
     * @param imageCount image count (user model only)
     */
    public void setImageCount(final int imageCount) {
        this.imageCount = imageCount;
    }

    /**
     * Return the image wavelength in meters (user model only)
     * @return image wavelength in meters (user model only) or null
     */
    public Double getWaveLength() {
        return this.wavelength;
    }

    /**
     * Define the image wavelength in meters (user model only)
     * @param wavelength image wavelength in meters (user model only) or null
     */
    public void setWaveLength(final Double wavelength) {
        this.wavelength = wavelength;
    }

    /**
     * Return the user model (user model only)
     * @return userModel user model (user model only)
     */
    public Object getUserModel() {
        return userModel;
    }

    /**
     * Define the user model (user model only)
     * @param userModel user model (user model only)
     */
    public void setUserModel(final Object userModel) {
        this.userModel = userModel;
    }

    /**
     * Return the (optional) airy radius of the (first) user model data (apodization)
     * @return (optional) airy radius of the (first) user model data (apodization)
     */
    public double getAiryRadius() {
        return airyRadius;
    }

    /**
     * Define the (optional) airy radius of the (first) user model data (apodization)
     * @param airyRadius (optional) airy radius of the (first) user model data (apodization)
     */
    public void setAiryRadius(double airyRadius) {
        this.airyRadius = airyRadius;
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
     * @param imageIndex image index used (user model only)
     * @param noiseService optional Complex visibility Noise Service ready to use to compute noise on model images
     * @param airyRadius (optional) airy radius of the (first) user model data (apodization)
     * @return true only if input parameters are equals
     */
    public boolean isValid(final String targetName, final int targetVersion,
                           final Rectangle.Double uvRect,
                           final ImageMode mode,
                           final int imageSize,
                           final IndexColorModel colorModel,
                           final ColorScale colorScale,
                           final int imageIndex,
                           final VisNoiseService noiseService,
                           final double airyRadius) {

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
        if (imageIndex != getImageIndex()) {
            return false;
        }
        // TODO: check correctly noise service equality (i.e. check each individual parameters)
        if (noiseService != getNoiseService()) {
            return false;
        }
        if (Double.isNaN(airyRadius) != Double.isNaN(getAiryRadius())
                || !Double.isNaN(airyRadius) && airyRadius != getAiryRadius()) {
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
     * @param imageIndex image index used (user model only)
     * @param airyRadius (optional) airy radius of the (first) user model data (apodization)
     * @return true only if input parameters are equals
     */
    public boolean isDataValid(final String targetName, final int targetVersion,
                               final Rectangle.Double uvRect,
                               final int imageSize,
                               final int imageIndex,
                               final double airyRadius) {

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
        if (imageIndex != getImageIndex()) {
            return false;
        }
        if (Double.isNaN(airyRadius) != Double.isNaN(getAiryRadius())
                || !Double.isNaN(airyRadius) && airyRadius != getAiryRadius()) {
            return false;
        }
        return true;
    }

    /**
     * Return the flag indicating if the data are reused to compute other images (avoid recycling)
     * @return flag indicating if the data are reused to compute other images (avoid recycling)
     */
    public boolean isDataReused() {
        return dataReused;
    }

    /**
     * Define the flag indicating if the data are reused to compute other images (avoid recycling)
     * @param dataReused flag indicating if the data are reused to compute other images (avoid recycling)
     */
    public void setDataReused(final boolean dataReused) {
        this.dataReused = dataReused;
    }

}
