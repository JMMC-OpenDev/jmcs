/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 * List the mime types that are used by multiples applications using jmcs.
 * It is also acceptable to define here the mimetypes specific to one
 * application.
 */
public enum MimeType {
    
    /** MimeType associated to Observation settings */
    ASPRO_OBSERVATION("application/x-aspro+xml", "Aspro Observation Settings", "asprox"),
    /** MimeType associated to xml LITpro settings */
    LITPRO_SETTINGS("application/vnd.jmmc.litpro+xml", "LITpro xml Settings", "litprox", "xml"),
    /** MimeType associated to P2PP Observing blocks */
    OBX("application/obx", "Observing Blocks", "obx"),
    /** MimeType associated to OIFITS format */
    OIFITS("application/oifits", "Optical Interferometry FITS", "fits", "oifits"),
    /** MimeType associated to PDF documents */
    PDF("application/pdf", "Portable Document Format", "pdf"),
    /** MimeType associated to VEGA Star Lists */
    STAR_LIST("text/plain", "Star Lists", "txt"),
    /** MimeType associated to Text files */
    TEXT_PLAIN("text/plain", "Text files", "txt");
    
    /** Class logger */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MimeType.class.getName());

    /**
     * Custom constructor
     * @param name mime type name
     * @param description short description
     * @param extensions accepted extensions
     */
    private MimeType(final String name, final String description, final String... extensions) {
        this.name = name;
        this.description = description + ' ' + Arrays.toString(extensions);
        this.extensions = Arrays.asList(extensions);
        FileFilterRepository.getInstance().put(name, extensions, this.description);
    }

    /* members */
    /** mime-type */
    private final String name;
    /** short mime-type description */
    private final String description;
    /** list of accepted extensions */
    private final List<String> extensions;

    /**
     * Return the short mime-type description
     * @return short mime-type description
     */
    public String getDescription() {
        logger.entering("MimeType", "getDescription");
        return description;
    }

    /**
     * Return all accepted extensions
     * @return accepted extensions as list
     */
    public List<String> getExtensions() {
        logger.entering("MimeType", "getExtensions");
        return extensions;
    }

    /** 
     * Return the first accepted extension
     * @return first accepted extension
     */
    public String getExtension() {
        if (extensions.isEmpty()) {
            return null;
        }
        return extensions.get(0);
    }

    /**
     * Return the mime-type
     * @return mime-type
     */
    public String getName() {
        logger.entering("MimeType", "getName");
        return name;
    }

    /**
     * Return the last registered file filter for the given mime type.
     *
     * @return the retrieved registered file filter.
     */
    public FileFilter getFileFilter() {
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
}
