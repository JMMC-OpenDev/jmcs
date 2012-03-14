/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import fr.jmmc.jmal.image.job.ImageXYProjectionJob.ProjectionResult;
import java.util.List;

/**
 * Simple Job dedicated to compute sum of rows and columns on X and Y axes i.e. projection on both axes
 * @author bourgesl
 */
public final class ImageXYProjectionJob extends AbstractImageJob<ProjectionResult> {

    /** undefined index (-1) */
    public final static int UNDEFINED_INDEX = -1;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     */
    public ImageXYProjectionJob(final float[][] array, final int width, final int height) {
        super("ImageXYProjectionJob", array, width, height);
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     */
    protected ImageXYProjectionJob(final ImageXYProjectionJob parentJob, final int jobIndex, final int jobCount) {
        super(parentJob, jobIndex, jobCount);
    }

    /**
     * Initialize a new child job
     * @param jobIndex job index used to process data interlaced
     * @param jobCount total number of concurrent jobs
     * @return child job
     */
    @Override
    protected ImageXYProjectionJob initializeChildJob(final int jobIndex, final int jobCount) {
        return new ImageXYProjectionJob(this, jobIndex, jobCount);
    }

    /**
     * Initialize the result object (one per job)
     * @return result Object
     */
    @Override
    protected ProjectionResult initializeResult() {
        return new ProjectionResult(_width, _height);
    }

    /**
     * Merge partial result objects to produce the final result object
     * @param partialResults partial result objects
     */
    @Override
    protected void merge(final List<ProjectionResult> partialResults) {

        final int width = _width;
        final int height = _height;
        double[] resultData, partialData;

        for (ProjectionResult partial : partialResults) {

            resultData = _result._columnData;
            partialData = partial._columnData;

            // iterate on cols:
            for (int i = 0; i < width; i++) {
                resultData[i] += partialData[i];
            }

            resultData = _result._rowData;
            partialData = partial._rowData;

            // iterate on rows:
            for (int i = 0; i < height; i++) {
                resultData[i] += partialData[i];
            }

            // column boundaries:
            if ((partial._columnLowerIndex != UNDEFINED_INDEX)
                    && ((_result._columnLowerIndex == UNDEFINED_INDEX) || (partial._columnLowerIndex < _result._columnLowerIndex))) {
                _result._columnLowerIndex = partial._columnLowerIndex;
            }
            if ((partial._columnUpperIndex != UNDEFINED_INDEX)
                    && ((_result._columnUpperIndex == UNDEFINED_INDEX) || (partial._columnUpperIndex > _result._columnUpperIndex))) {
                _result._columnUpperIndex = partial._columnUpperIndex;
            }
            // row boundaries:
            if ((partial._rowLowerIndex != UNDEFINED_INDEX)
                    && ((_result._rowLowerIndex == UNDEFINED_INDEX) || (partial._rowLowerIndex < _result._rowLowerIndex))) {
                _result._rowLowerIndex = partial._rowLowerIndex;
            }
            if ((partial._rowUpperIndex != UNDEFINED_INDEX)
                    && ((_result._rowUpperIndex == UNDEFINED_INDEX) || (partial._rowUpperIndex > _result._rowUpperIndex))) {
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
        if (value != 0f) {
            _result._columnData[col] += value;
            _result._rowData[row] += value;

            // column boundaries:
            if ((_result._columnLowerIndex == UNDEFINED_INDEX) || (col < _result._columnLowerIndex)) {
                _result._columnLowerIndex = col;
            }
            if ((_result._columnUpperIndex == UNDEFINED_INDEX) || (col > _result._columnUpperIndex)) {
                _result._columnUpperIndex = col;
            }
            // row boundaries:
            if ((_result._rowLowerIndex == UNDEFINED_INDEX) || (row < _result._rowLowerIndex)) {
                _result._rowLowerIndex = row;
            }
            if ((_result._rowUpperIndex == UNDEFINED_INDEX) || (row > _result._rowUpperIndex)) {
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
     * Return the sum of data values on column axis (X)
     * @return sum of data values on column axis (X)
     */
    public double[] getColumnData() {
        return _result._columnData;
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
     * Return the sum of data values on row axis (Y)
     * @return sum of data values on row axis (Y)
     */
    public double[] getRowData() {
        return _result._rowData;
    }

    /**
     * Result container
     */
    protected static class ProjectionResult {

        /** image width */
        protected final int _width;
        /** image height */
        protected final int _height;
        /** lower column index where projected data != 0.0 */
        protected int _columnLowerIndex = UNDEFINED_INDEX;
        /** upper column index where projected data != 0.0 */
        protected int _columnUpperIndex = UNDEFINED_INDEX;
        /** sum of data values on column axis (X) */
        protected final double[] _columnData;
        /** lower row index where projected data != 0.0 */
        protected int _rowLowerIndex = UNDEFINED_INDEX;
        /** upper row index where projected data != 0.0 */
        protected int _rowUpperIndex = UNDEFINED_INDEX;
        /** sum of data values on row axis (Y) */
        protected final double[] _rowData;

        /**
         * Protected Constructor
         * @param width image width
         * @param height image height
         */
        ProjectionResult(final int width, final int height) {
            super();
            this._width = width;
            this._height = height;
            this._columnData = new double[width];
            this._rowData = new double[height];
        }
    }
}
