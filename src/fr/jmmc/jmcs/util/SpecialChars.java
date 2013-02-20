/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author bourgesl
 */
public interface SpecialChars {

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
    public final static String SYMBOL_INFO = SystemUtils.IS_OS_WINDOWS ? "i" : "\u2139";
    /* units */
    /** micron unit (µm) (lower case) */
    public final static String UNIT_MICRO_METER = MU_LOWER + "m";
    /** Mega lambda unit (Ml) (lower case) */
    public final static String UNIT_MEGA_LAMBDA = "M" + LAMBDA_LOWER;
}
