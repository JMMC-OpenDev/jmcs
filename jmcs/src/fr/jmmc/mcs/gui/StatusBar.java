/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StatusBar.java,v 1.4 2009-04-09 06:26:07 sprette Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import fr.jmmc.mcs.log.*;

import java.awt.Font;
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

         //Create logo
        JLabel logoJmmc = new JLabel();
        logoJmmc.setIcon(new ImageIcon(getClass().getResource("logo_small.png")));
        logoJmmc.setVisible(true);

        //Create text logo
        JLabel textStatusBar = new JLabel();
        textStatusBar.setText("Provided by ");
        textStatusBar.setFont(new Font("Comic Sans MS", 2, 10));
        textStatusBar.setVisible(true);

        //StatusBar elements placement
        Box hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("Status : "));
        hBox.add(_statusLabel);
        hBox.add(Box.createHorizontalGlue());
        hBox.add(textStatusBar);
        hBox.add(logoJmmc);
        // Add one space on the right bottom angle because Mac OS X
        // corner is already decored
        hBox.add(Box.createHorizontalStrut(20));

        this.add(hBox);

        

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
