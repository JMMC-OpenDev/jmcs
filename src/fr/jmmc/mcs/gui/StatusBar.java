/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StatusBar.java,v 1.11 2011-04-06 15:42:13 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2010/10/11 13:28:52  lafrasse
 * Ensures that GUI updates are done in EDT.
 *
 * Revision 1.9  2010/05/20 13:13:35  mella
 * Open the application web page by one logo click
 *
 * Revision 1.8  2009/04/30 13:02:14  lafrasse
 * Replaced MCSLog by standard logging.
 * Added protection against multiple thread concurrent access.
 * Documentation enhancement.
 * Moved logo only under Mac OS X.
 *
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A status bar that can be shared all along an application.
 */
public class StatusBar extends JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.gui.StatusBar");
    /** Status label */
    private static final JLabel _statusLabel = new JLabel();

    /**
     * Constructor.
     *
     * Should be called at least once in order to allow usage.
     */
    public StatusBar() {
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
        if (SystemUtils.IS_OS_MAC_OSX) {
            hBox.add(Box.createHorizontalStrut(14));
        }

        add(hBox);

        // Get application data model to launch the default browser with the given link
        final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
        if (applicationDataModel != null) {
            jmmcLogo.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent evt) {
                    BrowserLauncher.openURL(applicationDataModel.getLinkValue());
                }
            });
        }
    }

    /**
     * Set the status bar text.
     *
     * @param message the message to be displayed by the status bar.
     */
    public static void show(final String message) {
        _logger.entering("StatusBar", "show");

        // update the status bar within EDT :
        if (SwingUtilities.isEventDispatchThread()) {
            setStatusLabel(message);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setStatusLabel(message);
                }
            });
        }
    }

    /**
     * Change the content of the status bar
     * @param message message to display
     */
    private static void setStatusLabel(final String message) {
        _statusLabel.setText(message);
    }
}
/*___oOo___*/
