/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import fr.jmmc.jmcs.collection.FixedSizeLinkedHashMap;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RecentFilesManager singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class RecentFilesManager {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(RecentFilesManager.class.getName());
    /** flag to enable/disable the recent file management */
    public static final boolean ENABLE_FILE_HISTORY = false;
    /** max number of entries */
    private static final int MAXIMUM_HISTORY_ENTRIES = 10;
    /** Singleton instance */
    private static volatile RecentFilesManager _instance = null;
    /* members */
    /** action registrar reference */
    private final ActionRegistrar _registrar;
    /** Hook to the "Open Recent" sub-menu */
    private final JMenu _menu;
    /** thread safe recent file names keyed by file paths */
    private final Map<String, String> _files = Collections.synchronizedMap(new FixedSizeLinkedHashMap<String, String>(MAXIMUM_HISTORY_ENTRIES));

    /**
     * Return the singleton instance
     * @return singleton instance
     */
    public static synchronized RecentFilesManager getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new RecentFilesManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Hidden constructor
     */
    protected RecentFilesManager() {
        // @TODO : the same for shared prefs.
        _registrar = ActionRegistrar.getInstance();
        _menu = new JMenu("Open Recent");
        populateMenuFromSharedPreferences();
        addCleanAction();
    }

    /**
     * Link RecentFilesManager menu to the "Open Recent" sub-menu
     * @return menu "Open Recent" sub-menu container
     */
    public JMenu getMenu() {
        if (!RecentFilesManager.ENABLE_FILE_HISTORY) {
            return null;
        }
        return _menu;
    }

    // @TODO : Handle MimeTypes !!!
    /**
     * Add a "Clear" item at end below a separator
     */
    private void addCleanAction() {

        final AbstractAction cleanAction = new AbstractAction("Clear History") {
            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            @Override
            public void actionPerformed(ActionEvent ae) {
                _menu.removeAll();
                _files.clear();
                _menu.setEnabled(false);
            }
        };

        _menu.add(new JSeparator());
        _menu.add(new JMenuItem(cleanAction));
    }

    /**
     * @TODO : grab recent files from shared preference
     */
    private void populateMenuFromSharedPreferences() {
        /*
         addFile("/tmp/toto", "toto");
         addFile("/tmp/titi", "titi");
         addFile("/tmp/tata", "tata");
         addFile("/tmp/tutu", "tutu");
         addFile("/Users/lafrasse/test.scvot", "test.scvot");
         */
        // If no recent files registered at all
        if (_files.isEmpty()) {
            // Make the "Open Recent" sub-menu disabled
            _menu.setEnabled(false);
        }
    }

    /**
     * TODO
     * @param file 
     */
    public synchronized void addFile(final File file) {
        if (!RecentFilesManager.ENABLE_FILE_HISTORY) {
            return;
        }

        // Check parameter validity
        if (!file.canRead()) {
            _logger.warn("Could not read file " + file);
            return;
        }

        // Check file path
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException ex) {
            _logger.warn("Could not resolve file path", ex);
            return;
        }
        if ((path == null) || (path.length() == 0)) {
            _logger.warn("Could not resolve empty file path");
            return;
        }

        // Check file name
        String name = file.getName();
        // If no name found
        if ((name == null) || (name.length() == 0)) {
            name = path; // Use path instead
        }

        // Store new recent file
        _files.put(path, name);

        // Clean, then re-fill sub-menu
        _menu.removeAll();
        for (final String currentPath : _files.keySet()) {

            final String currentName = _files.get(currentPath);

            final AbstractAction currentAction = new AbstractAction(currentName) {
                /** default serial UID for Serializable interface */
                private static final long serialVersionUID = 1;

                @Override
                public void actionPerformed(ActionEvent ae) {
                    _registrar.getOpenAction().actionPerformed(new ActionEvent(_registrar, 0, currentPath));
                }
            };

            _menu.add(new JMenuItem(currentAction));
        }

        addCleanAction();
        _menu.setEnabled(true);

        flushRecentFileListToSharedPrefrence();
    }

    /**
     * @TODO : Flush file list to shared preference
     */
    private static void flushRecentFileListToSharedPrefrence() {
    }
}
