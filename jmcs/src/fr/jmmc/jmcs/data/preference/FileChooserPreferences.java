/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.data.MimeType;
import java.io.File;
import java.util.Collections;
import java.util.List;
import fr.jmmc.jmcs.util.CollectionUtils;
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
    synchronized static FileChooserPreferences getInstance() {
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

        final List<String> emptyList = Collections.emptyList();
        setDefaultPreference(RECENT_FILE_PREFIX, emptyList);
    }

    /**
     * Forge the preference filename according to the application name and company.
     *
     * @return preference filename.
     */
    @Override
    protected String getPreferenceFilename() {

        final ApplicationDescription applicationDataModel = ApplicationDescription.getInstance();
        final String shortCompanyName = (applicationDataModel != null) ? applicationDataModel.getShortCompanyName() : "";
        final String programName = (applicationDataModel != null) ? applicationDataModel.getProgramName() : "";

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
        } catch (MissingPreferenceException mpe) {
            _logger.error("No recent files found.", mpe);
        } catch (PreferencesException pe) {
            _logger.error("Could not read preference for recent files", pe);
        }

        if ((paths == null) || (paths.isEmpty())) {
            _logger.debug("No recent files stored.");
            return null;
        }

        // Deserialize paths to recent file list
        if (_logger.isDebugEnabled()) {
            _logger.debug("Found recent files '{}'.", CollectionUtils.toString(paths));
        }

        return paths;
    }

    /**
     * @param paths path list to store in preferences
     */
    public static void setRecentFilePaths(final List<String> paths) {
        if (paths == null) {
            _logger.error("Null recent file list received");
            return;
        }

        // Try to store paths list to preference
        try {
            getInstance().setPreference(RECENT_FILE_PREFIX, paths);
            getInstance().saveToFile();
        } catch (PreferencesException pe) {
            _logger.error("Could not store recent file list in preference", pe);
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
