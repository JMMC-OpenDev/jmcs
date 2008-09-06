/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RegisteredAction.java,v 1.2 2008-09-06 07:53:19 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/09/04 15:47:29  lafrasse
 * First revision.
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.util.logging.*;

import javax.swing.*;


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
     * Constructor, that automatically register the action in RegisteredAction,
     * and assign it a name and an accelerator.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param actionName the name of the action.
     * @param actionAccelerator the accelerator of the action, like "ctrl Q".
     */
    public RegisteredAction(String classPath, String fieldName,
        String actionName, String actionAccelerator)
    {
        this(classPath, actionAccelerator);

        // Define action name and accelerator
        putValue(Action.NAME, actionName);
        putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(actionAccelerator));
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
