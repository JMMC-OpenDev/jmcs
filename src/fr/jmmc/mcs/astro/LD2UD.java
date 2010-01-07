/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LD2UD.java,v 1.4 2010-01-07 13:03:57 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2010/01/07 12:53:26  mella
 * Use command line arguments
 *
 * Revision 1.2  2010/01/07 10:30:36  lafrasse
 * Added MCS header.
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro;

/**
 * This executable class returns the uniform diameters given to the limb
 * darkening diameter and spectraltype of one star.
 * In fact it is just a wrapper to one ALX method.
 */
public class LD2UD {
    public static void main(String[] args) {
        try {
            System.out.println(ALX.ld2ud(Double.parseDouble(args[0]),args[1]));
        } catch (Exception e) {
            System.err.println("Usage: LD2UD <limb darkened diameter> <spectral type>");
            System.err.println("Exemple HD199947: LD2U '1.185' 'K3III'");
            System.err.println(ALX.ld2ud(1.185,"K3III"));
            System.err.println("Exemple HD188154: LD2U '' 'K5III'");
            System.err.println(ALX.ld2ud(2.604,"K5III"));
            System.err.println("Exemple HD178524: LD2U '1.754' 'F2II/III'");
            System.err.println(ALX.ld2ud(1.754,"F2II/III"));
        }        
    }
}
