/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.jmcs.util.StringUtils;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;

/**
 * Store data relative to a star.
 * If the star has a name, it is stored into one string property.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class Star extends Observable {

    /** comma separator */
    public static final String SEPARATOR_COMMA = ",";
    /* members */
    /** Star property-value backing store for String data */
    final Map<Property, String> _stringContent;
    /** Star property-value backing store for Double data */
    final Map<Property, Double> _doubleContent;

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
     * Return the main identifier or null if it is undefined
     * @return star name or null
     */
    public final String getMainId() {
        final String mainId = _stringContent.get(Property.MAIN_ID);
        if (StringUtils.isEmpty(mainId)) {
            return null;
        }
        return mainId;
    }

    /**
     * Return the star identifier (main id or first IDS or 'RA DEC' or null if all are undefined
     * @return star identifier or 'RA DEC' or null
     */
    public final String getId() {
        final String mainId = getMainId();
        if (mainId != null) {
            return mainId;
        }
        final String ids = _stringContent.get(Property.IDS);
        if (!StringUtils.isEmpty(ids)) {
            final int pos = ids.indexOf(SEPARATOR_COMMA);
            if (pos != -1) {
                return ids.substring(0, pos);
            }
        }
        final String ra = _stringContent.get(Property.RA);
        final String dec = _stringContent.get(Property.DEC);
        if (!StringUtils.isEmpty(ra) && !StringUtils.isEmpty(dec)) {
            return ra + ' ' + dec;
        }
        return null;
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
     * Serialize the star object content in a String object.
     *
     * @return a String object containing the data stored inside a star.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(255);

        for (Property key : _stringContent.keySet()) {
            sb.append(key).append("='").append(_stringContent.get(key)).append("'\n");
        }
        for (Property key : _doubleContent.keySet()) {
            sb.append(key).append("=").append(_doubleContent.get(key)).append('\n');
        }

        return sb.toString();
    }

    /**
     * Enumeration of all different properties a star can handle.
     */
    public enum Property {

        RA, DEC, RA_d, DEC_d,
        FLUX_B, FLUX_V, FLUX_G, FLUX_R, FLUX_I, FLUX_J, FLUX_H, FLUX_K,
        UD_U, UD_B, UD_V, UD_R, UD_I, UD_J, UD_H, UD_K, UD_L, UD_N, TEFF, LOGG,
        OTYPELIST,
        PROPERMOTION_RA, PROPERMOTION_DEC,
        PARALLAX, PARALLAX_err,
        SPECTRALTYPES,
        NOPROPERTY,
        NAME, IDS, MAIN_ID,
        RV, RV_DEF;

        /**
         * Give back the enumeration value from the corresponding string.
         *
         * For example:
         * Property.fromString("RA_d") == Property.RA_d;
         * Property.fromString("toto") == Property.NOPROPERTY;
         *
         * @param propertyName name of the sought enumeration value.
         *
         * @return the enumeration value from the corresponding string.
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
