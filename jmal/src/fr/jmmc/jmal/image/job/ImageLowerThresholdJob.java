/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic Job dedicated to replace image values lower than threshold by the threshold value
 * 
 * Note: use AtomicInteger to have a mutable Integer type not for concurrency issues
 * 
 * @author bourgesl
 */
public final class ImageLowerThresholdJob extends AbstractImageJob<AtomicInteger> {

    /** log coordinates + value when the threshold is reached */
    private final static boolean DEBUG = false;
    /* members */
    /** threshold value (exclusive) */
    final float _threshold;
    /** replacement value */
    final float _replaceBy;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param threshold threshold value to check
     * @param replaceBy replacement value to use
     */
    public ImageLowerThresholdJob(final float[][] array, final int width, final int height,
                                  final float threshold, final float replaceBy) {
        super("ImageLowerThresholdJob", array, width, height);
        this._threshold = threshold;
        this._replaceBy = replaceBy;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     */
    protected ImageLowerThresholdJob(final ImageLowerThresholdJob parentJob, final int jobIndex, final int jobCount) {
        super(parentJob, jobIndex, jobCount);
        this._threshold = parentJob._threshold;
        this._replaceBy = parentJob._replaceBy;
    }

    /**
     * Initialize a new child job
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     * @return child job
     */
    @Override
    protected ImageLowerThresholdJob initializeChildJob(final int jobIndex, final int jobCount) {
        return new ImageLowerThresholdJob(this, jobIndex, jobCount);
    }

    /**
     * Initialize the result object (one per job)
     * @return result Object
     */
    @Override
    protected AtomicInteger initializeResult() {
        return new AtomicInteger(0);
    }

    /**
     * Merge partial result objects to produce the final result object
     * @param partialResults partial result objects
     */
    @Override
    protected void merge(final List<AtomicInteger> partialResults) {
        for (AtomicInteger partial : partialResults) {
            _result.addAndGet(partial.get());
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
        if (value != 0f && value < _threshold) {
            if (DEBUG) {
                logger.info("threshold reached at column " + col + " row = " + row + " : " + value);
            }
            _array2D[row][col] = _replaceBy;
            _result.incrementAndGet();
        }
    }

    /**
     * Return the number of updated data values
     * @return number of updated data values
     */
    public int getUpdateCount() {
        return _result.get();
    }
}
