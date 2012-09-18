/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import fr.jmmc.jmal.image.job.ImageRegionThresholdJob.BoundaryResult;
import java.util.List;

/**
 * Simple Job dedicated to find boundaries of data values higher than given threshold
 * @author bourgesl
 */
public final class ImageRegionThresholdJob extends AbstractImageJob<BoundaryResult> {

    /** undefined lower index (Integer.MAX_VALUE) */
    public final static int UNDEFINED_LOWER_INDEX = Integer.MAX_VALUE;
    /** undefined upper index (Integer.MIN_VALUE) */
    public final static int UNDEFINED_UPPER_INDEX = Integer.MIN_VALUE;
    /** threshold value (inclusive) */
    final float _threshold;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param threshold threshold value to check
     */
    public ImageRegionThresholdJob(final float[][] array, final int width, final int height,
                                   final float threshold) {
        super("ImageRegionThresholdJob", array, width, height);
        this._threshold = threshold;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     */
    protected ImageRegionThresholdJob(final ImageRegionThresholdJob parentJob, final int jobIndex, final int jobCount) {
        super(parentJob, jobIndex, jobCount);
        this._threshold = parentJob._threshold;
    }

    /**
     * Initialize a new child job
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     * @return child job
     */
    @Override
    protected ImageRegionThresholdJob initializeChildJob(final int jobIndex, final int jobCount) {
        return new ImageRegionThresholdJob(this, jobIndex, jobCount);
    }

    /**
     * Initialize the result object (one per job)
     * @return result Object
     */
    @Override
    protected BoundaryResult initializeResult() {
        return new BoundaryResult();
    }

    /**
     * Merge partial result objects to produce the final result object
     * @param partialResults partial result objects
     */
    @Override
    protected void merge(final List<BoundaryResult> partialResults) {
        for (BoundaryResult partial : partialResults) {

            // column boundaries:
            if ((partial._columnLowerIndex != UNDEFINED_LOWER_INDEX) && (partial._columnLowerIndex < _result._columnLowerIndex)) {
                _result._columnLowerIndex = partial._columnLowerIndex;
            }
            if ((partial._columnUpperIndex != UNDEFINED_UPPER_INDEX) && (partial._columnUpperIndex > _result._columnUpperIndex)) {
                _result._columnUpperIndex = partial._columnUpperIndex;
            }
            // row boundaries:
            if ((partial._rowLowerIndex != UNDEFINED_LOWER_INDEX) && (partial._rowLowerIndex < _result._rowLowerIndex)) {
                _result._rowLowerIndex = partial._rowLowerIndex;
            }
            if ((partial._rowUpperIndex != UNDEFINED_UPPER_INDEX) && (partial._rowUpperIndex > _result._rowUpperIndex)) {
                _result._rowUpperIndex = partial._rowUpperIndex;
            }
        }
    }

    /**
     * Process the given value at the given row and column index
     * 
     * @param col row index
     * @param row column index
     */
    @Override
    protected void processValue(final int col, final int row, final float value) {
        if (value != 0f && value >= _threshold) {
            // column boundaries:
            if (col < _result._columnLowerIndex) {
                _result._columnLowerIndex = col;
            }
            if (col > _result._columnUpperIndex) {
                _result._columnUpperIndex = col;
            }
            // row boundaries:
            if (row < _result._rowLowerIndex) {
                _result._rowLowerIndex = row;
            }
            if (row > _result._rowUpperIndex) {
                _result._rowUpperIndex = row;
            }
        }
    }

    /**
     * Return the lower column index where projected data != 0.0
     * @return lower column index where projected data != 0.0
     */
    public int getColumnLowerIndex() {
        return _result._columnLowerIndex;
    }

    /**
     * Return the upper column index where projected data != 0.0
     * @return upper column index where projected data != 0.0
     */
    public int getColumnUpperIndex() {
        return _result._columnUpperIndex;
    }

    /**
     * Return the lower row index where projected data != 0.0
     * @return lower row index where projected data != 0.0
     */
    public int getRowLowerIndex() {
        return _result._rowLowerIndex;
    }

    /**
     * Return the upper row index where projected data != 0.0
     * @return upper row index where projected data != 0.0
     */
    public int getRowUpperIndex() {
        return _result._rowUpperIndex;
    }

    /**
     * Result container
     */
    protected static class BoundaryResult {
        /* members */

        /** lower column index where projected data != 0.0 */
        protected int _columnLowerIndex = UNDEFINED_LOWER_INDEX;
        /** upper column index where projected data != 0.0 */
        protected int _columnUpperIndex = UNDEFINED_UPPER_INDEX;
        /** lower row index where projected data != 0.0 */
        protected int _rowLowerIndex = UNDEFINED_LOWER_INDEX;
        /** upper row index where projected data != 0.0 */
        protected int _rowUpperIndex = UNDEFINED_UPPER_INDEX;
    }
}
