/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.util.Hashtable;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;


/**
 * ActionRegistrar singleton class.
 */
public class ActionRegistrar
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.util.ActionRegistrar");

    /** Singleton instance */
    private static ActionRegistrar _instance = null;

    /** Preference Action unic identifying key */
    private static final String _preferenceActionKey = "preferenceActionKey";

    /** File opening action unic identifying key */
    private static final String _openActionKey = "openActionKey";

    /** Quit Action unic identifying key */
    private static final String _quitActionKey = "quitActionKey";

    /**
     * Hastable to associate string keys like
     * "fr.jmmc.classpath.classname:fiedname" to AbstractAction instances.
     */
    private Hashtable<String, AbstractAction> _register = null;

    /** Hidden constructor */
    protected ActionRegistrar()
    {
        _register = new Hashtable<String, AbstractAction>();
    }

    /** Return the singleton instance */
    public static final synchronized ActionRegistrar getInstance()
    {
        // DO NOT MODIFY !!!
        if (_instance == null)
        {
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
    public AbstractAction put(String classPath, String fieldName,
        AbstractAction action)
    {
        _logger.entering("ActionRegistrar", "put");

        String         internalActionKey = classPath + ":" + fieldName;
        AbstractAction previousAction    = _register.put(internalActionKey,
                action);

        if (previousAction == null)
        {
            if (_logger.isLoggable(Level.FINEST)) {
              _logger.finest("Registered '" + internalActionKey +
                "' action for the first time.");
            }
        }
        else if (previousAction != action)
        {
            if (_logger.isLoggable(Level.WARNING)) {
                _logger.warning("Overwritten the previously registered '" +
                    internalActionKey + "' action.");
            }
        }
        else
        {
            if (_logger.isLoggable(Level.FINE)) {
              _logger.fine("Registered '" + internalActionKey +
                "' action succesfully.");
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
    public AbstractAction get(String classPath, String fieldName)
    {
        _logger.entering("ActionRegistrar", "get");

        if (classPath == null && fieldName == null) {
          return null;
        }

        String         internalActionKey = classPath + ":" + fieldName;
        AbstractAction retrievedAction   = _register.get(internalActionKey);

        if (retrievedAction == null)
        {
            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE, "Cannot find '" + internalActionKey + "' action :", new Throwable());
            }
        }
        else
        {
            if (_logger.isLoggable(Level.FINE)) {
              _logger.fine("Retrieved '" + internalActionKey +
                "' action succesfully.");
            }
        }

        return retrievedAction;
    }

    /**
     * Register an action dedicated to handle Preference panel display.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putPreferenceAction(AbstractAction action)
    {
        _logger.entering("ActionRegistrar", "putPreferenceAction");

        return _register.put(_preferenceActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Preference panel
     * display handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getPreferenceAction()
    {
        _logger.entering("ActionRegistrar", "getPreferenceAction");

        return _register.get(_preferenceActionKey);
    }

    /**
     * Register an action dedicated to file opening sequence.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putOpenAction(AbstractAction action)
    {
        _logger.entering("ActionRegistrar", "putOpenAction");

        return _register.put(_openActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to file opening sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getOpenAction()
    {
        _logger.entering("ActionRegistrar", "getOpenAction");

        return _register.get(_openActionKey);
    }

    /**
     * Register an action dedicated to handle Quit sequence.
     *
     * @param action the action instance to register.
     *
     * @return the previous registered action, null otherwise.
     */
    public AbstractAction putQuitAction(AbstractAction action)
    {
        _logger.entering("ActionRegistrar", "putQuitAction");

        return _register.put(_quitActionKey, action);
    }

    /**
     * Return the previously registered action dedicated to Quit sequence
     * handling.
     *
     * @return the retrieved registered action, null otherwise.
     */
    public AbstractAction getQuitAction()
    {
        _logger.entering("ActionRegistrar", "getQuitAction");

        return _register.get(_quitActionKey);
    }

    /**
     * Serialize the registrar content for output.
     *
     * @return the registrar content as a String.
     */
    @Override
    public String toString()
    {
        _logger.entering("ActionRegistrar", "toString");

        return _register.toString();
    }
}
/*___oOo___*/
