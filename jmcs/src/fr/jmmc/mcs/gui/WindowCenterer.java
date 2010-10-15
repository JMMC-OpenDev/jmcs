/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: WindowCenterer.java,v 1.9 2008-06-20 08:41:45 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2008/06/17 07:50:52  bcolucci
 * Take a Window object instead of a JFrame object in order to use it on JDialog.
 *
 * Revision 1.7  2008/06/13 08:25:00  bcolucci
 * Use point returned by getCenteringPoint in setLocation instead of x and y.
 *
 * Revision 1.6  2008/06/13 08:13:43  bcolucci
 * Fix getDisplayMode null pointer exception.
 *
 * Revision 1.5  2008/06/13 06:14:38  mella
 * Fix again bug (with comments in code ;)
 *
 * Revision 1.4  2008/06/10 09:12:02  bcolucci
 * Create a function which returns a centered point on the screen in order
 * to center the helpview window.
 *
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

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;

import java.util.logging.Logger;


/**
 * Facility static class to properly center a window
 * on the main screen (handle multiple screen setups).
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

    /** Get screen properties */
    public static void getScreenProperties() throws NullPointerException
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
    public static void centerOnMainScreen(Window frameToCenter)
        throws NullPointerException
    {
        // next try catch is mandatory to catach null pointer excpetion that
        // can occure on some virtual machine emulation (at least virtualBox)
        try
        {
            getScreenProperties();

            // Dimension of the JFrame
            Dimension frameSize = frameToCenter.getSize();

            // Get centering point
            Point point = getCenteringPoint(frameSize);

            frameToCenter.setLocation(point);
            _logger.fine("The window has been centered");
        }
        catch (Exception ex)
        {
            _logger.warning("Could not center window");
        }
    }

    /**
     * Returns the centered point in order
     * to center a frame on the screen
     * @return centered point
     */
    public static Point getCenteringPoint(Dimension frameDimension)
        throws NullPointerException
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
