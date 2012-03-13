/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import java.util.List;

/**
 * Basic Job dedicated to normalize image values i.e. multiply them by the given factor
 * 
 * @author bourgesl
 */
public final class ImageNormalizeJob extends AbstractImageJob<Void> {

    /* members */
    /** factor value */
    final double _factor;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param factor factor value to use
     */
    public ImageNormalizeJob(final float[][] array, final int width, final int height,
                             final double factor) {
        super("ImageNormalizeJob", array, width, height);
        this._factor = factor;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     */
    protected ImageNormalizeJob(final ImageNormalizeJob parentJob, final int jobIndex, final int jobCount) {
        super(parentJob, jobIndex, jobCount);
        this._factor = parentJob._factor;
    }

    /**
     * Initialize a new child job
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     * @return child job
     */
    @Override
    protected ImageNormalizeJob initializeChildJob(final int jobIndex, final int jobCount) {
        return new ImageNormalizeJob(this, jobIndex, jobCount);
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
        if (value != 0f) {
            // use double precision for accuracy:
            _array2D[row][col] = (float) (_factor * value);
        }
    }
}
