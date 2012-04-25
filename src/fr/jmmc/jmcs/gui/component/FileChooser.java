/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.data.preference.FileChooserPreferences;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.MimeType;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utility methods to create file and directory choosers
 * 
 * TODO: use FilePreferences to get/set current directory preference (per mime type) ...
 * 
 * @author Laurent BOURGES, Sylvain LAFRASSE, Guillaume MELLA.
 */
public final class FileChooser {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(FileChooser.class.getName());
    /** apple specific property to force awt FileDialog work on directories only */
    public final static String MAC_FILE_DIALOG_DIRECTORY = "apple.awt.fileDialogForDirectories";
    /** use native file chooser i.e. awt.FileDialog (Mac OS X) */
    private final static boolean USE_DIALOG_FOR_FILE_CHOOSER = SystemUtils.IS_OS_MAC_OSX;

    /**
     * Show the directory chooser using following properties:
     * @param title dialog title
     * @param givenDirectory optional current directory as file (last one used for given mime type if null)
     * @return File instance or null if dialog was discarded
     */
    public static File showDirectoryChooser(final String title, final File givenDirectory, final MimeType mimeType) {

        File preselectedDirectory = retrieveLastDirectoryForMimeType(givenDirectory, mimeType);
        File selectedDirectory = null;

        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title);
            if (preselectedDirectory != null) {
                fileDialog.setDirectory(preselectedDirectory.getParent());
                fileDialog.setFile(preselectedDirectory.getName());
            }

            // force the file dialog to use directories only:
            System.setProperty(MAC_FILE_DIALOG_DIRECTORY, "true");

            try {
                // waits for dialog inputs:
                fileDialog.setVisible(true);
            } finally {
                // restore system property:
                System.setProperty(MAC_FILE_DIALOG_DIRECTORY, "false");
            }

            // note: this avoid to choose the root folder '/':
            if (fileDialog.getFile() != null && fileDialog.getDirectory() != null) {
                selectedDirectory = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }
        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (preselectedDirectory != null) {
                fileChooser.setCurrentDirectory(preselectedDirectory);
            }

            // select one directory:
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle(title);

            final int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedDirectory = fileChooser.getSelectedFile();
            }
        }
        if (selectedDirectory != null) {
            if (!selectedDirectory.isDirectory()) {
                _logger.warn("Expected directory: {}", selectedDirectory);
                selectedDirectory = null;
            } else {
                if (!selectedDirectory.exists()) {
                    if (MessagePane.showConfirmDirectoryCreation(selectedDirectory.getAbsolutePath())) {
                        selectedDirectory.mkdirs();
                    } else {
                        StatusBar.show("directory creation cancelled.");
                        selectedDirectory = null;
                    }
                }
            }
        }
        if (selectedDirectory != null) {
            final String directory = selectedDirectory.getPath();
            FileChooserPreferences.setCurrentDirectoryForMimeType(mimeType, directory);
        }
        return selectedDirectory;
    }

    /**
     * Show the Open File Dialog using following properties:
     * @param title dialog title
     * @param givenDirectory optional current directory as file (last one used for given mime type if null)
     * @param mimeType optional file mime type used to get both file extension(s) and file chooser filter
     * @param defaultFileName optional default file name
     * @return File instance or null if dialog was discarded
     */
    public static File showOpenFileChooser(final String title, final File givenDirectory, final MimeType mimeType, final String defaultFileName) {

        File preselectedDirectory = retrieveLastDirectoryForMimeType(givenDirectory, mimeType);
        File selectedFile = null;

        if (USE_DIALOG_FOR_FILE_CHOOSER) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.LOAD);
            if (preselectedDirectory != null) {
                fileDialog.setDirectory(preselectedDirectory.getAbsolutePath());
            }
            if (mimeType != null) {
                fileDialog.setFilenameFilter(mimeType.getFileFilter());
            }
            if (defaultFileName != null) {
                fileDialog.setFile(defaultFileName);
            }

            // waits for dialog inputs:
            fileDialog.setVisible(true);

            if (fileDialog.getFile() != null && fileDialog.getDirectory() != null) {
                selectedFile = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }

        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (preselectedDirectory != null) {
                fileChooser.setCurrentDirectory(preselectedDirectory);
            }

            // select one file:
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (mimeType != null) {
                fileChooser.setFileFilter(mimeType.getFileFilter());
            }

            if (defaultFileName != null) {
                fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), defaultFileName));
            }

            fileChooser.setDialogTitle(title);

            final int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }
        }
        if (selectedFile != null) {
            // Mac OS X can return application packages:
            if (SystemUtils.IS_OS_MAC_OSX && selectedFile.isDirectory()) {
                _logger.warn("Selected file is an application package: {}", selectedFile);
                selectedFile = null;
            } else {
                if (!selectedFile.exists()) {
                    _logger.warn("Selected file does not exist: {}", selectedFile);

                    if (mimeType == null) {
                        selectedFile = null;
                    } else if (FileUtils.getExtension(selectedFile) == null) {
                        // try using the same file name with extension :
                        selectedFile = mimeType.checkFileExtension(selectedFile);
                        // check again if that file exists :
                        if (!selectedFile.exists()) {
                            selectedFile = null;
                        }
                    }
                }
            }
        }
        if (selectedFile != null) {
            final String directory = selectedFile.getParent();
            FileChooserPreferences.setCurrentDirectoryForMimeType(mimeType, directory);
        }
        return selectedFile;
    }

    /**
     * Show the Save File Dialog using following properties:
     * @param title dialog title
     * @param givenDirectory optional current directory as file (last one used for given mime type if null)
     * @param mimeType optional file mime type used to get both file extension(s) and file chooser filter
     * @param defaultFileName optional default file name
     * @return File instance or null if dialog was discarded
     */
    public static File showSaveFileChooser(final String title, final File givenDirectory, final MimeType mimeType, final String defaultFileName) {

        File preselectedDirectory = retrieveLastDirectoryForMimeType(givenDirectory, mimeType);
        File selectedFile = null;

        if (USE_DIALOG_FOR_FILE_CHOOSER) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.SAVE);
            if (preselectedDirectory != null) {
                fileDialog.setDirectory(preselectedDirectory.getAbsolutePath());
            }
            if (mimeType != null) {
                fileDialog.setFilenameFilter(mimeType.getFileFilter());
            }
            if (defaultFileName != null) {
                fileDialog.setFile(defaultFileName);
            }

            // waits for dialog inputs:
            fileDialog.setVisible(true);

            if (fileDialog.getFile() != null && fileDialog.getDirectory() != null) {
                selectedFile = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }

        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (preselectedDirectory != null) {
                fileChooser.setCurrentDirectory(preselectedDirectory);
            }

            // select one file:
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (mimeType != null) {
                fileChooser.setFileFilter(mimeType.getFileFilter());
            }

            if (defaultFileName != null) {
                fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), defaultFileName));
            }

            fileChooser.setDialogTitle(title);

            final int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }
        }
        if (selectedFile != null) {
            if (mimeType != null) {
                selectedFile = mimeType.checkFileExtension(selectedFile);
            }

            // Mac OS X already handles file overwrite confirmation:
            if (!SystemUtils.IS_OS_MAC_OSX && selectedFile.exists()) {
                if (!MessagePane.showConfirmFileOverwrite(selectedFile.getName())) {
                    StatusBar.show("overwritting cancelled.");
                    selectedFile = null;
                }
            }
        }
        if (selectedFile != null) {
            final String directory = selectedFile.getParent();
            FileChooserPreferences.setCurrentDirectoryForMimeType(mimeType, directory);
        }
        return selectedFile;
    }

    private static File retrieveLastDirectoryForMimeType(final File givenDirectory, final MimeType mimeType) {
        File preselectedDirectory = givenDirectory;
        if (preselectedDirectory == null) {
            preselectedDirectory = FileChooserPreferences.getLastDirectoryForMimeTypeAsFile(mimeType);
        }
        return preselectedDirectory;
    }

    /**
     * Forbidden constructor
     */
    private FileChooser() {
        super();
    }
}
