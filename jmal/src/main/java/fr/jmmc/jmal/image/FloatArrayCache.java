/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import fr.jmmc.jmal.util.GenericWeakCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple float[][] weak cache
 * @author bourgesl
 */
public final class FloatArrayCache {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(FloatArrayCache.class.getName());

    /** weak image cache for createImage()/recycleImage() */
    private final static GenericWeakCache<float[][]> array2dCache = new GenericWeakCache<float[][]>("FloatArray2D", true) {

        @Override
        protected boolean checkSizes(float[][] array2D, int length, int length2) {
            return (array2D.length == length && array2D[0].length == length2);
        }

        @Override
        public String getSizes(float[][] array2D) {
            return String.format("%d x %d", array2D.length, array2D[0].length);
        }
    };

    public static float[][] getArray(int length, int length2) {
        float[][] array2D = array2dCache.getItem(length, length2);
        if (array2D != null) {
            return array2D;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("create a new array: {} x {}", length + 4, length2 + 4);
        }
        return new float[length][length2];
    }

    public static void recycleArray(final float[][] array2D) {
        array2dCache.putItem(array2D);
    }

    private FloatArrayCache() {
        super();
    }

}
