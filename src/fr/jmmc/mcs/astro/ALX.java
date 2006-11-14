/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ALX.java,v 1.1 2006-11-14 14:41:57 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.mcs.astro;

import jmmc.mcs.log.MCSLogger;

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
    public static double convertRA(String raHms)
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
            Scanner s = new Scanner(raHms).useDelimiter(" ");
            hh     = s.nextDouble();
            hm     = s.nextDouble();
            hs     = s.nextDouble();
            s.close();
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
    public static double convertDEC(String decDms)
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
            Scanner s = new Scanner(decDms).useDelimiter(" ");
            dd     = s.nextDouble();
            dm     = s.nextDouble();
            ds     = s.nextDouble();
            s.close();
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
}
/*___oOo___*/
