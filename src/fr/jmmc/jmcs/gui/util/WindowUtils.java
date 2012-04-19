/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import fr.jmmc.jmcs.gui.MainMenuBar;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facility static class to properly center a window
 * on the main screen (handle multiple screen setups).
 * 
 * @author Brice COLUCCI, Guillaume MELLA, Laurent BOURGES.
 */
public class WindowUtils {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(WindowUtils.class.getName());
    /** Screen width */
    private static int _screenWidth = 0;
    /** Screen height */
    private static int _screenHeight = 0;

    /**
     * Get screen properties
     * @throws NullPointerException on some platform (virtual box)
     */
    public static void getScreenProperties() throws NullPointerException {
        // Get main screen size
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        DisplayMode dm = gs.getDisplayMode();
        _screenWidth = dm.getWidth();
        _screenHeight = dm.getHeight();
    }

    /**
     * Center the given JFrame on the main screen realestate.
     *
     * If the JFrame is bigger than the main screen, it will be moved to the upper-left main screen corner.
     *
     * @param frameToCenter the JFrame we want to center
     */
    public static void centerOnMainScreen(final Window frameToCenter) {
        // next try catch is mandatory to catach null pointer excpetion that
        // can occure on some virtual machine emulation (at least virtualBox)
        try {
            getScreenProperties();

            // Dimension of the JFrame
            Dimension frameSize = frameToCenter.getSize();

            // Get centering point
            Point point = getCenteringPoint(frameSize);

            frameToCenter.setLocation(point);

            _logger.debug("The window has been centered");

        } catch (NullPointerException npe) {
            _logger.warn("Could not center window");
        }
    }

    /**
     * Returns the centered point in order to center a frame on the screen
     * @param frameDimension frame size
     * @return centered point
     * @throws NullPointerException on some platform (virtual box)
     */
    public static Point getCenteringPoint(final Dimension frameDimension)
            throws NullPointerException {
        getScreenProperties();

        int x = (_screenWidth - frameDimension.width) / 2;
        x = Math.max(x, 0);

        int y = (_screenHeight - frameDimension.height) / 2;
        y = Math.max(y, 0);

        return new Point(x, y);
    }

    /**
     * Installs standard window-closing keyboard shortcuts (i.e ESC and ctrl-W).
     *
     * @param dialog the JDialog to close on keystroke.
     */
    public static void setClosingKeyboardShortcuts(final JDialog dialog) {
        setClosingKeyboardShortcuts(dialog.getRootPane(), dialog);
    }

    /**
     * Installs standard window-closing keyboard shortcuts (i.e ESC and ctrl-W).
     *
     * @param frame the JFrame to close on keystroke.
     */
    public static void setClosingKeyboardShortcuts(final JFrame frame) {
        setClosingKeyboardShortcuts(frame.getRootPane(), frame);
    }

    /**
     * Installs standard window-closing keyboard shortcuts (i.e ESC and ctrl-W).
     *
     * @param rootPane the pane to listen keystroke.
     * @param window the JFrame or JDialog to close on keystroke.
     */
    private static void setClosingKeyboardShortcuts(JRootPane rootPane, final Window window) {

        if ((rootPane == null) || (window == null)) {
            throw new IllegalArgumentException();
        }

        // Trap Escape key
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        // Trap command-W key
        KeyStroke metaWStroke = KeyStroke.getKeyStroke(MainMenuBar.getSystemCommandKey() + "W");

        // Close window on either stroke
        final ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                _logger.trace("Handling close window shortcut.");

                // trigger standard closing action (@see JFrame.setDefaultCloseOperation)
                // i.e. hide or dispose the window:
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        };

        rootPane.registerKeyboardAction(actionListener, escapeStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(actionListener, metaWStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Private constructor
     */
    private WindowUtils() {
        super();
    }
}
/*___oOo___*/
