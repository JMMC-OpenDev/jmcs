/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Star.java,v 1.21 2010-10-14 12:18:53 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.20  2010/10/13 20:56:11  bourgesl
 * corrected concurrency issues (error and query complete notifications) using EDT
 *
 * Revision 1.19  2010/04/09 09:24:36  bourgesl
 * added radial velocity (required by OIFITS)
 *
 * Revision 1.18  2010/04/08 13:34:32  bourgesl
 * added Star.fireNotification to use EDT instead of current thread
 *
 * Revision 1.17  2010/04/07 12:22:21  bourgesl
 * added IDS property to store simbad identifiers
 *
 * Revision 1.16  2010/02/18 11:02:36  mella
 * add logg and teff to the ld2ud outputs
 *
 * Revision 1.15  2010/01/28 16:35:04  mella
 * Use name as a property
 *
 * Revision 1.14  2010/01/21 10:04:01  bourgesl
 * added star name to be consistent with the query/result
 *
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

import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Store data relative to a star.
 * If the star has a name, it is stored into one string property.
 */
public class Star extends Observable {

    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.Star");
    /** Star property-value backing store for String data */
    private final Map<Property, String> _stringContent;
    /** Star property-value backing store for Double data */
    private final Map<Property, Double> _doubleContent;
    /** CDS Simbad error message */
    private String _cdsSimbadErrorMessage = null;

    /**
     * Constructor.
     */
    public Star() {
        super();
        _stringContent = new EnumMap<Property, String>(Property.class);
        _doubleContent = new EnumMap<Property, Double>(Property.class);
    }

    /**
     * Copy content of a given Star.
     *
     * @param source the star whose content should be copied from.
     */
    public final void copy(final Star source) {
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
    public final void clear() {
        _stringContent.clear();
        _doubleContent.clear();
        _cdsSimbadErrorMessage = null;

        setChanged();
    }

    /**
     * Define the star name
     * @param name star name
     */
    public final void setName(final String name) {
        setPropertyAsString(Property.NAME, name);
    }

    /**
     * Return the star name or null if it is undefined
     * @return star name or null
     */
    public final String getName() {
        return _stringContent.get(Property.NAME);
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
    public final String setPropertyAsString(final Property property, final String value) {
        final String previousValue = _stringContent.put(property, value);
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
    public final Double setPropertyAsDouble(final Property property, final Double value) {
        final Double previousValue = _doubleContent.put(property, value);
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
    public final String getPropertyAsString(final Property property) {
        String stringValue = _stringContent.get(property);

        if (stringValue == null) {
            final Double doubleValue = _doubleContent.get(property);

            if (doubleValue != null) {
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
    public final Double getPropertyAsDouble(final Property property) {
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
    public final void raiseCDSimbadErrorMessage(final String message) {
        // Use EDT to ensure only 1 thread (EDT) set and consume the error message :
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                clear();

                _cdsSimbadErrorMessage = message;

                _logger.severe("CDS Simbad problem : " + _cdsSimbadErrorMessage);

                fireNotification(Notification.QUERY_ERROR);
            }
        });
    }

    /**
     * Get the error message from CDS Simbad query execution, and reset it
     * for later use.
     *
     * @sa fr.jmmc.mcs.astro.star.StarResolver.
     * @sa fr.jmmc.mcs.astro.star.StarResolverWidget.
     *
     * @return A String object containing the error message, or null if
     * everything went fine.
     */
    public final String consumeCDSimbadErrorMessage() {
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
    public String toString() {
        final StringBuilder sb = new StringBuilder(255);

        for (Property key : _stringContent.keySet()) {
            sb.append(key).append("=").append(_stringContent.get(key)).append("\n");
        }
        for (Property key : _doubleContent.keySet()) {
            sb.append(key).append("=").append(_doubleContent.get(key)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Fires the notification to the registered observers
     * @param notification notification enum value
     */
    public final void fireNotification(final Notification notification) {
        // notify observers (swing components) within EDT :
        if (!SwingUtilities.isEventDispatchThread()) {
            _logger.log(Level.SEVERE, "invalid thread : use EDT", new Throwable());
        }

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Fire notification : " + notification);
        }
        notifyObservers(notification);
    }

    /**
     * Enumeration of all different observers notification a star can raise.
     */
    public enum Notification {

        QUERY_COMPLETE, QUERY_ERROR, UNKNOWN;
    };

    /**
     * Enumeration of all different properties a star can handle.
     */
    public enum Property {

        RA, DEC, RA_d, DEC_d,
        FLUX_N, FLUX_V, FLUX_I, FLUX_J, FLUX_H, FLUX_K,
        UD_B, UD_I, UD_J, UD_H, UD_K, UD_L, UD_N, UD_R, UD_U, UD_V, TEFF, LOGG,
        OTYPELIST,
        PROPERMOTION_RA, PROPERMOTION_DEC,
        PARALLAX, PARALLAX_err,
        SPECTRALTYPES,
        NOPROPERTY,
        NAME, IDS,
        RV, RV_DEF;

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
        public static Property fromString(final String propertyName) {
            try {
                return valueOf(propertyName);
            } catch (Exception ex) {
                return NOPROPERTY;
            }
        }
    };
}
/*___oOo___*/
