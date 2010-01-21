/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Star.java,v 1.14 2010-01-21 10:04:01 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.13  2010/01/14 12:40:20  bourgesl
 * Fix blanking value with white spaces for proper motion and parallax ' ; '
 * StringBuilder and Logger.isLoggable to avoid string.concat
 *
 * Revision 1.12  2010/01/07 13:47:50  mella
 * add missing Uniform Diameters in U band
 *
 * Revision 1.11  2010/01/07 13:05:06  mella
 * update list od Uniform Diameters
 *
 * Revision 1.10  2010/01/07 12:55:06  mella
 * sort UD properties
 *
 * Revision 1.9  2010/01/07 10:20:25  mella
 * Add some diam properties ld and uds
 *
 * Revision 1.8  2009/12/18 14:45:26  bourgesl
 * fixed method copy() :
 * - set changed flag (then notify observers)
 * - hashtable are copied by key/values pairs
 *
 * Revision 1.7  2009/12/16 15:53:02  lafrasse
 * Hardened CDS Simbad science star resolution mecanisms while failing.
 * Code, documentation and log refinments.
 *
 * Revision 1.6  2009/12/08 10:14:50  lafrasse
 * Added proper motion, parallax and spectral types storage and retrieval.
 *
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
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Store data relative to a star.
 */
public class Star extends Observable
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.Star");

    /** star name */
    private String _name = null;

    /** Star property-value backing store for String data */
    private final Map<Property, String> _stringContent;

    /** Star property-value backing store for Double data */
    private final Map<Property, Double> _doubleContent;

    /** CDS Simbad error message */
    private String _cdsSimbadErrorMessage = null;

    /**
     * Constructor.
     */
    public Star()
    {
        super();
        _stringContent     = new Hashtable<Property, String>();
        _doubleContent     = new Hashtable<Property, Double>();
    }

    /**
     * Copy content of a given Star.
     *
     * @param star the star whose content should be copied in.
     */
    public void copy(Star source)
    {
      _name = source.getName();

      _stringContent.clear();
      for (Map.Entry<Property, String> entry : source._stringContent.entrySet()) {
        _stringContent.put(entry.getKey(), entry.getValue());
      }

      _doubleContent.clear();
      for (Map.Entry<Property, Double> entry : source._doubleContent.entrySet()) {
        _doubleContent.put(entry.getKey(), entry.getValue());
      }

      setChanged();
    }

    /**
     * Clear all content.
     */
    public void clear()
    {
        _name = null;
        _stringContent.clear();
        _doubleContent.clear();
        _cdsSimbadErrorMessage = null;

        setChanged();
    }

    /**
     * Define the star name
     * @param name star name
     */
    public void setName(String name) {
      this._name = name;
      setChanged();
    }

    /**
     * Return the star name or null if it is undefined
     * @return star name or null
     */
    public String getName() {
      return _name;
    }

    /**
     * Assign a String value to a given star property.
     *
     * @param property the identifier of the property to store.
     * @param value the value of the property to store.
     *
     * @return the value of the previously stored value for the given property,
     * null otherwise.
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
     * @return the value of the previously stored value for the given property,
     * null otherwise.
     */
    public Double setPropertyAsDouble(Property property, Double value)
    {
        Double previousValue = _doubleContent.put(property, value);
        setChanged();

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
     * @return the value of the stored value for the given property,
     * null otherwise.
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
     * @return the value of the stored value for the given property,
     * null otherwise.
     */
    public Double getPropertyAsDouble(Property property)
    {
        return _doubleContent.get(property);
    }

    /**
     * Set an error message from CDS Simbad query execution, and notify
     * registered observers.
     *
     * @sa fr.jmmc.mcs.astro.star.StarResolver.
     * @sa fr.jmmc.mcs.astro.star.StarResolverWidget.
     *
     * @param message the error message to store.
     */
    public void raiseCDSimbadErrorMessage(String message)
    {
        _cdsSimbadErrorMessage = message;
        
        if (_logger.isLoggable(Level.SEVERE)) {
          _logger.severe("CDS Simbad problem : " + _cdsSimbadErrorMessage);
        }

        setChanged();
        notifyObservers(Notification.QUERY_ERROR);
    }

    /**
     * Get the error message from CDS Simbad query execution, and reset it
     * for later use.
     *
     * @sa fr.jmmc.mcs.astro.star.StarResolver.
     * @sa fr.jmmc.mcs.astro.star.StarResolverWidget.
     *
     * @retrun A String object containing the error message, or null if
     * everything went fine.
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
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(255);
        sb.append("NAME=").append(_name).append("\n");

        for (Property key : _stringContent.keySet()) {
            sb.append(key).append("=").append(_stringContent.get(key)).append("\n");
        }
        for (Property key : _doubleContent.keySet()) {
            sb.append(key).append("=").append(_doubleContent.get(key)).append("\n");
        }

        return sb.toString();
        /*return "Strings = " + _stringContent.toString() + " / Doubles = " +
        _doubleContent.toString() + " / CDS Simbad error = '" +
        _cdsSimbadErrorMessage + "'.";
         * */
    }

    /**
     * Enumeration of all different observers notification a star can raise.
     */
    public enum Notification
    {
        QUERY_COMPLETE, QUERY_ERROR, UNKNOWN;
    };

    /**
     * Enumeration of all different properties a star can handle.
     */
    public enum Property
    {
        RA, DEC, RA_d, DEC_d,
        FLUX_N, FLUX_V, FLUX_I, FLUX_J, FLUX_H, FLUX_K,
        UD_B, UD_I, UD_J, UD_H, UD_K, UD_L, UD_N, UD_R, UD_U, UD_V,
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
