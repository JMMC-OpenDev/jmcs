/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.8 2008-06-10 09:14:58 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

    /** Default menu components (file, edit...) */
    private Vector<JComponent> _defaultMenuComponents = null;

    /** Application's menubar */
    private JMenuBar _menuBar = new JMenuBar();

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
        _showSplashScreen = showSplashScreen;

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
        if (waitBeforeExecution == false)
        {
            // Run the application imediatly
            run();
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
        String xmlLocation = packageName + "/" + dataFileName;

        /* Take the defaultData XML in order
           to take the default menus */
        takeDefaultApplicationData();

        try
        {
            // Open XML file at path
            URL xmlURL = actualClass.getClassLoader().getResource(xmlLocation);

            _applicationDataModel = new ApplicationDataModel(xmlURL);

            // Add application's menus
            buildMenu();
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
                                                      .replace(".",
                    File.separator);

            // Default XML location
            defaultXmlLocation = defaultPackageName + File.separator +
                "ApplicationData.xml";

            URL defaultXmlURL = app.getClassLoader()
                                   .getResource(defaultXmlLocation);

            // We reinstantiate the application data model
            _applicationDataModel      = new ApplicationDataModel(defaultXmlURL);

            // Take default menus
            _defaultMenuComponents     = new Vector<JComponent>();

            JComponent[] jComponents   = _applicationDataModel.getMenusComponents();

            for (JComponent jComponent : jComponents)
            {
                _defaultMenuComponents.add(jComponent);
            }
        }
        catch (Exception ex2)
        {
            _logger.log(Level.WARNING,
                "Cannot unmarshal default " + defaultXmlLocation, ex2);
            System.exit(-1);
        }
    }

    /** Build Menubar */
    private void buildMenu()
    {
        JComponent[] menuComponentsTaken = _applicationDataModel.getMenusComponents();

        for (JComponent jComponent : menuComponentsTaken)
        {
            _defaultMenuComponents.add(jComponent);
        }

        _logger.fine(_defaultMenuComponents.size() + " menu elements found");

        Vector<JComponent> orderMenuItems = ordererMenuItems(_defaultMenuComponents);

        for (JComponent jComponent : orderMenuItems)
        {
            _menuBar.add(jComponent);
        }
    }

    /**
     * Orderer menu items according to
     * pattern : [File][Edit][...][...][Help]
     *
     * @param menu menus vector to orderer
     *
     * @return menus vector according to pattern
     */
    private Vector<JComponent> ordererMenuItems(Vector<JComponent> menu)
    {
        Vector<JComponent> ordererMenuItems = new Vector<JComponent>();

        for (JComponent comp : menu)
        {
            if (comp.getName().equals("File"))
            {
                ordererMenuItems.add(comp);
            }
        }

        for (JComponent comp : menu)
        {
            if (comp.getName().equals("Edit"))
            {
                ordererMenuItems.add(comp);
            }
        }

        for (JComponent comp : menu)
        {
            if (! comp.getName().equals("File") &&
                    ! comp.getName().equals("Edit") &&
                    ! comp.getName().equals("Help"))
            {
                ordererMenuItems.add(comp);
            }
        }

        for (JComponent comp : menu)
        {
            if (comp.getName().equals("Help"))
            {
                ordererMenuItems.add(comp);
            }
        }

        return ordererMenuItems;
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
                        if (_aboutBox != null)
                        {
                            if (! _aboutBox.isVisible())
                            {
                                _aboutBox.setVisible(true);
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
                args, "-:hv:", longopts, true);

        int    c; // argument key
        String arg; // argument value

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

                String name = _applicationDataModel.getProgramName();
                String version = _applicationDataModel.getProgramVersion();

                System.out.println(name + " v" + version);

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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected JMenuBar getMenuBar()
    {
        return _menuBar;
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
