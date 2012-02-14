/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import edu.emory.mathcs.utils.ConcurrencyUtils;
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
     * Copy the given input 2D array into another 2D array (larger and power of two if needed)
     * 
     * @param rows number of rows
     * @param cols number of columns
     * @param input input 2D array
     * @return input 2D array (larger if needed) but each dimension is a power of two.
     */
    public static float[][] pad(final int rows, final int cols, final float[][] input) {

        logger.debug("pad: original image [{} x {}]", cols, rows);

        // TODO: check if the image is too large ??

        if ((rows == cols)) {
            // square image
            if (ConcurrencyUtils.isPowerOf2(rows)) {
                logger.debug("pad: copy image [{} x {}]", rows, rows);

                return ImageArrayUtils.copy(rows, cols, input);
            }
            // padding needed:
            final int newSize = ConcurrencyUtils.nextPow2(rows);

            logger.debug("pad: enlarge image [{} x {}]", newSize, newSize);

            // pad image with 0 keeping the input at the image center:
            return ImageArrayUtils.enlarge(rows, cols, input, newSize, newSize);
        }

        // rectangle image:
        final int maxSize = Math.max(rows, cols);

        if (ConcurrencyUtils.isPowerOf2(maxSize)) {
            logger.debug("pad: copy image [{} x {}]", maxSize, maxSize);

            // pad image with 0 keeping the input at the image center:
            return ImageArrayUtils.enlarge(rows, cols, input, maxSize, maxSize);
        }

        // padding needed:
        final int newSize = ConcurrencyUtils.nextPow2(maxSize);

        logger.debug("pad: enlarge image [{} x {}]", newSize, newSize);

        // pad image with 0 keeping the input at the image center:
        return ImageArrayUtils.enlarge(rows, cols, input, newSize, newSize);
    }
}
