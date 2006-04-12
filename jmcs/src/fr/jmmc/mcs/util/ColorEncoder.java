/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ColorEncoder.java,v 1.3 2006-04-06 14:35:39 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/04/06 14:23:08  mella
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/31 14:29:53  mella
 * First Revision
 *
 *
 ******************************************************************************/
package jmmc.mcs.util;

import java.awt.Color;


/**
 * DOCUMENT ME!
 */
public class ColorEncoder
{
    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static String hexForInt(int i)
    {
        if (i == 0)
        {
            return "00";
        }

        return Integer.toHexString(i);
    }

    /** Converts a String to an integer and returns the specified opaque Color.
     * This method handles string formats that are used to represent octal and
     * hexidecimal numbers.
     * @param c The color to encode
     * @return the encoded string for given color
     */
    public static String encode(Color c)
    {
        String ret = "#";
        ret += hexForInt(c.getRed());
        ret += hexForInt(c.getGreen());
        ret += hexForInt(c.getBlue());

        return ret;
    }
}
