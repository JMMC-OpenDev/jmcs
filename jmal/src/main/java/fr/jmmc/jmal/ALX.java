/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Astronomical Library: ra/dec parser & formatter.
 *
 * Class regrouping usefull statics method to convert star coordinates between
 * different formats and units.
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA, Laurent BOURGES.
 */
public final class ALX {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(ALX.class.getName());
    /** Describe the micrometer (micron, or um) unit */
    public static final double MICRON = 1d;
    /** Describe the meter unit */
    public static final double METER = 1d;
    /** Describe the arcminute unit */
    public static final double ARCMIN = 1d;
    /** Specify the value of one arcminute in degrees */
    public static final double DEG_IN_ARCMIN = 60d;
    /** Specify the value of one arcminute in degrees */
    public static final double ARCMIN_IN_DEGREES = (1d / 60d);
    /** Describe the arcsecond unit */
    public static final double ARCSEC = 1d;
    /** Specify the value of one arcsecond in degrees */
    public static final double ARCSEC_IN_DEGREES = (1d / 3600d);
    /** Specify the value of one arcsecond in degrees */
    public static final double DEG_IN_ARCSEC = 3600d;
    /** Specify the value of one milli arcsecond in degrees */
    public static final double DEG_IN_MILLI_ARCSEC = 3600000d;
    /** Specify the value of one milli arcsecond in degrees */
    public static final double MILLI_ARCSEC_IN_DEGREES = (1d / 3600000d);
    /** Specify the value of one arcminute in arcsecond */
    public static final double ARCMIN_IN_ARCSEC = 60d;
    /** Specify the value of one hour in degrees */
    public static final double HOUR_IN_DEGREES = 360d / 24d;
    /** Specify the value of one hour in degrees */
    public static final double DEG_IN_HOUR = 24d / 360d;
    /** Specify the value of one minute in degrees */
    public static final double MIN_IN_DEG = 15d / 60d;
    /** Specify the value of one degree in minute */
    public static final double DEG_IN_MIN = 60d / 15d;
    /** Specify the value of one hour in minute */
    public static final double HOUR_IN_MIN = 60d;
    /** threshold for rounding millis (truncating) */
    public static final double MILLIS_ROUND_THRESHOLD = 0.5e-3d;
    
    /**
     * Forbidden constructor : utility class
     */
    private ALX() {
        super();
    }

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees or NaN if invalid value
     */
    public static double parseHMS(final String raHms) {

        // RA can be given as HH:MM:SS.TT or HH MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading space
        final String input = raHms.replace(':', ' ').trim();

        double hh = 0d;
        double hm = 0d;
        double hs = 0d;

        // Parse the given string
        try {
            final String[] tokens = input.split(" ");

            final int len = tokens.length;

            if (len > 0) {
                hh = Double.parseDouble(tokens[0]);
            }
            if (len > 1) {
                hm = Double.parseDouble(tokens[1]);
            }
            if (len > 2) {
                hs = Double.parseDouble(tokens[2]);
            }

        } catch (NumberFormatException nfe) {
            _logger.debug("format exception: ", nfe);
            hh = hm = hs = Double.NaN;
        }

        // Get sign of hh which has to be propagated to hm and hs
        final double sign = (input.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : hh already includes the sign :
        final double ra = (hh + sign * (hm * ARCMIN_IN_DEGREES + hs * ARCSEC_IN_DEGREES)) * HOUR_IN_DEGREES;

        if (_logger.isDebugEnabled()) {
            _logger.debug("HMS : ’{}' = '{}'.", raHms, ra);
        }

        return ra;
    }

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees [-180; -180] or NaN if invalid value
     */
    public static double parseRA(final String raHms) {
        double ra = parseHMS(raHms);

        // Set angle range [-180 - 180]
        if (ra > 180d) {
            ra -= 360d;
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("RA  : ’{}' = '{}'.", raHms, ra);
        }

        return ra;
    }

    /**
     * Convert the given Declinaison (DEC).
     *
     * @param decDms the declinaison as a DD:MM:SS.TT or DD MM SS.TT string.
     *
     * @return the declinaison as a double in degrees or NaN if invalid value
     */
    public static double parseDEC(final String decDms) {

        // DEC can be given as DD:MM:SS.TT or DD MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading space
        final String input = decDms.replace(':', ' ').trim();

        double dd = 0d;
        double dm = 0d;
        double ds = 0d;

        // Parse the given string
        try {
            final String[] tokens = input.split(" ");

            final int len = tokens.length;

            if (len > 0) {
                dd = Double.parseDouble(tokens[0]);
            }
            if (len > 1) {
                dm = Double.parseDouble(tokens[1]);
            }
            if (len > 2) {
                ds = Double.parseDouble(tokens[2]);
            }

        } catch (NumberFormatException nfe) {
            _logger.debug("format exception: ", nfe);
            dd = dm = ds = Double.NaN;
        }

        // Get sign of dd which has to be propagated to dm and ds
        final double sign = (input.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : dd already includes the sign :
        final double dec = dd + sign * (dm * ARCMIN_IN_DEGREES + ds * ARCSEC_IN_DEGREES);

        if (_logger.isDebugEnabled()) {
            _logger.debug("DEC : ’{}' = '{}'.", decDms, dec);
        }

        return dec;
    }

    /**
     * Return the DMS format of the given angle
     * 
     * Warning: this method creates a new StringBuilder(16) for each invocation; 
     * use toDMS(StringBuilder, double) instead to use a given StringBuilder instance
     *
     * @param angle angle in degrees within range [-90; 90]
     * @return string DMS representation
     */
    public static String toDMS(final double angle) {
        return toDMS(new StringBuilder(16), angle).toString();
    }

    /**
     * Return the HMS format of the given angle
     * 
     * Warning: this method creates a new StringBuilder(16) for each invocation; 
     * use toHMS(StringBuilder, double) instead to use a given StringBuilder instance
     *
     * @param angle angle in degrees > -360.0
     * @return string HMS representation, null otherwise
     */
    public static String toHMS(final double angle) {
        return toHMS(new StringBuilder(16), angle).toString();
    }

    /**
     * Append the DMS format of the given angle to given string builder
     * @param sb string builder to append into
     * @param angle angle in degrees within range [-90; 90]
     * @return given string builder
     */
    public static StringBuilder toDMS(final StringBuilder sb, final double angle) {
        return toDMS(sb, angle, 90d);
    }
        
    /**
     * Append the DMS format of the given angle to given string builder
     * @param sb string builder to append into
     * @param angle angle in degrees within range [-maxValue; maxValue]
     * @param maxValue maximum angle value in degrees
     * @return given string builder
     */
    public static StringBuilder toDMS(final StringBuilder sb, final double angle, final double maxValue) {
        final boolean negative;
        final double absAngle;
        if (angle < 0.0D) {
            negative = true;
            absAngle = -angle;
        } else {
            negative = false;
            absAngle = angle;
        }
        /* check boundaries */
        if (absAngle > maxValue) {
            return sb.append('~');
        }
        /* print deg field */
        final int iDeg = (int) Math.floor(absAngle);
        final double remainder = absAngle - iDeg;

        /* always print sign '+' as DEC is typically within range [-90; 90] */
        sb.append((negative) ? '-' : '+');
        if (iDeg < 10) {
            sb.append('0');
        }
        sb.append(iDeg);

        return toMS(sb, remainder);
    }

    /**
     * Append the HMS format of the given angle to given string builder
     * @param sb string builder to append into
     * @param angle angle in degrees > -360.0
     * @return given string builder
     */
    public static StringBuilder toHMS(final StringBuilder sb, final double angle) {
        final boolean negative;
        final double absAngle;
        if (angle < 0.0D) {
            negative = true;
            /* convert deg in hours */
            absAngle = -angle * DEG_IN_HOUR;
        } else {
            negative = false;
            /* convert deg in hours */
            absAngle = angle * DEG_IN_HOUR;
        }
        /* check boundaries */
        if (absAngle > 24d) {
            return sb.append('~');
        }
        /* print hour field */
        final int iHour = (int) Math.floor(absAngle);
        final double remainder = absAngle - iHour;

        /* avoid '+' for positive values as RA is typically within range [0.0; 24.0[ */
        if (negative) {
            sb.append('-');
        }
        if (iHour < 10) {
            sb.append('0');
        }
        sb.append(iHour);

        return toMS(sb, remainder);
    }

    private static StringBuilder toMS(final StringBuilder sb, final double angle) {
        final double fMinute = DEG_IN_ARCMIN * angle;
        final int iMinute = (int) Math.floor(fMinute);

        final double fSecond = ARCMIN_IN_ARCSEC * (fMinute - iMinute);
        final int iSecond = (int) Math.floor(fSecond);

        double remainder = fSecond - iSecond;

        // fix last digit by 2 ULP to fix 0.5 rounding
        remainder += 2.0 * Math.ulp(remainder);

        /* print min field */
        sb.append(':');
        if (iMinute < 10) {
            sb.append('0');
        }
        sb.append(iMinute);

        /* print min field */
        sb.append(':');
        if (iSecond < 10) {
            sb.append('0');
        }
        sb.append(iSecond);

        if (remainder >= MILLIS_ROUND_THRESHOLD) {
            final int iMillis = (int) Math.round(1e3d * remainder);
            sb.append('.');
            if (iMillis < 100) {
                sb.append('0');
            }
            if (iMillis < 10) {
                sb.append('0');
            }
            sb.append(iMillis);
        }
        return sb;
    }

    /**
     * Convert a value in arc-minute to minutes.
     *
     * @param arcmin the arc-minute value to convert.
     *
     * @return a double containing the converted value.
     */
    public static double arcmin2minutes(final double arcmin) {
        return arcmin * DEG_IN_HOUR;
    }

    /**
     * Convert a value in minutes to arc-minute.
     *
     * @param minutes the value in minutes to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2arcmin(final double minutes) {
        return minutes * HOUR_IN_DEGREES;
    }

    /**
     * Convert a value in arc-minute to degrees.
     *
     * @param arcmin the arc-minute value to convert.
     *
     * @return a double containing the converted value.
     */
    public static double arcmin2degrees(final double arcmin) {
        return arcmin * ARCMIN_IN_DEGREES;
    }

    /**
     * Convert a value in degrees to arc-minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2arcmin(final double degrees) {
        return degrees * DEG_IN_ARCMIN;
    }

    /**
     * Convert a minute value to degrees.
     *
     * @param minutes the value in minute to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2degrees(final double minutes) {
        return minutes * MIN_IN_DEG;
    }

    /**
     * Convert a value in degrees to minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2minutes(final double degrees) {
        return degrees * DEG_IN_MIN;
    }

    /**
     * Convert a value in hours to minute.
     *
     * @param hours the value in hours to convert.
     *
     * @return a double containing the converted value.
     */
    public static double hours2min(final double hours) {
        return hours * HOUR_IN_MIN;
    }
}
/*___oOo___*/
