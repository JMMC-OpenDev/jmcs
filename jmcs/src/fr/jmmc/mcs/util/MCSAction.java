/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSAction.java,v 1.3 2007-02-13 13:48:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/11/20 15:41:23  lafrasse
 * Added error handling code.
 *
 * Revision 1.1  2006/11/18 22:56:03  lafrasse
 * Moved from jmmc.scalib.sclgui and renamed from SCAction.java .
 * Added support for Key Accelerators (keyboard shortcut).
 *
 * Revision 1.2  2006/07/28 08:33:55  mella
 * add import to make it compile
 *
 * Revision 1.1  2006/06/26 14:30:40  mella
 * *** empty log message ***
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.log.*;
import fr.jmmc.mcs.util.*;

import javax.swing.*;


/**
 * Use this class  to define new Actions.
 */
public abstract class MCSAction extends AbstractAction
{
    /**
     * This constructor use the resource file to get text description and icon
     * of action.
     */
    public MCSAction(String actionName)
    {
        MCSLogger.trace();

        /*
           if ((actionName == null) || (Resources.actionExists(actionName) == false))
           {
                   throws(new Execption());
           }
         */

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
