/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.util.MimeType;
import java.io.File;
import java.util.List;
import org.ivoa.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gathers :
 * - user preferences related to local folders in FileChooser;
 * - recently used file paths.
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE
 */
public final class FileChooserPreferences extends Preferences {

    /** Singleton instance */
    private static FileChooserPreferences _singleton = null;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FileChooserPreferences.class.getName());
    /** File name prefix */
    private static final String FILENAME_PREFIX = "fr.jmmc.jmcs.filechooser.";
    /** File name suffix */
    private static final String FILENAME_SUFFIX = ".properties";
    /** Recent file prefix */
    private static final String RECENT_FILE_PREFIX = "recent_files.";

    /**
     * Private constructor that must be empty.
     */
    private FileChooserPreferences() {
        super(false); // No update notifications
    }

    /**
     * Return the singleton instance of FilePreferences.
     *
     * @return the singleton instance
     */
    protected synchronized static FileChooserPreferences getInstance() {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null) {
            _logger.debug("FilePreferences.getInstance()");
            // disable notifications:
            _singleton = new FileChooserPreferences();
            // enable future notifications:
            _singleton.setNotify(true);
        }
        return _singleton;
    }

    /**
     * Return the default directory (user home)
     * @return default directory (user home)
     */
    private static String getDefaultDirectory() {
        return System.getProperty("user.home");
    }

    /**
     * Define the default directory as user home directory for all known mime types.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        _logger.debug("FilePreferences.setDefaultPreferences");

        final String defaultDirectory = getDefaultDirectory();

        for (MimeType mimeType : MimeType.values()) {
            setDefaultPreference(mimeType.getId(), defaultDirectory);
        }
    }

    /**
     * Forge the preference filename according to the application name and company.
     *
     * @return preference filename.
     */
    @Override
    protected String getPreferenceFilename() {

        final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
        final String shortCompanyName = applicationDataModel.getShortCompanyName();
        final String programName = applicationDataModel.getProgramName();

        String preferenceFileName = FILENAME_PREFIX + shortCompanyName + "." + programName + FILENAME_SUFFIX;
        preferenceFileName = preferenceFileName.replace(" ", "");
        preferenceFileName = preferenceFileName.toLowerCase();

        return preferenceFileName;
    }

    /**
     * @return preference version number.
     */
    @Override
    protected int getPreferencesVersionNumber() {
        return 1;
    }

    /**
     * @return the last directory used for files having this MIME type (by default, user home).
     * @param mimeType MIME type to look for
     */
    public static File getLastDirectoryForMimeTypeAsFile(final MimeType mimeType) {
        return new File(getLastDirectoryForMimeTypeAsPath(mimeType));
    }

    /**
     * @return the last directory used for files having this MIME type (by default, user home).
     * @param mimeType MIME type to look for
     */
    public static String getLastDirectoryForMimeTypeAsPath(final MimeType mimeType) {
        if (mimeType == null) {
            return getDefaultDirectory();
        }
        return getInstance().getPreference(mimeType.getId());
    }

    /**
     * Define the last directory used for files having this MIME type.
     * @param mimeType mime type to look for
     * @param path file path to an existing directory
     */
    public static void setCurrentDirectoryForMimeType(final MimeType mimeType, final String path) {
        if (mimeType != null && path != null) {
            final String oldPath = getLastDirectoryForMimeTypeAsPath(mimeType);
            if (!path.equals(oldPath)) {
                try {
                    getInstance().setPreference(mimeType.getId(), path);
                    getInstance().saveToFile();
                } catch (PreferencesException pe) {
                    _logger.warn("Saving FilePreferences failure:", pe);
                }
            }
        }
    }

    /**
     * @return the recent file list, or null if none found.
     */
    public static List<String> getRecentFilePaths() {

        // Try to read paths list from preference
        List<String> paths = null;
        try {
            paths = getInstance().getPreferenceAsStringList(RECENT_FILE_PREFIX);
        } catch (MissingPreferenceException ex) {
            _logger.error("No recent files found.", ex);
            return null;
        } catch (PreferencesException ex) {
            _logger.error("Could not read preference for recent files", ex);
            return null;
        }

        if ((paths == null) || (paths.size() == 0)) {
            _logger.info("No recent files stored.");
            return null;
        }

        // Deserialize paths to recent file list
        _logger.info("Found recent files '" + CollectionUtils.toString(paths) + "'.");
        return paths;
    }

    /**
     * @param paths path list to store in preferences
     */
    public static void setRecentFilePaths(List<String> paths) {

        if ((paths == null) || (paths.size() == 0)) {
            _logger.error("Null recent file list received");
            return;
        }

        // Try to store paths list to preference
        try {
            getInstance().setPreference(RECENT_FILE_PREFIX, paths);
            getInstance().saveToFile();
        } catch (PreferencesException ex) {
            _logger.error("Could not store recent file list in preference", ex);
        }
    }

    /**
     * Run this program to generate the default file preference file.
     * @param args unused
     */
    public static void main(String[] args) {
        try {
            FileChooserPreferences.getInstance().saveToFile();
        } catch (PreferencesException pe) {
            _logger.error("property failure : ", pe);
        }
    }
}
