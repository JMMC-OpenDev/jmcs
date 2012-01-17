/**
 * *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 *****************************************************************************
 */
package fr.jmmc.jmal.image;

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

    /**
     * Forbidden constructor
     */
    private ImageUtils() {
        // no-op
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

        float c = iMax / (max - min);

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
     * @param array data array (1D)
     * @param min lower data value (lower threshold)
     * @param max upper data value (upper threshold)
     * @param colorModel color model
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float min, final float max,
                                            final IndexColorModel colorModel) {

        final float scalingFactor = ImageUtils.computeScalingFactor(min, max, colorModel.getMapSize());

        return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor);
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
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[] array, final float min,
                                            final IndexColorModel colorModel, final float scalingFactor) {
        if (array == null) {
            throw new IllegalStateException("Undefined data array.");
        }
        if (array.length != (width * height)) {
            throw new IllegalStateException("Invalid data array size: " + array.length + "; expected: " + (width * height) + ".");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("createImage: using array of size " + width + "x" + height);
        }

        // Start the computations :
        final long start = System.nanoTime();

        final BufferedImage image = createImage(width, height, colorModel);
        final WritableRaster imageRaster = image.getRaster();
        final DataBuffer dataBuffer = imageRaster.getDataBuffer();

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

        if (jobExecutor.isEnabled()
                && array.length >= JOB_THRESHOLD) {
            // split model image in parts for parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();
            final ComputeImagePart[] jobs = new ComputeImagePart[nJobs];

            final int step = array.length / nJobs;

            int pixStart = 0;
            int pixEnd = step;
            for (int i = 0; i < nJobs; i++) {
                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeImagePart(array, min, colorModel, scalingFactor, dataBuffer,
                        pixStart, ((i == (nJobs - 1)) || (pixEnd > array.length)) ? array.length : pixEnd);

                pixStart += step;
                pixEnd += step;
            }

            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            // start first part using this thread
            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join(futures);

        } else {
            // single processor: use this thread to compute the complete model image:
            new ComputeImagePart(array, min, colorModel, scalingFactor, dataBuffer, 0, height).run();
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
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float min, final float max,
                                            final IndexColorModel colorModel) {

        final float scalingFactor = ImageUtils.computeScalingFactor(min, max, colorModel.getMapSize());

        return ImageUtils.createImage(width, height, array, min, colorModel, scalingFactor);
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
     * @return new BufferedImage
     */
    public static BufferedImage createImage(final int width, final int height,
                                            final float[][] array, final float min,
                                            final IndexColorModel colorModel, final float scalingFactor) {
        if (array == null) {
            throw new IllegalStateException("Undefined data array.");
        }
        if (array.length != width) {
            throw new IllegalStateException("Invalid data array size: " + array.length + "; expected: " + width + ".");
        }
        if (array[0].length != height) {
            throw new IllegalStateException("Invalid data array size: " + array[0].length + "; expected: " + height + ".");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("createImage: using array of size " + width + "x" + height);
        }

        // Start the computations :
        final long start = System.nanoTime();

        final BufferedImage image = createImage(width, height, colorModel);
        final WritableRaster imageRaster = image.getRaster();
        final DataBuffer dataBuffer = imageRaster.getDataBuffer();

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

        if (jobExecutor.isEnabled()
                && (width * height) >= JOB_THRESHOLD) {
            // split model image in parts for parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();
            final ComputeImagePart[] jobs = new ComputeImagePart[nJobs];

            final int step = height / nJobs;

            int lineStart = 0;
            int lineEnd = step;
            for (int i = 0; i < nJobs; i++) {
                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeImagePart(array, width, min, colorModel, scalingFactor, dataBuffer,
                        lineStart, ((i == (nJobs - 1)) || (lineEnd > height)) ? height : lineEnd);

                lineStart += step;
                lineEnd += step;
            }

            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            // start first part using this thread
            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join(futures);

        } else {
            // single processor: use this thread to compute the complete model image:
            new ComputeImagePart(array, width, min, colorModel, scalingFactor, dataBuffer, 0, height).run();
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
            logger.debug("createImage: using array of size  " + width + "x" + height);
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
     * @param value data value to convert
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
     * @param value data value to convert
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
     * Compute image Task that process one image slice in parallel with other tasks working on the same image:
     * Convert the given 1D or 2D data array to RGB color using the given scaling factor
     */
    private static class ComputeImagePart implements Runnable {

        /* input */
        /** data array (1D) */
        private final float[] _array1D;
        /** data array (2D) */
        private final float[][] _array2D;
        /** image width */
        private final int _width;
        /** lower data value */
        private final float _min;
        /** indexed color model */
        private final IndexColorModel _colorModel;
        /** data to color linear scaling factor */
        private final float _scalingFactor;
        /* output */
        /** image raster dataBuffer */
        private final DataBuffer _dataBuffer;
        /* job boundaries */
        /** index of first line (inclusive) */
        private final int _lineStart;
        /** index of last line (exclusive) */
        private final int _lineEnd;

        /**
         * Create the task
         *
         * @param array data array (1D)
         * @param min lower data value
         * @param colorModel indexed color model
         * @param scalingFactor data to color linear scaling factor
         * @param dataBuffer image raster dataBuffer
         * @param lineStart index of first line (inclusive)
         * @param lineEnd index of last line (exclusive)
         */
        ComputeImagePart(final float[] array, final float min,
                         final IndexColorModel colorModel, final float scalingFactor,
                         final DataBuffer dataBuffer,
                         final int lineStart, final int lineEnd) {

            this._array1D = array;
            this._array2D = null;
            this._width = 0;
            this._min = min;
            this._colorModel = colorModel;
            this._scalingFactor = scalingFactor;
            this._dataBuffer = dataBuffer;
            this._lineStart = lineStart;
            this._lineEnd = lineEnd;
        }

        /**
         * Create the task
         *
         * @param array data array (2D)
         * @param width image width
         * @param min lower data value
         * @param colorModel indexed color model
         * @param scalingFactor data to color linear scaling factor
         * @param dataBuffer image raster dataBuffer
         * @param lineStart index of first line (inclusive)
         * @param lineEnd index of last line (exclusive)
         */
        ComputeImagePart(final float[][] array, final int width, final float min,
                         final IndexColorModel colorModel, final float scalingFactor,
                         final DataBuffer dataBuffer,
                         final int lineStart, final int lineEnd) {

            this._array1D = null;
            this._array2D = array;
            this._width = width;
            this._min = min;
            this._colorModel = colorModel;
            this._scalingFactor = scalingFactor;
            this._dataBuffer = dataBuffer;
            this._lineStart = lineStart;
            this._lineEnd = lineEnd;
        }

        /**
         * Execute the task i.e. performs the computations
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeImagePart: start [{} - {}]", _lineStart, _lineEnd);
            }
            // Copy members to local variables:
            /* input */
            final int width = _width;
            final float[] array1D = _array1D;
            final float[][] array2D = _array2D;
            final float min = _min;
            final IndexColorModel colorModel = _colorModel;
            final float scalingFactor = _scalingFactor;
            /* output */
            final DataBuffer dataBuffer = _dataBuffer;
            /* job boundaries */
            final int lineStart = _lineStart;
            final int lineEnd = _lineEnd;

            // Prepare other variables:
            final int iMaxColor = colorModel.getMapSize() - 1;

            /** Get the current thread to check if the computation is interrupted */
            final Thread currentThread = Thread.currentThread();

            // this step indicates when the thread.isInterrupted() is called in the for loop
            final int stepInterrupt = Math.min(16, 1 + (lineEnd - lineStart) / 32);

            if (USE_RGB_INTERPOLATION) {

                // initialize raster pixels
                if (array1D != null) {
                    for (int i = lineStart; i < lineEnd; i++) {
                        dataBuffer.setElem(i, getRGB(colorModel, iMaxColor, (array1D[i] - min) * scalingFactor, ALPHA_MASK));

                        // fast interrupt:
                        if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // pixel by pixel
                } else if (array2D != null) {
                    for (int i, offset, j = lineStart; j < lineEnd; j++) {
                        offset = width * j;

                        for (i = 0; i < width; i++) {
                            dataBuffer.setElem(offset + i, getRGB(colorModel, iMaxColor, (array2D[i][j] - min) * scalingFactor, ALPHA_MASK));
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
                    for (int i = lineStart; i < lineEnd; i++) {
                        dataBuffer.setElem(i, getColor(colorModel, iMaxColor, (array1D[i] - min) * scalingFactor));

                        // fast interrupt:
                        if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("ComputeImagePart: cancelled (vis)");
                            return;
                        }
                    } // pixel by pixel
                } else if (array2D != null) {
                    for (int i, offset, j = lineStart; j < lineEnd; j++) {
                        offset = width * j;
                        for (i = 0; i < width; i++) {
                            dataBuffer.setElem(offset + i, getColor(colorModel, iMaxColor, (array2D[i][j] - min) * scalingFactor));
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
                logger.debug("ComputeImagePart: end   [{} - {}]", _lineStart, _lineEnd);
            }
        }
    }
}
