/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampCapability.java,v 1.3 2010-10-04 23:42:07 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/10/04 21:59:41  lafrasse
 * Made mType() public.
 *
 * Revision 1.1  2010/09/14 14:29:49  lafrasse
 * First SAMP manager implementation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import java.util.Hashtable;

/**
 * Enumeration of all different SAMP capabilities, aka mTypes.
 */
public enum SampCapability {

    LOAD_VO_TABLE("table.load.votable"),
    LOAD_FITS_TABLE("table.load.fits"),
    LOAD_FITS_IMAGE("image.load.fits"),
    LOAD_SPECTRUM("spectrum.load.ssa-generic"),
    LOAD_BIBCODE("bibcode.load"),
    HIGHLIGHT_ROW("table.highlight.row"),
    SELECT_LIST("table.select.rowList"),
    POINT_COORDINATES("coord.pointAt.sky"),
    GET_ENV_VAR("client.env.get"),
    UNKNOWN("UNKNOWN");

    /** Store the SAMP 'cryptic' mType */
    private final String _mType;

    /** Blanking value for undefined Strings (null, ...) */
    public static final String UNKNOWN_MTYPE = "UNKNOWN";

    /** Constructor */
    SampCapability(String mType) {
        _mType = (mType == null ? UNKNOWN_MTYPE : mType);
        SampCapabilityNastyTrick._mTypes.put(mType, this);
    }

    /** Gives back the mType */
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
     * @param mType mType of the seeked SampCapability.
     *
     * @return a String containing the given catalog title, the reference if not found, SampCapability.UNKNOWN otherwise.
     */
    public static SampCapability fromMType(String mType) {
        if (mType == null) {
            return UNKNOWN;
        }

        SampCapability capability = SampCapabilityNastyTrick._mTypes.get(mType);
        if (capability == null) {
            return UNKNOWN;
        }

        return capability;
    }

    /**
     * For test and debug purpose only.
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
}

/**
 * To get over Java 1.5 limitation prohibiting static members in enum (initialization order hazard).
 *
 * @sa http://www.velocityreviews.com/forums/t145807-an-enum-mystery-solved.html
 * @sa http://www.jroller.com/ethdsy/entry/static_fields_in_enum
 */
class SampCapabilityNastyTrick {

    public static final Hashtable<String, SampCapability> _mTypes = new Hashtable();
}
