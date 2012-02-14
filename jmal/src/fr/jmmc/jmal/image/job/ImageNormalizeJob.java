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
    final float _factor;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param factor factor value to use
     */
    public ImageNormalizeJob(final float[][] array, final int width, final int height,
                             final float factor) {
        super("ImageNormalizeJob", array, width, height);
        this._factor = factor;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     */
    protected ImageNormalizeJob(final ImageNormalizeJob parentJob,
                                final int lineStart, final int lineEnd) {
        super(parentJob, lineStart, lineEnd);
        this._factor = parentJob._factor;
    }

    /**
     * Initialize a new child job
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     * @return child job
     */
    @Override
    protected ImageNormalizeJob initializeChildJob(final int lineStart, final int lineEnd) {
        return new ImageNormalizeJob(this, lineStart, lineEnd);
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
        _array2D[row][col] = value * _factor;
    }
}
