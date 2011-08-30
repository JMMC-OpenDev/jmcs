/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.App;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * This class opens a new splash-screen window. Informations of this window
 * have been taken from the XML file called <b>ApplicationData.xml</b>.
 * This file is saved into the application module which extends <b>App</b>
 * class. There is a default XML file which having the same name and which is
 * saved into the <b>App</b> module in order to avoid important bugs.
 *
 * To access to the XML informations, this class uses
 * <b>ApplicationDataModel</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to access to these informations.
 * 
 * @author Brice COLUCCI, Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class SplashScreen extends JFrame {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    private static final Logger _logger = Logger.getLogger(SplashScreen.class.getName());

    /* members */
    /** Splash screen has got the same model than about box */
    private final ApplicationDataModel _applicationDataModel;
    /** Logo label */
    private final JLabel _logoLabel = new JLabel();
    /** Panel */
    private final JPanel _panel = new JPanel();
    /** Program name label */
    private final JLabel _programNameLabel = new JLabel();
    /** Program version label */
    private final JLabel _programVersionLabel = new JLabel();

    /**
     * Creates a new SplashScreen object.
     */
    public SplashScreen() {
        _applicationDataModel = App.getSharedApplicationDataModel();
    }

    /**
     * Create the window fullfilled with all the information included in the Application data model.
     */
    public void display() {
        if (_applicationDataModel != null) {

            // Draw window
            setAllProperties();
            pack();
            WindowCenterer.centerOnMainScreen(this);

            // Show window :
            setVisible(true);

            // Use Timer to wait 2,5s before closing this dialog :
            final Timer timer = new Timer(2500, new ActionListener() {

                /**
                 * Handle the timer call
                 * @param ae action event
                 */
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    // Just call close to hide and dispose this frame :
                    close();
                }
            });

            // timer runs only once :
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Close this splash screen
     */
    public void close() {
        setVisible(false);
        dispose();
    }

    /**
     * Calls all "set properties" methods
     */
    private void setAllProperties() {
        setLogoLabelProperties();
        setProgramNameLabelProperties();
        setProgramVersionLabelProperties();
        setPanelProperties();
        setFrameProperties();

        _logger.fine("Every JFrame properties have been initialized");
    }

    /** Sets panel properties */
    private void setPanelProperties() {
        _panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        _panel.setLayout(new BorderLayout());
        _panel.add(_logoLabel, BorderLayout.PAGE_START);
        _panel.add(_programNameLabel, BorderLayout.CENTER);
        _panel.add(_programVersionLabel, BorderLayout.PAGE_END);

        _logger.fine("Every panel properties have been initialized");
    }

    /** Sets logo properties */
    private void setLogoLabelProperties() {
        _logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        /*
        Package pkgs[] = Package.getPackages();
        for(int i=0; i < pkgs.length; i++)
        {
        System.out.println(
        pkgs[i].getName() + " : '" +
        pkgs[i].getImplementationTitle() + "' by " +
        pkgs[i].getImplementationVendor() + " (version " +
        pkgs[i].getImplementationVersion() + ").");
        }
        
        // @TODO : get this from AppData.xml or pkgName/resources/AppIcon.png convention
        URL url = getClass().getResource("/fr/jmmc/scalib/resources/AppIcon.png");
        if (url == null)
        {
        url = getClass().getResource(_applicationDataModel.getLogoURL());
        }
        System.out.println("url = " + url);
        
        ImageIcon icon = new ImageIcon(url);
        Dimension fixedDimension = new Dimension(64, 64);
        _logoLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(fixedDimension.height, fixedDimension.width, Image.SCALE_SMOOTH)));
        _logoLabel.setMinimumSize(fixedDimension);
        _logoLabel.setMaximumSize(fixedDimension);
         */
        _logoLabel.setIcon(
                new ImageIcon(getClass().getResource(_applicationDataModel.getLogoURL())));
        _logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));


        _logger.fine("Every logo label properties have been initialized");
    }

    /** Sets program name label properties */
    private void setProgramNameLabelProperties() {
        _programNameLabel.setFont(new Font(null, 1, 28));
        _programNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        _programNameLabel.setText(_applicationDataModel.getProgramName());

        _logger.fine(
                "Every program name label properties have been initialized");
    }

    /** Sets program version label properties */
    private void setProgramVersionLabelProperties() {
        _programVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Pattern : "v{version} - {copyright}"
        _programVersionLabel.setText("Version "
                + _applicationDataModel.getProgramVersion()
                + " - " + _applicationDataModel.getCopyrightValue());
        _programVersionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        _logger.fine(
                "Every program version label properties have been initialized");
    }

    /** Sets frame properties */
    private void setFrameProperties() {
        getContentPane().add(_panel, BorderLayout.CENTER);

        setTitle(_applicationDataModel.getProgramName());
        setResizable(false);
        setUndecorated(true);
        setAlwaysOnTop(true);
    }
}
/*___oOo___*/
