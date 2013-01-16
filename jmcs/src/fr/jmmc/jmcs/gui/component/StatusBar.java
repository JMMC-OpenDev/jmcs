/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.resource.image.ResourceImage;
import fr.jmmc.jmcs.util.ImageUtils;
import fr.jmmc.jmcs.util.logging.ApplicationLogSingleton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;

/**
 * A status bar that can be shared all along an application.
 * 
 * @author Sylvain LAFRASSE, Samuel PRETTE, Guillaume MELLA, Laurent BOURGES.
 */
public class StatusBar extends JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _statusLogger = ApplicationLogSingleton.getInstance().getLogger(ApplicationLogSingleton.JMMC_STATUS_LOG);
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

        final Border leftBorder = new EmptyBorder(0, 0, 4, 0);

        // Create logo
        final String logoURL = App.getSharedApplicationDataModel().getCompanyLogoResourcePath();
        final ImageIcon imageIcon = ImageUtils.loadResourceIcon(logoURL);
        final ImageIcon scaledImageIcon = ImageUtils.getScaledImageIcon(imageIcon, 17, 0);
        final JLabel logo = new JLabel();
        logo.setIcon(scaledImageIcon);
        logo.setVisible(true);
        logo.setBorder(leftBorder);

        // Create text logo
        final JLabel textStatusBar = new JLabel();
        textStatusBar.setText("Provided by ");
        textStatusBar.setFont(new Font("Comic Sans MS", 2, 10));
        textStatusBar.setVisible(true);
        textStatusBar.setBorder(leftBorder);

        // Create status history button
        final ImageIcon historyIcon = ResourceImage.STATUS_HISTORY.icon();
        final JButton historyButton = new JButton(historyIcon);
        final Border historyBorder = new EmptyBorder(0, 4, 4, 0);
        historyButton.setBorder(historyBorder);
        historyButton.setToolTipText("Click to view status history");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.showLogConsole(ApplicationLogSingleton.JMMC_STATUS_LOG);
            }
        });

        // StatusBar elements placement
        final Box hBox = Box.createHorizontalBox();
        hBox.add(historyButton);
        hBox.add(new JLabel(" Status : "));
        hBox.add(_statusLabel);
        hBox.add(Box.createHorizontalGlue());
        hBox.add(textStatusBar);
        hBox.add(logo);

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
            logo.addMouseListener(new MouseAdapter() {
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
        SwingUtils.invokeEDT(new Runnable() {
            /**
             * Update the status bar using EDT
             */
            @Override
            public void run() {
                setStatusLabel(message);
            }
        });
    }

    /**
     * Set the status bar text if the current message equals the given previous message (ignore case)
     *
     * @param previous the previous message to override
     * @param message the message to be displayed by the status bar.
     */
    public static void showIfPrevious(final String previous, final String message) {
        SwingUtils.invokeEDT(new Runnable() {
            /**
             * Update the status bar using EDT
             */
            @Override
            public void run() {
                final String lastStatus = getStatusLabel();
                if (lastStatus != null && lastStatus.equalsIgnoreCase(previous)) {
                    setStatusLabel(message);
                }
            }
        });
    }

    /**
     * Change the content of the status bar
     * @param message message to display
     */
    private static void setStatusLabel(final String message) {
        _statusLabel.setText(message);

        // use status log:
        _statusLogger.info(message);
    }

    /**
     * Return the content of the status bar 
     * Note: Must be called by EDT
     * @return content of the status bar 
     */
    private static String getStatusLabel() {
        return _statusLabel.getText();
    }
}
/*___oOo___*/
