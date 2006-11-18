/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSAction.java,v 1.1 2006-11-18 22:56:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/28 08:33:55  mella
 * add import to make it compile
 *
 * Revision 1.1  2006/06/26 14:30:40  mella
 * *** empty log message ***
 *
 ******************************************************************************/
package jmmc.mcs.util;

import jmmc.mcs.log.*;

import jmmc.mcs.util.*;

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

        // Collect action info
        String    text        = Resources.getActionText(actionName);
        String    desc        = Resources.getActionDescription(actionName);
        ImageIcon icon        = Resources.getActionIcon(actionName);
        KeyStroke accelerator = Resources.getActionAccelerator(actionName);

        // Init action    
        // @TODO check if null must be checked...
        putValue(Action.NAME, text);
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.ACCELERATOR_KEY, accelerator);
    }
}
/*___oOo___*/
