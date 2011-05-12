/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;



/**
 * Use this class  to define new Actions.
 */
public abstract class MCSAction extends AbstractAction
{
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /**
     * This constructor use the resource file to get text description and icon
     * of action.
     * @param actionName name of the action as declared in the resource file
     */
    public MCSAction(final String actionName)
    {

        // Collect action info
        String    text        = Resources.getActionText(actionName);
        String    desc        = Resources.getActionDescription(actionName);
        ImageIcon icon        = Resources.getActionIcon(actionName);
        KeyStroke accelerator = Resources.getActionAccelerator(actionName);

        // Init action    
        if (text != null)
        {
            putValue(Action.NAME, text);
        }

        if (desc != null)
        {
            putValue(Action.SHORT_DESCRIPTION, desc);
        }

        if (icon != null)
        {
            putValue(Action.SMALL_ICON, icon);
        }

        if (accelerator != null)
        {
            putValue(Action.ACCELERATOR_KEY, accelerator);
        }
    }
}
/*___oOo___*/
