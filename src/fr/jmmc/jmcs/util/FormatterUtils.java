/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Helper methods related to Formatter (NumberFormat, DateFormat ...)
 * @author bourgesl
 */
public final class FormatterUtils {

    /** formatter string buffer argument */
    private final static StringBuffer _fmtBuffer = new StringBuffer(32);
    /** ignore formatter position argument */
    private final static FieldPosition _ignorePosition = new FieldPosition(0);

    /**
     * Private constructor
     */
    private FormatterUtils() {
        super();
    }

    /* NumberFormat */
    /**
     * Format the given double value using given formater
     * 
     * Note: this method is not thread safe (synchronization must be performed by callers)
     * 
     * @param fmt formatter to use
     * @param val double value
     * @return formatted value
     */
    public static String format(final NumberFormat fmt, final double val) {
        // reset shared buffer:
        _fmtBuffer.setLength(0);

        return format(fmt, _fmtBuffer, val).toString();
    }

    /**
     * Format the given double value using given formater and append into the given string buffer
     * 
     * Note: this method is thread safe
     * 
     * @param fmt formatter to use
     * @param sb string buffer to append to
     * @param val double value
     * @return formatted value
     */
    public static StringBuffer format(final NumberFormat fmt, final StringBuffer sb, final double val) {
        return fmt.format(val, sb, _ignorePosition);
    }

    /* DateFormat */
    /**
     * Format the given date using given formater
     * 
     * Note: this method is not thread safe (synchronization must be performed by callers)
     * 
     * @param fmt formatter to use
     * @param val date
     * @return formatted value
     */
    public static String format(final DateFormat fmt, final Date val) {
        // reset shared buffer:
        _fmtBuffer.setLength(0);

        return format(fmt, _fmtBuffer, val).toString();
    }

    /**
     * Format the given date using given formater and append into the given string buffer
     * 
     * Note: this method is thread safe
     * 
     * @param fmt formatter to use
     * @param sb string buffer to append to
     * @param val date
     * @return formatted value
     */
    public static StringBuffer format(final DateFormat fmt, final StringBuffer sb, final Date val) {
        return fmt.format(val, sb, _ignorePosition);
    }
}
