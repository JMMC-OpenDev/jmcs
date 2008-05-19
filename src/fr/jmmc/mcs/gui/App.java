/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.4 2008-05-19 14:34:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2008/05/16 13:13:31  bcolucci
 * Added automatic splashscreen display.
 * Added preliminary command-line option parsing.
 * Removed unecessary try/catch, and added argument checks.
 *
 * Revision 1.2  2008/04/24 16:00:02  mella
 * Added inner management of application data model.
 * Added static methods for SplashScreen, FeedbackReport and AboutBox.
 *
 * Revision 1.1  2008/04/22 12:31:41  bcolucci
 * App object creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.event.ActionEvent;

import java.io.*;

import java.net.URL;

import java.util.Vector;
import java.util.logging.*;

import javax.swing.AbstractAction;
import javax.swing.Action;


/** Represents the main application class */
public abstract class App
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc");

    /** Singleton reference */
    private static App _sharedInstance;

    /** Stream handler which permit us to keep logs report in strings */
    private static StreamHandler _streamHandler = null;

    /** Console handler which permit us to show logs report on the console */
    private static ConsoleHandler _consoleHandler = new ConsoleHandler();

    /** ByteArrayOutputStream which keeps logs report */
    private static ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream();

    /** Shared application data model */
    private static ApplicationDataModel _applicationDataModel;

    /** Splash screen thread */
    private static Thread _splashScreenThread = null;

    /** Splash screen */
    private static SplashScreen _splashScreen = null;

    /** Show the splash screen? */
    private boolean _showSplashScreen = true;

    /** Default XML file URL to avoid null objects if there is not ApplicationData.xml */
    private String _defaultApplicationDataURL = "fr/jmmc/mcs/gui/ApplicationData.xml";

    /** Creates a new App object */
    protected App(String[] args, boolean waitBeforeExecution)
    {
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        _streamHandler = new StreamHandler(_byteArrayOutputStream,
                simpleFormatter);

        // We add the memory handler created and the console one to the logger
        _logger.addHandler(_consoleHandler);
        _logger.addHandler(_streamHandler);
        _logger.setLevel(Level.INFO);

        _logger.fine(
            "Memory and console handler created and fixed to feedbackLogger");
        _logger.fine("App object instantiated and logger created");

        // Init _applicationDataModel
        Package p = getClass().getPackage();

        // Replace '.' by '/' of package name
        String packageName = p.getName().replace(".", "/");
        String xmlLocation = packageName + "/ApplicationData.xml";

        File   xmlFile     = new File("fr/jmmc/test/ApplicationData.xml");

        if (! xmlFile.exists())
        {
            xmlLocation = _defaultApplicationDataURL;
            _logger.warning("Taking default ApplicationData.xml");
        }

        _logger.fine("xmlLocation='" + xmlLocation + "'");

        try
        {
            // Open XML file at path
            URL xmlURL = getClass().getClassLoader().getResource(xmlLocation);

            _applicationDataModel = new ApplicationDataModel(xmlURL);
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE,
                "Cannot retrieve application data from xml", ex);
            System.exit(-1);
        }

        // Interpret arguments
        interpretArguments(args);

        if (waitBeforeExecution == false)
        {
            // Run the application
            run();
        }
    }

    /** Creates the action which launch exit method */
    public Action exitAction()
    {
        return new AbstractAction("Exit")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    exit();
                }
            };
    }

    /** Creates the action which open the about box window */
    public static Action aboutBoxAction()
    {
        return new AbstractAction("Show About Box")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        new AboutBox();
                    }
                }
            };
    }

    /** Creates the action which open the feedback report window */
    public static Action feedbackReportAction()
    {
        return new AbstractAction("Show Feedback Report")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        new FeedbackReport();
                    }
                }
            };
    }

    /**
     * Interpret command line arguments
     *
     * @param args DOCUMENT ME!
     */
    private void interpretArguments(String[] args)
    {
        // Number of arguments
        int nbArguments = args.length;

        // Check if the number is pair
        boolean validArguments = ((nbArguments % 2) == 0);

        if (validArguments)
        {
            // While we have (key, value)
            while (nbArguments >= 2)
            {
                String key   = args[nbArguments - 2];
                String value = args[nbArguments - 1];

                value        = value.toUpperCase();

                /* Options interpreted here */
                // Show splash screen?
                if (key.equals("-s") || key.equals("-splashscreen"))
                {
                    if (value.equals("TRUE") || value.equals("FALSE"))
                    {
                        _showSplashScreen = Boolean.valueOf(value);
                    }
                    else
                    {
                        showArgumentsHelp();
                    } // We don't regonize this value for this key
                }

                // Level of consol handler logging?
                else if (key.equals("-l") || key.equals("-level"))
                {
                    if (value.equals("SEVERE") || value.equals("WARNING") ||
                            value.equals("INFO") || value.equals("CONFIG") ||
                            value.equals("FINE") || value.equals("FINER") ||
                            value.equals("FINEST"))
                    {
                        _logger.setLevel(Level.parse(value));
                    }
                    else
                    {
                        showArgumentsHelp();
                    } // We don't regonize this value for this key
                }

                nbArguments -= 2;
            }
        }
        else
        {
            showArgumentsHelp();
        } // We don't have a pair number of arguments
    }

    /** Show command arguments help */
    public void showArgumentsHelp()
    {
        System.out.println(
            "------------- Arguments help --------------------------------------------");
        System.out.println(
            "| Key                  Value               Description                  |");
        System.out.println(
            "|-----------------------------------------------------------------------|");
        System.out.println(
            "| [-s | -splashscreen] [true | false]      Show splash screen or not    |");
        System.out.println(
            "| [-l | -level]        [LEVEL]             Define console logging level |");
        System.out.println(
            "-------------------------------------------------------------------------");
        System.out.println(
            "LEVEL = (SEVERE | WARNING | INFO | CONFIG | FINE | FINER | FINEST)\n");

        System.exit(0);
    }

    /** Initialize application objects */
    protected abstract void init();

    /** Execute application body */
    protected abstract void execute();

    /** Execute operations before closing application */
    protected abstract void exit();

    /** Describe the life cycle of the application */
    protected void run()
    {
        // Show splash screen if we have to
        if (_showSplashScreen)
        {
            showSplashScreen();
        }

        // Call abstract init method
        init();

        // Close the splash screen if we have to
        if (_showSplashScreen)
        {
            closeSplashScreen();
        }

        // Call abstract execute method
        execute();
    }

    /**
     * Returns logs report into a unique string
     *
     * @return logs report into a unique string
     */
    public static String getLogOutput()
    {
        // Needed in order to write all logs in the ouput stream buffer
        _streamHandler.flush();

        return _byteArrayOutputStream.toString();
    }

    /** Show splash screen */
    public static void showSplashScreen()
    {
        if (_applicationDataModel != null)
        {
            _logger.fine("Show splashscreen");

            // Instantiate splash screen
            _splashScreen           = new SplashScreen();

            // Instantiate the splash screen thread
            _splashScreenThread     = new Thread(_splashScreen);
        }
    }

    /** Close splash screen and stop the thread */
    public static void closeSplashScreen()
    {
        _logger.fine("Close splashscreen");
        _splashScreen.dispose();

        // Stop the splash screen thread
        _splashScreenThread.stop();
    }

    /** Show help view */
    public static void showHelpView()
    {
        new HelpView();
    }

    /**
     * Return ApplicationDataModel instance.
     *
     * @return ApplicationDataModel instance.
     */
    public static ApplicationDataModel getSharedApplicationDataModel()
    {
        return _applicationDataModel;
    }
}
/*___oOo___*/
