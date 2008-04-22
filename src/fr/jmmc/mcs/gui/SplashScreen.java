/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SplashScreen.java,v 1.2 2008-04-22 09:14:15 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/04/16 14:15:27  fgalland
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.*;
import java.awt.event.*;

import java.util.logging.*;

import javax.swing.*;


/**
 * Open a new Splash screen with informations
 * from XML file which should be named ApplicationData.xml in
 * the src folder
 */
public class SplashScreen extends JFrame
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(SplashScreen.class.getName());

    /** Splash screen has got the same model than about box */
    private ApplicationDataModel _applicationDataModel = null;

    /** Logo label */
    private JLabel _logoLabel = new JLabel();

    /** Panel */
    private JPanel _panel = new JPanel();

    /** Program name label */
    private JLabel _programNameLabel = new JLabel();

    /** Program version label */
    private JLabel _programVersionLabel = new JLabel();

    /**
     * Create the window fullfilled with all the information included in the XML file.
     *
     * The file should be located in "src", named "AboutBoxData.xml", following schema "src/AboutBoxSchema.xsd"
     */
    public SplashScreen()
    {
        // Instantiate model
        try
        {
            // Calls the model
            _applicationDataModel = new ApplicationDataModel();
            _logger.fine("Application data model constructed");
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE,
                "Cannot instantiate ApplicationDataModel object", ex);
        }

        // Draw window
        setAllTheProperties();
    }

    /**
     * Calls all "set properties" methods
     */
    private void setAllTheProperties()
    {
        setLogoLabelProperties();
        setProgramNameLabelProperties();
        setProgramVersionLabelProperties();
        setPanelProperties();
        setFrameProperties();

        _logger.fine("All of the jframe properties have been initialized");
    }

    /** Sets panel properties */
    private void setPanelProperties()
    {
        try
        {
            _panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            _panel.setLayout(new BorderLayout());
            _panel.add(_logoLabel, BorderLayout.PAGE_START);
            _panel.add(_programNameLabel, BorderLayout.CENTER);
            _panel.add(_programVersionLabel, BorderLayout.PAGE_END);

            _logger.fine("All of the panel properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set panel properties", ex);
        }
    }

    /** Sets logo properties */
    private void setLogoLabelProperties()
    {
        try
        {
            _logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            ImageIcon logo = new ImageIcon(getClass()
                                               .getResource(_applicationDataModel.getLogoURL()));
            _logoLabel.setIcon(logo);

            _logger.fine(
                "All of the logo label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set logo label properties", ex);
        }
    }

    /** Sets program name label properties */
    private void setProgramNameLabelProperties()
    {
        try
        {
            _programNameLabel.setFont(new Font(null, 1, 30));
            _programNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            String name = _applicationDataModel.getProgramName();
            _programNameLabel.setText(name);

            _logger.fine(
                "All of the program name label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set program name label properties", ex);
        }
    }

    /** Sets program version label properties */
    private void setProgramVersionLabelProperties()
    {
        try
        {
            _programVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Pattern : "v{version} - {copyright}"
            String version = _applicationDataModel.getProgramVersion();
            version += (" - " + _applicationDataModel.getCopyrightValue());
            _programVersionLabel.setText("v" + version);

            _logger.fine(
                "All of the program version label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set program version label properties", ex);
        }
    }

    /** Sets frame properties */
    private void setFrameProperties()
    {
        try
        {
            String programName = _applicationDataModel.getProgramName();

            getContentPane().add(_panel, BorderLayout.CENTER);

            setResizable(false);
            setUndecorated(true);
            setTitle(programName);
            pack();
            WindowCenterer.centerOnMainScreen(this);
            setVisible(true);
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Cannot set frame properties", ex);
        }
    }
}
/*___oOo___*/
