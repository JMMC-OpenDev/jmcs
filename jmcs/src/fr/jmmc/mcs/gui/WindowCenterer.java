/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: WindowCenterer.java,v 1.4 2008-06-10 09:12:02 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2008/05/29 10:00:10  mella
 * Fix bug when null is returned for the DisplayMode (was in VirtualBox)
 *
 * Revision 1.2  2008/05/16 12:37:05  bcolucci
 * Removed unnecessary try/catch.
 *
 * Revision 1.1  2008/04/16 14:15:27  bcolucci
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.*;

import java.util.logging.*;

import javax.swing.*;


/**
 * Facility static class to properly center a window on the main screen (handle multiple screen setups).
 */
public class WindowCenterer
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(WindowCenterer.class.getName());

    /** X position */
    public static int _XPOSITION = 0;

    /** Y position */
    public static int _YPOSITION = 0;

    /** Screen width */
    public static int _screenWidth = 0;

    /** Screen height */
    public static int _screenHeight = 0;

    /** Constructor */
    public static void getScreenProperties()
    {
        // Get main screen size
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gs = ge.getDefaultScreenDevice();
        DisplayMode         dm = gs.getDisplayMode();
        _screenWidth           = dm.getWidth();
        _screenHeight          = dm.getHeight();
    }

    /**
     * Center the given JFrame on the main screen realestate.
     *
     * If the JFrame is bigger than the main screen, it will be moved to the upper-left main screen corner.
     *
     * @param frameToCenter the JFrame we want to center
     */
    public static void centerOnMainScreen(JFrame frameToCenter)
    {
        getScreenProperties();

        // Dimension of the JFrame
        Dimension frameSize  = frameToCenter.getSize();

        int       _XPOSITION = (_screenWidth - frameSize.width) / 2;
        _XPOSITION           = Math.max(_XPOSITION, 0);

        int _YPOSITION       = (_screenHeight - frameSize.height) / 2;
        _YPOSITION           = Math.max(_YPOSITION, 0);

        frameToCenter.setLocation(_XPOSITION, _YPOSITION);
        _logger.fine("The window has been centered");
    }

    /**
     * Returns the centered point in order
     * to center a frame on the screen
     * @return centered point
     */
    public static Point getCenteredPoint(Dimension frameDimension)
    {
        getScreenProperties();

        int _XPOSITION = (_screenWidth - frameDimension.width) / 2;
        _XPOSITION = Math.max(_XPOSITION, 0);

        int _YPOSITION = (_screenHeight - frameDimension.height) / 2;
        _YPOSITION = Math.max(_YPOSITION, 0);

        return new Point(_XPOSITION, _YPOSITION);
    }
}
/*___oOo___*/
