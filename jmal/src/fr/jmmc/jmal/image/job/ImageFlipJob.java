/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import java.util.List;

/**
 * Basic Job dedicated to flip image values on the X or Y axis. 
 * Note: Image size must be an even number.
 * 
 * @author bourgesl
 */
public final class ImageFlipJob extends AbstractImageJob<Void> {

    /* members */
    /** true to flip x axis; false to flip y axis */
    final boolean _flipX;
    /** last index */
    final int _lastIdx;

    /**
     * Create the image Job
     *
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param flipX true to flip x axis; false to flip y axis
     */
    public ImageFlipJob(final float[][] array, final int width, final int height,
                        final boolean flipX) {
        // process only half rows or columns:
        super("ImageFlipJob", array, (flipX) ? width / 2 : width, (!flipX) ? height / 2 : height);
        if (width % 2 == 1) {
            throw new IllegalStateException("image width (" + width + ") must be an even number !");
        }
        if (height % 2 == 1) {
            throw new IllegalStateException("image height (" + height + ") must be an even number !");
        }
        this._flipX = flipX;
        this._lastIdx = (_flipX) ? 2 * _width - 1 : 2 * _height - 1;
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     */
    protected ImageFlipJob(final ImageFlipJob parentJob,
                           final int lineStart, final int lineEnd) {
        super(parentJob, lineStart, lineEnd);
        this._flipX = parentJob._flipX;
        this._lastIdx = parentJob._lastIdx;
    }

    /**
     * Initialize a new child job
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     * @return child job
     */
    @Override
    protected ImageFlipJob initializeChildJob(final int lineStart, final int lineEnd) {
        return new ImageFlipJob(this, lineStart, lineEnd);
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
        if (_flipX) {
            _array2D[row][col] = _array2D[row][_lastIdx - col];
            _array2D[row][_lastIdx - col] = value;
        } else {
            _array2D[row][col] = _array2D[_lastIdx - row][col];
            _array2D[_lastIdx - row][col] = value;
        }
    }
}