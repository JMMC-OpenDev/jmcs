/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;


import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FileFilterRepository singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public final class FileFilterRepository {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FileFilterRepository.class.getName());
    /** Singleton instance */
    private static FileFilterRepository _instance = null;
    /** Hashtable to associate mime type identifier keys to FileFilterRepository instances */
    private static final HashMap<Object, GenericFileFilter> _repository = new HashMap<Object, GenericFileFilter>(16);

    /** Hidden constructor */
    private FileFilterRepository() {
        super();
    }

    /** 
     * Return the singleton instance 
     * @return singleton instance 
     */
    public static synchronized FileFilterRepository getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new FileFilterRepository();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Register a file filter in the repository.
     *
     * @param mimeType the mime type identifier of the file.
     * @param fileExtension the file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     *
     * @return the previous registered file filter, null otherwise.
     */
    public GenericFileFilter put(final Object mimeType, final String fileExtension, final String description) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("FileFilterRepository - put(mimeType = '{}', fileExtension = '{}', description = '{}')",
                    new Object[]{mimeType, fileExtension, description});
        }

        return put(mimeType, new String[]{fileExtension}, description);
    }

    /**
     * Register a file filter in the repository.
     *
     * @param mimeType the mime type identifier of the file.
     * @param fileExtensions an array of file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     *
     * @return the previous registered file filter, null otherwise.
     */
    public GenericFileFilter put(final Object mimeType, final String[] fileExtensions, final String description) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("FileFilterRepository - put(mimeType = '{}', fileExtensions[] = '{}', description = '{}')",
                    new Object[]{mimeType, Arrays.toString(fileExtensions), description});
        }

        final GenericFileFilter filter = new GenericFileFilter(fileExtensions, description);

        final GenericFileFilter previousFilter = _repository.put(mimeType, filter);

        if (previousFilter == null) {
            _logger.trace("Registered '{}' filter for the first time.", mimeType);
        } else if (previousFilter != filter) {
            _logger.debug("Overwritten the previously registered '{}' file filter.", mimeType);
        } else {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Registered '{}' mimeType associated with file extensions '{}' ({}) succesfully.",
                        new Object[]{mimeType, Arrays.toString(fileExtensions), description});
            }
        }

        return previousFilter;
    }

    /**
     * Return the previously registered file filter for the given mime type.
     *
     * @param mimeType the mime type identifier of the file.
     *
     * @return the retrieved registered file filter, null otherwise.
     */
    public static GenericFileFilter get(final Object mimeType) {
        final GenericFileFilter retrievedFilter = _repository.get(mimeType);

        if (retrievedFilter == null) {
            _logger.error("Cannot find '{}' file filter.", mimeType);
        } else {
            _logger.debug("Retrieved '{}' file filter succesfully.", mimeType);
        }

        return retrievedFilter;
    }

    /**
     * Return the content of the object as a String for output.
     *
     * @return the content of the object as a String for output.
     */
    @Override
    public String toString() {
        if (_repository == null) {
            return "No file filter registered yet.";
        }

        return _repository.toString();
    }
}
/*___oOo___*/
