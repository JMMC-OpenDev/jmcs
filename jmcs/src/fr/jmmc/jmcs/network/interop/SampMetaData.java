/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of all different SAMP meta data identifiers.
 * 
 * @author Sylvain LAFRASSE.
 */
public enum SampMetaData {

    // Standard SAMP meta data identifiers
    /** A one word title for the application */
    NAME("samp.name"),
    /** A short description of the application, in plain text */
    DESCRIPTION_TEXT("samp.description.text"),
    /** A description of the application, in HTML */
    DESCRIPTION_HTML("samp.description.html"),
    /** The URL of an icon in png, gif or jpeg format */
    ICON_URL("samp.icon.url"),
    /** The URL of a documentation web page */
    DOCUMENTATION_URL("samp.documentation.url"),
    // Extended SAMP meta data identifiers
    /** Get the application JNLP URL */
    JNLP_URL("x-samp.jnlp.url"),
    /** Get the application home page URL */
    HOMEPAGE_URL("x-samp.homepage.url"),
    /** Get the application release notes URL */
    RELEASENOTES_URL("x-samp.releasenotes.url"),
    /** Get the application RSS feed URL */
    RSS_URL("x-samp.rss.url"),
    /** Get the application FAQ URL */
    FAQ_URL("x-samp.faq.url"),
    /** Get the application authors */
    AUTHORS("x-samp.authors"),
    /** Get the application release version */
    RELEASE_VERSION("x-samp.release.version"),
    /** Get the application release date */
    RELEASE_DATE("x-samp.release.date"),
    // Private JMMC SAMP capabilities are prefixed with application name:
    /** Undefined */
    UNKNOWN("UNKNOWN");

    /** Blanking value for undefined Strings (null, ...) */
    public static final String UNKNOWN_METADATA_ID = "UNKNOWN";
    /** AppLauncher prefix for custom samp meta data id */
    public final static String APP_LAUNCHER_PREFIX = "fr.jmmc.applauncher.";
    /** special value for our client stubs */
    public final static String STUB_TOKEN = "STUB";

    /**
     * Return the Samp meta data key 'fr.jmmc.applauncher.<clientName>'
     * @param name client name
     * @return 'fr.jmmc.applauncher.<clientName>'
     */
    public static String getStubMetaDataId(final String name) {
        return APP_LAUNCHER_PREFIX + name;
    }

    /* members */
    /** Store the SAMP raw meta data identifier */
    private final String _metaDataId;

    /**
     * Constructor
     * @param metaDataId samp meta data identifier
     */
    SampMetaData(final String metaDataId) {
        _metaDataId = (metaDataId == null) ? UNKNOWN_METADATA_ID : metaDataId;
        SampMetaData.SampMetaDataNastyTrick.TYPES.put(_metaDataId, this);
    }

    /**
     * Return the samp meta data identifier
     * @return samp meta data identifier
     */
    public String id() {
        return _metaDataId;
    }

    /**
     * Gives back the SAMP meta data of the corresponding identifier.
     *
     * For example:
     * SampMetaData.fromMetaDataId("samp.name") == SampMetaData.NAME;
     * SampMetaData.fromMetaDataId("toto") == SampMetaData.UNKNOWN;
     * SampMetaData.fromMetaDataId(null) == SampMetaData.UNKNOWN;
     *
     * @param metaDataId metaDataId of the sought SampCapability.
     *
     * @return the SampMetaData object corresponding to the given meta data id, SampMetaData.UNKNOWN otherwise.
     */
    public static SampMetaData fromMetaDataId(final String metaDataId) {
        if (metaDataId == null) {
            return UNKNOWN;
        }

        final SampMetaData metaData = SampMetaData.SampMetaDataNastyTrick.TYPES.get(metaDataId);
        if (metaData == null) {
            return UNKNOWN;
        }

        return metaData;
    }

    /**
     * For test and debug purpose only.
     * @param args unused
     */
    public static void main(String[] args) {
        // For each catalog in the enum
        for (SampMetaData metaData : SampMetaData.values()) {
            String id = metaData.id();
            System.out.println("SampMetaData '" + metaData + "' has id '" + id + "' : match '" + (metaData == SampMetaData.fromMetaDataId(id) ? "OK" : "FAILED") + "'.");
        }

        SampMetaData tmp;
        String id;

        id = "toto";
        tmp = SampMetaData.fromMetaDataId(id);
        System.out.println("'" + id + "' => '" + tmp + "'.");
        id = null;
        tmp = SampMetaData.fromMetaDataId(id);
        System.out.println("'" + id + "' => '" + tmp + "'.");
    }

    /**
     * To get over Java 1.5 limitation prohibiting static members in enum (initialization order hazard).
     *
     * @sa http://www.velocityreviews.com/forums/t145807-an-enum-mystery-solved.html
     * @sa http://www.jroller.com/ethdsy/entry/static_fields_in_enum
     */
    private final static class SampMetaDataNastyTrick {

        /** cached map of SampMetaData keyed by id */
        static final Map<String, SampMetaData> TYPES = new HashMap<String, SampMetaData>(16);

        /**
         * Forbidden constructor : utility class
         */
        private SampMetaDataNastyTrick() {
        }
    }
}
