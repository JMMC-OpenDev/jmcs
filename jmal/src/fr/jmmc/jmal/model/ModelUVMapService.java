/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.MutableComplex;
import fr.jmmc.jmal.image.ColorScale;
import fr.jmmc.jmal.image.ImageUtils;
import fr.jmmc.jmal.image.job.ImageMinMaxJob;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.util.ThreadLocalRandom;
import fr.jmmc.jmcs.util.concurrent.InterruptedJobException;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This stateless class generates an UV Map Image for given target Models and UV area
 *
 * @author Laurent BOURGES.
 */
public final class ModelUVMapService {

    /** standard visibility amplitude range [0;1] used by linear color scale */
    public final static Float[] RANGE_AMPLITUDE_LINEAR = new Float[]{Float.valueOf(0f), Float.valueOf(1f)};
    /** standard visibility amplitude range [0;1] used by logarithmic color scale */
    public final static Float[] RANGE_AMPLITUDE_LOGARITHMIC = new Float[]{Float.valueOf(9e-2f), Float.valueOf(1f)};
    /** standard visibility phase range [-180°;180°] */
    public final static Float[] RANGE_PHASE = new Float[]{Float.valueOf(-180f), Float.valueOf(180f)};
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ModelUVMapService.class.getName());
    /** threshold to use parallel jobs (65535 UV points) */
    private final static int JOB_THRESHOLD = 256 * 256 - 1;
    /** Jmcs Parallel Job executor */
    private static final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

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
     * @param uvRect expected UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @return UVMapData
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws IllegalArgumentException if a model parameter value is invalid
     * @throws RuntimeException if any exception occured during the computation
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final ImageMode mode,
                                         final int imageSize,
                                         final IndexColorModel colorModel,
                                         final ColorScale colorScale) {
        return computeUVMap(models, uvRect, null, null, null, mode, imageSize, colorModel, colorScale, null);
    }

    /**
     * Compute the UV Map for the given models and UV ranges
     *
     * @param models list of models to use
     * @param uvRect expected UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @param noiseService optional noise service to compute noisy complex visibilities before computing amplitude or phase
     * @return UVMapData
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws IllegalArgumentException if a model parameter value is invalid
     * @throws RuntimeException if any exception occured during the computation
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final ImageMode mode,
                                         final int imageSize,
                                         final IndexColorModel colorModel,
                                         final ColorScale colorScale,
                                         final VisNoiseService noiseService) {
        return computeUVMap(models, uvRect, null, null, null, mode, imageSize, colorModel, colorScale, noiseService);
    }

    /**
     * Compute the UV Map for the given models and UV ranges
     *
     * @param models list of models to use
     * @param uvRect expected UV frequency area in rad-1
     * @param refMin minimum reference value used only for sub images
     * @param refMax maximum reference value used only for sub images
     * @param refVisData reference complex visibility data (optional)
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @param noiseService optional noise service to compute noisy complex visibilities before computing amplitude or phase
     * @return UVMapData
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws IllegalArgumentException if a model parameter value is invalid
     * @throws RuntimeException if any exception occured during the computation
     */
    public static UVMapData computeUVMap(final List<Model> models,
                                         final Rectangle2D.Double uvRect,
                                         final Float refMin, final Float refMax,
                                         final float[][] refVisData,
                                         final ImageMode mode,
                                         final int imageSize,
                                         final IndexColorModel colorModel,
                                         final ColorScale colorScale,
                                         final VisNoiseService noiseService) {

        /** Get the current thread to check if the computation is interrupted */
        final Thread currentThread = Thread.currentThread();

        // Start the computations :
        final long start = System.nanoTime();

        // complex visibility data as float[rows][cols] where cols = 2 x imageSize to store complex values as (re, im)
        final float[][] visData;

        if (refVisData == null) {

            if (models == null || models.isEmpty()) {
                return null;
            }

            // Clone models and normalize fluxes :
            final List<Model> normModels = ModelManager.normalizeModels(models);

            final ModelFunctionComputeContext context;
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
                throw new InterruptedJobException("ModelUVMapService.computeUVMap: interrupted");
            }

            // 2 - Compute complex visibility for the given models :

            // use single precision for performance (image needs not double precision) :
            visData = new float[imageSize][2 * imageSize];

            // Should split the computation in parts ?
            final int nJobs = ((imageSize * imageSize * normModels.size()) >= JOB_THRESHOLD) ? jobExecutor.getMaxParallelJob() : 1;

            final ComputeModelPart[] jobs = new ComputeModelPart[nJobs];

            ModelFunctionComputeContext jobContext;
            for (int i = 0; i < nJobs; i++) {
                // clone computation contexts except for first job:
                jobContext = (i == 0) ? context : ModelManager.cloneContext(context);

                // ensure last job goes until lineEnd:
                jobs[i] = new ComputeModelPart(jobContext, u, v, imageSize, visData, i, nJobs);
            }

            // execute jobs in parallel or using current thread if only one job (throws InterruptedJobException if interrupted):
            jobExecutor.forkAndJoin("ModelUVMapService.computeUVMap", jobs);

        } else {
            // use reference complex visibility data:
            visData = refVisData;
        }


        // 3 - Extract the amplitude/phase/square amplitude to get the uv map :

        // data as float [rows][cols]:
        final float[][] data = convert(imageSize, visData, mode, noiseService);

        // fast interrupt :
        if (currentThread.isInterrupted()) {
            throw new InterruptedJobException("ModelUVMapService.computeUVMap: interrupted");
        }


        // 4 - Get the image with the given color model and color scale :
        final UVMapData uvMapData = computeImage(uvRect, refMin, refMax, mode, imageSize, colorModel, colorScale,
                imageSize, visData, data, uvRect, noiseService);

        if (logger.isInfoEnabled()) {
            logger.info("compute : duration = {} ms.", 1e-6d * (System.nanoTime() - start));
        }
        return uvMapData;
    }

    /**
     * Compute the uv map image given the model image data (amplitude or phase)
     * 
     * @param uvRect UV frequency area in rad-1
     * @param refMin minimum reference value used only for sub images
     * @param refMax maximum reference value used only for sub images
     * @param mode image mode (amplitude or phase)
     * @param imageSize expected number of pixels for both width and height of the generated image
     * @param colorModel color model to use
     * @param colorScale color scaling method
     * @param dataSize number of rows and columns of the model image data
     * @param data amplitude/phase/square amplitude data
     * @param imgData model image data (amplitude/phase/square amplitude)
     * @param uvMapRect concrete UV frequency area in rad-1
     * @param noiseService optional noise service to compute noisy complex visibilities before computing amplitude or phase
     * @return UVMapData
     * 
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws IllegalArgumentException if a model parameter value is invalid
     * @throws RuntimeException if any exception occured during the computation
     */
    public static UVMapData computeImage(final Rectangle2D.Double uvRect,
                                         final Float refMin, final Float refMax,
                                         final ImageMode mode,
                                         final int imageSize,
                                         final IndexColorModel colorModel,
                                         final ColorScale colorScale,
                                         final int dataSize,
                                         final float[][] data,
                                         final float[][] imgData,
                                         final Rectangle2D.Double uvMapRect,
                                         final VisNoiseService noiseService) {

        // ignore zero values if log color scale:
        final ImageMinMaxJob minMaxJob = new ImageMinMaxJob(imgData, dataSize, dataSize, (colorScale == ColorScale.LOGARITHMIC));

        minMaxJob.forkAndJoin();

        final float dataMin = minMaxJob.getMin();
        final float dataMax = minMaxJob.getMax();

        if (logger.isDebugEnabled()) {
            logger.debug("ImageMinMaxJob min: {} - max: {}", dataMin, dataMax);
        }

        // Get the image with the given color model :
        final ColorScale usedColorScale;

        // min - max range used by color conversion:
        final Float[] stdRange;
        switch (mode) {
            case SQUARE:
            case AMP:
                usedColorScale = colorScale;

                if (refMin == null || refMax == null) {
                    if ((colorScale == ColorScale.LOGARITHMIC || (noiseService != null && noiseService.isEnabled()))
                            && dataMin != dataMax && !Float.isInfinite(dataMin) && !Float.isInfinite(dataMax)) {

                        final Float[] defStdRange = (colorScale == ColorScale.LOGARITHMIC) ? RANGE_AMPLITUDE_LOGARITHMIC : RANGE_AMPLITUDE_LINEAR;

                        float stdMin = dataMin;
                        float stdMax = dataMax;

                        // force min to 0.1 at least to have log scale ticks displayed:
                        if (stdMin > defStdRange[0].floatValue()) {
                            stdMin = defStdRange[0].floatValue();
                        }
                        // force max to 1 because dataMax can be 0.99999:
                        if (stdMax < defStdRange[1].floatValue()) {
                            stdMax = defStdRange[1].floatValue();
                        }

                        stdRange = new Float[]{stdMin, stdMax};
                        break;
                    }
                }
                stdRange = (colorScale == ColorScale.LOGARITHMIC) ? RANGE_AMPLITUDE_LOGARITHMIC : RANGE_AMPLITUDE_LINEAR;
                break;
            case PHASE:
                usedColorScale = ColorScale.LINEAR;
                stdRange = RANGE_PHASE;
                break;
            default:
                return null;
        }

        // use the given reference extrema to make the value to color conversion :
        final Float min = (refMin != null) ? refMin : stdRange[0];
        final Float max = (refMax != null) ? refMax : stdRange[1];

        if (logger.isDebugEnabled()) {
            logger.debug("value range in [{}, {}]", min, max);
        }

        // throws InterruptedJobException if the current thread is interrupted (cancelled):
        final BufferedImage uvMap = ImageUtils.createImage(dataSize, dataSize, imgData, min.floatValue(), max.floatValue(), colorModel, usedColorScale);

        // provide results :;
        return new UVMapData(uvRect, mode, imageSize, colorModel, usedColorScale, min, max, Float.valueOf(dataMin), Float.valueOf(dataMax),
                data, uvMap, dataSize, uvMapRect, noiseService);
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
     * Compute model Task that computes the complex visibilities in parallel at each U,V frequencies
     */
    private static class ComputeModelPart implements Runnable {

        /* input */
        /** compute context (list of model functions, temporary variables) */
        private final ModelFunctionComputeContext _context;
        /** sampled U frequencies in rad-1 (width) */
        private final double[] _u;
        /** sampled V frequencies in rad-1 (height) */
        private final double[] _v;
        /** number of pixels for both width and height of the generated image */
        private final int _imageSize;
        /* output */
        /** image data as float[rows][cols] */
        private final float[][] _data;
        /* job boundaries */
        /** job index */
        private final int _jobIndex;
        /** total number of concurrent jobs */
        private final int _jobCount;

        /**
         * Create the task
         *
         * @param context compute context (list of model functions, temporary variables)
         * @param u sampled U frequencies in rad-1 (width)
         * @param v sampled V frequencies in rad-1 (height)
         * @param imageSize number of values for both width and height of the generated model
         * @param data visibility data as float[rows][cols] where cols = 2 x imageSize to store complex values as (re, im)
         * @param jobIndex job index used to process data interlaced
         * @param jobCount total number of concurrent jobs
         */
        ComputeModelPart(final ModelFunctionComputeContext context,
                         final double[] u, final double[] v,
                         final int imageSize, final float[][] data,
                         final int jobIndex, final int jobCount) {

            this._context = context;
            this._u = u;
            this._v = v;
            this._imageSize = imageSize;
            this._data = data;
            this._jobIndex = jobIndex;
            this._jobCount = jobCount;
        }

        /**
         * Execute the task i.e. performs the computations
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeModelPart: start [{}]", _jobIndex);
            }
            // Copy members to local variables:
            /* input */
            final ModelFunctionComputeContext context = _context;
            final double[] u = _u;
            final double[] v = _v;
            final int imageSize = _imageSize;
            /* output */
            final float[][] data = _data;
            /* job boundaries */
            final int jobIndex = _jobIndex;
            final int jobCount = _jobCount;

            // Prepare other variables:
            MutableComplex[] vis;
            final double[] ufreq = u;
            final double[] vfreq = new double[imageSize];

            final ModelManager modelManager = ModelManager.getInstance();

            /** Get the current thread to check if the computation is interrupted */
            final Thread currentThread = Thread.currentThread();

            // this step indicates when the thread.isInterrupted() is called in the for loop
            final int stepInterrupt = Math.min(4, 1 + imageSize / 32);

            // Compute model line by line to reduce memory footprint (complex array, double[] U and v frequencies ...)

            float[] row;

            // Compute model line by line:
            for (int i, j = jobIndex, c; j < imageSize; j += jobCount) {

                // ufreq corresponds to all U frequencies:
                // vfreq corresponds to the same V frequency:
                for (i = 0; i < imageSize; i++) {
                    vfreq[i] = v[j];
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

                // 3 - Copy the complex values to data (as float):
                row = data[j];

                for (i = 0; i < imageSize; i++) {
                    c = 2 * i;
                    row[c] = (float) vis[i].getReal();
                    row[c + 1] = (float) vis[i].getImaginary();
                }
            } // line by line

            // Compute done.
            if (logger.isDebugEnabled()) {
                logger.debug("ComputeModelPart: end   [{}]", _jobIndex);
            }
        }
    }

    /**
     * Convert the given FT data (complex data) of the given size to Amplitude or Phase according to the given mode
     * @param size number of rows = number of columns / 2 (re, im)
     * @param ftData FT data (complex data)
     * @param mode image mode (amplitude or phase)
     * @param noiseService optional noise service to compute noisy complex visibilities before computing amplitude or phase
     * @return amplitude or phase image
     */
    private static float[][] convert(final int size, final float[][] ftData, final ImageMode mode,
                                     final VisNoiseService noiseService) {

        final long start = System.nanoTime();

        final float[][] output = new float[size][size];

        // thread safe data converter:
        final VisConverter converter = VisConverter.create(mode, noiseService);

        /** Get the current thread to check if the computation is interrupted */
        final Thread currentThread = Thread.currentThread();

        // this step indicates when the thread.isInterrupted() is called in the for loop
        final int stepInterrupt = Math.min(16, 1 + size / 32);

        final int nJobs = jobExecutor.getMaxParallelJob();

        // computation tasks:
        final Runnable[] jobs = new Runnable[nJobs];

        // create tasks:
        for (int i = 0; i < nJobs; i++) {
            final int jobIndex = i;

            jobs[i] = new Runnable() {
                @Override
                public void run() {
                    // random instance dedicated to this thread:
                    final Random threadRandom = ThreadLocalRandom.current();

                    float[] oRow;
                    double re, im;

                    for (int r = jobIndex; r < size; r += nJobs) {
                        oRow = output[r];

                        for (int i = 0, c; i < size; i++) {
                            c = 2 * i;
                            re = ftData[r][c];
                            im = ftData[r][c + 1];

                            oRow[i] = converter.convert(re, im, threadRandom);
                        }

                        // fast interrupt:
                        if (r % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("convert: cancelled");
                            return;
                        }
                    }
                }
            };
        }

        // execute jobs in parallel or using current thread if only one job (throws InterruptedJobException if interrupted):
        jobExecutor.forkAndJoin("ModelUVMapService.convert", jobs);

        logger.info("convert: duration = {} ms.", 1e-6d * (System.nanoTime() - start));

        return output;
    }
}
