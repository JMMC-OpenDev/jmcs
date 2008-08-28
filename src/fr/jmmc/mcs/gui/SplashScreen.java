/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SplashScreen.java,v 1.8 2008-08-28 15:41:34 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.6  2008/05/20 08:49:24  bcolucci
 * Improved thread delay definition.
 *
 * Revision 1.5  2008/05/16 12:44:54  bcolucci
 * Threaded it.
 * Removed unecessary try/cath.
 *
 * Revision 1.4  2008/04/24 15:55:57  mella
 * Added applicationDataModel to constructor.
 *
 * Revision 1.3  2008/04/22 09:17:36  bcolucci
 * Corrected user name to bcolucci in CVS $Log (was fgalland).
 *
 * Revision 1.2  2008/04/22 09:14:15  bcolucci
 * Removed unused setRelativePosition().
 *
 * Revision 1.1  2008/04/16 14:15:27  bcolucci
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * This class opens a new splashscreen window. Informations of this window
 * have been taken from the XML file called <b>ApplicationData.xml</b>.
 * This file is saved into the application module which extends <b>App</b>
 * class. There is a default XML file which having the same name and which is
 * saved into the <b>App</b> module in order to avoid important bugs.
 *
 * To acces to the XML informations, this class uses
 * <b>ApplicationDataModel</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to acces to these informations.
 */
public class SplashScreen extends JFrame implements Runnable
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
     * Creates a new SplashScreen object.
     */
    public SplashScreen()
    {
        _applicationDataModel = App.getSharedApplicationDataModel();

        if (_applicationDataModel != null)
        {
            // Display the splashscreen
            run();
        }
    }

    /**
     * Create the window fullfilled with all the information included in the XML
     * file.
     *
     * The file should be located in "src", named "AboutBoxData.xml", following
     * schema "src/AboutBoxSchema.xsd"
     */
    public void run()
    {
        // Draw window
        setAllTheProperties();

        // Show window
        setVisible(true);

        // Minimum waiting
        int delay = 2500;

        try
        {
            Thread.sleep(delay);
        }
        catch (Exception ex)
        {
            _logger.severe("Could not wait '" + delay + "' ms.");
            ex.printStackTrace();
        }
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

        _logger.fine("Every JFrame properties have been initialized");
    }

    /** Sets panel properties */
    private void setPanelProperties()
    {
        _panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        _panel.setLayout(new BorderLayout());
        _panel.add(_logoLabel, BorderLayout.PAGE_START);
        _panel.add(_programNameLabel, BorderLayout.CENTER);
        _panel.add(_programVersionLabel, BorderLayout.PAGE_END);

        _logger.fine("Every panel properties have been initialized");
    }

    /** Sets logo properties */
    private void setLogoLabelProperties()
    {
        _logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon logo = new ImageIcon(getClass()
                                           .getResource(_applicationDataModel.getLogoURL()));
        _logoLabel.setIcon(logo);

        _logger.fine("Every logo label properties have been initialized");
    }

    /** Sets program name label properties */
    private void setProgramNameLabelProperties()
    {
        _programNameLabel.setFont(new Font(null, 1, 30));
        _programNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String name = _applicationDataModel.getProgramName();
        _programNameLabel.setText(name);

        _logger.fine(
            "Every program name label properties have been initialized");
    }

    /** Sets program version label properties */
    private void setProgramVersionLabelProperties()
    {
        _programVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Pattern : "v{version} - {copyright}"
        String version = _applicationDataModel.getProgramVersion();
        version += (" - " + _applicationDataModel.getCopyrightValue());
        _programVersionLabel.setText("Version " + version);

        _logger.fine(
            "Every program version label properties have been initialized");
    }

    /** Sets frame properties */
    private void setFrameProperties()
    {
        String programName = _applicationDataModel.getProgramName();
        getContentPane().add(_panel, BorderLayout.CENTER);

        setResizable(false);
        setUndecorated(true);
        setTitle(programName);
        setAlwaysOnTop(true);
        pack();
        WindowCenterer.centerOnMainScreen(this);
    }
}
/*___oOo___*/
