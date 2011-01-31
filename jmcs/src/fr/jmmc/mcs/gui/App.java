/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.76 2011-01-31 15:06:46 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.75  2011/01/31 13:26:30  bourgesl
 * added hasFileArgument() to let child class know if there is a file to open
 *
 * Revision 1.74  2010/11/04 16:45:30  lafrasse
 * Added isAlphaVersion()
 * .
 *
 * Revision 1.73  2010/10/22 11:00:13  bourgesl
 * fixed setFrame javadoc
 * added SampManager shutdown in QuitAction
 *
 * Revision 1.72  2010/10/11 13:58:44  bourgesl
 * create Main Menu bar in EDT (sync)
 *
 * Revision 1.71  2010/10/08 08:39:09  mella
 * Add javadoc info
 *
 * Revision 1.70  2010/10/05 12:02:59  bourgesl
 * added comments on MainMenuBar initialization
 *
 * Revision 1.69  2010/10/05 08:37:47  lafrasse
 * Removed default window centering.
 *
 * Revision 1.68  2010/10/04 10:23:51  mella
 * Add one setter to initialise the application frame. This allow temporary workarround for the appliaction which have not used the getFrame() method to build GUI over.
 *
 * Revision 1.67  2010/09/30 13:37:11  bourgesl
 * initialization catch Throwable (Errors too)
 * use MessagePane
 *
 * Revision 1.66  2010/09/26 12:40:58  bourgesl
 * application is ready after run() and before loading a file
 *
 * Revision 1.65  2010/09/25 13:37:33  bourgesl
 * added applicationReady flag used by the FeedbackReport to exit or not the application
 *
 * Revision 1.64  2010/09/24 15:46:04  bourgesl
 * use MessagePane
 *
 * Revision 1.63  2010/09/23 19:37:56  bourgesl
 * MemoryHandler (logs) initialized via static initializer to be ready immediately
 * comments when calling FeedBackReport
 *
 * Revision 1.62  2010/09/21 07:24:01  mella
 * Add getter method to check if the application is one production or development version
 *
 * Revision 1.61  2010/07/08 13:20:34  bourgesl
 * added a new method ready() called in run() method and after both execute() and open action in order to show the GUI only after open action done
 *
 * Revision 1.60  2010/06/28 14:26:18  lafrasse
 * Added actual software version when asked from CLI interface.
 *
 * Revision 1.59  2010/06/14 13:10:28  bourgesl
 * store temporaly the file open argument to execute the open action after the application startup
 *
 * Revision 1.58  2010/06/14 11:52:29  bourgesl
 * fixed command line parsing to get the file argument (-open file)
 *
 * Revision 1.57  2010/06/14 08:22:01  bourgesl
 * file argument string buffer is instanciated to avoid NPE
 * removed unused variables
 * javadoc
 *
 * Revision 1.56  2010/04/13 14:01:54  bourgesl
 * the static internal frame is lazy instanciated to avoid initialization issues (EDT)
 * the method getFrame() is public
 *
 * Revision 1.55  2010/01/14 13:03:04  bourgesl
 * use Logger.isLoggable to avoid a lot of string.concat()
 *
 * Revision 1.54  2009/10/07 15:58:34  lafrasse
 * Jalopization.
 *
 * Revision 1.53  2009/09/18 10:41:40  mella
 * Fix default acknoledgments form
 *
 * Revision 1.52  2009/05/13 09:24:24  lafrasse
 * Added a generic "Hot News (RSS Feed)" Help menu item.
 *
 * Revision 1.51  2009/04/30 09:05:05  lafrasse
 * Added code to automatically load a file given by the executing OS on startup.
 *
 * Revision 1.50  2009/04/16 15:42:49  lafrasse
 * Corrected Help icon URL.
 * Jalopization.
 *
 * Revision 1.49  2009/04/15 08:57:48  mella
 * add 22x22 help icon onto show help action
 *
 * Revision 1.48  2009/02/26 14:13:51  mella
 * improve getURLFromResourceFilename
 *
 * Revision 1.47  2008/12/16 11:04:21  lafrasse
 * Jalopization.
 *
 * Revision 1.46  2008/12/12 15:06:13  mella
 * Add public static showLogGui method
 *
 * Revision 1.45  2008/12/12 15:00:34  mella
 * Add public showLogGui method
 *
 * Revision 1.44  2008/10/17 10:41:54  lafrasse
 * Added FAQ handling.
 *
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


import fr.jmmc.mcs.interop.SampManager;
import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mcs.util.CommonPreferences;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mcs.util.Urls;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;



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

    /** Stream handler which permit us to keep logs report in strings */
    private static final StreamHandler _streamHandler;

    /** ByteArrayOutputStream which keeps logs report */
    private static final ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream(32768);

    /**
     * Static Logger initialization
     */
    static {
        // Logger's stream handler creation
        _streamHandler = new StreamHandler(_byteArrayOutputStream, new SimpleFormatter());

        // We add the memory handler create to the logger
        _mainLogger.addHandler(_streamHandler);
        _mainLogger.setLevel(Level.FINE);

        _mainLogger.finer("Main Logger properties set");
        _mainLogger.fine("Memory handler created and fixed to feedback logger.");
    }


    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.gui.App");

    /** Singleton reference */
    private static App _sharedInstance;

    /** flag indicating if the application started properly and is ready (visible) */
    private static boolean _applicationReady = false;

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

    /** Main frame of the application (singleton) */
    private static JFrame _applicationFrame = null;

    /** Arguments */
    private static String[] _args;

    /** If it's true, exit the application after the exit method */
    private static boolean _exitApplicationWhenClosed = true;

    /** Quit handling action */
    private static QuitAction _quitAction = null;

    /** Acknowledgement handling action */
    private static AcknowledgementAction _acknowledgementAction = null;

    /** Show help handling action */
    private static ShowHelpAction _showHelpAction = null;

    /** Show hto news handling action */
    private static ShowHotNewsAction _showHotNewsAction = null;

    /** Show release handling action */
    private static ShowReleaseAction _showReleaseAction = null;

    /** Show FAQ handling action */
    private static ShowFaqAction _showFaqAction = null;

    /* members */
    /** Store a proxy to the shared ActionRegistrar facility */
    private ActionRegistrar _registrar = null;

    /** temporarly store the file name argument for the open action */
    private String _fileArgument = null;

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

    /**
     * Constructor whith possibility to specify if the application should be
     * stopped when the exit method is called
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically     
     * @param exitWhenClosed if true, the application will close when exit method is called
     */
    protected App(String[] args, boolean waitBeforeExecution, boolean exitWhenClosed)
    {
        try
        {
            _registrar = ActionRegistrar.getInstance();

            String classPath = getClass().getName();
            new OpenAction(classPath, "_openAction");
            _quitAction = new QuitAction(classPath, "_quitAction");

            // Attributes affectations
            _args                          = args;
            _exitApplicationWhenClosed     = exitWhenClosed;

            // check in common preferences if the splashscreen must be show at startup
            _showSplashScreen = CommonPreferences.getInstance().getPreferenceAsBoolean(CommonPreferences.SPLASH_SCREEN_SHOW);

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
            _showHotNewsAction         = new ShowHotNewsAction("fr.jmmc.mcs.gui.App",
                    "_showHotNewsAction");
            _showReleaseAction         = new ShowReleaseAction("fr.jmmc.mcs.gui.App",
                    "_showReleaseAction");
            _showFaqAction             = new ShowFaqAction("fr.jmmc.mcs.gui.App",
                    "_showFaqAction");
            _showHelpAction            = new ShowHelpAction("fr.jmmc.mcs.gui.App",
                    "_showHelpAction");

            // If execution should not be delayed
            if (!waitBeforeExecution)
            {
                // Run the application imediately
                run();
            }

        } catch (Throwable th) {
            _logger.severe("Error while initializing the application");

            // In order to see the error window
            if (_splashScreen != null)
            {
                if (_splashScreen.isVisible())
                {
                    _splashScreen.setVisible(false);
                }
            }

            MessagePane.showErrorMessage(
                "An error occured while initializing the application");

            // Show the feedback report (modal) :
            new FeedbackReport(true, th);
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
            Class<?> app = Introspection.getClass("fr.jmmc.mcs.gui.App");

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
            if (_logger.isLoggable(Level.WARNING)) {
                _logger.log(Level.WARNING,
                    "Cannot laod '" + defaultXmlLocation +
                    "' default application data", ex);
            }
            System.exit(-1);
        }
    }

    /**
     * Return the action which displays and copy acknowledgement to clipboard
     * @return action which displays and copy acknowledgement to clipboard
     */
    public static Action acknowledgementAction()
    {
        return _acknowledgementAction;
    }

    /**
     * Creates the action which open the about box window
     * @return action which open the about box window
     */
    public static Action aboutBoxAction()
    {
        return new AbstractAction("About...")
            {
                /** default serial UID for Serializable interface */
                private static final long serialVersionUID = 1;

                /**
                 * Handle the action event
                 * @param evt action event
                 */
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        if (_aboutBox != null)
                        {
                            if (!_aboutBox.isVisible())
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

    /**
     * Creates the feedback action which open the feedback window
     * @return feedback action which open the feedback window
     */
    public static Action feedbackReportAction()
    {
        return feedbackReportAction(null);
    }

    /**
     * Creates the feedback action which open the feedback window
     * @param ex exception that occured
     * @return feedback action which open the feedback window
     */
    public static Action feedbackReportAction(final Exception ex)
    {
        return new AbstractAction("Report Feedback to JMMC...")
            {
                /** default serial UID for Serializable interface */
                private static final long serialVersionUID = 1;

                /**
                 * Handle the action event
                 * @param evt action event
                 */
                public void actionPerformed(ActionEvent evt)
                {
                    if (_applicationDataModel != null)
                    {
                        // Show the feedback report :
                        new FeedbackReport(ex);
                    }
                }
            };
    }

    /**
     * Return the action which tries to display the help
     * @return action which tries to display the help
     */
    public static Action showHelpAction()
    {
        return _showHelpAction;
    }

    /**
     * Return the action which tries to quit the application
     * @return action which tries to quit the application
     */
    public static Action quitAction()
    {
        return _quitAction;
    }

    /**
     * Return the action dedicated to display hot news
     * @return action dedicated to display hot news 
     */
    public static Action showHotNewsAction()
    {
        return _showHotNewsAction;
    }

    /**
     * Return the action dedicated to display release
     * @return action dedicated to display release
     */
    public static Action showReleaseAction()
    {
        return _showReleaseAction;
    }

    /**
     * Return the action dedicated to display FAQ
     * @return action dedicated to display FAQ 
     */
    public static Action showFaqAction()
    {
        return _showFaqAction;
    }

    /**
     * Interpret command line arguments
     *
     * @param args arguments
     */
    private final void interpretArguments(String[] args)
    {
        // List received arguments
        if (_logger.isLoggable(Level.FINEST)) {
            for (int i = 0; i < args.length; i++)
            {
                _logger.finest("args[" + i + "] = '" + args[i] + "'.");
            }
        }

        // Just leave method if no argument has been given
        if (args == null)
        {
            return;
        }

        // Array for long arguments (help & version)
        final LongOpt[] longopts = new LongOpt[]
            {
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("version", LongOpt.NO_ARGUMENT, null, 1),
                new LongOpt("loggui", LongOpt.NO_ARGUMENT, null, 2),
                new LongOpt("open", LongOpt.REQUIRED_ARGUMENT, null, 3)
            };

        // Instantiate the getopt object
        final Getopt getOpt = new Getopt(_applicationDataModel.getProgramName(),
                args, "hv:", longopts, true);

        int    c; // argument key
        String arg    = null; // argument value

        // While there is a argument key
        while ((c = getOpt.getopt()) != -1)
        {
            if (_logger.isLoggable(Level.FINEST)) {
              _logger.finest("opt = " + c);
            }

            switch (c)
            {
            // Show the arguments help
            case 'h':
                showArgumentsHelp();

                break;

            // Show the name and the version of the program
            case 1:
                // Show the application name on the shell
                System.out.println(_applicationDataModel.getProgramName() + " v" + _applicationDataModel.getProgramVersion());
                // Exit the application
                System.exit(0);

                break;

            // Display the LogGUI panel
            case 2:
                showLogGui();

                break;

            // Open the given file
            case 3:
                // get the file path argument and store it temporarly :
                this._fileArgument = getOpt.getOptarg();
                if (_logger.isLoggable(Level.INFO)) {
                  _logger.info("Should open '" + this._fileArgument + "'.");
                }
                break;

            // Set the logger level
            case 'v':
                arg = getOpt.getOptarg();

                if (arg != null)
                {
                    if (_logger.isLoggable(Level.INFO)) {
                      _logger.info("Set logger level to '" + arg + "'.");
                    }

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

    /**
     * Initialize application objects
     *
     * The actions which are present in menubar must be instantiated in this method.
     * 
     * @param args command line arguments
     */
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
    protected final void run()
    {
        // Show splash screen if we have to
        if (_showSplashScreen)
        {
            showSplashScreen();
        }

        // Delegate initialization to daughter class through abstract init() call
        init(_args);

        try {

          // Using invokeAndWait to be in sync with the main thread :
          SwingUtilities.invokeAndWait(new Runnable() {

            /**
             * Initializes swing components in EDT
             */
            public void run() {
              // Set JMenuBar
              final JFrame frame = getFrame();

              frame.setJMenuBar(new MainMenuBar(frame));

              // Set application frame common properties
              frame.pack();
            }
          });

        } catch (InterruptedException ie) {
          // propagate the exception :
          throw new IllegalStateException("App.run : interrupted", ie);
        } catch (InvocationTargetException ite) {
          // propagate the internal exception :
          throw new IllegalStateException("App.run : exception", ite.getCause());
        }

        // Close the splash screen if we have to
        if (_showSplashScreen)
        {
            closeSplashScreen();
        }

        // Delegate execution to daughter class through abstract execute() call
        execute();

        // Indicate that the application is ready (visible)
        _applicationReady = true;

        // If any file argument exists, open that file using the registered open action :
        if (_fileArgument != null)
        {
          SwingUtilities.invokeLater(new Runnable() {
            /**
             * Open the file using EDT :
             */
            public void run() {
              _registrar.getOpenAction()
                        .actionPerformed(new ActionEvent(_registrar, 0, _fileArgument));
              // clear :
              _fileArgument = null;
            }
          });
        }
    }

    /**
     * Return true if there is a file name argument for the open action (during startup)
     * @return true if there is a file name argument for the open action
     */
    protected final boolean hasFileArgument() {
      return this._fileArgument != null;
    }

    /**
     * Returns logs report into a unique string
     *
     * @return logs report into a unique string
     */
    public static String getLogOutput()
    {
        // Needed in order to write all logs in the ouput stream buffer
        if (_streamHandler != null) {
            _streamHandler.flush();
        }

        return _byteArrayOutputStream.toString();
    }

    /**
     * Show third party logging utility.
     */
    public static void showLogGui()
    {
        imx.loggui.LogMaster.startLogGui();
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
        // LAURENT : TODO CLEAN : ILLEGAL a thread must not be killed like this :
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
     * Tell if the application is a beta version or not.
     * This flag is given searching one 'b' in the program version number.
     *
     * @return true if it is a beta, false otherwise.
     */
    public static boolean isBetaVersion()
    {
        if (_applicationDataModel!=null)
        {
            return _applicationDataModel.getProgramVersion().contains("b");
        }
        return false;
    }

    /**
     * Tell if the application is an alpha version or not.
     * This flag is given searching one 'a' in the program version number.
     *
     * @return true if it is a alpha, false otherwise.
     */
    public static boolean isAlphaVersion()
    {
        if (_applicationDataModel!=null)
        {
            return _applicationDataModel.getProgramVersion().contains("a");
        }
        return false;
    }

    /**
     * Define the application frame (singleton).
     *
     * TODO : workaround to let App create the frame (getFrame)...
     * Concrete applications must be later refactored to initialize correctly the GUI using getFrame()
     *
     * @param frame application frame
     */
    public static void setFrame(final JFrame frame)
    {
        _applicationFrame = frame;
    }

    /**
     * Return the application frame (singleton)
     *
     * @return application frame
     */
    public static JFrame getFrame()
    {
      if (_applicationFrame == null)
      {
        _applicationFrame = new JFrame();
      }
        return _applicationFrame;
    }

    /**
     * Return the application frame panel
     *
     * @return application frame panel
     */
    public static Container getFramePanel()
    {
        return getFrame().getContentPane();
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
     * Return true if the Application is ready
     *
     * @return true if the Application is ready
     */
    public final static boolean isReady()
    {
        return _applicationReady;
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
        Class<?> actualClass = getClass();

        // It's package
        Package p = actualClass.getPackage();

        // the package name
        String packageName = p.getName();

        // Replace '.' by '/' of package name
        String packagePath = packageName.replace(".", "/");
        String filePath    = packagePath + "/" + fileName;
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("filePath = '" + filePath + "'.");
        }

        URL fileURL = null;

        try
        {
            // Open XML file at path
            fileURL = actualClass.getClassLoader().getResource(filePath);
        }
        catch (Exception ex)
        {
            if (_logger.isLoggable(Level.WARNING)) {
                _logger.log(Level.WARNING,
                    "Cannot load '" + filePath + "' resource file.", ex);
            }
        }

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("fileURL = '" + fileURL + "'.");
        }

        return Urls.fixJarURL(fileURL);
    }

    /** Action to correctly handle file opening. */
    protected class OpenAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public OpenAction(String classPath, String fieldName)
        {
            super(classPath, fieldName);

            // Disabled as this default implementation does nothing
            setEnabled(false);

            flagAsOpenAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("OpenAction", "actionPerformed");

            _logger.warning("No handler for default file opening.");
        }
    }

    /** Action to correctly handle operations before closing application. */
    protected class QuitAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public QuitAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Quit", "ctrl Q");

            flagAsQuitAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("QuitAction", "actionPerformed");

            _logger.fine("Should we kill the application ?");

            // If we are ready to finish application execution
            if (finish())
            {
                _logger.fine("Application should be killed.");

                // Verify if we are authorized to kill the application or not
                if (_exitApplicationWhenClosed)
                {
                    _logger.info("Killing the application.");

                    SampManager.shutdown();

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

    /** Action to copy acknowledgement text to the clipboard. */
    protected class AcknowledgementAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /** acknowlegment content */
        private String _acknowledgement = null;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
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
                _acknowledgement = "This research has made use of the " +
                    "Jean-Marie Mariotti Center\\texttt{" + appName +
                    "} service\n\\footnote{Available at " + appURL + "}";
            }

            // If the application does not provide an ApplicationData.xml file,
            // the generic acknowledgement found in JMCS will be used instead.
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("AcknowledgementAction", "actionPerformed");

            StringSelection ss = new StringSelection(_acknowledgement);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

            final String delimiter     = "---------------------------------------------------------------------------\n";
            final String message       = "The previous message has already been copied to your clipboard, in order to\n" +
                "let you conveniently paste it in your related publication.";
            final String windowTitle   = _applicationDataModel.getProgramName() +
                " Acknowledgment Note";
            final String windowContent = delimiter + _acknowledgement + "\n" +
                delimiter + "\n" + message;

            MessagePane.showMessage(windowContent, windowTitle);
        }
    }

    /** Action to show hot news RSS feed. */
    protected class ShowHotNewsAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public ShowHotNewsAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Hot News (RSS Feed)");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("ShowReleaseAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getHotNewsRSSFeedLinkValue());
        }
    }

    /** Action to show release. */
    protected class ShowReleaseAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public ShowReleaseAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Release Notes");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("ShowReleaseAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getReleaseNotesLinkValue());
        }
    }

    /** Action to show FAQ. */
    protected class ShowFaqAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public ShowFaqAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "Frequently Asked Questions");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("ShowFaqAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getFaqLinkValue());
        }
    }

    /** Action to show help. */
    protected class ShowHelpAction extends RegisteredAction
    {
        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        public ShowHelpAction(String classPath, String fieldName)
        {
            super(classPath, fieldName, "User Manual");
            setEnabled(HelpView.isAvailable());

            // Set Icon
            String icon = "/fr/jmmc/mcs/gui/help.png";
            this.putValue(SMALL_ICON,
                new ImageIcon(Urls.fixJarURL(getClass().getResource(icon))));
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        public void actionPerformed(ActionEvent evt)
        {
            _logger.entering("ShowHelpAction", "actionPerformed");
            HelpView.setVisible(true);
        }
    }
}
/*___oOo___*/
