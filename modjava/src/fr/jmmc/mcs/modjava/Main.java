/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Main.java,v 1.1 2008-07-01 08:58:13 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.mcs.gui.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;

import java.net.URL;

import java.util.logging.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * Class for tests
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Main extends App
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(Main.class.getName());

    /** Button to launch about box window */
    private JButton _aboutBoxButton = null;

    /** Button to launch feedback report window */
    private JButton _feedbackReportButton = null;

    /** Button to launch helpview window */
    private JButton _helpViewButton = null;

    /** Button to launch logGui */
    private JButton _logGuiButton = null;

    /** Button to save preferences */
    private JButton _savePreferencesButton = null;

    /** Button to load preferences */
    private JButton _loadPreferencesButton = null;

    /** Constructor */
    public Main(String[] args)
    {
        super(args, false, true, true);
    }

    /** Initialize application objects */
    @Override
    protected void init(String[] args)
    {
        _logger.warning("Initialize application objects");
        _logger.info(args.length + " arguments have been taken");

        // .. buttons
        _aboutBoxButton            = new JButton(aboutBoxAction());
        _feedbackReportButton      = new JButton(feedbackReportAction());
        _helpViewButton            = new JButton(openHelpFrame());
        _logGuiButton              = new JButton(logGuiAction());
        _savePreferencesButton     = new JButton(savePreferencesAction());
        _loadPreferencesButton     = new JButton(loadPreferencesAction());

        // Set borderlayout
        getFramePanel().setLayout(new BorderLayout());

        // Add buttons to panel
        getFramePanel().add(_aboutBoxButton, BorderLayout.NORTH);
        getFramePanel().add(_feedbackReportButton, BorderLayout.CENTER);

        //getFramePanel().add(_helpViewButton, BorderLayout.WEST);
        getFramePanel().add(_loadPreferencesButton, BorderLayout.WEST);

        //getFramePanel().add(_logGuiButton, BorderLayout.SOUTH);
        getFramePanel().add(_savePreferencesButton, BorderLayout.SOUTH);
    }

    /** Execute application body */
    @Override
    protected void execute()
    {
        _logger.warning("Execute application body");

        // Set others preferences
        try
        {
            setPreference("MAIN", "maim");
        }
        catch (Exception ex)
        {
        }

        // Show the frame
        getFrame().setVisible(true);

        //System.out.println("Program name : " + getPreference("application.name"));
    }

    /** Execute operations before closing application */
    @Override
    protected static void exit()
    {
        _logger.warning("Execute operations before closing application");
        System.exit(0);
    }

    /** Open help view action */
    private Action openHelpFrame()
    {
        return new AbstractAction("Open Help Frame")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    // Instantiate help JFrame
                    JFrame helpFrame = new JFrame("- Help frame -");

                    // Add buttons to panel
                    helpFrame.getContentPane()
                             .add(new JButton(helpViewAction()),
                        BorderLayout.NORTH);
                    helpFrame.getContentPane()
                             .add(new JButton("CENTER"), BorderLayout.CENTER);
                    helpFrame.getContentPane()
                             .add(new JButton("WEST"), BorderLayout.WEST);
                    helpFrame.getContentPane()
                             .add(new JButton("SOUTH"), BorderLayout.SOUTH);

                    // Set the frame properties
                    helpFrame.pack();
                    helpFrame.setLocationRelativeTo(null);
                    helpFrame.setVisible(true);
                }
            };
    }

    /**
     * Main
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        new Main(args);
    }
}
/*___oOo___*/
