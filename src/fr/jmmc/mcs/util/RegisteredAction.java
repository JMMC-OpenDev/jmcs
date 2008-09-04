/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RegisteredAction.java,v 1.1 2008-09-04 15:47:29 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.util.logging.*;

import javax.swing.AbstractAction;


/**
 * Action class customized to auto-register in ActionRegistrar when created.
 */
public abstract class RegisteredAction extends AbstractAction
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.util.RegisteredAction");

    /** Action Registrar */
    private static final ActionRegistrar _registrar = ActionRegistrar.getInstance();

    /**
     * Constructor, that automatically register the action in RegisteredAction.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     */
    public RegisteredAction(String classPath, String fieldName)
    {
        _registrar.put(classPath, fieldName, this);
    }

    /**
     * Flag the action as the one dedicated to handle Preference panel display.
     */
    public void flagAsPreferenceAction()
    {
        _logger.entering("RegisteredAction", "flagAsPreferenceAction");

        _registrar.putPreferenceAction(this);
    }

    /**
     * Flag the action as the one dedicated to handle Quit sequence.
     */
    public void flagAsQuitAction()
    {
        _logger.entering("RegisteredAction", "flagAsQuitAction");

        _registrar.putQuitAction(this);
    }
}
/*___oOo___*/
