/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.23 2008-07-04 14:32:12 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import fr.jmmc.mcs.util.Preferences;
import fr.jmmc.mcs.util.PreferencesException;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import java.io.ByteArrayOutputStream;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
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
public abstract class App implements Observer
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

    /** If it's true, exit the application after the exit method */
    private static boolean _exitApplicationWhenClosed = false;

    /** Application preferences */
    private static Preferences _preferences = new Preferences();

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

            // Set shared instance
            _sharedInstance = this;
            _logger.fine("Shared instance affected");

            // If execution should not be delayed
            if ((waitBeforeExecution == false))
            {
                // Run the application imediatly
                run();
            }
        }
        catch (Exception ex)
        {
            _logger.severe("Error during creation of the application");

            // In order to see the error window
            if (_splashScreen != null)
            {
                if (_splashScreen.isVisible())
                {
                    _splashScreen.setVisible(false);
                }
            }

            String errorMessage = "An error was occured while the application creation";
            JOptionPane.showMessageDialog(null, errorMessage, "Error",
                JOptionPane.ERROR_MESSAGE);

            // Show feedback report
            new FeedbackReport(((_applicationFrame != null) ? _applicationFrame
                                                            : null), true, ex,
                true);
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
                        new FeedbackReport(_applicationFrame, false, ex);
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

                    if (_exitApplicationWhenClosed == true)
                    {
                        System.exit(-1);
                    }
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

    /** Creates the action which save preferences */
    public static Action savePreferencesAction()
    {
        return new AbstractAction("Save preferences")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    savePreferences();
                }
            };
    }

    /** Creates the action which load preferences */
    public static Action loadPreferencesAction()
    {
        return new AbstractAction("Load preferences")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    loadPreferences();
                }
            };
    }

    /** Save preferences */
    public static void savePreferences()
    {
        try
        {
            // Save preferences with the application name
            _preferences.saveToFile(getProgramName());

            _logger.config("Preferences have been saved\n" + _preferences);
        }
        catch (Exception ex)
        {
        }
    }

    /** Load preferences */
    public static void loadPreferences()
    {
        // Get default preferences class name
        Class  c              = getSharedInstance().getClass();
        String packageName    = c.getPackage().getName();
        String preferenceName = packageName + ".Preferences";

        _logger.config("Default preferences class name : " + preferenceName);

        // Instantiate preferences
        _preferences = new Preferences();

        try
        {
            // Get preferences instance
            _preferences = (Preferences) Introspection.getMethodValue(preferenceName,
                    "getInstance");

            _logger.config("Preferences have been loaded\n" + _preferences);
        }
        catch (Exception ex)
        {
            _logger.warning("Cannot load preferences");
        }

        // Add app observer as observer for preferences
        _preferences.addObserver(getSharedInstance());
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
                // Show the application name on the shell
                System.out.println(getProgramName());
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

        // Load preferences
        loadPreferences();

        // Close the splash screen if we have to
        if (_showSplashScreen)
        {
            closeSplashScreen();
        }

        // Check program version (preferences)
        checkProgramVersion();

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
     * Return a string according to the
     * pattern : [programName] v[programVersion]
     *
     * @return program name
     */
    public static String getProgramName()
    {
        // Get application name and version
        String name    = _applicationDataModel.getProgramName();
        String version = _applicationDataModel.getProgramVersion();

        return name + " v" + version;
    }

    /**
     * Observer abstract method
     *
     * @param o observable object
     * @param arg argument
     */
    public void update(Observable o, Object arg)
    {
        _logger.config("Preferences have been changed");
    }

    /**
     * Return preferences
     *
     * @return preferences
     */
    protected Preferences getPreferences()
    {
        return _preferences;
    }

    /**
     * Set a preference
     *
     * @param preferenceName the preference name.
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName,
        Object preferenceValue) throws PreferencesException
    {
        _preferences.setPreference(preferenceName, preferenceValue);
    }

    /**
     * Set a preference.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     * @param preferenceValue the preference value.
     */
    final public void setPreference(String preferenceName, int preferenceIndex,
        Object preferenceValue) throws PreferencesException
    {
        _preferences.setPreference(preferenceName, preferenceIndex,
            preferenceValue);
    }

    /**
     * Set a preference order.
     *
     * @param preferenceName the preference name.
     * @param preferenceIndex the order number for the property (-1 for no order).
     */
    final public void setPreferenceOrder(String preferenceName,
        int preferenceIndex)
    {
        _preferences.setPreferenceOrder(preferenceName, preferenceIndex);
    }

    /**
     * Get a preference order.
     *
     * @param preferenceName the preference name.
     *
     * @return the order number for the property (-1 for no order).
     */
    final public int getPreferenceOrder(String preferenceName)
    {
        return _preferences.getPreferenceOrder(preferenceName);
    }

    /**
     * Get a preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return the preference value.
     */
    final public String getPreference(String preferenceName)
    {
        return _preferences.getPreference(preferenceName);
    }

    /**
     * Get a boolean preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one boolean representing the preference value.
     */
    final public boolean getPreferenceAsBoolean(String preferenceName)
    {
        return _preferences.getPreferenceAsBoolean(preferenceName);
    }

    /**
     * Get a double preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one double representing the preference value.
     */
    final public double getPreferenceAsDouble(String preferenceName)
    {
        return _preferences.getPreferenceAsDouble(preferenceName);
    }

    /**
     * Get a color preference value.
     *
     * @param preferenceName the preference name.
     *
     * @return one Color object representing the preference value.
     */
    final public Color getPreferenceAsColor(String preferenceName)
        throws PreferencesException
    {
        return _preferences.getPreferenceAsColor(preferenceName);
    }

    /**
     * Returns an Enumeration (ordered if possible) of preference names which
     * start with given string. One given empty string make all preference
     * entries returned.
     *
     * @return Enumeration a string enumeration of preference names
     */
    public Enumeration getPreferences(String prefix)
    {
        return _preferences.getPreferences(prefix);
    }

    /**
     * Return a double according to a string
     * which represents a version like "1.0.02.1"
     *
     * @param version version string
     *
     * @return version double
     */
    private double getVersionAsDouble(String version)
    {
        String   finalString = "";

        String[] tmpString   = version.split("\\.");
        finalString += (tmpString[0] + '.');

        if (tmpString.length > 1)
        {
            for (int i = 1; i < tmpString.length; i++)
            {
                finalString += tmpString[i];
            }
        }

        return Double.parseDouble(finalString);
    }

    /** Check version of preferences file and application version */
    private void checkProgramVersion()
    {
        // Get application informations
        String appName              = _applicationDataModel.getProgramName();
        String appVersionString     = _applicationDataModel.getProgramVersion();
        String appFileVersionString = _preferences.getPreference(
                "application.version");

        // In order to compare versions
        double appVersion     = 0.0;
        double appFileVersion = 0.0;

        if (appVersionString != null)
        {
            appVersion = getVersionAsDouble(appVersionString);
        }

        if (appFileVersionString != null)
        {
            appFileVersion = getVersionAsDouble(appFileVersionString);
        }

        /*System.out.println("APP : " + appVersion + "\tFILE : " +
           appFileVersion);*/

        // Some comments for dialogs
        String com1 = "Your application preferences file corresponding\nto an ";
        String com2 = "\nDo you want to correct it now?";
        String com3 = " preferences file version";

        // If the file version is older than application version
        if (appFileVersion < appVersion)
        {
            String infoMessage = com1 + "older application version." + com2;
            int    n           = JOptionPane.showConfirmDialog(null,
                    infoMessage, "Older" + com3, JOptionPane.YES_NO_OPTION);

            // TODO to correct the problem
            if (n == JOptionPane.YES_OPTION)
            {
                try
                {
                    _preferences.resetToDefaultPreferences();
                    _preferences.setPreference("application.version", appVersion);
                }
                catch (Exception ex)
                {
                }

                // Save preferences
                savePreferences();
            }
        }
        else if (appFileVersion > appVersion)
        {
            String infoMessage = com1 + "newer application version." + com2;
            int    n           = JOptionPane.showConfirmDialog(null,
                    infoMessage, "Newer" + com3, JOptionPane.YES_NO_OPTION);

            // TODO to correct the problem
            if (n == JOptionPane.YES_OPTION)
            {
                _preferences.resetToDefaultPreferences();

                try
                {
                    _preferences.setPreference("application.version", appVersion);
                }
                catch (Exception ex)
                {
                }

                // Save preferences
                savePreferences();
            }
        }

        // Set the application name property
        try
        {
            _preferences.setPreference("application.name", appName);
        }
        catch (Exception ex)
        {
        }
    }
}
/*___oOo___*/
