/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ALX.java,v 1.5 2008-05-30 12:31:11 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
     * Convert an arc-minute value to minutes.
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
     * Convert an arc-minute value to degrees.
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
}
/*___oOo___*/
