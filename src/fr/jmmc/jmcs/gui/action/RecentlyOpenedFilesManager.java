/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RecentlyOpenedFilesManager singleton class.
 * 
 * @author Sylvain LAFRASSE.
 */
public class RecentlyOpenedFilesManager {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(RecentlyOpenedFilesManager.class.getName());
    /** Singleton instance */
    private static volatile RecentlyOpenedFilesManager _instance = null;
    /** Hook to the "Open Recent" sub-menu */
    private static volatile JMenu _menu = null;
    /** JMenu to Action relations */
    private static final Map<String, String> _files = Collections.synchronizedMap(new LinkedHashMap<String, String>(8));
    private static volatile ActionRegistrar _registrar = null;

    /**
     * Return the singleton instance
     * @return singleton instance
     */
    private static synchronized RecentlyOpenedFilesManager getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new RecentlyOpenedFilesManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Hidden constructor
     */
    protected RecentlyOpenedFilesManager() {
        _registrar = ActionRegistrar.getInstance();
        // @TODO : the same for shared prefs.
    }

    /**
     * Link SampManager instance to the "Open Recent" sub-menu
     * @param menu "Open Recent" sub-menu container
     */
    public static synchronized JMenu getMenu() {

        getInstance();

        _menu = new JMenu("Open Recent");

        populateMenuFromSharedPreferences();
        // If no recent files registered at all
        if (_files.isEmpty()) {
            // Make the "Open Recent" sub-menu disabled
            _menu.setEnabled(false);
        }

        addCleanAction();

        return _menu;
    }

    // @TODO : Handle MimeTypes !!!
    /**
     * @TODO : Add a "Clear" item at end below a separator
     */
    private static void addCleanAction() {
    }

    /**
     * @TODO : grab recent files from shared preference
     */
    private static void populateMenuFromSharedPreferences() {
        /*
         addFile("/tmp/toto", "toto");
         addFile("/tmp/titi", "titi");
         addFile("/tmp/tata", "tata");
         addFile("/tmp/tutu", "tutu");
         addFile("/Users/lafrasse/test.scvot", "test.scvot");
         */
    }

    public static synchronized void addFile(final File file) {
        try {
            addFile(file.getCanonicalPath(), file.getName());
        } catch (IOException ex) {
            _logger.warn("Could not resole file path", ex);
        }
    }

    private static synchronized void addFile(final String path, final String name) {

        getInstance();

        // TODO : check params validity (null, ...)

        _files.put(path, name);

        // Clean, then fill sub-menu
        _menu.removeAll();
        for (final String currentPath : _files.keySet()) {

            final String currentName = _files.get(currentPath);

            final AbstractAction currentAction = new AbstractAction(currentName) {
                public void actionPerformed(ActionEvent ae) {
                    _registrar.getOpenAction().actionPerformed(new ActionEvent(_registrar, 0, currentPath));
                }
            };

            // TODO : disable action if file vanished

            _menu.add(new JMenuItem(currentAction));
        }

        addCleanAction();
        _menu.setEnabled(true);

        // TODO : Handle list size limit ?

        flushRecentFileListToSharedPrefrence();
    }

    /**
     * @TODO : Flush file list to shared preference
     */
    private static void flushRecentFileListToSharedPrefrence() {
    }
}
