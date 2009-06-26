/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StatusBar.java,v 1.8 2009-04-30 13:02:14 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2009/04/20 08:22:33  lafrasse
 * Optimized space between the JMMC logo and the resizing handle.
 *
 * Revision 1.6  2009/04/16 15:44:51  lafrasse
 * Jalopization.
 *
 * Revision 1.5  2009/04/15 11:55:24  mella
 * fix space
 *
 * Revision 1.4  2009/04/09 06:26:07  sprette
 * Change small jmmc logo name
 * Add space on the right bottom corner into the status bar (for Mac OS X)
 *
 * Revision 1.3  2009/04/08 13:00:51  sprette
 * First CVS test
 *
 * Revision 1.2  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
 * Revision 1.1  2006/11/18 22:52:56  lafrasse
 * Moved from jmmc.mcs.util .
 *
 * Revision 1.2  2006/07/12 15:49:21  lafrasse
 * Added class documentation
 *
 * Revision 1.1  2006/07/12 14:27:51  lafrasse
 * Creation
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import org.apache.commons.lang.SystemUtils;

import java.awt.Font;

import java.util.logging.*;

import javax.swing.*;


/**
 * A status bar that can be shared all along an application.
 */
public class StatusBar extends JPanel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.gui.StatusBar");

    /** Status label */
    private static JLabel _statusLabel = new JLabel();

    /**
     * Constructor.
     *
     * Should be called at least once in order to allow usage.
     */
    public StatusBar()
    {
        super();

        // Layed out horizontally
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // Create logo
        JLabel jmmcLogo = new JLabel();
        jmmcLogo.setIcon(new ImageIcon(getClass().getResource("logo_small.png")));
        jmmcLogo.setVisible(true);

        // Create text logo
        JLabel textStatusBar = new JLabel();
        textStatusBar.setText("Provided by ");
        textStatusBar.setFont(new Font("Comic Sans MS", 2, 10));
        textStatusBar.setVisible(true);

        // StatusBar elements placement
        Box hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("Status : "));
        hBox.add(_statusLabel);
        hBox.add(Box.createHorizontalGlue());
        hBox.add(textStatusBar);
        hBox.add(jmmcLogo);

        /*
         * Add a space on the right bottom angle because Mac OS X corner is
         * already decored with its resize handle
         */
        if (SystemUtils.IS_OS_MAC_OSX == true)
        {
            hBox.add(Box.createHorizontalStrut(14));
        }

        add(hBox);
    }

    /**
     * Set the satus bar text.
     *
     * @param message the message to be displayed bu the status bar.
     */
    public static synchronized void show(String message)
    {
        _logger.entering("StatusBar", "show");

        _statusLabel.setText(message);
    }
}
/*___oOo___*/
