/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility class gathers methods related to 2D array manipulation (images)
 * @author bourgesl
 */
public final class ImageArrayUtils {

    /**
     * Forbidden constructor
     */
    private ImageArrayUtils() {
        super();
    }

    public static final float[][] copy(final int rows, final int cols, final float[][] input) {
        return copy(rows, cols, input, new float[rows][cols]);
    }

    public static final float[][] copy(final int rows, final int cols, final float[][] input, final float[][] output) {
        return copy(rows, cols, input, output, 0);
    }

    public static final float[][] copy(final int rows, final int cols, final float[][] input, final float[][] output, final int rowOffset) {

        for (int j = 0; j < rows; j++) {
            System.arraycopy(input[j], 0, output[j + rowOffset], 0, cols);
        }

        return output;
    }

    public static final float[][] enlarge(final int rows, final int cols, final float[][] input, final int rowsDest, final int colsDest) {
        if (rowsDest < rows || colsDest < cols) {
            return null;
        }
        if (rowsDest == rows && colsDest == cols) {
            return copy(rows, cols, input);
        }
        final float[][] output = new float[rowsDest][colsDest];

        // center image in output:
        final int rowOffset = (rowsDest - rows) / 2;
        final int colOffset = (colsDest - cols) / 2;

        for (int j = 0; j < rows; j++) {
            System.arraycopy(input[j], 0, output[j + rowOffset], colOffset, cols);
        }

        return output;
    }

    public static final float[][] extract(final int rows, final int cols, final float[][] input, final int rowsDest, final int colsDest) {
        if (rowsDest > rows || colsDest > cols) {
            return null;
        }
        if (rowsDest == rows && colsDest == cols) {
            return copy(rows, cols, input);
        }
        final float[][] output = new float[rowsDest][colsDest];

        // extract image at the center of the input:
        final int rowOffset = (rows - rowsDest) / 2;
        final int colOffset = (cols - colsDest) / 2;

        for (int j = 0; j < rowsDest; j++) {
            System.arraycopy(input[j + rowOffset], colOffset, output[j], 0, colsDest);
        }

        return output;
    }

    public static final float[][] extract(final int rows, final int cols, final float[][] input,
                                          final int rows1, final int cols1, final int rows2, final int cols2) {
        if (rows1 < 0 || rows1 > rows || cols1 < 0 || cols1 > cols
                || rows2 < 0 || rows2 > rows || cols2 < 0 || cols2 > cols
                || rows2 < rows1 || cols2 < cols1) {
            return null;
        }
        if (rows1 == 0 && rows2 == rows && cols1 == 0 && cols2 == cols) {
            return copy(rows, cols, input);
        }

        int destRows = rows2 - rows1;
        int destCols = cols2 - cols1;

        final float[][] output = new float[destRows][destCols];

        for (int j = 0; j < destRows; j++) {
            System.arraycopy(input[j + rows1], cols1, output[j], 0, destCols);
        }

        return output;
    }

    /**
     * Copy all values from input array to output array
     * @param rows number of rows
     * @param cols number of columns
     * @param input input array
     * @param output output array
     */
    public static void multiArrayCopy(final int rows, final int cols, final float[][] input, final float[][] output) {
        for (int j = 0; j < rows; j++) {
            System.arraycopy(input[j], 0, output[j], 0, cols);
        }
    }

    /**
     * Return the sum of all values in the given array
     * @param data values to sum
     * @return sum of all values
     */
    public static float sum(final float[] data) {
        float sum = 0f;

        for (int i = 0, len = data.length; i < len; i++) {
            sum += data[i];
        }
        return sum;
    }
}
