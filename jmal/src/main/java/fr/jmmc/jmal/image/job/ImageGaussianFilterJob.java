/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import java.util.Arrays;
import java.util.List;
import net.jafama.FastMath;

/**
 * Basic Job dedicated to apply gaussian filter to image values i.e. multiply by gaussian weights
 * 
 * @author bourgesl
 */
public final class ImageGaussianFilterJob extends AbstractImageJob<Void> {

    /* members */
    /** gaussian weights along the x-axis */
    final double[] _colWeights;
    /** gaussian weights along the y-axis */
    final double[] _rowWeights;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param colFreqs spatial coordinates (rad) (relative to the half width)
     * @param rowFreqs spatial coordinates (rad) (relative to the half height)
     * @param fwhm gaussian sigma value (rad)
     */
    public ImageGaussianFilterJob(final float[][] array, final int width, final int height,
                                  final double[] colFreqs, final double[] rowFreqs, final double fwhm) {
        super("ImageGaussianFilterJob", array, width, height);

        /*
             * Gaussian 2D function:
             * g(x,y) = exp(-( (x - x0)^2 + (y - y0)^2 ) / (2 x sigma^2) )
         */
        // precompute normalization factor:
        final double sigma_norm = -1.0 / (2.0 * fwhm * fwhm);

        final double[] colWeights = colFreqs; // X
        final double[] rowWeights = rowFreqs; // Y        

        // Gaussian 2D function is separable:
        for (int i = 0; i < width; i++) {
            colWeights[i] = FastMath.exp(sigma_norm * (colFreqs[i] * colFreqs[i]));
        }
        for (int i = 0; i < height; i++) {
            rowWeights[i] = FastMath.exp(sigma_norm * (rowFreqs[i] * rowFreqs[i]));
        }

        this._colWeights = colWeights;
        this._rowWeights = rowWeights;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     */
    protected ImageGaussianFilterJob(final ImageGaussianFilterJob parentJob, final int jobIndex, final int jobCount) {
        super(parentJob, jobIndex, jobCount);
        this._colWeights = parentJob._colWeights;
        this._rowWeights = parentJob._rowWeights;
    }

    /**
     * Initialize a new child job
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     * @return child job
     */
    @Override
    protected ImageGaussianFilterJob initializeChildJob(final int jobIndex, final int jobCount) {
        return new ImageGaussianFilterJob(this, jobIndex, jobCount);
    }

    /**
     * Initialize the result object (one per job)
     * @return result Object
     */
    @Override
    protected Void initializeResult() {
        return null;
    }

    /**
     * Merge partial result objects to produce the final result object
     * @param partialResults partial result objects
     */
    @Override
    protected void merge(final List<Void> partialResults) {
        // no-op
    }

    /**
     * Process the given value at the given row and column index
     * 
     * @param col row index
     * @param row column index
     */
    @Override
    protected void processValue(final int col, final int row, final float value) {
        // use double precision for accuracy:
        _array2D[row][col] = (float) (_colWeights[col] * _rowWeights[row] * value);
    }
}
