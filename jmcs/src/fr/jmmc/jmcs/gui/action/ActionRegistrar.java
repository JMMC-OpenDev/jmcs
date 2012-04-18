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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.AbstractAction;

/**
 * ActionRegistrar singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public class ActionRegistrar {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(ActionRegistrar.class.getName());
    /** Singleton instance */
    private static ActionRegistrar _instance = null;
    /** Preference Action unique identifying key */
    private static final String _preferenceActionKey = "preferenceActionKey";
    /** File opening action unique identifying key */
    private static final String _openActionKey = "openActionKey";
    /** Quit Action unique identifying key */
    private static final String _quitActionKey = "quitActionKey";
    /* members */
    /**
     * Hashtable to associate string keys like
     * "fr.jmmc.classpath.classname:fieldname" to AbstractAction instances.
     */
    private final Map<String, AbstractAction> _register = Collections.synchronizedMap(new HashMap<String, AbstractAction>());
    /** unique action keys requiring differed initialization (after application startup) */
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
        final String internalActionKey = classPath + ":" + fieldName;
        final AbstractAction previousAction = _register.put(internalActionKey, action);

        if (previousAction == null) {
            _logger.debug("Registered '{}' action for the first time.", internalActionKey);
        } else if (previousAction != action) {
            _logger.debug("Overwritten the previously registered '{}' action.", internalActionKey);
        } else {
            _logger.debug("Registered '{}' action succesfully.", internalActionKey);
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
        if (classPath == null && fieldName == null) {
            return null;
        }

        final String internalActionKey = classPath + ":" + fieldName;
        final AbstractAction retrievedAction = getAction(internalActionKey);

        if (retrievedAction == null) {
            _logger.error("Cannot find '{}' action :", internalActionKey, new Throwable());
            _logger.error("Current registered actions are: {}", dumpRegisteredActions());
        } else {
            _logger.debug("Retrieved '{}' action succesfully.", internalActionKey);
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
        return _register.put(_preferenceActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Preference panel
     * display handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getPreferenceAction() {
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
        return _register.put(_openActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to file opening sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getOpenAction() {
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
        return _register.put(_quitActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Quit sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getQuitAction() {
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
        final String internalActionKey = classPath + ":" + fieldName;

        if (getAction(internalActionKey) instanceof RegisteredAction) {
            _logger.debug("Action '{}' will be initialized later.", internalActionKey);

            _deferedInitActions.add(internalActionKey);
        }
    }

    /**
     * Perform defered initialization of such actions
     */
    public void performDeferedInitialization() {
        RegisteredAction action;
        for (String actionKey : _deferedInitActions) {
            action = (RegisteredAction) getAction(actionKey);

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
