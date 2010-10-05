/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: JmmcCapability.java,v 1.3 2010-10-05 07:20:07 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/10/04 23:42:07  lafrasse
 * Fixed common name for NastyTrick class.
 *
 * Revision 1.1  2010/10/04 22:00:27  lafrasse
 * First revision, mainly for SearchCal query launching from ASPRO2.
 *
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import java.util.Hashtable;

/**
 * Enumeration of all different SAMP capabilities private to JMMC mTypes.
 */
public enum JmmcCapability {

    START_SEARCHCAL_QUERY("fr.jmmc.searchcal.query.start"),
    START_LITPRO_SETTING("fr.jmmc.litpro.setting.start"),
    UNKNOWN("UNKNOWN");

    /** Store the SAMP 'cryptic' mType */
    private final String _mType;

    /** Blanking value for undefined Strings (null, ...) */
    public static final String UNKNOWN_MTYPE = "UNKNOWN";

    /** Constructor */
    JmmcCapability(String mType) {
        _mType = (mType == null ? UNKNOWN_MTYPE : mType);
        JmmcCapabilityNastyTrick._mTypes.put(mType, this);
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
    public static JmmcCapability fromMType(String mType) {
        if (mType == null) {
            return UNKNOWN;
        }

        JmmcCapability capability = JmmcCapabilityNastyTrick._mTypes.get(mType);
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
        for (JmmcCapability capability : JmmcCapability.values()) {
            String mType = capability.mType();
            System.out.println("Capability '" + capability + "' has mType '" + mType + "' : match '" + (capability == JmmcCapability.fromMType(mType) ? "OK" : "FAILED") + "'.");
        }

        JmmcCapability tmp;
        String mType;

        mType = "toto";
        tmp = JmmcCapability.fromMType(mType);
        System.out.println("'" + mType + "' => '" + tmp + "'.");
        mType = null;
        tmp = JmmcCapability.fromMType(mType);
        System.out.println("'" + mType + "' => '" + tmp + "'.");
    }
}

/**
 * To get over Java 1.5 limitation prohibiting static members in enum (initialization order hazard).
 *
 * @sa http://www.velocityreviews.com/forums/t145807-an-enum-mystery-solved.html
 * @sa http://www.jroller.com/ethdsy/entry/static_fields_in_enum
 */
class JmmcCapabilityNastyTrick {

    public static final Hashtable<String, JmmcCapability> _mTypes = new Hashtable();
}
