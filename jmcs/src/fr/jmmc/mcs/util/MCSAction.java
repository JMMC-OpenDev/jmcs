/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSAction.java,v 1.4 2010-09-24 15:43:12 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
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
