/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import edu.emory.mathcs.jtransforms.fft.RealFFTUtils_2D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import fr.jmmc.jmal.complex.ImmutableComplex;
import fr.jmmc.jmal.model.ImageMode;
import fr.jmmc.jmcs.util.concurrent.InterruptedJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility class provides methods to deal with FFT transforms
 * @author bourgesl
 */
public final class FFTUtils {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(FFTUtils.class.getName());

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

        logger.info("testFFT: image size = " + inputSize + " - FFT size = " + fftSize + " - output size = " + outputSize);

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
        return convert(fftSize, fftData, mode, fftSize);
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

        final long start = System.nanoTime();

        // rows, cols must be power of two or at least even numbers:
        final RealFFTUtils_2D unpacker = new RealFFTUtils_2D(fftSize, fftSize);

        final float[][] output = new float[outputSize][outputSize];

        final boolean isAmp = (mode == ImageMode.AMP);

        final int ro2 = outputSize / 2;
        final int fftOffset = fftSize - ro2;

        final int nthreads = ConcurrencyUtils.getNumberOfThreads();

        // computation tasks:
        final Runnable[] tasks = new Runnable[nthreads];

        // create tasks:
        for (int i = 0; i < nthreads; i++) {
            final int n0 = i;

            tasks[i] = new Runnable() {

                @Override
                public void run() {
                    float[] oRow;
                    float re, im;

                    // Process quadrant 1 and 2 (cache locality):
                    for (int r = n0; r < ro2; r += nthreads) {
                        oRow = output[r];

                        for (int i = 0, c; i < ro2; i++) {
                            // quadrant 1:
                            c = 2 * i;
                            re = unpacker.unpack(r, c, fftData);
                            im = unpacker.unpack(r, c + 1, fftData);

                            oRow[i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));

                            // quadrant 2:
                            c = 2 * (fftOffset + i);
                            re = unpacker.unpack(r, c, fftData);
                            im = unpacker.unpack(r, c + 1, fftData);

                            oRow[ro2 + i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));
                        }
                    }

                    // Process quadrant 4 and 3 (cache locality):
                    for (int r = n0; r < ro2; r += nthreads) {
                        oRow = output[r + ro2];

                        for (int i = 0, c; i < ro2; i++) {
                            // quadrant 4:
                            c = 2 * i;
                            re = unpacker.unpack(r + fftOffset, c, fftData);
                            im = unpacker.unpack(r + fftOffset, c + 1, fftData);

                            oRow[i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));

                            // quadrant 3:
                            c = 2 * (fftOffset + i);
                            re = unpacker.unpack(r + fftOffset, c, fftData);
                            im = unpacker.unpack(r + fftOffset, c + 1, fftData);

                            oRow[ro2 + i] = (float) ((isAmp) ? ImmutableComplex.abs(re, im) : ImmutableComplex.getArgument(re, im));
                        }
                    }
                }
            };
        }

        if (nthreads > 1) {
            // fork and join tasks:
            ConcurrencyUtils.forkAndJoin(tasks);

        } else {
            // execute the single task using the current thread:
            tasks[0].run();
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
     * @param size input size of the real FFT (rows = columns = size)
     * @param fftData real FFT
     * @param outputSize output size (must be an even number)
     * @return sub part of the given real FFT encoded as another real FFT 2D array
     */
    public static float[][] extractFFT(final int size, final float[][] fftData, final int outputSize) {
        if (outputSize % 2 == 1) {
            throw new IllegalStateException("invalid output size (" + outputSize + ") must be an even number !");
        }
        if (outputSize > size) {
            throw new IllegalStateException("invalid output size (" + outputSize + ") > fft size (" + size + ") !");
        }
        if (outputSize == size) {
            return fftData;
        }

        logger.info("extractFFT: output size = " + outputSize + " - FFT size = " + size);
        /*
         * <pre>
         * a[k1][2*k2] = Re[k1][k2] = Re[rows-k1][columns-k2], 
         * a[k1][2*k2+1] = Im[k1][k2] = -Im[rows-k1][columns-k2], 
         *       0&lt;k1&lt;rows, 0&lt;k2&lt;columns/2, 
         * 
         * a[0][2*k2] = Re[0][k2] = Re[0][columns-k2], 
         * a[0][2*k2+1] = Im[0][k2] = -Im[0][columns-k2], 
         *       0&lt;k2&lt;columns/2, 
         * a[k1][0] = Re[k1][0] = Re[rows-k1][0], 
         * a[k1][1] = Im[k1][0] = -Im[rows-k1][0], 
         * a[rows-k1][1] = Re[k1][columns/2] = Re[rows-k1][columns/2], 
         * a[rows-k1][0] = -Im[k1][columns/2] = Im[rows-k1][columns/2], 
         *       0&lt;k1&lt;rows/2, 
         * a[0][0] = Re[0][0], 
         * a[0][1] = Re[0][columns/2], 
         * a[rows/2][0] = Re[rows/2][0], 
         * a[rows/2][1] = Re[rows/2][columns/2]
         * </pre>
         */

        final int ro2 = outputSize / 2; // half of row dimension

        final int ef2 = size - ro2; // index of the first row in FFT (quadrant 3 / 4)

        final float[][] output = new float[outputSize][outputSize];

        float[] oRow, fRow;
        float[] o2Row, f2Row;

        for (int r = 0; r < ro2; r++) {
            oRow = output[r];
            o2Row = output[r + ro2];

            fRow = fftData[r];
            f2Row = fftData[ef2 + r];

            for (int i = 0, c; i < ro2; i++) {
                c = 2 * i;

                /*
                 * a[k1][2*k2] = Re[k1][k2] = Re[rows-k1][columns-k2], 
                 * a[k1][2*k2+1] = Im[k1][k2] = -Im[rows-k1][columns-k2], 
                 *       0&lt;k1&lt;rows, 0&lt;k2&lt;columns/2, 
                 */

                // TODO: use System.arrayCopy()

                // quadrant 1:
                oRow[c] = fRow[c];
                oRow[c + 1] = fRow[c + 1];

                // quadrant 4:
                o2Row[c] = f2Row[c];
                o2Row[c + 1] = f2Row[c + 1];
            }
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