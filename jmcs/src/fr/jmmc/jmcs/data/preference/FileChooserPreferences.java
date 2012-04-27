/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.util.MimeType;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gathers user preferences related to local folders in FileChooser
 * @author Laurent BOURGES, Sylvain LAFRASSE
 */
public final class FileChooserPreferences extends Preferences {

    /** Singleton instance */
    private static FileChooserPreferences _singleton = null;
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(FileChooserPreferences.class.getName());

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
            logger.debug("FilePreferences.getInstance()");
            // disable notifications:
            _singleton = new FileChooserPreferences();
            // enable future notifications:
            _singleton.setNotify(true);
        }
        return _singleton;
    }

    /**
     * Define the default directory as user home directory for all known mime types.
     *
     * @throws PreferencesException if any preference value has a unsupported class type
     */
    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        logger.debug("FilePreferences.setDefaultPreferences");

        final String defaultDirectory = System.getProperty("user.home");

        final MimeType[] values = MimeType.values();
        for (MimeType mimeType : values) {
            setDefaultPreference(mimeType.toString(), defaultDirectory);
        }
    }

    /**
     * Forge the preference filename according to the application name and company.
     *
     * @return preference filename.
     */
    @Override
    protected String getPreferenceFilename() {

        final String shortCompanyName = App.getSharedApplicationDataModel().getShortCompanyName();
        final String programName = App.getSharedApplicationDataModel().getProgramName();
        final String prefix = "fr.jmmc.jmcs.filechooser.";

        String preferenceFileName = prefix + shortCompanyName + "." + programName + ".properties";

        preferenceFileName = preferenceFileName.replace(" ", "");
        preferenceFileName = preferenceFileName.toLowerCase();

        return preferenceFileName;
    }

    /**
     *  Return preference version number.
     *
     * @return preference version number.
     */
    @Override
    protected int getPreferencesVersionNumber() {
        return 1;
    }

    /**
     * Return the last directory used for files having this mime type. By default = user home
     * @param mimeType mime type to look for
     * @return last directory used or user home as File
     */
    public static File getLastDirectoryForMimeTypeAsFile(final MimeType mimeType) {
        return new File(getLastDirectoryForMimeTypeAsPath(mimeType));
    }

    /**
     * Return the last directory used for files having this mime type. By default = user home
     * @param mimeType mime type to look for
     * @return last directory used or user home
     */
    public static String getLastDirectoryForMimeTypeAsPath(final MimeType mimeType) {
        return getInstance().getPreference(mimeType.toString());
    }

    /**
     * Define the last directory used for files having this mime type
     * @param mimeType mime type to look for
     * @param path file path to an existing directory
     */
    public static void setCurrentDirectoryForMimeType(final MimeType mimeType, final String path) {
        if (path != null) {
            final String oldPath = getLastDirectoryForMimeTypeAsPath(mimeType);
            if (!path.equals(oldPath)) {
                try {
                    getInstance().setPreference(mimeType.toString(), path);
                    getInstance().saveToFile();
                } catch (PreferencesException pe) {
                    logger.warn("Saving FilePreferences failure:", pe);
                }
            }
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
            logger.error("property failure : ", pe);
        }
    }
}
