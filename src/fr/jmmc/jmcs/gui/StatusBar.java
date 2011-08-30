/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.App;
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
 * 
 * @author Sylvain LAFRASSE, Samuel PRETTE, Guillaume MELLA, Laurent BOURGES.
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
        jmmcLogo.setIcon(new ImageIcon(getClass().getResource("/fr/jmmc/jmcs/resource/logo_small.png")));
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
