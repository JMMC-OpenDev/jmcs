/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ALX.java,v 1.9 2009-11-27 15:57:16 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import fr.jmmc.mcs.log.MCSLogger;

import java.util.*;


/**
 * Astronomical Library Extension.
 *
 * Class regrouping usefull statics method to convert star coordinates between
 * different formats and units.
 */
public class ALX
{
    /** Describe the micrometer (micron, or um) unit */
    public static final double MICRON = 1.0;

    /** Describe the meter unit */
    public static final double METER = 1.0;

    /** Describe the arcminute unit */
    public static final double ARCMIN = 1.0;

    /** Specify the value of one arcminute in degrees */
    public static final double ARCMIN_IN_DEGREES = (1.0 / 60.0);

    /** Describe the arcsecond unit */
    public static final double ARCSEC = 1.0;

    /** Specify the value of one arcsecond in degrees */
    public static final double ARCSEC_IN_DEGREES = (1.0 / 3600.0);

    /**
     * Convert the given Right Ascension (RA).
     *
     * @param raHms the right ascension as a HH:MM:SS.TT or HH MM SS.TT string.
     *
     * @return the right ascension as a double in degrees.
     */
    public static double parseRA(String raHms)
    {
        MCSLogger.trace();

        double hh;
        double hm;
        double hs;

        // RA can be given as HH:MM:SS.TT or HH MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading pace
        raHms     = raHms.replace(':', ' ');
        raHms     = raHms.trim();

        // Parse the given string
        // sscanf(raHms, "%f %f %f", &hh, &hm, &hs)
        try
        {
            /* !!! Replace by the code below to remove dependency on Java 1.5
               Scanner s = new Scanner(raHms).useDelimiter(" ");
               hh     = s.nextDouble();
               hm     = s.nextDouble();
               hs     = s.nextDouble();
               s.close(); */
            String[] tokens = raHms.split(" ");
            hh     = Double.parseDouble(tokens[0]);
            hm     = Double.parseDouble(tokens[1]);
            hs     = Double.parseDouble(tokens[2]);
        }
        catch (Exception e)
        {
            hh     = 0.0;
            hm     = 0.0;
            hs     = 0.0;
        }

        // Get sign of hh which has to be propagated to hm and hs
        double sign = (raHms.startsWith("-")) ? (-1.0) : 1.0;

        // Convert to degrees
        double ra = (hh + ((sign * hm) / 60.0) + ((sign * hs) / 3600.0)) * 15.0;

        // Set angle range [-180 - 180]
        if (ra > 180)
        {
            ra = -1.0 * (360 - ra);
        }

        MCSLogger.debug("RA  : ’" + raHms + "' = '" + ra + "'.");

        return ra;
    }

    /**
     * Convert the given Declinaison (DEC).
     *
     * @param raHms the declinaison as a DD:MM:SS.TT or DD MM SS.TT string.
     *
     * @return the declinaison as a double in degrees.
     */
    public static double parseDEC(String decDms)
    {
        MCSLogger.trace();

        double dd;
        double dm;
        double ds;

        // DEC can be given as DD:MM:SS.TT or DD MM SS.TT. 
        // Replace ':' by ' ', and remove trailing and leading pace
        decDms     = decDms.replace(':', ' ');
        decDms     = decDms.trim();

        // Parse the given string
        // sscanf(decDms, "%f %f %f", &dd, &dm, &ds)
        try
        {
            /* !!! Replace by the code below to remove dependency on Java 1.5
               Scanner s = new Scanner(decDms).useDelimiter(" ");
               dd     = s.nextDouble();
               dm     = s.nextDouble();
               ds     = s.nextDouble();
               s.close(); */
            String[] tokens = decDms.split(" ");
            dd     = Double.parseDouble(tokens[0]);
            dm     = Double.parseDouble(tokens[1]);
            ds     = Double.parseDouble(tokens[2]);
        }
        catch (Exception e)
        {
            dd     = 0.0;
            dm     = 0.0;
            ds     = 0.0;
        }

        // Get sign of hh which has to be propagated to hm and hs
        double sign = (decDms.startsWith("-")) ? (-1.0) : 1.0;

        // Convert to degrees
        double dec = dd + ((sign * dm) / 60.0) + ((sign * ds) / 3600.0);

        MCSLogger.debug("DEC : ’" + decDms + "' = '" + dec + "'.");

        return dec;
    }

    /**
     * Extract one or more spectral types of the given spectral type.
     *
     * @param rawSpectralType the spectral type to analyze.
     *
     * @return a Vector of String containing found spectral types (if any).
     */
    public static Vector spectralTypes(String rawSpectralType)
    {
        Vector foundSpectralTypes = new Vector();

        for (int i = 0; i < rawSpectralType.length(); i++)
        {
            char c = rawSpectralType.charAt(i);

            // If the luminosity class has been reached
            if ((c == 'I') || (c == 'V'))
            {
                // Skip those characters
                continue;
            }

            // If the spectral type has been reached
            // eg. the uppercase alphabetic parts of a spectral type
            if ((Character.isLetter(c) == true) &&
                    (Character.isUpperCase(c) == true))
            {
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
    public static Vector luminosityClasses(String rawSpectralType)
    {
        MCSLogger.trace();

        Vector  foundLuminosityClasses = new Vector();
        String  foundLuminosityClass   = "";
        boolean luminosityClassFound   = false;

        int     rawSpectralTypeSize    = rawSpectralType.length();

        // Scan every given spectral type characters
        for (int i = 0; i < rawSpectralTypeSize; i++)
        {
            char c = rawSpectralType.charAt(i);

            // If a luminosity class has been reached
            // eg. a part of a spectral type composed of I & V (roman numbers)
            if ((c == 'I') || (c == 'V'))
            {
                // Re-copy its content to build a result string
                foundLuminosityClass     = foundLuminosityClass + c;

                // Mark the discovery
                luminosityClassFound     = true;

                // If we are on the last char of the spectral type
                if (i == (rawSpectralTypeSize - 1))
                {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);
                }
            }
            else
            {
                // if a luminosiy class was just entirely found
                if (luminosityClassFound == true)
                {
                    // Store the luminosity class as a result
                    foundLuminosityClasses.add(foundLuminosityClass);

                    // Reset in case another luminosity class can be found
                    foundLuminosityClass     = "";
                    luminosityClassFound     = false;
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
    public static double arcmin2minutes(double arcmin)
    {
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
    public static double minutes2arcmin(double minutes)
    {
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
    public static double arcmin2degrees(double arcmin)
    {
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
    public static double degrees2arcmin(double degrees)
    {
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
    public static double minutes2degrees(double minutes)
    {
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
    public static double degrees2minutes(double degrees)
    {
        double minutes = degrees * 4;

        return minutes;
    }

    /** Set this class with limited executable features
     * The user can use it giving one method name and argument value
     *  e.g.:
     *  java -cp ../jmcs.jar fr.jmmc.mcs.astro.ALX parseRA "1:1:1"
     *  java -cp ../jmcs.jar fr.jmmc.mcs.astro.ALX spectralTypes "M1/M2/IV/III"
     *  java -cp ../jmcs.jar fr.jmmc.mcs.astro.ALX luminosityClasses "M1/M2/IV/III"
     *
     *  If no argument is given, then it prints out the usage form
     */
    public static void main(String[] args)
    {
        Class c = null;

        try
        {
            c = Class.forName(ALX.class.getName());

            String                   method     = args[0];
            String                   arg        = args[1];

            java.lang.reflect.Method userMethod = c.getMethod(method,
                    String.class);
            System.out.println("" + userMethod.invoke(arg, arg));
        }
        catch (Throwable e)
        {
            java.lang.reflect.Method[] m = c.getDeclaredMethods();
            System.out.println("Usage: <progname> <methodName> <arg>");
            System.out.println("     where <methodName> can be:");

            for (int i = 0; i < m.length; i++)
            {
                System.out.println("       - " + m[i].getName());
            }
        }
    }
}
/*___oOo___*/
