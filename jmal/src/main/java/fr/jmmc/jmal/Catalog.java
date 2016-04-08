/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Enumeration of all different catalogs and their properties.
 * If catalog list get updated, please check consistency with:
 * - SearchCal vobsCATALOG.h (#define block)
 * - SearchCal GUI (XLST) (sclgui/src/fr/jmmc/sclgui/resource/sclguiVOTableToHTML.xsl)
 * - getStar service (XSLT) : link to source file
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA.
 */
public enum Catalog {

    /* keep ordering as follows (used by default color association) */
    ASCC_2_5("I/280", "ASCC-2.5", "All-sky Compiled Catalogue of 2.5 million stars"),
    USNO_B("I/284", "USNO-B", "The USNO-B1.0 Catalog"),
    CIO("II/225/catalog", "CIO", "Catalog of Infrared Observations, Edition 5"),
    JP11("II/7A/catalog", "JP11", "UBVRIJKLMNH Photoelectric Catalogue"),
    _2MASS("II/246/out", "2MASS", "2MASS All-Sky Catalog of Point Sources"),
    BSC("V/50/catalog", "BSC", "Bright Star Catalogue, 5th Revised Ed."),
    Merand("J/A+A/433/1155", "Merand", "Calibrator stars for 200m baseline interferometry"),
    DENIS("B/denis", "DENIS", "The DENIS database"),
    J_K_DENIS("J/A+A/413/1037", "J-K DENIS", "J-K DENIS photometry of bright southern stars"),
    HIC("I/196/main", "HIC", "Hipparcos Input Catalogue, Version 2"),
    LBSI("J/A+A/393/183/catalog", "LBSI", "Catalogue of calibrator stars for LBSI"),
    MIDI("MIDI", "MIDI", "Photometric observations and angular size estimates of mid infrared interferometric calibration sources"),
    SBSC("V/36B/bsc4s", "SBSC", "The Supplement to the Bright Star Catalogue"),
    SB9("B/sb9/main", "SB9", "SB9: 9th Catalogue of Spectroscopic Binary Orbits"),
    WDS("B/wds/wds", "WDS", "The Washington Visual Double Star Catalog"),
    AKARI("II/297/irc", "AKARI", "AKARI/IRC mid-IR all-sky Survey (ISAS/JAXA, 2010)"),
    HIP2("I/311/hip2", "HIP2", "Hipparcos, the New Reduction (van Leeuwen, 2007)"),
    HIP1("I/239/hip_main", "HIP1", "Hipparcos and Tycho Catalogues (ESA 1997)"),
    SIMBAD("SIMBAD", "SIMBAD", "SIMBAD Astronomical Database")
    ;
    /* members */
    /** Store the catalog CDS 'cryptic' reference */
    private final String _reference;
    /** Store the catalog CDS 'human-readable' name */
    private final String _title;
    /** Store the catalog CDS 'abbreviated' description */
    private final String _description;
    /** Blanking value for undefined Strings (null, ...) */
    public static final String UNKNOWN = "Unknown";

    Catalog(final String reference, final String title, final String description) {
        _reference = (reference == null ? UNKNOWN : reference);
        addCatalog(_reference);

        _title = (title == null ? UNKNOWN : title);
        addTitle(_reference, _title);

        _description = (description == null ? UNKNOWN : description);
        addDescription(_reference, _description);
    }

    private void addTitle(final String reference, final String title) {
        CatalogMapping._titles.put(reference, title);
    }

    private void addDescription(final String reference, final String description) {
        CatalogMapping._descriptions.put(reference, description);
    }

    private void addCatalog(final String reference) {
        CatalogMapping._catalogs.put(reference, this);
    }

    /**
     * Give back the catalog reference.
     *
     * @return a String containing the given catalog reference, Catalog.UNKNOWN otherwise.
     */
    public String reference() {
        return _reference;
    }

    /**
     * Give back the catalog title.
     *
     * @return a String containing the given catalog title, Catalog.UNKNOWN otherwise.
     */
    public String title() {
        return _title;
    }

    /**
     * Give back the catalog description.
     *
     * @return a String containing the given catalog description, Catalog.UNKNOWN otherwise.
     */
    public String description() {
        return _description;
    }

    /**
     * Give back the catalog title of the corresponding reference.
     *
     * For example:
     * Catalog.titleFromReference("V/50/catalog") == "BSC";
     * Catalog.titleFromReference("toto") == "toto";
     * Catalog.titleFromReference(null) == Catalog.UNKNOWN;
     *
     * @param reference reference of the sought catalog.
     *
     * @return a String containing the given catalog title, the reference if not found, Catalog.UNKNOWN otherwise.
     */
    public static String titleFromReference(final String reference) {
        if (reference == null) {
            return UNKNOWN;
        }

        String title = CatalogMapping._titles.get(reference);
        if (title == null) {
            title = reference;
        }

        return title;
    }

    /**
     * Give back the catalog description of the corresponding reference.
     *
     * For example:
     * Catalog.descriptionFromReference("V/50/catalog") == "Bright Star Catalogue, 5th Revised Ed.";
     * Catalog.descriptionFromReference("toto") == "toto";
     * Catalog.descriptionFromReference(null) == Catalog.UNKNOWN;
     *
     * @param reference reference of the sought catalog.
     *
     * @return a String containing the given catalog description, the reference if not found, Catalog.UNKNOWN otherwise.
     */
    public static String descriptionFromReference(final String reference) {
        if (reference == null) {
            return UNKNOWN;
        }

        String description = CatalogMapping._descriptions.get(reference);
        if (description == null) {
            description = reference;
        }

        return description;
    }

    /**
     * Give back the catalog of the corresponding reference.
     *
     * @param reference reference of the sought catalog.
     *
     * @return the sought catalog, null otherwise.
     */
    public static Catalog catalogFromReference(final String reference) {
        return (reference == null) ? null : CatalogMapping._catalogs.get(reference);
    }

    @Override
    public String toString() {
        return toString(new StringBuilder(128)).toString();
    }

    /**
     * Append the string representation to the given buffer
     * @param sb buffer to append to
     * @return sb given buffer (fluent API)
     */
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(_title).append(" - '").append(_description).append("' (").append(_reference).append(")");
    }

    /**
     *  Display html table with the list of catalogs.
     *  (Used by catalogs.jsp in jmcs webapps.)
     * @return one html table
     */
    public static String toHtmlTable() {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("<table>\n");
        sb.append("<tr><th>Title</th><th>Reference</th><th>Description</th></tr>\n");

        // Display one tr by catalog in the enum
        for (Catalog catalog : Catalog.values()) {
            String ref = catalog.reference();
            sb.append("<tr style='background-color:");
            sb.append(fr.jmmc.jmcs.util.ColorEncoder.encode(getDefaultColor(catalog)));
            sb.append("'><td>");
            sb.append(catalog.title());
            sb.append("</td><td>");
            if ("SIMBAD".equals(ref)) {
                sb.append("<a href='http://simbad.u-strasbg.fr/simbad/");
            } else {
                sb.append("<a href='http://cdsarc.u-strasbg.fr/cgi-bin/VizieR?-source=");
                sb.append(ref);
            }
            sb.append("'>");
            sb.append(ref);
            sb.append("</a>");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(catalog.description());
            sb.append("</td>");
            sb.append("</tr>\n");
        }
        sb.append("\n</table>\n");
        return sb.toString();
    }

    public static Color getDefaultColor(final Catalog catalog) {
        if (catalog != null) {
            final float saturation = 0.5f;
            final float brightness = 1.0f;

            // Hue is between ]0;1] as hue = 0 is equivalent to hue = 1
            final float computedHue = ((float) (catalog.ordinal() + 1) / (float) (Catalog.values().length));

            return Color.getHSBColor(computedHue, saturation, brightness);
        }
        return Color.BLACK;
    }

    /**
     * For test and debug purpose only.
     */
    public static void main(String[] args) {
        // For each catalog in the enum
        for (Catalog catalog : Catalog.values()) {
            String reference = catalog.reference();
            String title = catalog.title();
            String description = catalog.description();

            System.out.println("Catalog '" + reference + "' : title = '" + title + "' - description ='" + description + "'.");
            System.out.println("catalogFromReference('" + reference + "') = " + catalogFromReference(reference) + ".");
            System.out.println("titleFromReference('" + reference + "') = '" + titleFromReference(reference) + "'.");
            System.out.println("descriptionFromReference('" + reference + "') = '" + descriptionFromReference(reference) + "'.");
            System.out.println();
        }

        // Unknown catalog test
        String unknownReference = "toto";
        System.out.println("catalogFromReference('" + unknownReference + "') = " + catalogFromReference(unknownReference) + ".");
        System.out.println("titleFromReference('" + unknownReference + "') = '" + titleFromReference(unknownReference) + "'.");
        System.out.println("descriptionFromReference('" + unknownReference + "') = '" + descriptionFromReference(unknownReference) + "'.");


        System.out.println();
        System.out.println();

        // Display in xml each catalog in the enum
        for (Catalog catalog : Catalog.values()) {
            String reference = catalog.reference();
            String title = catalog.title();
            String description = catalog.description();
            System.out.println("<catalog ref='" + reference
                    + "' title='" + title
                    + "' color='" + fr.jmmc.jmcs.util.ColorEncoder.encode(getDefaultColor(catalog))
                    + "' description='" + description + "'/>");
        }

        // Display in html
        System.out.println("HTML version  :\n" + Catalog.toHtmlTable());
    }

    /** mappings: specific class to ensure mappings are defined before Confidence instances (initialization order issue) */
    private static final class CatalogMapping {

        private final static int CAPACITY = 32;
        final static Map<String, String> _titles = new HashMap<String, String>(CAPACITY);
        final static Map<String, String> _descriptions = new HashMap<String, String>(CAPACITY);
        final static Map<String, Catalog> _catalogs = new LinkedHashMap<String, Catalog>(CAPACITY);

        private CatalogMapping() {
        }
    }
}
