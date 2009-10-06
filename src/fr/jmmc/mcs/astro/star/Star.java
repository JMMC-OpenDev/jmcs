/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Star.java,v 1.1 2009-10-06 15:54:17 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import java.util.Hashtable;
import java.util.Observable;
import java.util.logging.*;


/**
 * Store informations relative to a star.
 */
public class Star extends Observable
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.Star");

    /** Star property-value backing store */
    Hashtable content = null;

    /**
     * Constructor.
     */
    public Star()
    {
        super();
        content = new Hashtable();
    }

    /**
     * Assign a value to a given star property.
     *
     * @param property the identifier of the property to store.
     * @param value the value of the property to store.
     *
     * @return the value of the previously stored value for the given property, null otherwise.
     */
    public Object setPropertyValue(StarProperty property, Object value)
    {
        setChanged();

        return content.put(property, value);
    }

    /**
     * Retrieve the value of a given star property.
     *
     * @param property the identifier of the property to store.
     *
     * @return the value of the stored value for the given property, null otherwise.
     */
    public Object getPropertyValue(StarProperty property)
    {
        return content.get(property);
    }

    /**
     * Serialize the star object content in a String object.
     *
     * @return a String object containing the data stored inside a star.
     */
    public String toString()
    {
        return content.toString();
    }
}
/*___oOo___*/
