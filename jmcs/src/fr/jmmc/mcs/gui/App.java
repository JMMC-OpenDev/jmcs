/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.44 2008-10-17 10:41:54 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.43  2008/10/16 14:19:06  mella
 * Use new help view handling
 *
 * Revision 1.42  2008/10/16 13:59:03  lafrasse
 * Renamed actions.
 *
 * Revision 1.41  2008/10/16 12:03:29  lafrasse
 * Added a default automatically generated acknowledgement.
 *
 * Revision 1.40  2008/10/16 11:42:19  mella
 * Fix compilation pb with getProgramName
 *
 * Revision 1.39  2008/10/16 11:39:45  mella
 * Remove getProgramName method
 *
 * Revision 1.38  2008/10/16 09:19:42  lafrasse
 * Refined Acknoledgement window.
 *
 * Revision 1.37  2008/10/16 09:17:42  mella
 * add new supported option
 *
 * Revision 1.36  2008/10/16 08:53:53  mella
 * remove system.out.println
 *
 * Revision 1.35  2008/10/16 08:22:14  mella
 * Add showReleaseAction
 * Add acknowledgmentAction
 * Add quitAction getter
 * Remove logging outputHandler and set mainLogger level to FINE
 *
 * Revision 1.34  2008/10/07 13:43:03  mella
 * Fix Fine log level
 *
 * Revision 1.33  2008/09/22 16:49:51  lafrasse
 * Moved MainMenuBar initialization after init() method call (instead of before).
 *
 * Revision 1.32  2008/09/09 12:02:59  lafrasse
 * Separated App logger from shared logger.
 *
 * Revision 1.31  2008/09/06 07:56:05  lafrasse
 * Moved QuitAction name and accelerator definition to new RegisteredAction
 * constructor.
 *
 * Revision 1.30  2008/09/05 21:59:21  lafrasse
 * Added name and accelerator to QuitAction.
 *
 * Revision 1.29  2008/09/05 16:19:27  lafrasse
 * Enhanced code documentation.
 * Corrected API typo.
 * Removed unused showPreferenceAction.
 *
 * Revision 1.28  2008/09/05 16:08:14  lafrasse
 * Implemented secured application exit.
 *
 * Revision 1.27  2008/09/04 16:03:35  lafrasse
 * Code, documentation and log enhancement.
 *
 * Revision 1.26  2008/09/02 12:31:48  lafrasse
 * Code, documentation and logging cleanup.
 *
 * Revision 1.25  2008/09/01 11:45:18  lafrasse
 * Added a hook for preference window display.
 *
 * Revision 1.24  2008/09/01 11:10:47  lafrasse
 * Moved to new fr.jmmc.jmcs.util.Preferences APIs.
 * Removed unecessary proxy methods to fr.jmmc.jmcs.util.Preferences.
 * Improved logging.
 *
 * Revision 1.23  2008/07/04 14:32:12  lafrasse
 * Added missing Vector class import.
 * iAdded premiminary support for application exit when instancied by another app.
 *
 * Revision 1.22  2008/06/27 11:30:03  bcolucci
 * Set save/load methods to public instead of private in order
 * to not oblige programmer to use the action if he doesn't need.
 * Add a method to manage the versions differences between the
 * application and the properties file.
 * Add a method to convert a string representing a version like
 * "1.00.12" to a double like 1.0012 to permit to compare it.
 * Add methods getters of preferences in order to not oblige
 * programmer to call preferences getter each time he wants
 * to call a preferences function.
 *
 * Revision 1.21  2008/06/25 12:06:35  bcolucci
 * Add save/load properties functions and actions.
 * Improve the way to load properties.
 * Fix some logs.
 *
 * Revision 1.20  2008/06/25 08:22:52  bcolucci
 * Implement Observer for preferences.
 * Add a preferences attribute.
 * Get preferences in the run method according
 * to the Preferences class in the application
 * module.
 *
 * Revision 1.19  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.18  2008/06/19 13:09:03  bcolucci
 * Hide the splashscreen if it's opened if there is an error during the application creation.
 *
 * Revision 1.17  2008/06/17 13:04:18  bcolucci
 * Change a log level and the creation error window message.
 *
 * Revision 1.16  2008/06/17 12:39:06  bcolucci
 * Improve the way to catch the exception while application creation and
 * send the exception to the feedback report.
 *
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

import fr.jmmc.mcs.util.*;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import java.io.ByteArrayOutputStream;

import java.net.URL;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.*;


/**
 * This class represents an application. In order to use
 * fonctionnalities that are implemented here, you have to
 * extend your application from this class.
 *
 * This class is a singleton with abstract methods. If you extend
 * a class from this one, you can have a splashscreen, an about
 * window, the menubar generation, the possibility to have a help
 * window using <b>jmcsGenerateHelpSetFromHtml</b> from the bash
 * script called <b>jmcsHTML2HelpSet.sh</b> located into the src
 * folder of jmcs, the feedback report etc...
 *
 * To acces to the XML informations, this class uses
 * <b>ApplicationDataModel</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to acces to these informations.
 */
public abstract class App
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _mainLogger = Logger.getLogger("fr.jmmc");

    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.gui.App");

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

    /** If it's true, exit the application after the exit method */
    private static boolean _exitApplicationWhenClosed = true;

    /** Application preferences */
    private static Preferences _preferences;

    /** Quit handling action */
    private static QuitAction _quitAction = null;

    /** Acknowledgement handling action */
    private static AcknowledgementAction _acknowledgementAction = null;

    /** Show help handling action */
    private static ShowHelpAction _showHelpAction = null;

    /** Show release handling action */
    private static ShowReleaseAction _showReleaseAction = null;

    /** Show FAQ handling action */
    private static ShowFaqAction _showFaqAction = null;

    /**
     * Creates a new App object
     *
     * @param args command-line arguments
     */
    protected App(String[] args)
    {
        // Start application imediatly, with splashscreen
        this(args, false);
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
        this(args, waitBeforeExecution, showSplashScreen, true);
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
        String classPath = getClass().getName();
        _quitAction = new QuitAction(classPath, "_quitAction");

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

            // We add the memory handler create to the logger
            _mainLogger.addHandler(_streamHandler);
            _mainLogger.setLevel(Level.FINE);
            _logger.finer("Main Logger properties set");

            _logger.fine("Memory handler created and fixed to feedbackLogger.");
            _logger.fine("App object instantiated and logger created.");

            // Set the application data attribute
            loadApplicationData();
            _logger.fine("Application data loaded.");

            // Interpret arguments
            interpretArguments(args);

            // Set shared instance
            _sharedInstance            = this;

            // Build Acknowledgment, ShowRelease and ShowHelp Actions
            // (the creation must be done after applicationModel instanciation)
            _acknowledgementAction     = new AcknowledgementAction("fr.jmmc.mcs.gui.App",
                    "_acknowledgementAction");
            _showReleaseAction         = new ShowReleaseAction("fr.jmmc.mcs.gui.App",
                    "_showReleaseAction");
            _showFaqAction             = new ShowFaqAction("fr.jmmc.mcs.gui.App",
                    "_showFaqAction");
            _showHelpAction            = new ShowHelpAction("fr.jmmc.mcs.gui.App",
                    "_showHelpAction");

            // If execution should not be delayed
            if (waitBeforeExecution == false)
            {
                // Run the application imediatly
                run();
            }
        }
        catch (Exception ex)
        {
            _logger.severe("Error while initalizing the application");

            // In order to see the error window
            if (_splashScreen != null)
            {
                if (_splashScreen.isVisible())
                {
                    _splashScreen.setVisible(false);
                }
            }

            JOptionPane.showMessageDialog(null,
                "An error occured while initalizing the application", "Error",
                JOptionPane.ERROR_MESSAGE);

            // Show feedback report
            new FeedbackReport(_applicationFrame, true, ex, true);
        }
    }

    /**
     * Load application data if Applicationdata.xml exists into the module.
     * Otherwise, uses the default ApplicationData.xml.
     */
    private void loadApplicationData()
    {
        URL fileURL = getURLFromResourceFilename("ApplicationData.xml");

        if (fileURL == null)
        {
            // Take the defaultData XML in order to take the default menus
            loadDefaultApplicationData();
        }
        else
        {
            try
            {
                // We reinstantiate the application data model
                _applicationDataModel = new ApplicationDataModel(fileURL);
            }
            catch (Exception ex)
            {
                _logger.log(Level.SEVERE,
                    "Could not load application data from '" + fileURL +
                    "' file.", ex);
                loadDefaultApplicationData();
            }
        }
    }

    /** Load the default ApplicationData.xml */
    private void loadDefaultApplicationData()
    {
        String defaultXmlLocation = "";

        try
        {
            // The App class
            Class app = Introspection.getClass("fr.jmmc.mcs.gui.App");

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
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot laod '" + defaultXmlLocation +
                "' default application data", ex);
            System.exit(-1);
        }
    }

    /** Return the action which displays and copy acknowledgement to clipboard */
    public static Action acknowledgementAction()
    {
        return _acknowledgementAction;
    }

    /** Creates the action which open the about box window */
    public static Action aboutBoxAction()
    {
        return new AbstractAction("About...")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        if (_aboutBox != null)
                        {
                            if (_aboutBox.isVisible() == false)
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
        return new AbstractAction("Report Feedback to JMMC...")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        new FeedbackReport(_applicationFrame, false, ex);
                    }
                }
            };
    }

    /** Return the action which tries to display the help */
    public static Action showHelpAction()
    {
        return _showHelpAction;
    }

    /** Return the action which tries to quit the application */
    public static Action quitAction()
    {
        return _quitAction;
    }

    /** Return the action dedicated to display release */
    public static Action showReleaseAction()
    {
        return _showReleaseAction;
    }

    /** Return the action dedicated to display FAQ */
    public static Action showFaqAction()
    {
        return _showFaqAction;
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
        LongOpt[] longopts = new LongOpt[]
            {
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 0),
                new LongOpt("version", LongOpt.NO_ARGUMENT, null, 1),
                new LongOpt("loggui", LongOpt.NO_ARGUMENT, null, 2)
            };

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
                // Show the application name on the shell
                System.out.println(_applicationDataModel.getProgramName());
                // Exit the application
                System.exit(0);

                break;

            // Display the LogGUI panel
            case 2:
                imx.loggui.LogMaster.startLogGui();

                break;

            // Set the logger level
            case 'v':
                arg = getOpt.getOptarg();

                if (arg != null)
                {
                    _logger.info("Set logger level to '" + arg + "'.");

                    if (arg.equals("0"))
                    {
                        _mainLogger.setLevel(Level.OFF);
                    }
                    else if (arg.equals("1"))
                    {
                        _mainLogger.setLevel(Level.SEVERE);
                    }
                    else if (arg.equals("2"))
                    {
                        _mainLogger.setLevel(Level.WARNING);
                    }
                    else if (arg.equals("3"))
                    {
                        _mainLogger.setLevel(Level.INFO);
                    }
                    else if (arg.equals("4"))
                    {
                        _mainLogger.setLevel(Level.FINE);
                    }
                    else if (arg.equals("5"))
                    {
                        _mainLogger.setLevel(Level.ALL);
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
            "| [-loggui]                    Show the logging tool                    |");
        System.out.println(
            "| [-v]         [0|1|2|3|4|5]   Define console logging level             |");
        System.out.println(
            "| [-version]                   Show application name and version        |");
        System.out.println(
            "| [-h|-help]                   Show arguments help                      |");
        System.out.println(
            "-------------------------------------------------------------------------");
        System.out.println(
            "LEVEL : O=OFF, 1=SEVERE, 2=WARNING, 3=INFO, 4=FINE, 5=ALL\n");

        System.exit(0);
    }

    /** Initialize application objects */
    protected abstract void init(String[] args);

    /** Execute application body */
    protected abstract void execute();

    /**
     * Hook to handle operations before closing application.
     *
     * This method is automatically triggered when the application "Quit" menu
     * has been used. Thus, you have a chance to do things like saves before the
     * application really quit.
     *
     * The default implementation lets the application quitting gently.
     *
     * @warning This method should be overriden to handle quit as you intend to.
     * In its default behavior, all changes that occured during application life
     * will be lost.
     *
     * @return should return true if the application can exit, false otherwise
     * to cancel exit.
     */
    protected boolean finish()
    {
        _logger.info("Default App.finish() handler called.");

        return true;
    }

    /** Describe the life cycle of the application */
    protected void run()
    {
        // Show splash screen if we have to
        if (_showSplashScreen == true)
        {
            showSplashScreen();
        }

        // Call abstract init method with arguments
        init(_args);

        // Set JMenuBar
        MainMenuBar mainMenuBar = new MainMenuBar(_applicationFrame);
        _applicationFrame.setJMenuBar(mainMenuBar);

        // Set application frame common properties
        _applicationFrame.pack();
        _applicationFrame.setLocationRelativeTo(null);

        // Close the splash screen if we have to
        if (_showSplashScreen == true)
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

    /**
     * Return App shared instance
     *
     * @return shared instance
     */
    public static App getSharedInstance()
    {
        return _sharedInstance;
    }

    /**
     * Get URL from resource filename.
     *
     * @param fileName name of searched file.
     *
     * @return resource file URL
     */
    public URL getURLFromResourceFilename(String fileName)
    {
        // The class which is extended from App
        Class actualClass = getClass();

        // It's package
        Package p = actualClass.getPackage();

        // the package name
        String packageName = p.getName();

        // Replace '.' by '/' of package name
        String packagePath = packageName.replace(".", "/");
        String filePath    = packagePath + "/" + fileName;
        _logger.fine("filePath = '" + filePath + "'.");

        URL fileURL = null;

        try
        {
            // Open XML file at path
            fileURL = actualClass.getClassLoader().getResource(filePath);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot load '" + filePath + "' resource file.", ex);
        }

        _logger.fine("fileURL = '" + fileURL + "'.");

        return fileURL;
    }

    /* Action to correctly handle operations before closing application. */
    protected class QuitAction extends RegisteredAction
    {
        public QuitAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Quit", "ctrl Q");

            flagAsQuitAction();
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("QuitAction", "actionPerformed");

            _logger.fine("Should we kill the application ?");

            // If we are ready to finish application execution
            if (finish() == true)
            {
                _logger.fine("Application should be killed.");

                // Verify if we are authorized to kill the application or not
                if (_exitApplicationWhenClosed == true)
                {
                    _logger.info("Killing the application.");
                    System.exit(-1);
                }
                else
                {
                    _logger.fine("Application left opened as required.");
                }
            }
            else
            {
                _logger.fine("Application killing cancelled.");
            }
        }
    }

    /* Action to copy acknowledgement text to the clipboard. */
    protected class AcknowledgementAction extends RegisteredAction
    {
        String _acknowledgement = null;

        public AcknowledgementAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Copy Acknowledgement to Clipboard");
            _acknowledgement = _applicationDataModel.getAcknowledgment();

            // If the application does not provide an acknowledgement
            if (_acknowledgement == null)
            {
                // Generate one instead
                String appName   = _applicationDataModel.getProgramName();
                String appURL    = _applicationDataModel.getLinkValue();
                _acknowledgement = "This research has made use of the \\texttt{" +
                    appName +
                    "} service of the Jean-Marie Mariotti Center\n\\footnote{Available at " +
                    appURL + "}";
            }

            // If the application does not provide an ApplicationData.xml file,
            // the generic acknowledgement found in JMCS will be used instead.
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("AcknowledgementAction", "actionPerformed");

            StringSelection ss = new StringSelection(_acknowledgement);
            Toolkit.getDefaultToolkit().getSystemClipboard()
                   .setContents(ss, null);

            String delimiter     = "---------------------------------------------------------------------------\n";
            String message       = "The previous message has already been copied to your clipboard, in order to\n" +
                "let you conveniently paste it in your related publication.";
            String windowTitle   = _applicationDataModel.getProgramName() +
                " Acknowledgment Note";
            String windowContent = delimiter + _acknowledgement + "\n" +
                delimiter + "\n" + message;

            JOptionPane.showMessageDialog(null, windowContent, windowTitle,
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /* Action to show release. */
    protected class ShowReleaseAction extends RegisteredAction
    {
        public ShowReleaseAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Release Notes");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("ShowReleaseAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getReleaseNotesLinkValue());
        }
    }

    /* Action to show FAQ. */
    protected class ShowFaqAction extends RegisteredAction
    {
        public ShowFaqAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Frequently Asked Questions");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("ShowFaqAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getFaqLinkValue());
        }
    }

    /* Action to show help. */
    protected class ShowHelpAction extends RegisteredAction
    {
        public ShowHelpAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "User Manual");
            setEnabled(HelpView.isAvailable());
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            _logger.entering("ShowHelpAction", "actionPerformed");
            HelpView.setVisible(true);
        }
    }
}
/*___oOo___*/
