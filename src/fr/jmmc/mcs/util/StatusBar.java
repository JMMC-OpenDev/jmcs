/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StatusBar.java,v 1.2 2006-07-12 15:49:21 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/07/12 14:27:51  lafrasse
 * Creation
 *
 ******************************************************************************/
package jmmc.mcs.util;

import jmmc.mcs.log.MCSLogger;

import javax.swing.*;


/**
 * A status bar that can be shared all along an application.
 */
public class StatusBar extends JPanel
{
    /** Status label */
    static JLabel _statusLabel = new JLabel();

    /**
     * Constructor.
     *
     * Should be call at least one in order to allow usage.
     */
    public StatusBar()
    {
        super();

        // Layed out horizontally
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(new JLabel("Status : "));
        add(_statusLabel);
    }

    /**
     * Set the satus bar text.
     *
     * @param message the message to be displayed bu the status bar.
     */
    public static void show(String message)
    {
        MCSLogger.trace();

        _statusLabel.setText(message);
    }
}
/*___oOo___*/
