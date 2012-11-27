/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

/**
 * This class handles double number comparisons with absolute error and number helper methods
 * http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm
 *
 * @author bourgesl
 */
public final class NumberUtils {

    /**
     * Smallest positive number used in double comparisons (rounding).
     */
    public final static double EPSILON = 1e-6d;

    /**
     * Private constructor
     */
    private NumberUtils() {
        super();
    }

    /**
     * Returns true if two doubles are considered equal.  
     * Test if the absolute difference between two doubles has a difference less than EPSILON.
     *
     * @param a double to compare.
     * @param b double to compare.
     * @return true true if two doubles are considered equal.
     */
    public static boolean equals(final double a, final double b) {
        return equals(a, b, EPSILON);
    }

    /**
     * Returns true if two doubles are considered equal. 
     * 
     * Test if the absolute difference between the two doubles has a difference less then a given
     * double (epsilon).
     *
     * @param a double to compare.
     * @param b double to compare
     * @param epsilon double which is compared to the absolute difference.
     * @return true if a is considered equal to b.
     */
    public static boolean equals(final double a, final double b, final double epsilon) {
        return (a == b) ? true : (Math.abs(a - b) < epsilon);
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double.  
     * 
     * Test if the difference of first minus second is greater than EPSILON.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     *              double
     */
    public static boolean greaterThan(final double a, final double b) {
        return greaterThan(a, b, EPSILON);
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double.
     *
     * Test if the difference of first minus second is greater then
     * a given double (epsilon).
     *
     * @param a first double
     * @param b second double
     * @param epsilon double which is compared to the absolute difference.
     * @return true if the first double is considered greater than the second
     *              double
     */
    public static boolean greaterThan(final double a, final double b, final double epsilon) {
        return a + epsilon - b > 0d;
    }

    /**
     * Returns true if the first double is considered less than the second
     * double.
     *
     * Test if the difference of second minus first is greater than EPSILON.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     *              double
     */
    public static boolean lessThan(final double a, final double b) {
        return greaterThan(b, a, EPSILON);
    }

    /**
     * Returns true if the first double is considered less than the second
     * double.  Test if the difference of second minus first is greater then
     * a given double (epsilon).  Determining the given epsilon is highly
     * dependant on the precision of the doubles that are being compared.
     *
     * @param a first double
     * @param b second double
     * @param epsilon double which is compared to the absolute difference.
     * @return true if the first double is considered less than the second
     *              double
     */
    public static boolean lessThan(final double a, final double b, final double epsilon) {
        return a - epsilon - b < 0d;
    }

    /**
     * Returns an {@code Integer} instance representing the specified
     * {@code int} value.  If a new {@code Integer} instance is not
     * required, this method should generally be used in preference to
     * the constructor {@link #Integer(int)}, as this method is likely
     * to yield significantly better space and time performance by
     * caching frequently requested values.
     *
     * This method will always cache values in the range -128 to 128 * 1024,
     * inclusive, and may cache other values outside of this range.
     *
     * @param  i an {@code int} value.
     * @return an {@code Integer} instance representing {@code i}.
     */
    public static Integer valueOf(final int i) {
        return IntegerCache.get(i);
    }

    /**
     * Integer Cache to support the object identity semantics of autoboxing for values between
     * -128 and HIGH value (inclusive).
     */
    private static final class IntegerCache {

        /** lower value */
        static final int low = -128;
        /** higher value */
        static final int high = 128 * 1024;
        /** Integer cache */
        static final Integer cache[];

        static {
            // high value may be configured by system property (NumberUtils.IntegerCache.high)

            cache = new Integer[(high - low) + 1];
            int j = low;
            for (int k = 0, len = cache.length; k < len; k++) {
                cache[k] = new Integer(j++);
            }
        }

        /**
         * Return cached Integer instance or new one
         * @param i integer value
         * @return cached Integer instance or new one 
         */
        static Integer get(final int i) {
            if (i >= low && i <= high) {
                return cache[i + (-low)];
            }
            return new Integer(i);
        }

        /**
         * Forbidden constructor
         */
        private IntegerCache() {
        }
    }
}
