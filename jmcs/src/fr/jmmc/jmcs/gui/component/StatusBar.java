/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.jmcs.util.ImageUtils;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import org.apache.commons.lang.SystemUtils;

import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A status bar that can be shared all along an application.
 * 
 * @author Sylvain LAFRASSE, Samuel PRETTE, Guillaume MELLA, Laurent BOURGES.
 */
public class StatusBar extends JPanel {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(StatusBar.class.getName());
    /** Status label */
    private static final JLabel _statusLabel = new JLabel();
    /** Store all status messages */
    private static final List<String> _history = new ArrayList<String>(100);

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
        String logoURL = App.getSharedApplicationDataModel().getCompanyLogoResourcePath();
        final ImageIcon imageIcon = new ImageIcon(getClass().getResource(logoURL));
        JLabel logo = new JLabel();
        ImageIcon scaledImageIcon = ImageUtils.getScaledImageIcon(imageIcon, 17, 0);
        logo.setIcon(scaledImageIcon);
        logo.setVisible(true);

        // Create text logo
        JLabel textStatusBar = new JLabel();
        textStatusBar.setText("Provided by ");
        textStatusBar.setFont(new Font("Comic Sans MS", 2, 10));
        textStatusBar.setVisible(true);

        JButton historyButton = new JButton("Status :");
        historyButton.setToolTipText("Click to view status history");
        historyButton.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showStatusHistoryWindow();
            }
        });

        // StatusBar elements placement
        Box hBox = Box.createHorizontalBox();
        hBox.add(historyButton);
        //hBox.add(new JLabel("Status : "));
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
        _history.add(message);
    }

    /**
     * Return the content of the status bar 
     * Note: Must be called by EDT
     * @return content of the status bar 
     */
    private static String getStatusLabel() {
        return _statusLabel.getText();
    }

    private void showStatusHistoryWindow() {
        // TODO :
        // - auto-refresh;
        // - add event time of each entry;
        // - put in LogbackGui ???
        JFrame frame = new JFrame("Status History");

        JList _columnList = new JList(_history.toArray());
        _columnList.setCellRenderer(new AlternateRawColorCellRenderer());

        // Add scrolling capacity to the list
        JScrollPane scrollingList = new JScrollPane(_columnList);
        scrollingList.setPreferredSize(new Dimension(400, 250));
        scrollingList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollingList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollingList.getVerticalScrollBar().setFocusable(false);
        scrollingList.setBorder(BorderFactory.createLoweredBevelBorder());
        frame.add(scrollingList);

        frame.pack();
        WindowUtils.centerOnMainScreen(frame);
        WindowUtils.setClosingKeyboardShortcuts(null, frame);
        frame.setVisible(true);
    }
}
/*___oOo___*/
