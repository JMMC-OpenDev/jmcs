/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store data relative to a star.
 * If the star has a name, it is stored into one string property.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class Star extends Observable {

    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = LoggerFactory.getLogger(Star.class.getName());
    /** Star property-value backing store for String data */
    final Map<Property, String> _stringContent;
    /** Star property-value backing store for Double data */
    final Map<Property, Double> _doubleContent;
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
        clear();

        for (Map.Entry<Property, String> entry : source._stringContent.entrySet()) {
            _stringContent.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Property, Double> entry : source._doubleContent.entrySet()) {
            _doubleContent.put(entry.getKey(), entry.getValue());
        }
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

            if (doubleValue == null) {
                return "";
            } else {
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
        SwingUtils.invokeEDT(new Runnable() {

            /**
             * The state is left unchanged (no clear), only the error message is notified
             */
            @Override
            public void run() {
                _cdsSimbadErrorMessage = message;

                // set changed to notify observers:
                setChanged();

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
        final String message = _cdsSimbadErrorMessage;
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
        if (!SwingUtils.isEDT()) {
            _logger.error("Invalid thread : use EDT", new Throwable());
        }

        _logger.debug("Fire notification: {}", notification);

        notifyObservers(notification);
    }

    /**
     * Enumeration of all different observers notification a star can raise.
     */
    public enum Notification {

        /** sucessfull query */
        QUERY_COMPLETE,
        /** error state (CDS or parsing failure */
        QUERY_ERROR,
        /** unknow state */
        UNKNOWN;
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
         * @param propertyName name of the sought enum value.
         *
         * @return the enum value from the corresponding string.
         */
        public static Property fromString(final String propertyName) {
            try {
                return valueOf(propertyName);
            } catch (IllegalArgumentException iae) {
                return NOPROPERTY;
            }
        }
    };
}
/*___oOo___*/
