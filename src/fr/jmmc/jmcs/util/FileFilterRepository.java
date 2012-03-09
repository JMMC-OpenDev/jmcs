/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.File;

import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileFilter;

/**
 * FileFilterRepository singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class FileFilterRepository {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FileFilterRepository.class.getName());
    /** Singleton instance */
    private static FileFilterRepository _instance = null;
    /**
     * Hashtable to associate string keys like
     * "application-x/scvot-file" to FileFilterRepository instances.
     */
    private static final HashMap<String, FileFilter> _repository = new HashMap<String, FileFilter>(16);

    /** Hidden constructor */
    protected FileFilterRepository() {
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
     * @param mimeType the mime type of the file, like "application-x/scvot-file".
     * @param fileExtension the file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     *
     * @return the previous registered file filter, null otherwise.
     */
    public FileFilter put(final String mimeType, final String fileExtension, final String description) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("FileFilterRepository - put(mimeType = '{}', fileExtension = '{}', description = '{}')",
                    new Object[]{mimeType, fileExtension, description});
        }

        return put(mimeType, new String[]{fileExtension}, description);
    }

    /**
     * Register a file filter in the repository.
     *
     * @param mimeType the mime type of the file, like "application-x/scvot-file".
     * @param fileExtensions an array of file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     *
     * @return the previous registered file filter, null otherwise.
     */
    public FileFilter put(final String mimeType, final String[] fileExtensions, final String description) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("FileFilterRepository - put(mimeType = '{}', fileExtensions[] = '{}', description = '{}')",
                    new Object[]{mimeType, Arrays.toString(fileExtensions), description});
        }

        final FileFilter filter = new GenericFileFilter(fileExtensions, description);

        final FileFilter previousFilter = _repository.put(mimeType, filter);

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
     * @param mimeType the mime type of the file filter, like "application-x/scvot-file".
     *
     * @return the retrieved registered file filter, null otherwise.
     */
    public static FileFilter get(final String mimeType) {
        final FileFilter retrievedFilter = _repository.get(mimeType);

        if (retrievedFilter == null) {
            _logger.error("Cannot find '{}' file filter.", mimeType);
        } else {
            _logger.debug("Retrieved '{}' file filter succesfully.", mimeType);
        }

        return retrievedFilter;
    }

    /**
     * Return the previously registered file filter for the given mime type as enum.
     *
     * @param mimeType MimeType enum value
     *
     * @return the retrieved registered file filter, null otherwise.
     */
    public static FileFilter get(final MimeType mimeType) {
        return get(mimeType.getName());
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

/**
 * A convenience implementation of FileFilter that filters out all files except
 * for those type extensions that it knows about.
 *
 * Extensions are of the type "foo", which is typically found on Windows and
 * Unix boxes, but not on older Macintosh which use ResourceForks (case ignored).
 *
 * Example - create a new filter that filters out all files but gif and jpg files:
 *     GenericFileFilter filter = new GenericFileFilter(
 *                   new String{"gif", "jpg"}, "JPEG & GIF Images")
 *
 * Strongly inspired of ExampleFileFilter class from FileChooserDemo under the
 * demo/jfc directory in the JDK.
 */
final class GenericFileFilter extends FileFilter {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(GenericFileFilter.class.getName());
    /** Hold each file extensions */
    private final HashMap<String, String> _fileExtensions = new HashMap<String, String>(4);
    /** Filter description */
    private final String _description;
    /** number of dots in extension (0 or 1 supported) */
    private final int _nDots;

    /**
     * Creates a new GenericFileFilter object.
     *
     * @param fileExtensions an array of file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     */
    GenericFileFilter(final String[] fileExtensions, final String description) {
        super();

        if (_logger.isDebugEnabled()) {
            _logger.debug("GenericFileFilter(fileExtensions = '{}', description = '{}')",
                    Arrays.toString(fileExtensions), description);
        }

        final int nbOfFileExtensions = fileExtensions.length;

        boolean hasDot = false;
        for (int i = 0; i < nbOfFileExtensions; i++) {
            // Add filters one by one
            final String fileExtension = fileExtensions[i].toLowerCase();
            
            hasDot |= fileExtension.contains(".");

            _fileExtensions.put(fileExtension, description);

            if (_logger.isTraceEnabled()) {
                _logger.trace("GenericFileFilter(...) - Added fileExtensions[{}]/{}] = '{}'.",
                        new Object[]{(i + 1), nbOfFileExtensions, fileExtension});
            }
        }
        
        _nDots = (hasDot) ? 2 : 1;

        _description = description;
    }

    /**
     * Return whether the given file is accepted by this filter, or not.
     *
     * @param currentFile the file to test
     *
     * @return true if file is accepted, false otherwise.
     */
    @Override
    public boolean accept(final File currentFile) {
        if (currentFile != null) {
            String fileName = currentFile.getName();

            // If current file is not regular (e.g directory, links, ...)
            if (!currentFile.isFile()) {
                _logger.trace("Accepting non-regular file '{}'.", fileName);

                return true; // Accept it to ensure navigation through directory and so
            }

            // If the file has no extension
            final String fileExtension = FileUtils.getExtension(currentFile, _nDots);

            if (fileExtension == null) {
                return false; // Discard it
            }

            // If corresponding mime-type is handled
            final String fileType = _fileExtensions.get(fileExtension);

            if (fileType != null) {
                _logger.debug("Accepting file '{}' of type '{}'.", fileName, fileType);

                return true; // Accept it
            }
        }

        return false;
    }

    /**
     * Return the description of this filter.
     *
     * @return the description of this filter.
     */
    @Override
    public String getDescription() {
        return _description;
    }

    /**
     * Return the content of the object as a String for output.
     *
     * @return the content of the object as a String for output.
     */
    @Override
    public String toString() {
        final String fileExtensions;

        if (_fileExtensions != null) {
            fileExtensions = _fileExtensions.toString();
        } else {
            fileExtensions = "NONE";
        }

        return "File extensions registered for '" + _description + "' : " + fileExtensions;
    }
}
/*___oOo___*/
