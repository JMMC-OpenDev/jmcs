/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ALX.java,v 1.27 2011-03-01 09:44:55 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.26  2011/02/28 10:43:43  bourgesl
 * use isLoggable(level) for logging statements containing string concatenations (optimisation)
 *
 * Revision 1.25  2011/02/28 08:49:23  mella
 * Remove test code from main to keeep executable form
 *
 * Revision 1.24  2011/02/28 08:47:53  mella
 * Move star type analyser from LD2UD part
 * Add minimum doc to explain magic numbers
 *
 * Revision 1.23  2011/02/23 16:43:51  mella
 * Fix lum class analysis returned by sptype of CDS
 *
 * Revision 1.22  2010/09/24 13:15:32  bourgesl
 * removed debug messages in toMS
 *
 * Revision 1.21  2010/09/24 11:58:53  lafrasse
 * Fixed round bug in toDms().
 * Renamed toDms() to toDMS()
 * Added toHMS()
 *
 * Revision 1.20  2010/09/09 15:57:10  bourgesl
 * new method toDMS(angle) to format the angle in degrees to the +/-DD:MM:SS.MMM
 *
 * Revision 1.19  2010/06/22 13:01:44  bourgesl
 * added constant MILLI_ARCSEC_IN_DEGREES
 *
 * Revision 1.18  2010/04/13 14:00:00  bourgesl
 * fixed syntax
 *
 * Revision 1.17  2010/04/08 08:40:50  bourgesl
 * added parseHMS method that converts HMS to degrees without any range correction like parseRA. Tested intensively with aspro EditableStarResolverWidget
 *
 * Revision 1.16  2010/02/18 11:02:30  mella
 * add logg and teff to the ld2ud outputs
 *
 * Revision 1.15  2010/01/20 13:58:31  mella
 * add javadoc
 *
 * Revision 1.14  2010/01/20 13:56:12  mella
 * remove duplicated code
 *
 * Revision 1.13  2010/01/11 22:06:33  mella
 * fix formulae to get ld from ud
 *
 * Revision 1.12  2010/01/08 16:22:22  mella
 * add most material to compute ld to ud
 *
 * Revision 1.11  2010/01/07 10:21:07  mella
 * Add first prototype version of ld2ud program
 *
 * Revision 1.10  2009/12/01 14:19:18  lafrasse
 * Corrected spectralType() to ignore 'SB' as 'S' and 'B'  spectral type tokens (Feedback Report ID : #1259360028).
 *
 * Revision 1.9  2009/11/27 15:57:16  lafrasse
 * Jalopization.
 *
 * Revision 1.8  2009/11/25 08:28:02  mella
 * Add executable feature
 *
 * Revision 1.7  2009/11/02 12:05:42  lafrasse
 * Added angle conversion constants.
 *
 * Revision 1.6  2009/10/20 12:38:45  lafrasse
 * Added conversion from degrees to minutes and vice et versa.
 *
 * Revision 1.5  2008/05/30 12:31:11  lafrasse
 * Changed convertRA() & convertDEC() APIs to parseRA() & parseDEC() respectively.
 * Added methods for "arcmin to/from degrees and minutes" conversion.
 *
 * Revision 1.4  2007/06/21 07:38:51  lafrasse
 * Jalopization.
 *
 * Revision 1.3  2007/05/16 14:34:43  lafrasse
 * Removed the dependency on the Java 1.5 'Scanner' class.
 *
 * Revision 1.2  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
 * Revision 1.1  2006/11/14 14:41:57  lafrasse
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro;

import cds.astro.Sptype;
import fr.jmmc.mcs.astro.star.Star;
import fr.jmmc.mcs.astro.star.Star.Property;
import java.text.DecimalFormat;

import java.text.ParseException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Astronomical Library Extension.
 *
 * Class regrouping usefull statics method to convert star coordinates between
 * different formats and units.
 */
public class ALX {

    /** Class name */
    static final String className_ = LD2UD.class.getName();
    /** Logger */
    static final Logger logger_ = Logger.getLogger(className_);
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

        DWARF,
        GIANT,
        SUPERGIANT
    }

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees.
     */
    public static double parseHMS(String raHms) {
        double hh;
        double hm;
        double hs;

        // RA can be given as HH:MM:SS.TT or HH MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading space
        raHms = raHms.replace(':', ' ');
        raHms = raHms.trim();

        // Parse the given string
        try {
            String[] tokens = raHms.split(" ");
            hh = Double.parseDouble(tokens[0]);
            hm = Double.parseDouble(tokens[1]);
            hs = Double.parseDouble(tokens[2]);
        } catch (Exception e) {
            hh = 0.0;
            hm = 0.0;
            hs = 0.0;
        }

        // Get sign of hh which has to be propagated to hm and hs
        final double sign = (raHms.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : dd already includes the sign :
        final double ra = (hh + sign * (hm / 60d + hs / 3600d)) * 15d;

        if (logger_.isLoggable(Level.FINE)) {
            logger_.fine("HMS  : ’" + raHms + "' = '" + ra + "'.");
        }

        return ra;
    }

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees  [-180 - 180].
     */
    public static double parseRA(String raHms) {
        double ra = parseHMS(raHms);

        // Set angle range [-180 - 180]
        if (ra > 180d) {
            ra = -1d * (360d - ra);
        }

        if (logger_.isLoggable(Level.FINE)) {
            logger_.fine("RA  : ’" + raHms + "' = '" + ra + "'.");
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
    public static double parseDEC(String decDms) {
        double dd;
        double dm;
        double ds;

        // DEC can be given as DD:MM:SS.TT or DD MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading space
        decDms = decDms.replace(':', ' ');
        decDms = decDms.trim();

        // Parse the given string
        try {
            String[] tokens = decDms.split(" ");
            dd = Double.parseDouble(tokens[0]);
            dm = Double.parseDouble(tokens[1]);
            ds = Double.parseDouble(tokens[2]);
        } catch (Exception e) {
            dd = 0.0;
            dm = 0.0;
            ds = 0.0;
        }

        // Get sign of dd which has to be propagated to dm and ds
        final double sign = (decDms.startsWith("-")) ? -1d : 1d;

        // Convert to degrees
        // note : dd already includes the sign :
        double dec = dd + sign * (dm / 60d + ds / 3600d);

        if (logger_.isLoggable(Level.FINE)) {
            logger_.fine("DEC : ’" + decDms + "' = '" + dec + "'.");
        }

        return dec;
    }

    /**
     * Return the DMS format of the given angle
     * @param angle angle in degrees > -360.0
     * @return string DMS representation
     */
    public final static String toDMS(final double angle) {
        if (angle < -360.0) {
            return null;
        }

        double normalizedAngle = Math.abs(angle) % 360.0;

        final int iDeg = (int) Math.floor(normalizedAngle);
        final double rest = normalizedAngle - iDeg;

        final StringBuilder sb = new StringBuilder();
        if (angle < 0.0) {
            sb.append("-");
        }
        sb.append(iDeg).append(toMS(rest));
        return sb.toString();
    }

    /**
     * Return the HMS format of the given angle
     * @param angle angle in degrees > -360.0
     * @return string HMS representation, null otherwise
     */
    public final static String toHMS(final double angle) {
        if (angle < -360.0) {
            return null;
        }
        double normalizedAngle = (angle + 360.0) % 360.0;
        double fHour = 24.0 * (normalizedAngle / 360.0);
        int iHour = (int) Math.floor(fHour);
        double rest = normalizedAngle - (iHour / 24.0 * 360.0);

        final StringBuilder sb = new StringBuilder();
        sb.append(iHour).append(toMS(rest));
        return sb.toString();
    }

    private final static String toMS(final double angle) {
        double fMinute = 60.0 * angle;
        int iMinute = (int) Math.floor(fMinute);

        double fSecond = 60.0 * (fMinute - iMinute);

        final StringBuilder sb = new StringBuilder();
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
     * @return a Vector of String containing found spectral types (if any).
     */
    public static Vector spectralTypes(String rawSpectralType) {
        // Remove any "SB" token (Feedback Report ID : #1259360028)
        if (rawSpectralType.contains("SB")) {
            rawSpectralType = rawSpectralType.replaceAll("SB", "");
        }

        Vector foundSpectralTypes = new Vector();

        for (int i = 0; i < rawSpectralType.length(); i++) {
            char c = rawSpectralType.charAt(i);

            // If the luminosity class has been reached
            if ((c == 'I') || (c == 'V')) {
                // Skip those characters
                continue;
            }

            // If the spectral type has been reached
            // eg. the uppercase alphabetic parts of a spectral type
            if ((Character.isLetter(c) == true)
                    && (Character.isUpperCase(c) == true)) {
                // Re-copy its content for later use (as a String object)
                foundSpectralTypes.add("" + c);
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
    public static Vector luminosityClasses(String rawSpectralType) {

        Vector foundLuminosityClasses = new Vector();
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
                if (luminosityClassFound == true) {
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
    public static double arcmin2minutes(double arcmin) {
        double minutes = (arcmin / 15);

        return minutes;
    }

    /**
     * Convert a value in minutes to arc-minute.
     *
     * @param minutes the value in minutes to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2arcmin(double minutes) {
        double arcmin = (minutes * 15);

        return arcmin;
    }

    /**
     * Convert a value in arc-minute to degrees.
     *
     * @param arcmin the arc-minute value to convert.
     *
     * @return a double containing the converted value.
     */
    public static double arcmin2degrees(double arcmin) {
        double degrees = (arcmin / 60);

        return degrees;
    }

    /**
     * Convert a value in degrees to arc-minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2arcmin(double degrees) {
        double arcmin = (degrees * 60);

        return arcmin;
    }

    /**
     * Convert a minute value to degrees.
     *
     * @param minutes the value in minute to convert.
     *
     * @return a double containing the converted value.
     */
    public static double minutes2degrees(double minutes) {
        double degrees = minutes / 4;

        return degrees;
    }

    /**
     * Convert a value in degrees to minute.
     *
     * @param degrees the value in degrees to convert.
     *
     * @return a double containing the converted value.
     */
    public static double degrees2minutes(double degrees) {
        double minutes = degrees * 4;

        return minutes;
    }

    /** 
     * ld should be a diamvk.
     */
    /**
     * Compute teff and logg from given spectral type and return a star
     * with Uniform diameters properties computed from the nearest
     * teff and logg found in the various tables .
     * @param ld
     * @param sptype
     * @return a Star with UD properties.
     * @throws ParseException
     */
    public static Star ld2ud(double ld, String sptype) throws ParseException {
        double teff = LD2UD.getEffectiveTemperature(sptype);
        double logg = LD2UD.getGravity(sptype);
        return ld2ud(ld, teff, logg);
    }

    /**
     * Return a star with Uniform diameters properties computed from the nearest
     * teff and logg found in the various tables.
     *
     * @param ld should be a diamvk.
     * @param teff
     * @param logg
     * @return a Star with UD properties.     
     */
    public static Star ld2ud(double ld, double teff, double logg) {
        Star star = new Star();
        star.setPropertyAsDouble(Property.TEFF, teff);
        star.setPropertyAsDouble(Property.LOGG, logg);
        Property[] uds = new Property[]{
            Property.UD_B, Property.UD_I, Property.UD_J, Property.UD_H, Property.UD_K,
            Property.UD_L, Property.UD_N, Property.UD_R, Property.UD_U, Property.UD_V
        };

        for (Property ud : uds) {
            double diam = ld / LD2UD.getLimbDarkenedCorrectionFactor(ud, teff, logg);
            star.setPropertyAsDouble(ud, diam);
        }
        return star;
    }

    public static int getTemperatureClass(String spectype) throws ParseException {
        Sptype sp = new Sptype(spectype);
        String spNum = sp.getSpNumeric();
        int firstDotIndex = spNum.indexOf(".");
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
     * @throws ParseException
     */
    public static int getLuminosityClass(String spectype) throws ParseException {
        Sptype sp = new Sptype(spectype);
        String spNum = sp.getSpNumeric();
        //int firstDotIndex = spNum.indexOf(".");
        int secondDotIndex = spNum.lastIndexOf(".");
        // we must only use the two last chars of lum part as significative
        // I classes returned 113 instead of 13

        int lumCode = Integer.parseInt(spNum.substring(secondDotIndex - 2, secondDotIndex));

        // Check that luminosity code has been extracted properly
        if (lumCode < 0 || lumCode > 100) {
            throw new IllegalStateException("Luminosity code extracted must not exceed 99 was " + lumCode);
        }

        return lumCode;
    }

    /**
     * Return STARTYPE according given spectral type.
     * @see getLuminosityClass javadoc for magic numbers
     * @param sptype
     * @return dwarf, giant or supergiant
     */
    public static STARTYPE getStarType(String sptype) {
        // Daniel wrote:
        // If the luminosity class is unknown, by default one can suppose that
        // the star is a giant (III)
        // (cds sptypes returns 0 when luminosity code is missing)

        int lumCode;
        try {
            lumCode = ALX.getLuminosityClass(sptype);
        } catch (ParseException ex) {
            if (logger_.isLoggable(Level.WARNING)) {
                logger_.warning("Returning Dwarf because spectral type can not be parsed (" + sptype + ") reason : "+ex.getMessage());
            }
            return STARTYPE.DWARF;
        }

        if (lumCode > 37 || lumCode == 0) {
            logger_.fine("This star his handled has a Dwarf");
            return STARTYPE.DWARF;
        }
        //Giants
        if (lumCode > 23) {
            logger_.fine("This star his handled has a Giant");
            return STARTYPE.GIANT;
        }
        // Supergiants
        logger_.fine("This star his handled has a SuperGiant");
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
        /*
        System.out.println("" + STARTYPE.DWARF);
        System.out.println("" + STARTYPE.GIANT);
        System.out.println("" + STARTYPE.SUPERGIANT);

        
        for (int i = -10050; i < 11000; i++) {
        double f = i / 27.9;
        System.out.println("toDMS(" + f + ") = '" + toDMS(f) + "'.");
        System.out.println("toHMS(" + f + ") = '" + toHMS(f) + "'.");
        }         
         */

        Class c = null;

        try {
            c = Class.forName(ALX.class.getName());

            String method = args[0];
            String arg = args[1];

            java.lang.reflect.Method userMethod = c.getMethod(method,
                    String.class);
            System.out.println("" + userMethod.invoke(arg, arg));
        } catch (Throwable e) {
            java.lang.reflect.Method[] m = c.getDeclaredMethods();
            System.out.println("Usage: <progname> <methodName> <arg>");
            System.out.println("     where <methodName> can be:");

            for (int i = 0; i < m.length; i++) {
                System.out.println("       - " + m[i].getName());
            }
        }
    }
}
/*___oOo___*/
