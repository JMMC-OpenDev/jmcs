/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RegisteredAction.java,v 1.9 2010-09-24 15:43:12 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2009/04/30 09:00:50  lafrasse
 * Minor documentation update.
 *
 * Revision 1.7  2009/01/05 13:43:08  lafrasse
 * Added Open action handling.
 *
 * Revision 1.6  2008/10/16 08:55:49  mella
 * Inherit MCS action to first init action parameters like MCSAction
 *
 * Revision 1.5  2008/09/19 08:49:53  lafrasse
 * Minor compilation bug correction.
 *
 * Revision 1.4  2008/09/19 08:46:20  lafrasse
 * Forced 'preference' and 'quit' action names and aaccelerators.
 *
 * Revision 1.3  2008/09/08 14:28:18  lafrasse
 * Added third contructor with action name only.
 * Corrected a bug that was linking naled action with its actionName instead of
 * fieldName.
 *
 * Revision 1.2  2008/09/06 07:53:19  lafrasse
 * Added a second constructeur to set action name and accelerator.
 *
 * Revision 1.1  2008/09/04 15:47:29  lafrasse
 * First revision.
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;



/**
 * Action class customized to auto-register in ActionRegistrar when created.
 */
public abstract class RegisteredAction extends MCSAction
{
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.util.RegisteredAction");

    /** Action Registrar */
    private static final ActionRegistrar _registrar = ActionRegistrar.getInstance();

    /**
     * Constructor, that automatically register the action in RegisteredAction.
     * Action name, icon, accelerator and description is first inited using
     * fieldName to build a MCSAction.
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     */
    public RegisteredAction(String classPath, String fieldName)
    {
        super(fieldName);
        _registrar.put(classPath, fieldName, this);
    }

    /**
     * Constructor, that automatically register the action in RegisteredAction,
     * and assign it a name.
     * Action name, icon, accelerator and description is first inited following MCSAction.
     * Then actionName set or overwritte action name.
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param actionName the name of the action.
     */
    public RegisteredAction(String classPath, String fieldName,
        String actionName)
    {
        this(classPath, fieldName);

        // Define action name and accelerator
        putValue(Action.NAME, actionName);
    }

    /**
     * Constructor, that automatically register the action in RegisteredAction,
     * and assign it a name and an accelerator.
     * Action name, icon, accelerator and description is first inited following MCSAction.
     * Then actionName and actionAccelerator set or overwritte action name and action accelerator.
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
        this(classPath, fieldName, actionName);

        // Define action name and accelerator
        putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(actionAccelerator));
    }

    /**
     * Flag the action as the one dedicated to handle Preference panel display.
     */
    public void flagAsPreferenceAction()
    {
        _logger.entering("RegisteredAction", "flagAsPreferenceAction");

        // Force the preference action name
        putValue(Action.NAME, "Preferences...");

        _registrar.putPreferenceAction(this);
    }

    /**
     * Flag the action as the one dedicated to file opening sequence.
     */
    public void flagAsOpenAction()
    {
        _logger.entering("RegisteredAction", "flagAsOpenAction");

        // Force the 'open' action name
        putValue(Action.NAME, "Open");

        // Force the 'open' keyboard shortcut
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl O"));

        _registrar.putOpenAction(this);
    }

    /**
     * Flag the action as the one dedicated to handle Quit sequence.
     */
    public void flagAsQuitAction()
    {
        _logger.entering("RegisteredAction", "flagAsQuitAction");

        // Force the 'quit' action name
        putValue(Action.NAME, "Quit");

        // Force the 'quit' keyboard shortcut
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl Q"));

        _registrar.putQuitAction(this);
    }
}
/*___oOo___*/
