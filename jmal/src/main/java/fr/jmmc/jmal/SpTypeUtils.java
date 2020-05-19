/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import cds.astro.Sptype;
import fr.jmmc.jmal.star.Star;
import fr.jmmc.jmal.star.Star.Property;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpType utilities using cds sptype.jar

 * @author bourgesl
 */
public final class SpTypeUtils {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SpTypeUtils.class.getName());

    /**
     * Forbidden constructor : utility class
     */
    private SpTypeUtils() {
        super();
    }

    /**
     * Extract one or more spectral types of the given spectral type.
     *
     * @param rawSpectralType the spectral type to analyze.
     * @param list optional list to store results (and reuse memory)
     *
     * @return a List of String containing found spectral types (if any).
     */
    public static List<String> spectralTypes(final String rawSpectralType,
                                             final List<String> list) {

        final List<String> foundSpectralTypes = (list == null) ? new ArrayList<String>() : list;
        if (!foundSpectralTypes.isEmpty()) {
            foundSpectralTypes.clear();
        }

        String spectralType = rawSpectralType;

        // Remove any "SB" token (Feedback Report ID : #1259360028)
        if (spectralType.contains("SB")) {
            spectralType = spectralType.replaceAll("SB", "");
        }

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
     * @param list optional list to store results (and reuse memory)
     *
     * @return a List of String containing found luminosity classes (if any).
     */
    public static List<String> luminosityClasses(final String rawSpectralType,
                                                 final List<String> list) {

        final List<String> foundLuminosityClasses = (list == null) ? new ArrayList<String>() : list;
        if (!foundLuminosityClasses.isEmpty()) {
            foundLuminosityClasses.clear();
        }

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
                foundLuminosityClass += c;

                // Mark the discovery
                luminosityClassFound = true;

                // If we are on the last char of the spectral type
                if (i == (rawSpectralTypeSize - 1)) {
                    // Store the luminosity class as a result
                    list.add(foundLuminosityClass);
                }
            } else {
                // if a luminosiy class was just entirely found
                if (luminosityClassFound) {
                    // Store the luminosity class as a result
                    list.add(foundLuminosityClass);

                    // Reset in case another luminosity class can be found
                    foundLuminosityClass = "";
                    luminosityClassFound = false;
                }
            }
        }

        return list;
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
     * Helper that returns the StarType according given spectral type.
     * @see #getStarType(cds.astro.Sptype)
     * @param sptype spectral type value
     * @return dwarf, giant or supergiant
     */
    public static StarType getStarType(final String sptype) {
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
        return StarType.DWARF;
    }

    /**
     * Return the StarType according given spectral type.
     * @see #getLuminosityClass(cds.astro.Sptype) javadoc for magic numbers
     * @param sptype spectral type value
     * @return dwarf, giant or supergiant
     */
    public static StarType getStarType(final Sptype sptype) {

        final int lumCode = getLuminosityClass(sptype);

        if (lumCode > 37 || lumCode == 0) {
            _logger.debug("This star is handled as a Dwarf");
            return StarType.DWARF;
        }
        //Giants
        if (lumCode > 23) {
            _logger.debug("This star is handled as a Giant");
            return StarType.GIANT;
        }
        // Supergiants
        _logger.debug("This star is handled as a SuperGiant");
        return StarType.SUPERGIANT;
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
     * @param args cmd line arguments
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
