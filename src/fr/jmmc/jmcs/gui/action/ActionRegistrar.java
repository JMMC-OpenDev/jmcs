/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 * ActionRegistrar singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class ActionRegistrar {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(ActionRegistrar.class.getName());
    /** Singleton instance */
    private static ActionRegistrar _instance = null;
    /** Preference Action unic identifying key */
    private static final String _preferenceActionKey = "preferenceActionKey";
    /** File opening action unic identifying key */
    private static final String _openActionKey = "openActionKey";
    /** Quit Action unic identifying key */
    private static final String _quitActionKey = "quitActionKey";
    /* members */
    /**
     * Hastable to associate string keys like
     * "fr.jmmc.classpath.classname:fiedname" to AbstractAction instances.
     */
    private final Map<String, AbstractAction> _register = Collections.synchronizedMap(new HashMap<String, AbstractAction>());
    /** unique action keys requiring defered initialization (after application startup) */
    private final Set<String> _deferedInitActions = new HashSet<String>();

    /** Hidden constructor */
    private ActionRegistrar() {
        super();
    }

    /** @return the singleton instance */
    public static synchronized ActionRegistrar getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new ActionRegistrar();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Register an action, its class and field name, in the registrar.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction put(final String classPath, final String fieldName, final AbstractAction action) {
        _logger.entering("ActionRegistrar", "put");

        final String internalActionKey = classPath + ":" + fieldName;
        final AbstractAction previousAction = _register.put(internalActionKey, action);

        if (previousAction == null) {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("Registered '" + internalActionKey + "' action for the first time.");
            }
        } else if (previousAction != action) {
            if (_logger.isLoggable(Level.WARNING)) {
                _logger.warning("Overwritten the previously registered '" + internalActionKey + "' action.");
            }
        } else {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Registered '" + internalActionKey + "' action succesfully.");
            }
        }

        return previousAction;
    }

    /**
     * Return the previously registered action for the given class path and
     * field name.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction get(final String classPath, final String fieldName) {
        _logger.entering("ActionRegistrar", "get");

        if (classPath == null && fieldName == null) {
            return null;
        }

        final String internalActionKey = classPath + ":" + fieldName;
        final AbstractAction retrievedAction = getAction(internalActionKey);

        if (retrievedAction == null) {
            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE, "Cannot find '" + internalActionKey + "' action :", new Throwable());
                _logger.log(Level.SEVERE, "Current registered actions are :" + dumpRegisteredActions());
            }
        } else {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Retrieved '" + internalActionKey + "' action succesfully.");
            }
        }

        return retrievedAction;
    }

    /**
     * Return the previously registered action for the given key.
     *
     * @param actionKey action key
     * @return the retrieved registered action, null otherwise.
     */
    private AbstractAction getAction(final String actionKey) {
        _logger.entering("ActionRegistrar", "getAction");

        return _register.get(actionKey);
    }

    /**
     * Register an action dedicated to handle Preference panel display.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putPreferenceAction(final AbstractAction action) {
        _logger.entering("ActionRegistrar", "putPreferenceAction");

        return _register.put(_preferenceActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Preference panel
     * display handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getPreferenceAction() {
        _logger.entering("ActionRegistrar", "getPreferenceAction");

        return getAction(_preferenceActionKey);
    }

    /**
     * Register an action dedicated to file opening sequence.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putOpenAction(final AbstractAction action) {
        _logger.entering("ActionRegistrar", "putOpenAction");

        return _register.put(_openActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to file opening sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getOpenAction() {
        _logger.entering("ActionRegistrar", "getOpenAction");

        return getAction(_openActionKey);
    }

    /**
     * Register an action dedicated to handle Quit sequence.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putQuitAction(final AbstractAction action) {
        _logger.entering("ActionRegistrar", "putQuitAction");

        return _register.put(_quitActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Quit sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getQuitAction() {
        _logger.entering("ActionRegistrar", "getQuitAction");

        return getAction(_quitActionKey);
    }

    /**
     * Indicate that the given action (previoulsy registered) must be initialized after the application startup
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     */
    protected void flagAsDeferedInitAction(final String classPath, final String fieldName) {
        _logger.entering("ActionRegistrar", "flagAsDeferedInitAction");

        final String internalActionKey = classPath + ":" + fieldName;

        if (getAction(internalActionKey) instanceof RegisteredAction) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Action '" + internalActionKey + "' will be initialized later.");
            }

            _deferedInitActions.add(internalActionKey);
        }
    }
    
    /**
     * Perform defered initialization of such actions
     */
    public void performDeferedInitialization() {
        _logger.entering("ActionRegistrar", "performDeferedInitialization");
        
        RegisteredAction action;
        for (String actionKey : _deferedInitActions) {
            action = (RegisteredAction)getAction(actionKey);
            
            if (action != null) {
                action.performDeferedInitialization();
            }
        }
    }

    /**
     * Serialize the registrar content for output.
     *
     * @return the registrar content as a String.
     */
    @Override
    public String toString() {
        _logger.entering("ActionRegistrar", "toString");

        return _register.toString();
    }

    /**
     * Dump name of all registered actions  (sorted by keys)
     * @return string one line per name of registered action
     */
    public String dumpRegisteredActions() {
        // sort properties :
        final String[] keys = new String[_register.size()];
        _register.keySet().toArray(keys);
        Arrays.sort(keys);

        final StringBuilder sb = new StringBuilder(2048);
        for (String key : keys) {
            sb.append(key).append("\n");
        }
        return sb.toString();
    }
}
/*___oOo___*/
