/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * List the mime types that are used by multiples applications using jMCS.
 * It is also acceptable to define here the mime types specific to one
 * application.
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public enum MimeType {

    /** MimeType associated to SearchCal calibrator list */
    SEARCHCAL_CALIBRATORLIST("application/x-searchcal+votable+xml", "SearchCal Calibrator List", "scvot"),
    /** MimeType associated to Observation settings */
    ASPRO_OBSERVATION("application/x-aspro+xml", "Aspro Observation Settings", "asprox"),
    /** MimeType associated to XML LITpro settings */
    LITPRO_SETTINGS("application/vnd.jmmc.litpro+xml", "LITpro XML Settings", "litprox", "xml"),
    /** MimeType associated to P2PP Observing blocks */
    OBX("application/obx", "Observing Blocks", "obx"),
    /** MimeType associated to OIFITS format */
    OIFITS("application/oifits", "Optical Interferometry FITS", "fits", "oifits"),
    /** MimeType associated to FITS format */
    FITS_IMAGE("application/fits", "FITS Image", "fits", "fits.gz"),
    /** MimeType associated to PDF documents */
    PDF("application/pdf", "Portable Document Format", "pdf"),
    /** MimeType associated to VEGA Star Lists */
    STAR_LIST("text/plain", "Star Lists", "txt"),
    /** MimeType associated to Character-Separated Values format */
    CSV("text/csv", "CSV", "txt"),
    /** MimeType associated to HTML format */
    HTML("text/html", "HTML", "html"),
    /** MimeType associated to Text files */
    PLAIN_TEXT("text/plain", "Text files", "txt"),
    /** MimeType associated to URL */
    URL("text/plain", "URL", "url");

    /**
     * Custom constructor
     * @param mimeType mime type name
     * @param name short description
     * @param extensions accepted extensions
     */
    private MimeType(final String mimeType, final String name, final String... extensions) {
        _mimeType = mimeType;
        _name = name;
        _fullDescription = name + ' ' + Arrays.toString(extensions);
        _extensions = Arrays.asList(extensions);
        FileFilterRepository.getInstance().put(mimeType, extensions, this._fullDescription);
    }

    /* members */
    /** mime-type */
    private final String _mimeType;
    /** mime-type name */
    private final String _name;
    /** full mime-type description */
    private final String _fullDescription;
    /** list of accepted extensions */
    private final List<String> _extensions;

    /**
     * Return the mime-type name
     * @return mime-type name
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the short mime-type description
     * @return short mime-type description
     */
    public String getDescription() {
        return _fullDescription;
    }

    /**
     * Return all accepted extensions
     * @return accepted extensions as list
     */
    public List<String> getExtensions() {
        return _extensions;
    }

    /** 
     * Return the first accepted extension
     * @return first accepted extension
     */
    public String getExtension() {
        if (_extensions.isEmpty()) {
            return null;
        }
        return _extensions.get(0);
    }

    /**
     * Return the mime-type
     * @return mime-type
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * Return the last registered file filter for the given mime type.
     *
     * @return the retrieved registered file filter.
     */
    public GenericFileFilter getFileFilter() {
        return FileFilterRepository.get(this);
    }

    /**
     * Check if the given file has an accepted extension. 
     * If not, return a new file with the first accepted extension
     * @param file file to check
     * @return given file or new file with the first accepted extension
     */
    public File checkFileExtension(final File file) {
        final String ext = FileUtils.getExtension(file);

        if (ext == null || !getExtensions().contains(ext)) {
            // add or replace current extension by the first accepted extension:
            final String fileNamePart = FileUtils.getFileNameWithoutExtension(file);
            return new File(file.getParentFile(), fileNamePart + '.' + getExtension());
        }
        return file;
    }

    @Override
    public String toString() {
        return _mimeType + ", matching " + _fullDescription + " file extension(s).";
    }

    /**
     * For test and debug purpose only.
     * @param args unused
     */
    public static void main(String[] args) {
        // For each catalog in the enum
        for (MimeType mimeType : MimeType.values()) {
            System.out.println("MimeType '" + mimeType._name + "' = '" + mimeType.toString() + "'.");
        }
    }
}
