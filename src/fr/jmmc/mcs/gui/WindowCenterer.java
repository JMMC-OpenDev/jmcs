/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: WindowCenterer.java,v 1.1 2008-04-16 14:15:27 fgalland Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

    /**
     * Center the given JFrame on the main screen realestate.
     *
     * If the JFrame is bigger than the main screen, it will be moved to the upper-left main screen corner.
     *
     * @param frameToCenter the JFrame we want to center
     */
    public static void centerOnMainScreen(JFrame frameToCenter)
    {
        try
        {
            // Get main screen size
            GraphicsEnvironment ge           = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice      gs           = ge.getDefaultScreenDevice();
            DisplayMode         dm           = gs.getDisplayMode();
            int                 screenWidth  = dm.getWidth();
            int                 screenHeight = dm.getHeight();

            // Dimension of the JFrame
            Dimension frameSize = frameToCenter.getSize();

            int       xPosition = (screenWidth - frameSize.width) / 2;
            xPosition           = Math.max(xPosition, 0);

            int yPosition       = (screenHeight - frameSize.height) / 2;
            yPosition           = Math.max(yPosition, 0);

            frameToCenter.setLocation(xPosition, yPosition);

            _logger.fine("The window has been centered");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot center the jframe", ex);
        }
    }
}
/*___oOo___*/
