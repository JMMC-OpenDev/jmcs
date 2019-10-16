/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.service;

import fr.jmmc.jmcs.data.preference.SessionSettingsPreferences;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.jmcs.util.collection.FixedSizeLinkedHashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RecentValuesManager singleton class.
 * 
 * @author Laurent BOURGES.
 */
public final class RecentValuesManager {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(RecentValuesManager.class.getName());
    /** Maximum number of recent values by field type */
    private static final int MAXIMUM_HISTORY_ENTRIES = 10;
    /** undefined value for values */
    private static final String VALUE_NONE = "[None]";

    /** Singleton instance */
    private static volatile RecentValuesManager _instance = null;
    /* Members */
    /** Flag to enable or disable this feature */
    boolean _enabled = true;
    /** recent values repositories */
    private final Map<String, Map<String, String>> _repository = new HashMap<String, Map<String, String>>(64);
    /** popup menus associated to components (via weak reference) */
    private final Map<String, WeakHashMap<JPopupMenu, JPopupMenu>> _popupMenus = new HashMap<String, WeakHashMap<JPopupMenu, JPopupMenu>>(64);
    /** listener associated to popup menus */
    private final WeakHashMap<JPopupMenu, WeakActionListener> _listeners = new WeakHashMap<JPopupMenu, WeakActionListener>(64);

    /**
     * Return the singleton instance
     * @return singleton instance
     */
    static synchronized RecentValuesManager getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new RecentValuesManager();
        }

        return _instance;
        // DO NOT MODIFY !!!
    }

    /**
     * Hidden constructor
     */
    protected RecentValuesManager() {
        super();
    }

    /**
     * Enables or disables this feature
     * @param enabled false to disable
     */
    public static void setEnabled(final boolean enabled) {
        getInstance()._enabled = enabled;
    }

    /**
     * Return flag to enable or disable this feature
     * @return true if enabled; false otherwise
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Return the Popup menu
     * @param key given key
     * @param listener action listener invoked when clicking on the popup menu
     * @return Popup menu
     */
    public static JPopupMenu getMenu(final String key, final ActionListener listener) {
        final RecentValuesManager rvm = getInstance();
        if (rvm.isEnabled()) {
            return rvm.createMenu(key, listener);
        }
        return null;
    }

    private JPopupMenu createMenu(final String key, final ActionListener listener) {
        // create Popup
        final JPopupMenu popupMenu = new JPopupMenu();

        WeakHashMap<JPopupMenu, JPopupMenu> map = _popupMenus.get(key);
        if (map == null) {
            map = new WeakHashMap<JPopupMenu, JPopupMenu>();
            _popupMenus.put(key, map);
        }
        map.put(popupMenu, popupMenu);

        _listeners.put(popupMenu, new WeakActionListener(listener));

        synchronized (_repository) {
            if (_repository.get(key) == null) {
                populateRepositoryFromPreferences(key);
            }
        }
        refreshMenus(key);

        if (_logger.isDebugEnabled()) {
            _logger.debug("createMenu[{}]: {}", key, System.identityHashCode(popupMenu));
            _logger.debug("listener: {}", System.identityHashCode(listener));
        }

        return popupMenu;
    }

    public static boolean isUndefinedValue(final String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return VALUE_NONE.equals(value);
    }

    /**
     * Add the given recent value.
     * @param key given key
     * @param input value as String
     */
    public static void addValue(final String key, final String input) {
        final String value;
        if (isUndefinedValue(input)) {
            // encode undefined value (empty or null):
            value = VALUE_NONE;
        } else {
            value = input;
        }

        final RecentValuesManager rvm = getInstance();
        if (!rvm.isEnabled() || !rvm.storeValue(key, value)) {
            return;
        }
        rvm.refreshMenus(key);
        rvm.flushRepositoryToPreferences(key);
    }

    /**
     * Store the given value in the recent repository.
     * @param key given key
     * @param value value to be added in the repository
     * @return true if operation succeeded else false.
     */
    private boolean storeValue(final String key, final String value) {
        // Check parameter validity
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        // Store value (at first position if already referenced)
        synchronized (_repository) {
            Map<String, String> repositoryValues = _repository.get(key);
            if (repositoryValues == null) {
                repositoryValues = new FixedSizeLinkedHashMap<String, String>(MAXIMUM_HISTORY_ENTRIES);
                _repository.put(key, repositoryValues);
            }
            // 
            repositoryValues.remove(value);
            repositoryValues.put(value, value);
        }
        return true;
    }

    /**
     * Grab recent values from shared preference.
     * @param key given key
     */
    private void populateRepositoryFromPreferences(final String key) {
        final List<String> values = SessionSettingsPreferences.getRecentValues(key);
        if (values == null) {
            return;
        }
        for (String value : values) {
            storeValue(key, value);
        }
    }

    /**
     * Flush values associated to the given key to shared preference.
     * @param key given key
     */
    private void flushRepositoryToPreferences(final String key) {
        // Create list of paths
        final List<String> values;
        synchronized (_repository) {
            values = new ArrayList<String>(_repository.get(key).keySet());
        }
        // Put this to prefs
        SessionSettingsPreferences.setRecentValues(key, values);
    }

    /**
     * Refresh content of the associated pop menu.
     * @param key given key
     */
    private void refreshMenus(final String key) {
        cleanup();

        final WeakHashMap<JPopupMenu, JPopupMenu> map = _popupMenus.get(key);
        if (map != null) {
            for (JPopupMenu popupMenu : map.keySet()) {
                refreshMenu(key, popupMenu);
            }
        }
    }

    private void cleanup() {
        for (Map.Entry<String, WeakHashMap<JPopupMenu, JPopupMenu>> entry : _popupMenus.entrySet()) {

            final WeakHashMap<JPopupMenu, JPopupMenu> map = entry.getValue();
            if (map != null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("popupMenus[{}]: {}", entry.getKey(), map.size());
                }

                for (Iterator<JPopupMenu> it = map.keySet().iterator(); it.hasNext();) {
                    final JPopupMenu popupMenu = it.next();
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("popupMenu: {}", System.identityHashCode(popupMenu));
                    }

                    final WeakActionListener listener = getPopupListenerAlive(popupMenu);
                    if (listener == null) {
                        _listeners.remove(popupMenu);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("removing popup menu: {}", System.identityHashCode(popupMenu));
                        }
                        it.remove();
                    } else {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("listener: {}", System.identityHashCode(listener));
                        }
                    }
                }
            }
        }
    }

    private WeakActionListener getPopupListenerAlive(final JPopupMenu popupMenu) {
        final WeakActionListener listener = _listeners.get(popupMenu);
        if (listener == null || !listener.isAlive()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getPopupListenerAlive: listener[{}] is dead", System.identityHashCode(listener));
            }
            return null;
        }
        return listener;
    }

    /**
     * Refresh content of the given pop menu.
     * @param key given key
     * @param popupMenu popup menu to update
     */
    private void refreshMenu(final String key, final JPopupMenu popupMenu) {
        final WeakActionListener listener = getPopupListenerAlive(popupMenu);
        if (listener == null) {
            return;
        }

        // Clean, then re-fill sub-menu
        popupMenu.removeAll();
        popupMenu.setEnabled(false);

        // For each registered values
        final ListIterator<Map.Entry<String, String>> iter;
        synchronized (_repository) {
            final Map<String, String> repositoryValues = _repository.get(key);
            if (repositoryValues == null) {
                return;
            } else {
                iter = new ArrayList<Map.Entry<String, String>>(repositoryValues.entrySet()).listIterator(repositoryValues.size());
            }
        }

        while (iter.hasPrevious()) {
            final Map.Entry<String, String> entry = iter.previous();
//            final String currentKey = entry.getKey();
            final String currentValue = entry.getValue();

            // Create an action to set the value (call back):
            final AbstractAction currentAction = new ClickValueAction(currentValue, listener);

            final JMenuItem menuItem = new JMenuItem(currentAction);
            popupMenu.add(menuItem);
        }
        if (popupMenu.getComponentCount() != 0) {
            popupMenu.setEnabled(true);
            addCleanAction(key, popupMenu);
        }
    }

    private void addCleanAction(final String key, final JPopupMenu popupMenu) {
        final AbstractAction cleanAction = new AbstractAction("Clear") {
            private static final long serialVersionUID = 1;

            @Override
            public void actionPerformed(final ActionEvent ae) {
                synchronized (_repository) {
                    final Map<String, String> repositoryValues = _repository.get(key);
                    if (repositoryValues != null) {
                        repositoryValues.clear();
                        flushRepositoryToPreferences(key);
                    }
                }
                popupMenu.removeAll();
                popupMenu.setEnabled(false);
            }
        };
        popupMenu.add(new JSeparator());
        popupMenu.add(new JMenuItem(cleanAction));
    }

    final static class ClickValueAction extends AbstractAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        private final String value;
        private final WeakActionListener listener;

        ClickValueAction(final String value, final WeakActionListener listener) {
            super(value);
            this.value = value;
            this.listener = listener;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            _logger.debug("actionPerformed : {}", value);

            // currentKey == currentValue = command:
            final String command = ae.getActionCommand();
            if (isUndefinedValue(command)) {
                listener.actionPerformed(new ActionEvent(ae.getSource(), ActionEvent.ACTION_PERFORMED, null));
            } else {
                listener.actionPerformed(ae);
            }
        }
    }

    final static class WeakActionListener implements ActionListener {

        private final WeakReference<ActionListener> delegate;

        WeakActionListener(final ActionListener listener) {
            this.delegate = new WeakReference<ActionListener>(listener);
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            final ActionListener listener = this.delegate.get();
            if (listener == null) {
                _logger.debug("actionPerformed: listener[{}] is dead", System.identityHashCode(this));
            } else {
                listener.actionPerformed(ae);
            }
        }

        boolean isAlive() {
            return this.delegate.get() != null;
        }
    }
}
