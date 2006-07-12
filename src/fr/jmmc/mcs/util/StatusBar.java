/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StatusBar.java,v 1.1 2006-07-12 14:27:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.scalib.sclgui;

import jmmc.mcs.log.MCSLogger;

import javax.swing.*;


/**
 * Class description goes here.
 */
public class StatusBar extends JPanel
{
    /** Status label */
    static JLabel _statusLabel = new JLabel();

    /**
     * ...constructor StatusBar documentation comment...
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
