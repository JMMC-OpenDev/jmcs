/**
 * *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 *****************************************************************************
 */
package fr.jmmc.jmal.image;

import fr.jmmc.jmcs.util.concurrent.InterruptedJobException;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains several utility methods to produce Image objects from raw data
 *
 * @author Laurent BOURGES.
 */
public final class ImageUtils {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class.getName());
    /** alpha integer mask */
    private final static int ALPHA_MASK = 0xff << 24;
    /** flag to use RGB color interpolation */
    public final static boolean USE_RGB_INTERPOLATION = true;
    /** threshold to use parallel jobs (256 x 256 pixels) */
    private final static int JOB_THRESHOLD = 256 * 256 - 1;
    /** Jmcs Parallel Job executor */
    private static final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

    /**
     * Forbidden constructor
     */
    private ImageUtils() {
        // no-op
    }

    /**
     * Convert min/max range to the given color scaling method
     * @param min data min value
     * @param max data max value
     * @param colorScale color scaling method
     * @return scaled min / max values
     */
    public static float[] scaleMinMax(final float min, final float max, final ColorScale colorScale) {

        final float scaledMin;
        final float scaledMax;
        switch (colorScale) {
            default:
            case LINEAR:
                scaledMin = min;
                scaledMax = max;
                break;
            case LOGARITHMIC:
                // protect against negative values including zero:
                if (min <= 0f || max <= 0f) {
                    throw new IllegalArgumentException("Negative or zero values in range[" + min + " - " + max + "] !");
                }
                scaledMin = (float) Math.log10(min);
                scaledMax = (float) Math.log10(max);

                if (logger.isDebugEnabled()) {
                    logger.debug("scaleMinMax: new range[{} - {}]", scaledMin, scaledMax);
                }
                break;
        }
        return new float[]{scaledMin, scaledMax};
    }

    /**
     * Return the value to pixel coefficient
     *
     * @param min data min value
     * @param max data max value
     * @param iMaxColor maximum number of colors to use
     * @return data to color linear scaling factor
     */
    public static float computeScalingFactor(final float min, final float max, final int iMaxColor) {
        final int iMax = iMaxColor - 1;

        float factor = iMax / (max - min);

        if (factor == 0f) {
            factor = 1f;
        }

        return factor;
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (1D)
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float min, final float max,
                                            final IndexColorModel colorModel) {
        return ImageUtils.createImage(width, height, array, min, max, colorModel, ColorScale.LINEAR);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (1D)
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @param colorScale color scaling method
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float min, final float max,
                                            final IndexColorModel colorModel,
                                            final ColorScale colorScale) {

        final float[] scaledMinMax = scaleMinMax(min, max, colorScale);

        final float scalingFactor = computeScalingFactor(scaledMinMax[0], scaledMinMax[1], colorModel.getMapSize());

        return ImageUtils.createImage(width, height, array, scaledMinMax[0], colorModel, scalingFactor, colorScale);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (1D)
     * @param min lower data value (lower threshold)
     * @param colorModel color model
     * @param scalingFactor value to pixel coefficient
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float min,
                                            final IndexColorModel colorModel, final float scalingFactor) {
        return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor, ColorScale.LINEAR);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (1D)
     * @param scaledMin minimum data value or log10(min) 
     * @param colorModel color model
     * @param scalingFactor value to pixel coefficient
     * @param colorScale color scaling method
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float scaledMin,
                                            final IndexColorModel colorModel, final float scalingFactor,
                                            final ColorScale colorScale) {
        if (array == null) {
            throw new IllegalStateException("Undefined data array.");
        }
        if (array.length != (width * height)) {
            throw new IllegalStateException("Invalid data array size: " + array.length + "; expected: " + (width * height) + ".");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("createImage: using array of size {} x {}", width, height);
        }

        // Start the computations :
        final long start = System.nanoTime();

        final BufferedImage image = createImage(width, height, colorModel);
        final WritableRaster imageRaster = image.getRaster();
        final DataBuffer dataBuffer = imageRaster.getDataBuffer();

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        if (jobExecutor.isEnabled()
                && array.length >= JOB_THRESHOLD) {
            // split model image in parts for parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();
            final ComputeImagePart[] jobs = new ComputeImagePart[nJobs];

            for (int i = 0; i < nJobs; i++) {
                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeImagePart(array, scaledMin, colorModel, scalingFactor, colorScale, dataBuffer, i, nJobs);
            }

            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join("ImageUtils.createImage", futures);

        } else {
            // single processor: use this thread to compute the complete model image:
            new ComputeImagePart(array, scaledMin, colorModel, scalingFactor, colorScale, dataBuffer, 0, 1).run();
        }

        // fast interrupt :
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedJobException("ImageUtils.createImage: interrupted");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("compute : duration = {} ms.", 1e-6d * (System.nanoTime() - start));
        }

        return image;
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (2D)
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float min, final float max,
                                            final IndexColorModel colorModel) {
        return ImageUtils.createImage(width, height, array, min, max, colorModel, ColorScale.LINEAR);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (2D)
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @param colorScale color scaling method
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float min, final float max,
                                            final IndexColorModel colorModel, final ColorScale colorScale) {

        final float[] scaledMinMax = scaleMinMax(min, max, colorScale);

        final float scalingFactor = computeScalingFactor(scaledMinMax[0], scaledMinMax[1], colorModel.getMapSize());

        return ImageUtils.createImage(width, height, array, scaledMinMax[0], colorModel, scalingFactor, colorScale);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (2D) [rows][cols]
     * @param min lower data value (lower threshold)
     * @param colorModel color model
     * @param scalingFactor value to pixel coefficient
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float min,
                                            final IndexColorModel colorModel, final float scalingFactor) {
        return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor, ColorScale.LINEAR);
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param array data array (2D) [rows][cols]
     * @param scaledMin minimum data value or log10(min) 
     * @param colorModel color model
     * @param scalingFactor value to pixel coefficient
     * @param colorScale color scaling method
     * @return new BufferedImage or null if interrupted
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float scaledMin,
                                            final IndexColorModel colorModel, final float scalingFactor,
                                            final ColorScale colorScale) {
        if (array == null) {
            throw new IllegalStateException("Undefined data array.");
        }
        if (array.length != height) {
            throw new IllegalStateException("Invalid data array size: " + array.length + "; expected: " + height + ".");
        }
        if (array[0].length != width) {
            throw new IllegalStateException("Invalid data array size: " + array[0].length + "; expected: " + width + ".");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("createImage: using array of size {} x {}", width, height);
        }

        // Start the computations :
        final long start = System.nanoTime();

        final BufferedImage image = createImage(width, height, colorModel);
        final WritableRaster imageRaster = image.getRaster();
        final DataBuffer dataBuffer = imageRaster.getDataBuffer();

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        if (jobExecutor.isEnabled() && (width * height) >= JOB_THRESHOLD) {
            // process image using parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();
            final ComputeImagePart[] jobs = new ComputeImagePart[nJobs];

            for (int i = 0; i < nJobs; i++) {
                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeImagePart(array, width, height, scaledMin, colorModel, scalingFactor, colorScale, dataBuffer, i, nJobs);
            }

            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join("ImageUtils.createImage", futures);

        } else {
            // single processor: use this thread to compute the complete model image:
            new ComputeImagePart(array, width, height, scaledMin, colorModel, scalingFactor, colorScale, dataBuffer, 0, 1).run();
        }

        // fast interrupt :
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedJobException("ImageUtils.createImage: interrupted");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("compute : duration = {} ms.", 1e-6d * (System.nanoTime() - start));
        }

        return image;
    }

    /**
     * Create an Image from the given data array using the specified Color Model
     *
     * @param width image width
     * @param height image height
     * @param colorModel color model
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final IndexColorModel colorModel) {

        if (logger.isDebugEnabled()) {
            logger.debug("createImage: using array of size {} x {}", width, height);
        }

        final ColorModel imageColorModel;
        final WritableRaster imageRaster;

        if (USE_RGB_INTERPOLATION) {
            imageColorModel = ColorModel.getRGBdefault();
            imageRaster = imageColorModel.createCompatibleWritableRaster(width, height);

        } else {
            imageColorModel = colorModel;
            imageRaster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, width, height, new int[]{0xFF}, null);
        }

        // do not initialize raster pixels
        return new BufferedImage(imageColorModel, imageRaster, false, null);
    }

    /**
     * Return the color index using the indexed color model for the given value (linear scale)
     *
     * @param colorModel color model
     * @param iMaxColor index of the highest color
     * @param value data value to convert between 0.0 and 255.0
     * @return color index
     */
    public static int getColor(final IndexColorModel colorModel, final int iMaxColor, final float value) {
        int colorIdx = Math.round(value);

        if (colorIdx < 0) {
            colorIdx = 0;
        } else if (colorIdx > iMaxColor) {
            colorIdx = iMaxColor;
        }
        return colorIdx;
    }

    /**
     * Return an RGB color (32bits) using the given color model for the given value (linear scale)
     *
     * @param colorModel color model
     * @param iMaxColor index of the highest color
     * @param value data value to convert between 0.0 and 255.0
     * @param alphaMask alpha mask (0 - 255) << 24
     * @return RGB color
     */
    public static int getRGB(final IndexColorModel colorModel, final int iMaxColor, final float value, final int alphaMask) {
        int minColorIdx = (int) Math.floor(value);

        final float ratio = value - minColorIdx;

        if (minColorIdx < 0) {
            minColorIdx = 0;
        } else if (minColorIdx > iMaxColor) {
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

    /**
     * Scale the given value using linear or logarithmic scale
     * 
     * @param doLog10 true to use logarithmic scale
     * @param scaledMin minimum data value or log10(min) 
     * @param scalingFactor data to color linear scaling factor
     * @param value value to convert
     * @return scaled value
     */
    public static float getScaledValue(final boolean doLog10, final float scaledMin, final float scalingFactor, final float value) {
        if (doLog10) {
            if (value <= 0f) {
                // lowest color
                return 0f;
            }
            return ((float) Math.log10(value) - scaledMin) * scalingFactor;
        }

        return (value - scaledMin) * scalingFactor;
    }

    /**
     * Compute image Task that process one image slice in parallel with other tasks working on the same image:
     * Convert the given 1D or 2D data array to RGB color using the given scaling factor
     */
    private static class ComputeImagePart implements Runnable {

        /* input */
        /** data array (1D) */
        private final float[] _array1D;
        /** data array (2D) [rows][cols] */
        private final float[][] _array2D;
        /** image width */
        private final int _width;
        /** image height */
        private final int _height;
        /** lower data value */
        private final float _scaledMin;
        /** indexed color model */
        private final IndexColorModel _colorModel;
        /** color scaling method */
        private final ColorScale _colorScale;
        /** data to color linear scaling factor */
        private final float _scalingFactor;
        /* output */
        /** image raster dataBuffer */
        private final DataBuffer _dataBuffer;
        /* job boundaries */
        /** job index */
        private final int _jobIndex;
        /** total number of concurrent jobs */
        private final int _jobCount;

        /**
         * Create the task
         *
         * @param array data array (1D)
         * @param scaledMin lower data value
         * @param colorModel indexed color model
         * @param scalingFactor data to color linear scaling factor
         * @param colorScale color scaling method
         * @param dataBuffer image raster dataBuffer
         * @param jobIndex job index used to process data interlaced
         * @param jobCount total number of concurrent jobs
         */
        ComputeImagePart(final float[] array, final float scaledMin,
                         final IndexColorModel colorModel, final float scalingFactor, final ColorScale colorScale,
                         final DataBuffer dataBuffer,
                         final int jobIndex, final int jobCount) {

            this._array1D = array;
            this._array2D = null;
            this._width = array.length;
            this._height = 0;
            this._scaledMin = scaledMin;
            this._colorModel = colorModel;
            this._colorScale = colorScale;
            this._scalingFactor = scalingFactor;
            this._dataBuffer = dataBuffer;
            this._jobIndex = jobIndex;
            this._jobCount = jobCount;
        }

        /**
         * Create the task
         *
         * @param array data array (2D)
         * @param width image width
         * @param height image height
         * @param scaledMin lower data value
         * @param colorModel indexed color model
         * @param scalingFactor data to color linear scaling factor
         * @param colorScale color scaling method
         * @param dataBuffer image raster dataBuffer
         * @param jobIndex job index used to process data interlaced
         * @param jobCount total number of concurrent jobs
         */
        ComputeImagePart(final float[][] array, final int width, final int height, final float scaledMin,
                         final IndexColorModel colorModel, final float scalingFactor, final ColorScale colorScale,
                         final DataBuffer dataBuffer,
                         final int jobIndex, final int jobCount) {

            this._array1D = null;
            this._array2D = array;
            this._width = width;
            this._height = height;
            this._scaledMin = scaledMin;
            this._colorModel = colorModel;
            this._colorScale = colorScale;
            this._scalingFactor = scalingFactor;
            this._dataBuffer = dataBuffer;
            this._jobIndex = jobIndex;
            this._jobCount = jobCount;
        }

        /**
         * Execute the task i.e. performs the computations
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeImagePart: start [{}]", _jobIndex);
            }
            // Copy members to local variables:
            /* input */
            final int width = _width;
            final int height = _height;
            final float[] array1D = _array1D;
            final float[][] array2D = _array2D;
            final float scaledMin = _scaledMin;
            final IndexColorModel colorModel = _colorModel;
            final float scalingFactor = _scalingFactor;
            final boolean doLog10 = (_colorScale == ColorScale.LOGARITHMIC);
            /* output */
            final DataBuffer dataBuffer = _dataBuffer;
            /* job boundaries */
            final int jobIndex = _jobIndex;
            final int jobCount = _jobCount;

            // Prepare other variables:
            final int iMaxColor = colorModel.getMapSize() - 1;

            /** Get the current thread to check if the computation is interrupted */
            final Thread currentThread = Thread.currentThread();

            // this step indicates when the thread.isInterrupted() is called in the for loop
            final int stepInterrupt = Math.min(16, 1 + height / 16);

            if (USE_RGB_INTERPOLATION) {

                // initialize raster pixels
                if (array1D != null) {
                    for (int i = jobIndex; i < width; i += jobCount) {

                        dataBuffer.setElem(i, getRGB(colorModel, iMaxColor,
                                getScaledValue(doLog10, scaledMin, scalingFactor, array1D[i]), ALPHA_MASK));

                        // fast interrupt:
                        if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // pixel by pixel
                } else if (array2D != null) {
                    float[] row;
                    for (int i, offset, j = jobIndex, lastRow = height - 1; j < height; j += jobCount) {
                        // inverse vertical axis (0 at bottom, height at top):
                        offset = width * (lastRow - j);
                        row = array2D[j];

                        for (i = 0; i < width; i++) {

                            dataBuffer.setElem(offset + i, getRGB(colorModel, iMaxColor,
                                    getScaledValue(doLog10, scaledMin, scalingFactor, row[i]), ALPHA_MASK));
                        }

                        // fast interrupt:
                        if (j % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // line by line
                }

            } else {

                // initialize raster pixels
                if (array1D != null) {
                    for (int i = jobIndex; i < width; i += jobCount) {

                        dataBuffer.setElem(i, getColor(colorModel, iMaxColor,
                                getScaledValue(doLog10, scaledMin, scalingFactor, array1D[i])));

                        // fast interrupt:
                        if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // pixel by pixel
                } else if (array2D != null) {
                    float[] row;
                    for (int i, offset, j = jobIndex, lastRow = height - 1; j < height; j += jobCount) {
                        // inverse vertical axis (0 at bottom, height at top):
                        offset = width * (lastRow - j);
                        row = array2D[j];

                        for (i = 0; i < width; i++) {

                            dataBuffer.setElem(offset + i, getColor(colorModel, iMaxColor,
                                    getScaledValue(doLog10, scaledMin, scalingFactor, row[i])));
                        }

                        // fast interrupt:
                        if (j % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // line by line
                }
            }

            // Compute done.
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeImagePart: end   [{}]", _jobIndex);
            }
        }
    }
}
