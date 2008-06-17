/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.16 2008-06-17 12:39:06 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.15  2008/06/17 11:33:04  bcolucci
 * Add the possibility to access to the arguments from the init abstract method.
 *
 * Revision 1.14  2008/06/17 11:15:36  bcolucci
 * Catch exception during all the creation of the application
 * and show the feedback report window.
 * Put the aboutbox in front of screen if it is in background when we click
 * on the button a second time.
 *
 * Revision 1.13  2008/06/17 08:01:53  bcolucci
 * Add the application main frame as attribute.
 * Add getter for the application frame and another for it's panel.
 * Import creation of the menubar in App instead of the application class
 * because App has got the application frame.
 * Set common properties on application frame (pack and setLocation).
 * Call the new constructors of aboutbox and feedbackReport in order
 * to set them modal.
 *
 * Revision 1.12  2008/06/13 08:46:35  bcolucci
 * Add the default case in argument interpretation function.
 * Modify the exit method for applet.
 * Add logGui action method.
 *
 * Revision 1.11  2008/06/12 07:42:08  bcolucci
 * Modify the way to access to the exit method (we have to check if when we call
 * the exit method from App, it calls exit from child class too).
 *
 * Revision 1.10  2008/06/10 09:24:12  bcolucci
 * Replace "File.separator" by "/" in order to load ressources.
 *
 * Revision 1.9  2008/06/10 09:19:18  bcolucci
 * *** empty log message ***
 *
 * Revision 1.8  2008/06/10 09:14:58  bcolucci
 * Modify the action which show aboutbox window in order to not show it
 * many times in the same application.
 * Implement a solution for generate menus.
 *
 * Revision 1.7  2008/05/29 10:11:29  mella
 * Accept null as App argument
 *
 * Revision 1.6  2008/05/27 06:36:27  mella
 * Fix getResource Separator
 *
 * Revision 1.5  2008/05/20 08:48:47  bcolucci
 * Added a constructor to show/hide splashscreen on startup.
 * Updated command-line argument handling to use getopt.
 * Added an action to show the Help View.
 * Changed way to retrieve the ApplicationData.xml file.
 *
 * Revision 1.4  2008/05/19 14:34:03  lafrasse
 * Added an option to delay execution for further initialisation of the inheriting
 * class.
 *
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

import gnu.getopt.*;

import java.awt.*;
import java.awt.event.ActionEvent;

import java.io.*;

import java.net.URL;

import java.util.Vector;
import java.util.logging.*;

import javax.swing.*;


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
    private static boolean _showSplashScreen = true;

    /** AboutBox */
    private static AboutBox _aboutBox = null;

    /** Main frame of the application */
    private static JFrame _applicationFrame = new JFrame();

    /** Arguments */
    private static String[] _args;

    /** Default menu components (file, edit...) */
    private Vector<JComponent> _defaultMenuComponents = null;

    /** If it's true, exit the application after the exit method */
    private boolean _exitApplicationWhenClosed = false;

    /** Creates a new App object
     *
     * @param args command-line arguments
     */
    protected App(String[] args)
    {
        // Start application imediatly, with splashscreen
        this(args, false, true);
    }

    /** Creates a new App object, with possibility to delay execution
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     */
    protected App(String[] args, boolean waitBeforeExecution)
    {
        // Start application with splashscreen
        this(args, waitBeforeExecution, true);
    }

    /** Constructor whith possibility to specify if the splashscreen should be shown
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     * @param showSplashScreen if false, do not display splashscreen
     */
    protected App(String[] args, boolean waitBeforeExecution,
        boolean showSplashScreen)
    {
        /* Start application and define that the it will
           not stop itself when exit method will be called */
        this(args, waitBeforeExecution, showSplashScreen, false);
    }

    /**
     * Constructor whith possibility to specify if the application should be
     * stopped when the exit method is called
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     * @param showSplashScreen if false, do not display splashscreen
     * @param exitWhenClosed if true, the application will close when exit method is called
     */
    protected App(String[] args, boolean waitBeforeExecution,
        boolean showSplashScreen, boolean exitWhenClosed)
    {
        try
        {
            // Attributes affectations
            _args                          = args;
            _showSplashScreen              = showSplashScreen;
            _exitApplicationWhenClosed     = exitWhenClosed;

            // Logger's stream handler creation
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            _streamHandler = new StreamHandler(_byteArrayOutputStream,
                    simpleFormatter);

            // We add the memory handler created and the console one to the logger
            _logger.addHandler(_consoleHandler);
            _logger.addHandler(_streamHandler);
            _logger.setLevel(Level.INFO);
            _logger.fine("Logger properties set");

            _logger.fine(
                "Memory and console handler created and fixed to feedbackLogger");
            _logger.fine("App object instantiated and logger created");

            // Set the application data attribute
            setApplicationData();
            _logger.fine("Application data set");

            // Interpret arguments
            interpretArguments(args);

            // If execution should not be delayed
            if ((waitBeforeExecution == false))
            {
                // Run the application imediatly
                run();
            }
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE,
                "Error during creation of the application", ex);

            String errorMessage = "Application error";
            JOptionPane.showMessageDialog(null, errorMessage,
                "An error was occured while the application creation",
                JOptionPane.ERROR_MESSAGE);

            // Show feedback report
            new FeedbackReport(null, false, ex);
        }
    }

    /**
     * Set the application data if Applicationdata.xml
     * exists into the module. Else, taking the
     * default ApplicationData.xml.
     */
    private void setApplicationData()
    {
        // The class which is extended from App
        Class actualClass = getClass();

        // It's package
        Package p = actualClass.getPackage();

        // Replace '.' by '/' of package name
        String packageName = p.getName().replace(".", "/");
        String xmlLocation = packageName + "/" + "ApplicationData.xml";

        /* Take the defaultData XML in order
           to take the default menus */
        takeDefaultApplicationData();

        try
        {
            // Open XML file at path
            URL xmlURL = actualClass.getClassLoader().getResource(xmlLocation);

            _applicationDataModel = new ApplicationDataModel(xmlURL);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot unmarshal " + xmlLocation, ex);
        }
    }

    /** Take the default ApplicationData.xml */
    private void takeDefaultApplicationData()
    {
        String defaultXmlLocation = "";

        try
        {
            // The App class
            Class app = Class.forName("fr.jmmc.mcs.gui.App");

            // The App package
            Package defaultPackage = app.getPackage();

            // Replace '.' by '/' of package name
            String defaultPackageName = defaultPackage.getName()
                                                      .replace(".", "/");

            // Default XML location
            defaultXmlLocation = defaultPackageName + "/" +
                "ApplicationData.xml";

            URL defaultXmlURL = app.getClassLoader()
                                   .getResource(defaultXmlLocation);

            // We reinstantiate the application data model
            _applicationDataModel = new ApplicationDataModel(defaultXmlURL);
        }
        catch (Exception ex2)
        {
            _logger.log(Level.WARNING,
                "Cannot unmarshal default " + defaultXmlLocation, ex2);
            System.exit(-1);
        }
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
                        if (_aboutBox != null)
                        {
                            if (! _aboutBox.isVisible())
                            {
                                _aboutBox.setVisible(true);
                            }
                            else
                            {
                                _aboutBox.toFront();
                            }
                        }
                        else
                        {
                            _aboutBox = new AboutBox();
                        }
                    }
                }
            };
    }

    /** Creates the feedback action which open the feedback window */
    public static Action feedbackReportAction()
    {
        return feedbackReportAction(null);
    }

    /** Creates the feedback action which open the feedback window */
    public static Action feedbackReportAction(final Exception ex)
    {
        return new AbstractAction("Show Feedback Report")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        new FeedbackReport(_applicationFrame, true, ex);
                    }
                }
            };
    }

    /** Creates the helpview action which open the helpview window */
    public static Action helpViewAction()
    {
        return new AbstractAction("Show Help View")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        new HelpView();
                    }
                }
            };
    }

    /** Creates the exit action which properly exit the application (applet) */
    public static Action exitAction()
    {
        return new AbstractAction("Quit")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    // TODO : est-ce que ca appelle la methode de la classe fille ?????
                    exit();
                }
            };
    }

    /** Creates the action which open logGui */
    public static Action logGuiAction()
    {
        return new AbstractAction("LogGui")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    imx.loggui.LogMaster.startLogGui();
                }
            };
    }

    /**
     * Interpret command line arguments
     *
     * @param args arguments
     */
    protected void interpretArguments(String[] args)
    {
        // Just leave method if no argument has been given
        if (args == null)
        {
            return;
        }

        // Array for long arguments (help & version)
        LongOpt[] longopts = new LongOpt[2];
        longopts[0]     = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 0);
        longopts[1]     = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 1);

        // Instantiate the getopt object
        Getopt getOpt = new Getopt(_applicationDataModel.getProgramName(),
                args, "hv:", longopts, true);

        int    c; // argument key
        String arg    = null; // argument value

        // While there is a argument key
        while ((c = getOpt.getopt()) != -1)
        {
            switch (c)
            {
            // Show the arguments help
            case 'h':
                showArgumentsHelp();

                break;

            // Show the arguments help
            case 0:
                showArgumentsHelp();

                break;

            // Show the name and the version of the program
            case 1:

                // Get application name and version
                String name    = _applicationDataModel.getProgramName();
                String version = _applicationDataModel.getProgramVersion();

                // Show it on the shell
                System.out.println(name + " v" + version);

                // Exit the application
                System.exit(0);

                break;

            // Set the logger level
            case 'v':
                arg = getOpt.getOptarg();

                if (arg != null)
                {
                    _logger.info("Set logger level to " + arg);

                    if (arg.equals("1"))
                    {
                        _logger.setLevel(Level.SEVERE);
                    }
                    else if (arg.equals("2"))
                    {
                        _logger.setLevel(Level.WARNING);
                    }
                    else if (arg.equals("3"))
                    {
                        _logger.setLevel(Level.INFO);
                    }
                    else if (arg.equals("4"))
                    {
                        _logger.setLevel(Level.CONFIG);
                    }
                    else if (arg.equals("5"))
                    {
                        _logger.setLevel(Level.FINEST);
                    }
                    else
                    {
                        showArgumentsHelp();
                    }
                }

                break;

            // Show the arguments help
            case '?':
                showArgumentsHelp();

                break;

            default:
                System.out.println("Unknow command");
                System.exit(-1);

                break;
            }
        }

        _logger.fine("Application arguments interpreted");
    }

    /** Show command arguments help */
    public static void showArgumentsHelp()
    {
        System.out.println(
            "------------- Arguments help --------------------------------------------");
        System.out.println(
            "| Key          Value           Description                              |");
        System.out.println(
            "|-----------------------------------------------------------------------|");
        System.out.println(
            "| [-h]                         Show the options help                    |");
        System.out.println(
            "| [-v]         [1|2|3|4|5]     Define console logging level             |");
        System.out.println(
            "| [-version]                   Show application name and version        |");
        System.out.println(
            "| [-h|-help]                   Show arguments help                      |");
        System.out.println(
            "-------------------------------------------------------------------------");
        System.out.println(
            "LEVEL : 1=SEVERE, 2=WARNING, 3=INFO, 4=CONFIG, 5=FINEST\n");

        System.exit(0);
    }

    /** Initialize application objects */
    protected abstract void init(String[] args);

    /** Execute application body */
    protected abstract void execute();

    /** Execute operations before closing application */
    protected static void exit()
    {
        boolean isNotAnApplet = true;

        if (isNotAnApplet == true)
        {
            System.exit(-1);
        }
    }

    /** Describe the life cycle of the application */
    protected void run()
    {
        // Show splash screen if we have to
        if (_showSplashScreen)
        {
            showSplashScreen();
        }

        // Set JMenuBar
        MainMenuBar mainMenuBar = new MainMenuBar(_applicationFrame);
        _applicationFrame.setJMenuBar(mainMenuBar);

        // Call abstract init method with arguments
        init(_args);

        // Set application frame common properties
        _applicationFrame.pack();
        _applicationFrame.setLocationRelativeTo(null);

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
    private static void showSplashScreen()
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
    private static void closeSplashScreen()
    {
        _logger.fine("Close splashscreen");
        _splashScreen.dispose();

        // Stop the splash screen thread
        _splashScreenThread.stop();
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

    /**
     * Return the application frame
     *
     * @return application frame
     */
    protected static Frame getFrame()
    {
        return _applicationFrame;
    }

    /**
     * Return the application frame panel
     *
     * @return application frame panel
     */
    protected static Container getFramePanel()
    {
        return _applicationFrame.getContentPane();
    }
}
/*___oOo___*/
