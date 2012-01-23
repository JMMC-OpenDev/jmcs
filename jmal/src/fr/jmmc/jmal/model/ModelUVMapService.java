/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.MutableComplex;
import fr.jmmc.jmal.image.ColorModels;
import fr.jmmc.jmal.image.ImageUtils;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class generates an UV Map Image for given target Models and UV area
 *
 * @author Laurent BOURGES.
 */
public final class ModelUVMapService {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ModelUVMapService.class.getName());
    /** default image width / height */
    private final static int DEFAULT_IMAGE_SIZE = 512;
    /** default color model (aspro - Rainbow) */
    private final static IndexColorModel DEFAULT_COLOR_MODEL = ColorModels.getColorModel("aspro");
    /** standard visibility amplitude range [0;1] */
    private final static float[] RANGE_AMPLITUDE = new float[]{0f, 1f};
    /** standard visibility phase range [-PI;PI] */
    private final static float[] RANGE_PHASE = new float[]{(float) -Math.PI, (float) Math.PI};
    /** threshold to use parallel jobs (65535 UV points) */
    private final static int JOB_THRESHOLD = 256 * 256 - 1;

    /**
     * Image modes (amplitude, phase)
     */
    public enum ImageMode {

        /** Amplitude */
        AMP,
        /** Phase */
        PHASE
    }

    /**
     * Forbidden constructor
     */
    private ModelUVMapService() {
        // no-op
    }

    /**
     * Compute the UV Map for the given models and UV ranges
     *
     * @param models list of models to use
     * @param uvRect UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @return UVMapData
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final ImageMode mode) {
        return computeUVMap(models, uvRect, null, null, mode, DEFAULT_IMAGE_SIZE, DEFAULT_COLOR_MODEL);
    }

    /**
     * Compute the UV Map for the given models and UV ranges
     *
     * @param models list of models to use
     * @param uvRect UV frequency area in rad-1
     * @param refMin minimum reference double value used only for sub images
     * @param refMax maximum reference double value used only for sub images
     * @param mode image mode (amplitude or phase)
     * @return UVMapData
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final Float refMin, final Float refMax,
                                         final ImageMode mode) {
        return computeUVMap(models, uvRect, refMin, refMax, mode, DEFAULT_IMAGE_SIZE, DEFAULT_COLOR_MODEL);
    }

    /**
     * Compute the UV Map for the given models and UV ranges
     *
     * @param models list of models to use
     * @param uvRect UV frequency area in rad-1
     * @param refMin minimum reference value used only for sub images
     * @param refMax maximum reference value used only for sub images
     * @param mode image mode (amplitude or phase)
     * @param imageSize number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @return UVMapData
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final Float refMin, final Float refMax,
                                         final ImageMode mode,
                                         final int imageSize,
                                         final IndexColorModel colorModel) {

        if (models == null || models.isEmpty()) {
            return null;
        }

        UVMapData uvMapData;

        /** Get the current thread to check if the computation is interrupted */
        final Thread currentThread = Thread.currentThread();

        // Start the computations :
        final long start = System.nanoTime();

        // Clone models and normalize fluxes :
        final List<Model> normModels = ModelManager.normalizeModels(models);

        final ModelComputeContext context;
        try {
            // prepare models once for all:
            context = ModelManager.getInstance().prepareModels(normModels, imageSize);

        } catch (IllegalArgumentException iae) {
            // ModelManager.prepareModels throws an IllegalArgumentException if a parameter value is invalid :
            logger.warn("Invalid argument :", iae);
            throw iae;
        }

        // 1 - Prepare UFreq and VFreq arrays (small) :
        final double[] u = computeFrequencySamples(imageSize, uvRect.getX(), uvRect.getMaxX());
        final double[] v = computeFrequencySamples(imageSize, uvRect.getY(), uvRect.getMaxY());

        // fast interrupt :
        if (currentThread.isInterrupted()) {
            return null;
        }

        // use single precision for performance (image needs not double precision) :
        final float[][] data = new float[imageSize][imageSize];

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        // TODO: add threshold check here (imageSize * imageSize * modelSize) > 300k

        final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

        if (jobExecutor.isEnabled()
                && (imageSize * imageSize * normModels.size()) >= JOB_THRESHOLD) {
            // split model image in parts for parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();

            if (imageSize % nJobs != 0) {
                throw new IllegalStateException("ImageSize[" + imageSize + "] and nJobs[" + nJobs + "] must be compatible (power of 2)");
            }

            final int step = imageSize / nJobs;

            final ComputeModelPart[] jobs = new ComputeModelPart[nJobs];

            int lineStart = 0;
            int lineEnd = step;
            for (int i = 0; i < nJobs; i++) {
                // clone computation contexts except for first job:
                final ModelComputeContext jobContext = (i == 0) ? context : ModelManager.cloneContext(context);

                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeModelPart(jobContext, u, v, imageSize, mode, data,
                        lineStart, ((i == (nJobs - 1)) || (lineEnd > imageSize)) ? imageSize : lineEnd);

                lineStart += step;
                lineEnd += step;
            }

            // fast interrupt :
            if (currentThread.isInterrupted()) {
                logger.debug("main thread cancelled.");
                return null;
            }

            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            // start first part using this thread
            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join(futures);

        } else {
            // single processor: use this thread to compute the complete model image:
            new ComputeModelPart(context, u, v, imageSize, mode, data, 0, imageSize).run();
        }

        // fast interrupt :
        if (currentThread.isInterrupted()) {
            logger.debug("main thread cancelled.");
            return null;
        }

        // 4 - Get the image with the given color model :

        // min - max range used by color conversion:
        final float[] stdRange;
        switch (mode) {
            case AMP:
                stdRange = RANGE_AMPLITUDE;
                break;
            case PHASE:
                stdRange = RANGE_PHASE;
                break;
            default:
                return null;
        }

        // use the given reference extrema to make the value to color conversion :
        final float min = (refMin != null) ? refMin.floatValue() : stdRange[0];
        final float max = (refMax != null) ? refMax.floatValue() : stdRange[1];

        if (logger.isDebugEnabled()) {
            logger.debug("value range in [{}, {}]", min, max);
        }

        final BufferedImage uvMap = ImageUtils.createImage(imageSize, imageSize, data, min, max, colorModel);

        // fast interrupt :
        if (currentThread.isInterrupted()) {
            logger.debug("main thread cancelled.");
            return null;
        }

        // provide results :
        uvMapData = new UVMapData(mode, imageSize, colorModel, uvRect, Float.valueOf(min), Float.valueOf(max), data, uvMap);

        if (logger.isInfoEnabled()) {
            logger.info("compute : duration = {} ms.", 1e-6d * (System.nanoTime() - start));
        }
        return uvMapData;
    }

    /**
     * Return the frequencies in rad-1
     *
     * @param nbSamples number of sampled values
     * @param min minimum frequency value
     * @param max maximum frequency value
     * @return sampled frequencies in rad-1
     */
    private static double[] computeFrequencySamples(final int nbSamples, final double min, final double max) {
        final double[] freq = new double[nbSamples];

        final double step = (max - min) / nbSamples;

        freq[0] = min;
        for (int i = 1; i < nbSamples; i++) {
            freq[i] = freq[i - 1] + step;
        }

        return freq;
    }

    /**
     * Compute model Task that computes one part of the model image in parallel with other tasks working on the same image:
     * Compute the complex value at each U,V frequencies and get its amplitude or phase depending on the image mode.
     */
    private static class ComputeModelPart implements Runnable {

        /* input */
        /** compute context (list of model functions, temporary variables) */
        private final ModelComputeContext _context;
        /** sampled U frequencies in rad-1 (width) */
        private final double[] _u;
        /** sampled V frequencies in rad-1 (height) */
        private final double[] _v;
        /** number of pixels for both width and height of the generated image */
        private final int _imageSize;
        /** image mode (amplitude or phase) */
        private final ImageMode _mode;
        /* output */
        /** image data as float[rows][cols] */
        private final float[][] _data;
        /* job boundaries */
        /** index of first line (inclusive) */
        private final int _lineStart;
        /** index of last line (exclusive) */
        private final int _lineEnd;

        /**
         * Create the task
         *
         * @param context compute context (list of model functions, temporary variables)
         * @param u sampled U frequencies in rad-1 (width)
         * @param v sampled V frequencies in rad-1 (height)
         * @param imageSize number of pixels for both width and height of the generated image
         * @param mode image mode (amplitude or phase)
         * @param data image data as float[rows][cols]
         * @param lineStart index of first line (inclusive)
         * @param lineEnd index of last line (exclusive)
         */
        ComputeModelPart(final ModelComputeContext context,
                         final double[] u, final double[] v,
                         final int imageSize, final ImageMode mode,
                         final float[][] data,
                         final int lineStart, final int lineEnd) {

            this._context = context;
            this._u = u;
            this._v = v;
            this._imageSize = imageSize;
            this._mode = mode;
            this._data = data;
            this._lineStart = lineStart;
            this._lineEnd = lineEnd;
        }

        /**
         * Execute the task i.e. performs the computations
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeModelPart: start [{} - {}]", _lineStart, _lineEnd);
            }
            // Copy members to local variables:
            /* input */
            final ModelComputeContext context = _context;
            final double[] u = _u;
            final double[] v = _v;
            final int imageSize = _imageSize;
            final ImageMode mode = _mode;
            /* output */
            final float[][] data = _data;
            /* job boundaries */
            final int lineStart = _lineStart;
            final int lineEnd = _lineEnd;

            // Prepare other variables:
            MutableComplex[] vis;
            final double[] ufreq = new double[imageSize];
            final double[] vfreq = new double[imageSize];

            final ModelManager modelManager = ModelManager.getInstance();

            /** Get the current thread to check if the computation is interrupted */
            final Thread currentThread = Thread.currentThread();

            // this step indicates when the thread.isInterrupted() is called in the for loop
            final int stepInterrupt = Math.min(4, 1 + (lineEnd - lineStart) / 32);

            // Compute model line by line to reduce memory footprint (complex array, double[] U and v frequencies ...)

            // Note : the image is produced from an array where 0,0 corresponds to the upper left corner
            // whereas it corresponds in UV to the lower U and Upper V coordinates => inverse the V axis

            // Compute model line by line:
            for (int i, k, j = lineStart; j < lineEnd; j++) {
                // inverse the v axis for the image :
                k = imageSize - j - 1;

                for (i = 0; i < imageSize; i++) {
                    ufreq[i] = u[i];
                    vfreq[i] = v[k];
                }

                // 2 - Compute complex visibility for the given models :
                vis = modelManager.computeModels(context, ufreq, vfreq);

                if (vis == null) {
                    return;
                }

                // fast interrupt:
                if (j % stepInterrupt == 0 && currentThread.isInterrupted()) {
                    logger.debug("ComputeModelPart: cancelled (vis)");
                    return;
                }

                // 3 - Extract the amplitude/phase to get the uv map :

                switch (mode) {
                    case AMP:
                        for (i = 0; i < imageSize; i++) {
                            // amplitude = complex modulus (abs in commons-math) :
                            data[i][j] = (float) vis[i].abs();
                        }
                        break;
                    case PHASE:
                        for (i = 0; i < imageSize; i++) {
                            // phase [-PI;PI] = complex phase (argument in commons-math) :
                            data[i][j] = (float) vis[i].getArgument();
                        }
                        break;
                    default:
                        return;
                }
            } // line by line

            // Compute done.
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeModelPart: end [{} - {}]", _lineStart, _lineEnd);
            }
        }
    }
}
