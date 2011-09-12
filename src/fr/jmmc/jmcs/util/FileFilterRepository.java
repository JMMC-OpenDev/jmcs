/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.File;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

/**
 * FileFilterRepository singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class FileFilterRepository {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(FileFilterRepository.class.getName());
    /** Singleton instance */
    private static FileFilterRepository _instance = null;
    /**
     * Hastable to associate string keys like
     * "application-x/scvot-file" to FileFilterRepository instances.
     */
    private static final HashMap<String, FileFilter> _repository = new HashMap<String, FileFilter>(16);

    /** Hidden constructor */
    protected FileFilterRepository() {
        _logger.entering("FileFilterRepository", "FileFilterRepository");
    }

    /** 
     * Return the singleton instance 
     * @return singleton instance 
     */
    public static synchronized FileFilterRepository getInstance() {
        _logger.entering("FileFilterRepository", "getInstance");

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
        _logger.entering("FileFilterRepository", "put");

        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("FileFilterRepository - put(mimeType = '" + mimeType
                    + "', fileExtension = '" + fileExtension + "', description = '"
                    + description + "')");
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
        _logger.entering("FileFilterRepository", "put[]");

        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("FileFilterRepository - put(mimeType = '" + mimeType
                    + "', fileExtensions[] = '" + fileExtensions + "', description = '"
                    + description + "')");
        }

        final FileFilter filter = new GenericFileFilter(fileExtensions, description);

        final FileFilter previousFilter = _repository.put(mimeType, filter);

        if (previousFilter == null) {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("Registered '" + mimeType + "' filter for the first time.");
            }
        } else if (previousFilter != filter) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Overwritten the previously registered '" + mimeType + "' file filter.");
            }
        } else {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Registered '" + mimeType
                        + "' mimeType associated with file extension '" + fileExtensions
                        + "'  (" + description + ") succesfully.");
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
        _logger.entering("FileFilterRepository", "get");

        final FileFilter retrievedFilter = _repository.get(mimeType);

        if (retrievedFilter == null) {
            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.severe("Cannot find '" + mimeType + "' file filter.");
            }
        } else {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Retrieved '" + mimeType + "' file filter succesfully.");
            }
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
        _logger.entering("FileFilterRepository", "get");

        return get(mimeType.getName());
    }

    /**
     * Return the content of the object as a String for output.
     *
     * @return the content of the object as a String for output.
     */
    @Override
    public String toString() {
        _logger.entering("FileFilterRepository", "toString");

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
    private static final Logger _logger = Logger.getLogger(GenericFileFilter.class.getName());
    /** Hold each file extensions */
    private final HashMap<String, String> _fileExtensions = new HashMap<String, String>(4);
    /** Filter description */
    private final String _description;

    /**
     * Creates a new GenericFileFilter object.
     *
     * @param fileExtensions an array of file extensions associated to the mime type.
     * @param description the humanly readable description for the mime type.
     */
    GenericFileFilter(final String[] fileExtensions, final String description) {
        super();

        _logger.entering("GenericFileFilter", "GenericFileFilter");

        if (_logger.isLoggable(Level.FINER)) {
            _logger.finer("GenericFileFilter(fileExtensions = '" + fileExtensions
                    + "', description = '" + description + "')");
        }

        final int nbOfFileExtensions = fileExtensions.length;

        for (int i = 0; i < nbOfFileExtensions; i++) {
            // Add filters one by one
            final String fileExtension = fileExtensions[i].toLowerCase();

            _fileExtensions.put(fileExtension, description);

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("GenericFileFilter(...) - Added fileExtensions["
                        + (i + 1) + "/" + nbOfFileExtensions + "] = '" + fileExtension
                        + "'.");
            }
        }

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
        _logger.entering("GenericFileFilter", "accept");

        if (currentFile != null) {
            String fileName = currentFile.getName();

            // If current file is not regular (e.g directory, links, ...)
            if (!currentFile.isFile()) {
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("Accepting non-regular file '" + fileName + "'.");
                }

                return true; // Accept it to ensure navigation through directory and so
            }

            // If the file has no extension
            final String fileExtension = FileUtils.getExtension(currentFile);

            if (fileExtension == null) {
                return false; // Discard it
            }

            // If corresponding mime-type is handled
            final String fileType = _fileExtensions.get(fileExtension);

            if (fileType != null) {
                if (_logger.isLoggable(Level.FINER)) {
                    _logger.finer("Accepting file '" + fileName + "' of type '" + fileType + "'.");
                }

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
        _logger.entering("GenericFileFilter", "getDescription");

        return _description;
    }

    /**
     * Return the content of the object as a String for output.
     *
     * @return the content of the object as a String for output.
     */
    @Override
    public String toString() {
        _logger.entering("GenericFileFilter", "toString");

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
