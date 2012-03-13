/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import edu.emory.mathcs.jtransforms.fft.RealFFTUtils_2D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import fr.jmmc.jmal.complex.ImmutableComplex;
import fr.jmmc.jmal.model.ImageMode;
import fr.jmmc.jmal.model.VisNoiseService;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility class provides methods to deal with FFT transforms
 * @author bourgesl
 */
public final class FFTUtils {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(FFTUtils.class.getName());
    /** Jmcs Parallel Job executor */
    private static final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

    /**
     * Forbidden constructor
     */
    private FFTUtils() {
        super();
    }

    /**
     * Return the power of two equals or larger given one value
     * @param value value to use
     * @return power of two equals or larger
     */
    public static int getPowerOfTwo(final int value) {
        if (ConcurrencyUtils.isPowerOf2(value)) {
            return value;
        }
        return ConcurrencyUtils.nextPow2(value);
    }

    /**
     * Computes only a subset of the 2D forward DFT of real data
     * @param inputSize input image size (width == height); must be an even number
     * @param array input image
     * @param fftSize FFT dimensions (width == height); must be a power of two
     * @param outputSize output size (width == height); must be an even number
     * @return subset of the 2D real FFT array (power of two) of the given size outputSize = rows = columns
     */
    public static float[][] computeFFT(final int inputSize, final float[][] array, final int fftSize, final int outputSize) {

        logger.info("computeFFT: image size = " + inputSize + " - FFT size = " + fftSize + " - output size = " + outputSize);

        // FFT sub size must be larger than input image:
        final int fftSubSize = Math.max(inputSize, outputSize);

        logger.info("FloatFFT_2D.realForwardSubset: FFT size = " + fftSize + " - sub size = " + fftSubSize
                + " - input size = " + inputSize + " - start...");

        long start = System.nanoTime();

        // use size to have hyper resolution in fourier plane:
        FloatFFT_2D fft2d = new FloatFFT_2D(fftSize, fftSize, true); // rows, cols must be power of two !!

        // compute subset of real FFT (power of 2):
        float[][] fftData = fft2d.realForwardSubset(fftSubSize, inputSize, array);

        // fast interrupt:
        if (Thread.currentThread().isInterrupted()) {
            return null;
        }

        long time = System.nanoTime() - start;

        logger.info("FloatFFT_2D.realForwardSubset: duration = " + (1e-6d * time) + " ms.");

        // free FFT2D (GC):
        fft2d = null;


        // extract part of the FFT:

        if (fftSubSize > outputSize) {
            start = System.nanoTime();

            fftData = extractFFT(fftSubSize, fftData, outputSize);

            logger.info("extractFFT: duration = " + (1e-6d * (System.nanoTime() - start)) + " ms.");
        }

        return fftData;
    }

    /**
     * Convert the given FFT data (real packed data) of the given size to Amplitude or Phase according to the given mode
     * And shift quadrants to have zero (DC) at the image center
     * @param fftSize number of rows = number of columns
     * @param fftData FFT data (real packed data)
     * @param mode image mode (amplitude or phase)
     * @return amplitude or phase image
     */
    public static float[][] convert(final int fftSize, final float[][] fftData, final ImageMode mode) {
        return convert(fftSize, fftData, mode, fftSize, null);
    }

    /**
     * Convert the given FFT data (real packed data) of the given size to Amplitude or Phase according to the given mode
     * And shift quadrants to have zero (DC) at the image center
     * @param fftSize number of rows = number of columns
     * @param fftData FFT data (real packed data)
     * @param mode image mode (amplitude or phase)
     * @param outputSize output size (width == height); must be an even number
     * @return amplitude or phase image
     */
    public static float[][] convert(final int fftSize, final float[][] fftData, final ImageMode mode, final int outputSize) {
        return convert(fftSize, fftData, mode, outputSize, null);
    }

    /**
     * Convert the given FFT data (real packed data) of the given size to Amplitude or Phase according to the given mode
     * And shift quadrants to have zero (DC) at the image center
     * @param fftSize number of rows = number of columns
     * @param fftData FFT data (real packed data)
     * @param mode image mode (amplitude or phase)
     * @param outputSize output size (width == height); must be an even number
     * @param noiseService optional noise service to compute noisy complex visibilities before computing amplitude or phase
     * @return amplitude or phase image
     */
    public static float[][] convert(final int fftSize, final float[][] fftData, final ImageMode mode, final int outputSize,
                                    final VisNoiseService noiseService) {

        final long start = System.nanoTime();

        // rows, cols must be power of two or at least even numbers:
        final RealFFTUtils_2D unpacker = new RealFFTUtils_2D(fftSize, fftSize);

        final float[][] output = new float[outputSize][outputSize];

        final boolean isAmp = (mode == ImageMode.AMP);
        final boolean doNoise = (noiseService != null && noiseService.isEnabled());

        final int ro2 = outputSize / 2;
        final int fftOffset = fftSize - ro2;

        /** Get the current thread to check if the computation is interrupted */
        final Thread currentThread = Thread.currentThread();

        // this step indicates when the thread.isInterrupted() is called in the for loop
        final int stepInterrupt = Math.min(16, 1 + outputSize / 32);

        final int nJobs = jobExecutor.getMaxParallelJob();

        // computation tasks:
        final Runnable[] jobs = new Runnable[nJobs];

        // create tasks:
        for (int i = 0; i < nJobs; i++) {
            final int n0 = i;

            jobs[i] = new Runnable() {

                @Override
                public void run() {
                    float[] oRow;
                    double re, im, err;

                    // Process quadrant 1 and 2 (cache locality):
                    for (int r = n0; r < ro2; r += nJobs) {
                        oRow = output[r];

                        for (int i = 0, c; i < ro2; i++) {
                            // quadrant 1:
                            c = 2 * i;
                            re = unpacker.unpack(r, c, fftData);
                            im = unpacker.unpack(r, c + 1, fftData);

                            if (doNoise) {
                                err = noiseService.computeVisComplexErrorValue(ImmutableComplex.abs(re, im));
                                re += noiseService.gaussianNoise(err);
                                im += noiseService.gaussianNoise(err);
                            }

                            oRow[i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));

                            // quadrant 2:
                            c = 2 * (fftOffset + i);
                            re = unpacker.unpack(r, c, fftData);
                            im = unpacker.unpack(r, c + 1, fftData);

                            if (doNoise) {
                                err = noiseService.computeVisComplexErrorValue(ImmutableComplex.abs(re, im));
                                re += noiseService.gaussianNoise(err);
                                im += noiseService.gaussianNoise(err);
                            }

                            oRow[ro2 + i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));
                        }

                        // fast interrupt:
                        if (r % stepInterrupt == 0 && currentThread.isInterrupted()) {
                            logger.debug("convert: cancelled");
                            return;
                        }
                    }

                    // Process quadrant 4 and 3 (cache locality):
                    for (int r = n0; r < ro2; r += nJobs) {
                        oRow = output[r + ro2];

                        for (int i = 0, c; i < ro2; i++) {
                            // quadrant 4:
                            c = 2 * i;
                            re = unpacker.unpack(r + fftOffset, c, fftData);
                            im = unpacker.unpack(r + fftOffset, c + 1, fftData);

                            if (doNoise) {
                                err = noiseService.computeVisComplexErrorValue(ImmutableComplex.abs(re, im));
                                re += noiseService.gaussianNoise(err);
                                im += noiseService.gaussianNoise(err);
                            }

                            oRow[i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));

                            // quadrant 3:
                            c = 2 * (fftOffset + i);
                            re = unpacker.unpack(r + fftOffset, c, fftData);
                            im = unpacker.unpack(r + fftOffset, c + 1, fftData);

                            if (doNoise) {
                                err = noiseService.computeVisComplexErrorValue(ImmutableComplex.abs(re, im));
                                re += noiseService.gaussianNoise(err);
                                im += noiseService.gaussianNoise(err);
                            }

                            oRow[ro2 + i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));
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

        if (nJobs > 1) {
            // execute jobs in parallel:
            final Future<?>[] futures = jobExecutor.fork(jobs);

            logger.debug("wait for jobs to terminate ...");

            jobExecutor.join("FFTUtils.convert", futures);

        } else {
            // execute the single task using the current thread:
            jobs[0].run();
        }

        logger.info("convert: duration = " + (1e-6d * (System.nanoTime() - start)) + " ms.");

        FFTUtils.shiftQuadrants(outputSize, output);

        return output;
    }

    /**
     * Shift quadrants in the given square 2D array (in-place)
     * @param size number of rows = number of columns
     * @param data data array to process
     */
    public static void shiftQuadrants(final int size, final float[][] data) {
        final long start = System.nanoTime();

        // shift quadrants:
    /*
         * | 1 2 | => | 3 4 |
         * | 4 3 |    | 2 1 |
         */

        final int ro2 = size / 2; // half of row dimension

        float[] row, row2;
        float tmp;
        for (int r = 0, idxR, c, idxC; r < ro2; r++) {
            idxR = r + ro2;

            row = data[r];
            row2 = data[idxR];

            for (c = 0; c < ro2; c++) {
                idxC = c + ro2;

                // switch 1 <--> 3:
                tmp = row[c];
                row[c] = row2[idxC];
                row2[idxC] = tmp;

                // switch 2 <--> 4:
                tmp = row2[c];
                row2[c] = row[idxC];
                row[idxC] = tmp;
            }
        }

        logger.info("shiftQuadrants: duration = " + (1e-6d * (System.nanoTime() - start)) + " ms.");
    }

    /**
     * Extract a sub part of the given real FFT 2D array
     * 
     * @see FloatFFT_2D.realForward(float[][])
     * 
     * @param size input size of the real FFT (rows = columns = size)
     * @param fftData real FFT
     * @param outputSize output size (must be an even number)
     * @return sub part of the given real FFT encoded as another real FFT 2D array
     */
    public static float[][] extractFFT(final int size, final float[][] fftData, final int outputSize) {
        if (outputSize % 2 == 1) {
            throw new IllegalStateException("Invalid output size (" + outputSize + ") must be an even number !");
        }
        if (outputSize > size) {
            throw new IllegalStateException("Invalid output size (" + outputSize + ") > fft size (" + size + ") !");
        }
        if (outputSize == size) {
            return fftData;
        }

        logger.info("extractFFT: output size = " + outputSize + " - FFT size = " + size);

        final int ro2 = outputSize / 2; // half of row dimension

        final int ef2 = size - ro2; // index of the first row in FFT (quadrant 3 / 4)

        final float[][] output = new float[outputSize][outputSize];

        /*
         * a[k1][2*k2] = Re[k1][k2] = Re[rows-k1][columns-k2], 
         * a[k1][2*k2+1] = Im[k1][k2] = -Im[rows-k1][columns-k2], 
         *       0&lt;k1&lt;rows, 0&lt;k2&lt;columns/2, 
         */

        float[] oRow, fRow;

        for (int r = 0; r < ro2; r++) {
            oRow = output[r];
            fRow = fftData[r];

            // copy complex data (re, im) from fftData beginning to output row:
            System.arraycopy(fRow, 0, oRow, 0, outputSize);

            oRow = output[r + ro2];
            fRow = fftData[ef2 + r];

            // copy complex data (re, im) from fftData end to output row:
            System.arraycopy(fRow, 0, oRow, 0, outputSize);
        }

        // Probleme connu (symetrie horiz/vert) for row=rows/2 column=columns/2

        // Solution: fixer a[rows][0/1] avec rows > 1
        /*
         * a[rows-k1][1] = Re[k1][columns/2] = Re[rows-k1][columns/2], 
         * a[rows-k1][0] = -Im[k1][columns/2] = Im[rows-k1][columns/2], 
         *       0&lt;k1&lt;rows/2, 
         */
        for (int r = 1, j; r < ro2; r++) {
            j = outputSize - r;

            output[j][1] = fftData[r][outputSize];
            output[j][0] = -fftData[r][outputSize + 1];
        }
        /*
         * a[0][1] = Re[0][columns/2], 
         */
        output[0][1] = fftData[0][outputSize];

        /*
         * a[rows/2][0] = Re[rows/2][0], 
         * a[rows/2][1] = Re[rows/2][columns/2]
         */
        output[ro2][0] = fftData[ro2][0]; // quadrant 2
        output[ro2][1] = fftData[ro2][outputSize]; // quadrant 3

        return output;
    }
}
