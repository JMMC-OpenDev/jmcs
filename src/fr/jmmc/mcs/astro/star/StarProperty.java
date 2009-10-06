/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarProperty.java,v 1.1 2009-10-06 15:54:18 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;


/**
 * Enumeration of all different properties a star can handle.
 *
 * @author lafrasse
 */
public enum StarProperty
{RA, DEC, RA_d, DEC_d, FLUX_V, FLUX_I, FLUX_J, FLUX_H, FLUX_K, OTYPELIST, 
    NOPROPERTY;
    /**
     * Give back the enum value from the corresponding string.
     *
     * For example:
     * StarProperty.fromString("RA_d") == StarProperty.RA_d;
     * StarProperty.fromString("toto") == StarProperty.NOPROPERTY;
     *
     * @param propertyName name of the seeked enum value.
     *
     * @return the enum value from the corresponding string.
     */
    public static StarProperty fromString(String propertyName)
    {
        try
        {
            return valueOf(propertyName);
        }
        catch (Exception ex)
        {
            return NOPROPERTY;
        }
    }
}
/*___oOo___*/
