/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.awt.Color;

/**
 * Color to String encoder
 * 
 * @author Guillaume MELLA.
 */
public class ColorEncoder {

    /** Hidden constructor */
    private ColorEncoder() {
    }

    /**
     * Convert the given integer to an hexadecimal string value
     *
     * @param i integer value
     *
     * @return hexadecimal string value
     */
    private static String hexForInt(final int i) {
        if (i == 0) {
            return "00";
        }

        return Integer.toHexString(i);
    }

    /** 
     * Converts a String to an integer and returns the specified opaque Color.
     * This method handles string formats that are used to represent octal and
     * hexidecimal numbers.
     * @param c The color to encode
     * @return the encoded string for given color
     */
    public static String encode(final Color c) {
        String ret = "#";
        ret += hexForInt(c.getRed());
        ret += hexForInt(c.getGreen());
        ret += hexForInt(c.getBlue());

        return ret;
    }
}
