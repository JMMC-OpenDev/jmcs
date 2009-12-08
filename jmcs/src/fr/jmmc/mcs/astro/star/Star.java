/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Star.java,v 1.6 2009-12-08 10:14:50 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2009/10/23 15:38:20  lafrasse
 * Added error (querying and parsing) management.
 *
 * Revision 1.4  2009/10/23 12:23:35  lafrasse
 * Moved observer notification responsability to developper.
 * Added a property for N flux magnitude value.
 *
 * Revision 1.3  2009/10/13 15:34:54  lafrasse
 * Moved StarProperty enumeration in Star.
 * Typed getters and setters for string and Double.
 *
 * Revision 1.2  2009/10/08 14:31:02  lafrasse
 * Added Observer notification when any property is set.
 *
 * Revision 1.1  2009/10/06 15:54:17  lafrasse
 * First release.
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import java.util.Hashtable;
import java.util.Observable;
import java.util.logging.*;


/**
 * Store data relative to a star.
 */
public class Star extends Observable
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.Star");

    /** Star property-value backing store for String data */
    private Hashtable<Property, String> _stringContent = null;

    /** Star property-value backing store for Double data */
    private Hashtable<Property, Double> _doubleContent = null;

    /** CDS Simbad error message */
    private String _cdsSimbadErrorMessage = null;

    /**
     * Constructor.
     */
    public Star()
    {
        super();
        _stringContent     = new Hashtable();
        _doubleContent     = new Hashtable();
    }

    /**
     * Clear all content.
     */
    public void clear()
    {
        setChanged();

        _stringContent.clear();
        _doubleContent.clear();
        _cdsSimbadErrorMessage = null;
    }

    /**
     * Assign a String value to a given star property.
     *
     * @param property the identifier of the property to store.
     * @param value the value of the property to store.
     *
     * @return the value of the previously stored value for the given property, null otherwise.
     */
    public String setPropertyAsString(Property property, String value)
    {
        String previousValue = _stringContent.put(property, value);
        setChanged();

        return previousValue;
    }

    /**
     * Assign a Double value to a given star property.
     *
     * @param property the identifier of the property to store.
     * @param value the value of the property to store.
     *
     * @return the value of the previously stored value for the given property, null otherwise.
     */
    public Double setPropertyAsDouble(Property property, Double value)
    {
        setChanged();

        Double previousValue = _doubleContent.put(property, value);

        return previousValue;
    }

    /**
     * Retrieve the value of a given star property as a String.
     *
     * If none has been set, try to return the Double.toString().
     * Retrieve the value of a given star property as a Double.
     *
     * @param property the identifier of the property to retrieve.
     *
     * @return the value of the stored value for the given property, null otherwise.
     */
    public String getPropertyAsString(Property property)
    {
        String stringValue = _stringContent.get(property);

        if (stringValue == null)
        {
            Double doubleValue = _doubleContent.get(property);

            if (doubleValue != null)
            {
                stringValue = doubleValue.toString();
            }
        }

        return stringValue;
    }

    /**
     * Retrieve the value of a given star property as a Double.
     *
     * @param property the identifier of the property to retrieve.
     *
     * @return the value of the stored value for the given property, null otherwise.
     */
    public Double getPropertyAsDouble(Property property)
    {
        return _doubleContent.get(property);
    }

    /**
     * Set an error message from CDS Simbad query execution, and notify registered observers.
     *
     * @sa fr.jmmc.mcs.astro.star.StarResolver.
     * @sa fr.jmmc.mcs.astro.star.StarResolverWidget.
     *
     * @param message the error message to store.
     */
    public void raiseCDSimbadErrorMessage(String message)
    {
        setChanged();

        _cdsSimbadErrorMessage = message;
        _logger.severe("CDS Simbad problem : " + _cdsSimbadErrorMessage);

        notifyObservers(null);
    }

    /**
     * Get the error message from CDS Simbad query execution, and reset it for later use.
     *
     * @sa fr.jmmc.mcs.astro.star.StarResolver.
     * @sa fr.jmmc.mcs.astro.star.StarResolverWidget.
     *
     * @retrun A String object containing the error message, or null if everything went fine.
     */
    public String consumeCDSimbadErrorMessage()
    {
        String message = _cdsSimbadErrorMessage;
        _cdsSimbadErrorMessage = null; // reset error message
        return message;
    }

    /**
     * Serialize the star object content in a String object.
     *
     * @return a String object containing the data stored inside a star.
     */
    public String toString()
    {
        return "Strings = " + _stringContent.toString() + " / Doubles = " +
        _doubleContent.toString() + " / CDS Simbad error = '" +
        _cdsSimbadErrorMessage + "'.";
    }

    /**
     * Enumeration of all different properties a star can handle.
     */
    public enum Property
    {
        RA, DEC, RA_d, DEC_d,
        FLUX_N, FLUX_V, FLUX_I, FLUX_J, FLUX_H, FLUX_K,
        OTYPELIST,
        PROPERMOTION_RA, PROPERMOTION_DEC,
        PARALLAX, PARALLAX_err,
        SPECTRALTYPES,
        NOPROPERTY;

        /**
         * Give back the enum value from the corresponding string.
         *
         * For example:
         * Property.fromString("RA_d") == Property.RA_d;
         * Property.fromString("toto") == Property.NOPROPERTY;
         *
         * @param propertyName name of the seeked enum value.
         *
         * @return the enum value from the corresponding string.
         */
        public static Property fromString(String propertyName)
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
    };
}
/*___oOo___*/
