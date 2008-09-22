/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Main.java,v 1.2 2008-09-22 16:53:50 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/07/01 08:58:13  lafrasse
 * Added jmcs test application from bcolucci.
 *
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
 * @version $Revision: 1.2 $
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

    /** Actions class */
    Actions _actions;

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

        _actions                  = new Actions();

        // .. buttons
        _aboutBoxButton           = new JButton(aboutBoxAction());
        _feedbackReportButton     = new JButton(feedbackReportAction());
        _helpViewButton           = new JButton(openHelpFrame());

        // Set borderlayout
        getFramePanel().setLayout(new BorderLayout());

        // Add buttons to panel
        getFramePanel().add(_aboutBoxButton, BorderLayout.NORTH);
        getFramePanel().add(_feedbackReportButton, BorderLayout.CENTER);
    }

    /** Execute application body */
    @Override
    protected void execute()
    {
        _logger.info("Execute application body");

        // Set others preferences
        try
        {
            Preferences.getInstance().setPreference("MAIN", "maim");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Failed setting preference.", ex);
        }

        // Show the frame
        getFrame().setVisible(true);
    }

    /** Execute operations before closing application */
    @Override
    protected boolean finnish()
    {
        _logger.warning("Execute operations before closing application");

        // Quit application
        return true;
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
