/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.awt.Font;
import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author bourgesl
 */
public final class SpecialChars {

    /** greek chars */
    /** delta (upper case) */
    public final static String DELTA_UPPER = "\u0394";
    /** lambda (lower case) */
    public final static String LAMBDA_LOWER = "\u03BB";
    /** mu (lower case) */
    public final static String MU_LOWER = "µ";
    /* symbols */
    /** copyright symbol (c) */
    public final static String SYMBOL_COPYRIGHT = "\u00A9";
    /** information symbol (lower case) */
    public final static String SYMBOL_INFO = (!SystemUtils.IS_OS_WINDOWS && canDisplay('\u2139')) ? "\u2139" : "i";
    /* units */
    /** micron unit (µm) (lower case) */
    public final static String UNIT_MICRO_METER = MU_LOWER + "m";
    /** Mega lambda unit (Ml) (lower case) */
    public final static String UNIT_MEGA_LAMBDA = "M" + LAMBDA_LOWER;
    
    /**
     * Private constructor (utility class)
     */
    private SpecialChars() {
        // no-op
    }
    
    /**
     * Test if the default font (SansSerif) can display the given character
     * @param ch character to check
     * @return true if the default font (SansSerif) can display the given character; false otherwise
     */
    private static boolean canDisplay(final char ch) {
        final Font font = new Font("SansSerif", Font.PLAIN, 12);
        return (font.canDisplay(ch));
    }
}
