/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

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
     * @param currentDir optional current directory as file
     * @return File instance or null if dialog was discarded
     */
    public static File showDirectoryChooser(final String title, final File currentDir) {
        File dir = null;

        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title);
            if (currentDir != null) {
                fileDialog.setDirectory(currentDir.getParent());
                fileDialog.setFile(currentDir.getName());
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
                dir = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }
        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (currentDir != null) {
                fileChooser.setCurrentDirectory(currentDir);
            }

            // select one directory:
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle(title);

            final int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dir = fileChooser.getSelectedFile();
            }
        }
        if (dir != null) {
            if (!dir.isDirectory()) {
                _logger.warn("Expected directory: {}", dir);
                dir = null;
            } else {
                if (!dir.exists()) {
                    if (MessagePane.showConfirmDirectoryCreation(dir.getAbsolutePath())) {
                        dir.mkdirs();
                    } else {
                        StatusBar.show("directory creation cancelled.");
                        dir = null;
                    }
                }
            }
        }
        return dir;
    }

    /**
     * Show the Open File Dialog using following properties:
     * @param title dialog title
     * @param currentDir optional current directory as file
     * @param mimeType optional file mime type used to get both file extension(s) and file chooser filter
     * @param defaultFileName optional default file name
     * @return File instance or null if dialog was discarded
     */
    public static File showOpenFileChooser(final String title, final File currentDir, final MimeType mimeType, final String defaultFileName) {
        File file = null;

        if (USE_DIALOG_FOR_FILE_CHOOSER) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.LOAD);
            if (currentDir != null) {
                fileDialog.setDirectory(currentDir.getAbsolutePath());
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
                file = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }

        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (currentDir != null) {
                fileChooser.setCurrentDirectory(currentDir);
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
                file = fileChooser.getSelectedFile();
            }
        }
        if (file != null) {
            // Mac OS X can return application packages:
            if (SystemUtils.IS_OS_MAC_OSX && file.isDirectory()) {
                _logger.warn("Selected file is an application package: {}", file);
                file = null;
            } else {
                if (!file.exists()) {
                    _logger.warn("Selected file does not exist: {}", file);

                    if (mimeType == null) {
                        file = null;
                    } else if (FileUtils.getExtension(file) == null) {
                        // try using the same file name with extension :
                        file = mimeType.checkFileExtension(file);
                        // check again if that file exists :
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                }
            }
        }
        return file;
    }

    /**
     * Show the Save File Dialog using following properties:
     * @param title dialog title
     * @param currentDir optional current directory as file
     * @param mimeType optional file mime type used to get both file extension(s) and file chooser filter
     * @param defaultFileName optional default file name
     * @return File instance or null if dialog was discarded
     */
    public static File showSaveFileChooser(final String title, final File currentDir, final MimeType mimeType, final String defaultFileName) {
        File file = null;

        if (USE_DIALOG_FOR_FILE_CHOOSER) {
            final FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.SAVE);
            if (currentDir != null) {
                fileDialog.setDirectory(currentDir.getAbsolutePath());
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
                file = new File(fileDialog.getDirectory(), fileDialog.getFile());
            }

        } else {
            final JFileChooser fileChooser = new JFileChooser();
            if (currentDir != null) {
                fileChooser.setCurrentDirectory(currentDir);
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
                file = fileChooser.getSelectedFile();
            }
        }
        if (file != null) {
            if (mimeType != null) {
                file = mimeType.checkFileExtension(file);
            }

            // Mac OS X already handles file overwrite confirmation:
            if (!SystemUtils.IS_OS_MAC_OSX && file.exists()) {
                if (!MessagePane.showConfirmFileOverwrite(file.getName())) {
                    StatusBar.show("overwritting cancelled.");
                    file = null;
                }
            }
        }
        return file;
    }

    /**
     * Forbidden constructor
     */
    private FileChooser() {
        super();
    }
}
