/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import cds.astro.Sptype;
import fr.jmmc.jmal.star.Star;
import fr.jmmc.jmal.star.Star.Property;
import java.text.DecimalFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Astronomical Library Extension.
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
    public static final double MICRON = 1.0d;
    /** Describe the meter unit */
    public static final double METER = 1.0d;
    /** Describe the arcminute unit */
    public static final double ARCMIN = 1.0d;
    /** Specify the value of one arcminute in degrees */
    public static final double ARCMIN_IN_DEGREES = (1.0d / 60.0d);
    /** Describe the arcsecond unit */
    public static final double ARCSEC = 1.0d;
    /** Specify the value of one arcsecond in degrees */
    public static final double ARCSEC_IN_DEGREES = (1.0d / 3600.0d);
    /** Specify the value of one milli arcsecond in degrees */
    public static final double MILLI_ARCSEC_IN_DEGREES = ARCSEC_IN_DEGREES / 1000d;
    /** Sun surface gravity  = 4.378 cm s-2 (AQ, 340/14 SUN) */
    public static final double SUN_LOGG = 4.378;

    /** Star type enumeration DWARF/GIANT/SUPERGIANT */
    public enum STARTYPE {

        /** Dwarf */
        DWARF,
        /** Giant */
        GIANT,
        /** Super Giant */
        SUPERGIANT
    }

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
     * @return the right ascension as a double in degrees.
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
            hh = 0d;
            hm = 0d;
            hs = 0d;
        }

        // Get sign of hh which has to be propagated to hm and hs
        final double sign = (input.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : hh already includes the sign :
        final double ra = (hh + sign * (hm / 60d + hs / 3600d)) * 15d;

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
     * @return the right ascension as a double in degrees  [-180; -180].
     */
    public static double parseRA(final String raHms) {
        double ra = parseHMS(raHms);

        // Set angle range [-180 - 180]
        if (ra > 180d) {
            ra = -1d * (360d - ra);
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
     * @return the declinaison as a double in degrees.
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
            dd = 0d;
            dm = 0d;
            ds = 0d;
        }

        // Get sign of dd which has to be propagated to dm and ds
        final double sign = (input.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : dd already includes the sign :
        final double dec = dd + sign * (dm / 60d + ds / 3600d);

        if (_logger.isDebugEnabled()) {
            _logger.debug("DEC : ’{}' = '{}'.", decDms, dec);
        }

        return dec;
    }

    /**
     * Return the DMS format of the given angle
     * @param angle angle in degrees > -360.0
     * @return string DMS representation
     */
    public static String toDMS(final double angle) {
        if (angle < -360d) {
            return null;
        }

        final double normalizedAngle = Math.abs(angle) % 360d;

        final int iDeg = (int) Math.floor(normalizedAngle);
        final double rest = normalizedAngle - iDeg;

        final StringBuilder sb = new StringBuilder();
        if (angle < 0d) {
            sb.append("-");
        }
        sb.append(iDeg);
        toMS(rest, sb);
        return sb.toString();
    }

    /**
     * Return the HMS format of the given angle
     * @param angle angle in degrees > -360.0
     * @return string HMS representation, null otherwise
     */
    public static String toHMS(final double angle) {
        if (angle < -360d) {
            return null;
        }

        final double normalizedAngle = (angle + 360d) % 360d;

        final double fHour = 24d * (normalizedAngle / 360d);
        final int iHour = (int) Math.floor(fHour);
        final double rest = normalizedAngle - (iHour / 24d * 360d);

        final StringBuilder sb = new StringBuilder();
        sb.append(iHour);
        toMS(rest, sb);
        return sb.toString();
    }

    private static String toMS(final double angle, final StringBuilder sb) {
        final double fMinute = 60d * angle;
        final int iMinute = (int) Math.floor(fMinute);

        final double fSecond = 60d * (fMinute - iMinute);

        DecimalFormat formatter = new DecimalFormat(":00");
        sb.append(formatter.format(iMinute));

        formatter = new DecimalFormat(":00.###");
        sb.append(formatter.format(fSecond));

        return sb.toString();
    }

    /**
     * Extract one or more spectral types of the given spectral type.
     *
     * @param rawSpectralType the spectral type to analyze.
     *
     * @return a List of String containing found spectral types (if any).
     */
    public static List<String> spectralTypes(final String rawSpectralType) {

        String spectralType = rawSpectralType;

        // Remove any "SB" token (Feedback Report ID : #1259360028)
        if (spectralType.contains("SB")) {
            spectralType = spectralType.replaceAll("SB", "");
        }

        final List<String> foundSpectralTypes = new ArrayList<String>();

        for (int i = 0, len = spectralType.length(); i < len; i++) {
            final char c = spectralType.charAt(i);

            // If the luminosity class has been reached
            if ((c == 'I') || (c == 'V')) {
                // Skip those characters
                continue;
            }

            // If the spectral type has been reached
            // eg. the uppercase alphabetic parts of a spectral type
            if (Character.isLetter(c) && Character.isUpperCase(c)) {
                // Re-copy its content for later use (as a String object)
                foundSpectralTypes.add(Character.valueOf(c).toString());
            }
        }

        return foundSpectralTypes;
    }

    /**
     * Extract one or more luminosity classes of the given spectral type.
     *
     * @param rawSpectralType the spectral type to analyze.
     *
     * @return a Vector of String containing found luminosity classes (if any).
     */
    public static List<String> luminosityClasses(final String rawSpectralType) {

        final List<String> foundLuminosityClasses = new ArrayList<String>();

        String foundLuminosityClass = "";
        boolean luminosityClassFound = false;

        int rawSpectralTypeSize = rawSpectralType.length();

        // Scan every given spectral type characters
        for (int i = 0; i < rawSpectralTypeSize; i++) {
            char c = rawSpectralType.charAt(i);

            // If a luminosity class has been reached
            // eg. a part of a spectral type composed of I & V (roman numbers)
            if ((c == 'I') || (c == 'V')) {
                // Re-copy its content to build a result string
                foundLuminosityClass = foundLuminosityClass + c;

                // Mark the discovery
                luminosityClassFound = true;

                // If we are on the last char of the spectral type
                if (i == (rawSpectralTypeSize - 1)) {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);
                }
            } else {
                // if a luminosiy class was just entirely found
                if (luminosityClassFound) {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);

                    // Reset in case another luminosity class can be found
                    foundLuminosityClass = "";
                    luminosityClassFound = false;
                }
            }
        }

        return foundLuminosityClasses;
    }

    /**
     * Convert a value in arc-minute to minutes.
     *
     * @param arcmin the arc-minute value to convert.
     *
     * @return a double containing the converted value.
     */
    public static double arcmin2minutes(final double arcmin) {
        final double minutes = (arcmin / 15d);

        return minutes;
    }

    /**
     * Convert a value in minutes to arc-minute.
     *
     * @param minutes the value in minutes to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2arcmin(final double minutes) {
        final double arcmin = (minutes * 15d);

        return arcmin;
    }

    /**
     * Convert a value in arc-minute to degrees.
     *
     * @param arcmin the arc-minute value to convert.
     *
     * @return a double containing the converted value.
     */
    public static double arcmin2degrees(final double arcmin) {
        final double degrees = (arcmin / 60d);

        return degrees;
    }

    /**
     * Convert a value in degrees to arc-minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2arcmin(final double degrees) {
        final double arcmin = (degrees * 60d);

        return arcmin;
    }

    /**
     * Convert a minute value to degrees.
     *
     * @param minutes the value in minute to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2degrees(final double minutes) {
        final double degrees = minutes / 4d;

        return degrees;
    }

    /**
     * Convert a value in degrees to minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2minutes(final double degrees) {
        final double minutes = degrees * 4d;

        return minutes;
    }

    /**
     * Return on Sptype object (CDS lib) according to given spectral type as string.
     * Please use this method instead of directly instantiating Sptype object so we can
     * adapt some feature in the futur. Fallback may probably be implemented to improve parsability.
     *
     * @param spectralType spectral type value
     * @return initialized Sptype object
     * @throws ParseException if given spectral type is not being parsable
     */
    public static Sptype getSptype(final String spectralType) throws ParseException {
        final Sptype s = new Sptype(spectralType);

        _logger.debug("Parsing of sptype '{}' get numerical value of: {}", spectralType, s.getSpNumeric());

        return s;
    }

    /**
     * Compute teff and logg from given spectral type and return a star
     * with Uniform diameters properties computed from the nearest
     * teff and logg found in the various tables .
     * ld sign is not checked, so negative values will be returned for a given
     * negative diameter.
     * @param ld limb darkened diameter
     * @param sptype spectral type value
     * @return a Star with UD properties.
     * @throws ParseException if given spectral type is not being parsable
     */
    public static Star ld2ud(final double ld, final String sptype) throws ParseException {
        final Sptype s = getSptype(sptype);
        final double teff = LD2UD.getEffectiveTemperature(s);
        final double logg = LD2UD.getGravity(s);
        return ld2ud(ld, teff, logg);
    }

    /**
     * Return a star with Uniform diameters properties computed from the nearest
     * teff and logg found in the various tables.
     *
     * @param ld should be a diamvk.
     * @param teff effective temperature
     * @param logg surface gravity
     * @return a Star with UD properties.     
     */
    public static Star ld2ud(final double ld, final double teff, final double logg) {
        final Star star = new Star();
        star.setPropertyAsDouble(Property.TEFF, teff);
        star.setPropertyAsDouble(Property.LOGG, logg);
        final Property[] uds = new Property[]{
            Property.UD_B, Property.UD_I, Property.UD_J, Property.UD_H, Property.UD_K,
            Property.UD_L, Property.UD_N, Property.UD_R, Property.UD_U, Property.UD_V
        };

        for (Property ud : uds) {
            final double diam = ld / LD2UD.getLimbDarkenedCorrectionFactor(ud, teff, logg);
            star.setPropertyAsDouble(ud, diam);
        }
        return star;
    }

    /**
     * Helper that returns the first part of the numeric code for a given spectral type as string.
     * @see #getTemperatureClass(cds.astro.Sptype) 
     *
     * @param spectype spectral type value
     * @return integer corresponding to first numeric code part.
     * @throws ParseException if given spectral type is not being parsable
     */
    public static int getTemperatureClass(final String spectype) throws ParseException {
        return getTemperatureClass(getSptype(spectype));
    }

    /**
     * Return the first part of the numeric code return by CDS tool.
     *
     * @param sptype
     * @return integer corresponding to first numeric code part.
     */
    public static int getTemperatureClass(final Sptype sptype) {
        final String spNum = sptype.getSpNumeric();
        final int firstDotIndex = spNum.indexOf(".");
        return Integer.parseInt(spNum.substring(0, firstDotIndex));
    }

    /**
     * Return one luminosity class code from 00 to 99 according given spectral type.
     * 00 is a special case that indicated one missing luminosity class.
     * 
     *        >NN<
     * 0112.00113.000000000 I
     * 0112.00024.000000000 II
     * 0112.00032.000000000 III
     * 0112.00040.000000000 IV
     * 0112.00048.000000000 V
     *     
     * @param spectype spectral type value
     * @return the luminosity integer value
     * @throws ParseException if given spectral type is not being parsable
     */
    public static int getLuminosityClass(final String spectype) throws ParseException {
        return getLuminosityClass(getSptype(spectype));
    }

    /**
     * Return one luminosity class code from 00 to 99 according given spectral type.
     * 00 is a special case that indicated one missing luminosity class.
     * 
     *        >NN<
     * 0112.00113.000000000 I
     * 0112.00024.000000000 II
     * 0112.00032.000000000 III
     * 0112.00040.000000000 IV
     * 0112.00048.000000000 V
     *     
     * @param sptype spectral type value
     * @return the luminosity integer value     
     */
    public static int getLuminosityClass(final Sptype sptype) {
        final String spNum = sptype.getSpNumeric();
        //int firstDotIndex = spNum.indexOf(".");
        final int secondDotIndex = spNum.lastIndexOf(".");
        // we must only use the two last chars of lum part as significative
        // I classes returned 113 instead of 13

        final int lumCode = Integer.parseInt(spNum.substring(secondDotIndex - 2, secondDotIndex));

        // Check that luminosity code has been extracted properly
        if (lumCode < 0 || lumCode > 100) {
            throw new IllegalStateException("Luminosity code extracted must not exceed 99 was " + lumCode);
        }

        return lumCode;
    }

    /**
     * Helper that returns one  STARTYPE according given spectral type.
     * @see #getStarType(cds.astro.Sptype)
     * @param sptype spectral type value
     * @return dwarf, giant or supergiant
     */
    public static STARTYPE getStarType(final String sptype) {
        // Daniel wrote:
        // If the luminosity class is unknown, by default one can suppose that
        // the star is a -g-i-a-n-t- dwarf
        // (cds sptypes returns 0 when luminosity code is missing)
        try {
            final Sptype s = getSptype(sptype);
            return getStarType(s);
        } catch (ParseException ex) {
            _logger.warn("Returning Dwarf because spectral type can not be parsed ({}) reason: {}", sptype, ex.getMessage());
        }
        return STARTYPE.DWARF;
    }

    /**
     * Return one STARTYPE according given spectral type.
     * @see #getLuminosityClass(cds.astro.Sptype) javadoc for magic numbers
     * @param sptype spectral type value
     * @return dwarf, giant or supergiant
     */
    public static STARTYPE getStarType(final Sptype sptype) {

        final int lumCode = ALX.getLuminosityClass(sptype);

        if (lumCode > 37 || lumCode == 0) {
            _logger.debug("This star is handled as a Dwarf");
            return STARTYPE.DWARF;
        }
        //Giants
        if (lumCode > 23) {
            _logger.debug("This star is handled as a Giant");
            return STARTYPE.GIANT;
        }
        // Supergiants
        _logger.debug("This star is handled as a SuperGiant");
        return STARTYPE.SUPERGIANT;
    }

    /**
     * Set this class with limited executable features.
     *
     * The user can use it giving one method name and argument value
     *  e.g.:
     *  ALX parseRA "1:1:1"
     *  ALX spectralTypes "M1/M2/IV/III"
     *  ALX luminosityClasses "M1/M2/IV/III"
     *
     * If no argument is given, then it prints out the usage form.
     */
    public static void main(String[] args) {
        Class<?> c = null;

        try {
            c = Class.forName(ALX.class.getName());

            String method = args[0];
            String arg = args[1];

            java.lang.reflect.Method userMethod = c.getMethod(method, String.class);
            System.out.println("" + userMethod.invoke(arg, arg));

        } catch (Throwable th) { // main (test)
            System.out.println("Usage: <progname> <methodName> <arg>");
            System.out.println("     where <methodName> can be:");

            if (c != null) {
                java.lang.reflect.Method[] m = c.getDeclaredMethods();
                for (int i = 0; i < m.length; i++) {
                    System.out.println("       - " + m[i].getName());
                }
            }
        }
    }
}
/*___oOo___*/
