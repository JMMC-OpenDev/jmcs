/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of all different SAMP capabilities, a.k.a mTypes.
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA, Laurent BOURGES.
 */
public enum SampCapability {

    // Standard SAMP mTypes
    /** Load VOTable MType */
    LOAD_VO_TABLE("table.load.votable"),
    /** Load fits table MType */
    LOAD_FITS_TABLE("table.load.fits"),
    /** Load fits image MType */
    LOAD_FITS_IMAGE("image.load.fits"),
    /** Load spectrum MType */
    LOAD_SPECTRUM("spectrum.load.ssa-generic"),
    /** Load BibCode MType */
    LOAD_BIBCODE("bibcode.load"),
    /** Highlight row MType */
    HIGHLIGHT_ROW("table.highlight.row"),
    /** Select rows MType */
    SELECT_LIST("table.select.rowList"),
    /** Point at given coordinates MType */
    POINT_COORDINATES("coord.pointAt.sky"),
    /** Get environment variable MType */
    GET_ENV_VAR("client.env.get"),
    // Private JMMC SAMP capabilities are prefixed with application name:
    /** JMMC SearchCal Start Query MType */
    APPLAUNCHERTESTER_TRY_LAUNCH("fr.jmmc.applaunchertester.try.launch"),
    /** JMMC SearchCal Start Query MType */
    SEARCHCAL_START_QUERY("fr.jmmc.searchcal.start.query"),
    /** JMMC LITpro open settings file MType */
    LITPRO_START_SETTING("fr.jmmc.litpro.start.setting"),
    /** OCA Pivot load star list MType */
    LOAD_STAR_LIST("starlist.load"),
    /** Undefined MType */
    UNKNOWN("UNKNOWN");

    /** Blanking value for undefined Strings (null, ...) */
    public static final String UNKNOWN_MTYPE = "UNKNOWN";
    
    /* members */
    /** Store the SAMP 'cryptic' mType */
    private final String _mType;

    /**
     * Constructor
     * @param mType samp message type (MTYPE)
     */
    SampCapability(final String mType) {
        this._mType = (mType == null) ? UNKNOWN_MTYPE : mType;
        SampCapabilityNastyTrick.TYPES.put(this._mType, this);
    }

    /**
     * Return the samp message type (MTYPE)
     * @return samp message type (MTYPE)
     */
    public String mType() {
        return _mType;
    }

    /**
     * Gives back the SAMP capability of the corresponding mType.
     *
     * For example:
     * SampCapability.fromMType("client.env.get") == SampCapability.GET_ENV_VAR;
     * SampCapability.fromMType("toto") == SampCapability.UNKNOWN;
     * SampCapability.fromMType(null) == SampCapability.UNKNOWN;
     *
     * @param mType mType of the sought SampCapability.
     *
     * @return a String containing the given catalog title, the reference if not found, SampCapability.UNKNOWN otherwise.
     */
    public static SampCapability fromMType(final String mType) {
        if (mType == null) {
            return UNKNOWN;
        }

        final SampCapability capability = SampCapabilityNastyTrick.TYPES.get(mType);
        if (capability == null) {
            return UNKNOWN;
        }

        return capability;
    }

    /**
     * For test and debug purpose only.
     * @param args unused
     */
    public static void main(String[] args) {
        // For each catalog in the enum
        for (SampCapability capability : SampCapability.values()) {
            String mType = capability.mType();
            System.out.println("Capability '" + capability + "' has mType '" + mType + "' : match '" + (capability == SampCapability.fromMType(mType) ? "OK" : "FAILED") + "'.");
        }

        SampCapability tmp;
        String mType;

        mType = "toto";
        tmp = SampCapability.fromMType(mType);
        System.out.println("'" + mType + "' => '" + tmp + "'.");
        mType = null;
        tmp = SampCapability.fromMType(mType);
        System.out.println("'" + mType + "' => '" + tmp + "'.");
    }

    /**
     * To get over Java 1.5 limitation prohibiting static members in enum (initialization order hazard).
     *
     * @sa http://www.velocityreviews.com/forums/t145807-an-enum-mystery-solved.html
     * @sa http://www.jroller.com/ethdsy/entry/static_fields_in_enum
     */
    private final static class SampCapabilityNastyTrick {

        /** cached map of SampCapability keyed by mType */
        static final Map<String, SampCapability> TYPES = new HashMap<String, SampCapability>(16);

        /**
         * Forbidden constructor : utility class
         */
        private SampCapabilityNastyTrick() {
            super();
        }
    }
}
