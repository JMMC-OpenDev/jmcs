/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MimeType.java,v 1.1 2010-12-13 16:35:54 mella Exp $"
 *
 */
package fr.jmmc.mcs.util;

import javax.swing.filechooser.FileFilter;

/**
 * List the mime types that are used by multiples applications using jmcs.
 * It is also acceptable to define here the mimetypes specific to one
 * application.
 */
public enum MimeType {

    /** MimeType associated to xml LITpro setting files */
    LITPRO_SETTINGS("application/vnd.jmmc.litpro+xml", "LITpro xml Settings",
    new String[]{"litprox", "xml"}),
    /** MimeType associated to  OIFITS format */
    OIFITS("application/oifits","Optical Interferometry FITS",
           new String[]{"oifits", "fits"});


    /** Class logger */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("fr.jmmc.mcs.util.MimeType");

    /**
     * Custom constructor
     * @param name mime type name
     * @param description short description
     * @param extensions excepted prefixes
     */
    private MimeType(final String name, final String description,
            String[] extensions) {
        this.name = name;
        this.description = description;
        this.extensions = extensions;
        FileFilterRepository.getInstance().put(name, extensions, description);
    }

    /* members */
    /** mime-type */
    private final String name;
    /** short mime-type description */
    private final String description;
    /** list of accepted extensions */
    private String[] extensions;

    public String getDescription() {
        logger.entering("MimeType", "getDescription");
        return description;
    }

    public String[] getExtensions() {
        logger.entering("MimeType", "getExtensions");
        return extensions;
    }

    /** Return the first registered extension */
    public String getExtension(){
        if(extensions.length==0){
            return null;
        }
        return extensions[0];
    }

    public String getName() {
        logger.entering("MimeType", "getName");
        return name;
    }

     /**
     * Return the last registered file filter for the given mime type.
     *
     * @return the retrieved registered file filter.
     */
    public FileFilter getFileFilter(){
        return FileFilterRepository.get(this);
    }
    
}
