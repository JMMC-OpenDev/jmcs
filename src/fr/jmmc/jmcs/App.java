/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import fr.jmmc.jmcs.util.Introspection;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.AboutBox;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.HelpView;
import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.gui.SplashScreen;
import fr.jmmc.jmcs.gui.SwingSettings;
import fr.jmmc.jmcs.util.Urls;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang.SystemUtils;

/**
 * This class represents an application. In order to use
 * functionalities that are implemented here, you have to
 * extend your application from this class.
 *
 * This class is a singleton with abstract methods. If you extend
 * a class from this one, you can have a splash-screen, an about
 * window, the menu bar generation, the possibility to have a help
 * window using <b>jmcsGenerateHelpSetFromHtml</b> from the bash
 * script called <b>jmcsHTML2HelpSet.sh</b> located into the src
 * folder of jMCS, the feedback report etc...
 *
 * To access to the XML informations, this class uses
 * <b>ApplicationDataModel</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to access to these informations.
 * 
 * @author Brice COLUCCI, Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class App {

    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _mainLogger = Logger.getLogger("fr.jmmc");
    /** Stream handler which permit us to keep logs report in strings */
    private static final StreamHandler _streamHandler;
    /** ByteArrayOutputStream which keeps logs report */
    private static final ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream(32768);

    /**
     * Static Logger initialization and Network settings
     */
    static {
        // Logger's stream handler creation
        _streamHandler = new StreamHandler(_byteArrayOutputStream, new SimpleFormatter());

        // We add the memory handler create to the logger
        _mainLogger.addHandler(_streamHandler);

        _mainLogger.info("Memory handler created and attached to feedback logger.");

        // Define default network settings:
        // note: settings must be set before using any URLConnection (loadApplicationData)
        NetworkSettings.defineDefaults();

        // Try to define swing settings (laf, locale...) again if
        // next line was not present at the first line of the main method.
        SwingSettings.setup();

    }
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.gui.App");
    /** flag to avoid calls to System.exit() (JUnit) */
    private static boolean _avoidSystemExit = false;
    /** Singleton reference */
    private static App _sharedInstance;
    /** flag indicating if the application started properly and is ready (visible) */
    private static boolean _applicationReady = false;
    /** Shared application data model */
    private static ApplicationDataModel _applicationDataModel;
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
    /** Show the splash screen ? */
    private boolean _showSplashScreen = true;
    /** Splash screen */
    private SplashScreen _splashScreen = null;
    /** temporarly store the file name argument for the open action */
    private String _fileArgument = null;

    /**
     * Creates a new App object
     *
     * @param args command-line arguments
     */
    protected App(String[] args) {
        // Start application immediatly, with splashscreen
        this(args, false);
    }

    /** Creates a new App object, with possibility to delay execution
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     */
    protected App(String[] args, boolean waitBeforeExecution) {
        // Start application with splashscreen
        this(args, waitBeforeExecution, true);
    }

    /**
     * Constructor with possibility to specify if the application should be
     * stopped when the exit method is called
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     * @param exitWhenClosed if true, the application will close when exit method is called
     */
    protected App(String[] args, boolean waitBeforeExecution, boolean exitWhenClosed) {
        try {
            _registrar = ActionRegistrar.getInstance();

            String classPath = getClass().getName();
            new OpenAction(classPath, "_openAction");
            _quitAction = new QuitAction(classPath, "_quitAction");

            // Attributes affectations
            _args = args;
            _exitApplicationWhenClosed = exitWhenClosed;

            // Check in common preferences whether startup splashscreen should be shown or not
            _showSplashScreen = CommonPreferences.getInstance().getPreferenceAsBoolean(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN);

            _logger.fine("App object instantiated and logger created.");

            // Set the application data attribute
            loadApplicationData();
            _logger.fine("Application data loaded.");

            // Interpret arguments
            interpretArguments(args);

            // Set shared instance
            _sharedInstance = this;

            // Build Acknowledgment, ShowRelease and ShowHelp Actions
            // (the creation must be done after applicationModel instanciation)
            _acknowledgementAction = new AcknowledgementAction("fr.jmmc.mcs.gui.App",
                    "_acknowledgementAction");
            _showHotNewsAction = new ShowHotNewsAction("fr.jmmc.mcs.gui.App",
                    "_showHotNewsAction");
            _showReleaseAction = new ShowReleaseAction("fr.jmmc.mcs.gui.App",
                    "_showReleaseAction");
            _showFaqAction = new ShowFaqAction("fr.jmmc.mcs.gui.App",
                    "_showFaqAction");
            _showHelpAction = new ShowHelpAction("fr.jmmc.mcs.gui.App",
                    "_showHelpAction");

            // If execution should not be delayed
            if (!waitBeforeExecution) {
                // Run the application immediately
                run();
            }

        } catch (Throwable th) { // main initialization

            // In order to see the error window
            if (_splashScreen != null) {
                if (_splashScreen.isVisible()) {
                    _splashScreen.setVisible(false);
                }
            }

            MessagePane.showErrorMessage("An error occured while initializing the application");

            // Show the feedback report (modal) :
            new FeedbackReport(true, th);
        }
    }

    /**
     * Load application data if Applicationdata.xml exists into the module.
     * Otherwise, uses the default ApplicationData.xml.
     */
    private void loadApplicationData() {
        final URL fileURL = getURLFromResourceFilename("ApplicationData.xml");

        if (fileURL == null) {
            // Take the defaultData XML in order to take the default menus
            loadDefaultApplicationData();
        } else {
            try {
                // We reinstantiate the application data model
                _applicationDataModel = new ApplicationDataModel(fileURL);
            } catch (IllegalStateException iae) {
                _logger.log(Level.SEVERE, "Could not load application data from '" + fileURL + "' file.", iae);

                // Take the defaultData XML in order to take the default menus:
                loadDefaultApplicationData();
            }
        }
    }

    /**
     * Load the default ApplicationData.xml
     * @throws IllegalStateException if the default ApplicationData.xml can not be loaded
     */
    private void loadDefaultApplicationData() throws IllegalStateException {
        String defaultXmlLocation = "";

        // The App class
        final Class<?> appClass = App.class;

        // The App package
        final Package defaultPackage = appClass.getPackage();

        // Replace '.' by '/' of package name
        final String defaultPackageName = defaultPackage.getName().replace(".", "/");

        // Default XML location
        defaultXmlLocation = defaultPackageName + "/ApplicationData.xml";

        final URL defaultXmlURL = appClass.getClassLoader().getResource(defaultXmlLocation);

        if (defaultXmlURL == null) {
            throw new IllegalStateException("Cannot load default application data.");
        }

        _logger.log(Level.SEVERE, "Loading application data from '" + defaultXmlURL + "' file.");

        // We reinstantiate the application data model
        _applicationDataModel = new ApplicationDataModel(defaultXmlURL);
    }

    /**
     * Return the action which displays and copy acknowledgement to clipboard
     * @return action which displays and copy acknowledgement to clipboard
     */
    public static Action acknowledgementAction() {
        return _acknowledgementAction;
    }

    /**
     * Creates the action which open the about box window
     * @return action which open the about box window
     */
    public static Action aboutBoxAction() {
        return new AbstractAction("About...") {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            /**
             * Handle the action event
             * @param evt action event
             */
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (_applicationDataModel != null) {
                    if (_aboutBox != null) {
                        if (!_aboutBox.isVisible()) {
                            _aboutBox.setVisible(true);
                        } else {
                            _aboutBox.toFront();
                        }
                    } else {
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
    public static Action feedbackReportAction() {
        return feedbackReportAction(null);
    }

    /**
     * Creates the feedback action which open the feedback window
     * @param ex exception that occured
     * @return feedback action which open the feedback window
     */
    public static Action feedbackReportAction(final Exception ex) {
        return new AbstractAction("Report Feedback to JMMC...") {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            /**
             * Handle the action event
             * @param evt action event
             */
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (_applicationDataModel != null) {
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
    public static Action showHelpAction() {
        return _showHelpAction;
    }

    /**
     * Return the action which tries to quit the application
     * @return action which tries to quit the application
     */
    public static Action quitAction() {
        return _quitAction;
    }

    /**
     * Return the action dedicated to display hot news
     * @return action dedicated to display hot news 
     */
    public static Action showHotNewsAction() {
        return _showHotNewsAction;
    }

    /**
     * Return the action dedicated to display release
     * @return action dedicated to display release
     */
    public static Action showReleaseAction() {
        return _showReleaseAction;
    }

    /**
     * Return the action dedicated to display FAQ
     * @return action dedicated to display FAQ 
     */
    public static Action showFaqAction() {
        return _showFaqAction;
    }

    /**
     * Interpret command line arguments
     *
     * @param args arguments
     */
    private final void interpretArguments(String[] args) {
        // List received arguments
        if (_logger.isLoggable(Level.FINEST)) {
            for (int i = 0; i < args.length; i++) {
                _logger.finest("args[" + i + "] = '" + args[i] + "'.");
            }
        }

        // Just leave method if no argument has been given
        if (args == null) {
            return;
        }

        // Array for long arguments (help & version)
        final LongOpt[] longopts = new LongOpt[]{
            new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
            new LongOpt("version", LongOpt.NO_ARGUMENT, null, 1),
            new LongOpt("loggui", LongOpt.NO_ARGUMENT, null, 2),
            new LongOpt("open", LongOpt.REQUIRED_ARGUMENT, null, 3)
        };

        // Instantiate the getopt object
        final Getopt getOpt = new Getopt(_applicationDataModel.getProgramName(),
                args, "hv:", longopts, true);

        int c; // argument key
        String arg = null; // argument value

        // While there is a argument key
        while ((c = getOpt.getopt()) != -1) {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("opt = " + c);
            }

            switch (c) {
                // Show the arguments help
                case 'h':
                    showArgumentsHelp();
                    break;

                // Show the name and the version of the program
                case 1:
                    // Show the application name on the shell
                    System.out.println(_applicationDataModel.getProgramName() + " v" + _applicationDataModel.getProgramVersion());

                    // Exit the application
                    App.exit(0);
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

                    if (arg != null) {
                        if (_logger.isLoggable(Level.INFO)) {
                            _logger.info("Set logger level to '" + arg + "'.");
                        }

                        if (arg.equals("0")) {
                            _mainLogger.setLevel(Level.OFF);
                        } else if (arg.equals("1")) {
                            _mainLogger.setLevel(Level.SEVERE);
                        } else if (arg.equals("2")) {
                            _mainLogger.setLevel(Level.WARNING);
                        } else if (arg.equals("3")) {
                            _mainLogger.setLevel(Level.INFO);
                        } else if (arg.equals("4")) {
                            _mainLogger.setLevel(Level.FINE);
                        } else if (arg.equals("5")) {
                            _mainLogger.setLevel(Level.ALL);
                        } else {
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

                    // Exit the application
                    App.exit(-1);
                    break;
            }
        }

        _logger.fine("Application arguments interpreted");
    }

    /** Show command arguments help */
    public static void showArgumentsHelp() {
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

        // Exit the application
        App.exit(0);
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
    protected boolean finish() {
        _logger.info("Default App.finish() handler called.");

        return true;
    }

    /**
     * Hook to handle operations when exiting application.
     * @see App#exit(int)
     */
    public void onFinish() {
        // Disconnect from SAMP Hub :
        SampManager.shutdown();

        // Close all HTTP connections (http client) :
        MultiThreadedHttpConnectionManager.shutdownAll();
    }

    /**
     * Exit the application :
     * - calls onFinish()
     * - System.exit(statusCode)
     * @param statusCode status code to return
     */
    public final static void exit(final int statusCode) {
        _logger.info("Killing the application.");

        try {
            final App application = App.getSharedInstance();

            if (application != null) {
                application.onFinish();
            }
        } finally {
            _sharedInstance = null;
            _applicationFrame = null;

            if (!_avoidSystemExit) {
                // anyway, exit :
                System.exit(statusCode);
            }
        }
    }

    /**
     * Define the  flag to avoid calls to System.exit() (JUnit)
     * @param flag true to avoid calls to System.exit()
     */
    public final static void setAvoidSystemExit(final boolean flag) {
        _avoidSystemExit = flag;
    }

    /** 
     * Describe the life cycle of the application
     */
    protected final void run() {
        // Show splash screen if we have to
        if (_showSplashScreen) {
            try {
                // Using invokeAndWait to be in sync with the main thread :
                SwingUtilities.invokeAndWait(new Runnable() {

                    /**
                     * Initializes Splash Screen in EDT
                     */
                    @Override
                    public void run() {
                        showSplashScreen();
                    }
                });

            } catch (InterruptedException ie) {
                // propagate the exception :
                throw new IllegalStateException("App.run : interrupted", ie);
            } catch (InvocationTargetException ite) {
                // propagate the internal exception :
                throw new IllegalStateException("App.run : exception", ite.getCause());
            }
        }

        // Delegate initialization to daughter class through abstract init() call
        init(_args);

        try {

            // Using invokeAndWait to be in sync with the main thread :
            SwingUtilities.invokeAndWait(new Runnable() {

                /**
                 * Initializes swing components in EDT
                 */
                @Override
                public void run() {
                    // If running under Mac OS X
                    if (SystemUtils.IS_OS_MAC_OSX) {
                        // Set application name :
                        // system properties must be set before using any Swing component:
                        // Hope nothing as already been done...
                        System.setProperty("com.apple.mrj.application.apple.menu.about.name", _applicationDataModel.getProgramName());
                    }

                    // Define the jframe associated to the application which will get the JmenuBar
                    final JFrame frame = getFrame();

                    // Use OSXAdapter on the frame
                    macOSXRegistration(frame);

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

        // Delegate execution to daughter class through abstract execute() call
        execute();

        // Indicate that the application is ready (visible)
        _applicationReady = true;

        // If any file argument exists, open that file using the registered open action :
        if (_fileArgument != null) {
            SwingUtilities.invokeLater(new Runnable() {

                /**
                 * Open the file using EDT :
                 */
                @Override
                public void run() {
                    _registrar.getOpenAction().actionPerformed(new ActionEvent(_registrar, 0, _fileArgument));
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
    public static String getLogOutput() {
        // Needed in order to write all logs in the ouput stream buffer
        if (_streamHandler != null) {
            _streamHandler.flush();
        }

        return _byteArrayOutputStream.toString();
    }

    /**
     * Show third party logging utility.
     */
    public static void showLogGui() {
        imx.loggui.LogMaster.startLogGui();
    }

    /** Show the splash screen */
    private void showSplashScreen() {
        if (_applicationDataModel != null) {
            _logger.fine("Show splash screen");

            // Instantiate the splash screen :
            _splashScreen = new SplashScreen();

            // Show the splash screen :
            _splashScreen.display();
        }
    }

    /**
     * Return ApplicationDataModel instance.
     *
     * @return ApplicationDataModel instance.
     */
    public static ApplicationDataModel getSharedApplicationDataModel() {
        return _applicationDataModel;
    }

    /**
     * Tell if the application is a beta version or not.
     * This flag is given searching one 'b' in the program version number.
     *
     * @return true if it is a beta, false otherwise.
     */
    public static boolean isBetaVersion() {
        if (_applicationDataModel != null) {
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
    public static boolean isAlphaVersion() {
        if (_applicationDataModel != null) {
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
    public static void setFrame(final JFrame frame) {
        _applicationFrame = frame;
    }

    /**
     * Return the application frame (singleton)
     *
     * @return application frame
     */
    public static JFrame getFrame() {
        if (_applicationFrame == null) {
            _applicationFrame = new JFrame();
        }
        return _applicationFrame;
    }

    /**
     * Show the application frame and bring it to front
     */
    public static void showFrameToFront() {
        final JFrame frame = getFrame();

        if (frame.isVisible()) {
            // ensure window is visible (not iconified):
            if (frame.getState() == Frame.ICONIFIED) {
                frame.setState(Frame.NORMAL);
            }
            frame.toFront();
        }
    }

    /**
     * Return the application frame panel
     *
     * @return application frame panel
     */
    public static Container getFramePanel() {
        return getFrame().getContentPane();
    }

    /**
     * Return App shared instance
     *
     * @return shared instance
     */
    public static App getSharedInstance() {
        return _sharedInstance;
    }

    /**
     * Return true if the Application is ready
     *
     * @return true if the Application is ready
     */
    public final static boolean isReady() {
        return _applicationReady;
    }

    /**
     * Get URL from resource filename.
     *
     * @param fileName name of searched file.
     *
     * @return resource file URL
     */
    public URL getURLFromResourceFilename(final String fileName) {
        // The class which is extended from App
        final Class<?> actualClass = getClass();

        // Its package
        final Package p = actualClass.getPackage();

        // the package name
        final String packageName = p.getName();

        // Replace '.' by '/' of package name
        final String packagePath = packageName.replace(".", "/");

        final String filePath = packagePath + "/resource/" + fileName;
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("filePath = '" + filePath + "'.");
        }

        // resolve file path:
        final URL fileURL = actualClass.getClassLoader().getResource(filePath);

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("fileURL = '" + fileURL + "'.");
        }

        return Urls.fixJarURL(fileURL);
    }

    /** Action to correctly handle file opening. */
    protected class OpenAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        OpenAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            // Disabled as this default implementation does nothing
            setEnabled(false);

            flagAsOpenAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("OpenAction", "actionPerformed");

            _logger.warning("No handler for default file opening.");
        }
    }

    /** Action to correctly handle operations before closing application. */
    protected class QuitAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        QuitAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Quit", "ctrl Q");

            flagAsQuitAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("QuitAction", "actionPerformed");

            _logger.fine("Should we kill the application ?");

            // If we are ready to finish application execution
            if (finish()) {
                _logger.fine("Application should be killed.");

                // Verify if we are authorized to kill the application or not
                if (_exitApplicationWhenClosed) {

                    // Exit the application
                    App.exit(0);

                } else {
                    _logger.fine("Application left opened as required.");
                }
            } else {
                _logger.fine("Application killing cancelled.");
            }
        }
    }

    /** Action to copy acknowledgement text to the clipboard. */
    protected class AcknowledgementAction extends RegisteredAction {

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
        AcknowledgementAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Copy Acknowledgement to Clipboard");
            _acknowledgement = _applicationDataModel.getAcknowledgment();

            // If the application does not provide an acknowledgement
            if (_acknowledgement == null) {
                // Generate one instead
                String appName = _applicationDataModel.getProgramName();
                String appURL = _applicationDataModel.getLinkValue();
                _acknowledgement = "This research has made use of the "
                        + "Jean-Marie Mariotti Center\\texttt{" + appName
                        + "} service\n\\footnote{Available at " + appURL + "}";
            }

            // If the application does not provide an ApplicationData.xml file,
            // the generic acknowledgement found in JMCS will be used instead.
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("AcknowledgementAction", "actionPerformed");

            StringSelection ss = new StringSelection(_acknowledgement);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

            final String delimiter = "---------------------------------------------------------------------------\n";
            final String message = "The previous message has already been copied to your clipboard, in order to\n"
                    + "let you conveniently paste it in your related publication.";
            final String windowTitle = _applicationDataModel.getProgramName()
                    + " Acknowledgment Note";
            final String windowContent = delimiter + _acknowledgement + "\n"
                    + delimiter + "\n" + message;

            MessagePane.showMessage(windowContent, windowTitle);
        }
    }

    /** Action to show hot news RSS feed. */
    protected class ShowHotNewsAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowHotNewsAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Hot News (RSS Feed)");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("ShowReleaseAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getHotNewsRSSFeedLinkValue());
        }
    }

    /** Action to show release. */
    protected class ShowReleaseAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowReleaseAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Release Notes");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("ShowReleaseAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getReleaseNotesLinkValue());
        }
    }

    /** Action to show FAQ. */
    protected class ShowFaqAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowFaqAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Frequently Asked Questions");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("ShowFaqAction", "actionPerformed");
            BrowserLauncher.openURL(_applicationDataModel.getFaqLinkValue());
        }
    }

    /** Action to show help. */
    protected class ShowHelpAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowHelpAction(String classPath, String fieldName) {
            super(classPath, fieldName, "User Manual");
            setEnabled(HelpView.isAvailable());

            // Set Icon
            String icon = "/fr/jmmc/jmcs/resource/help.png";
            this.putValue(SMALL_ICON,
                    new ImageIcon(Urls.fixJarURL(getClass().getResource(icon))));
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.entering("ShowHelpAction", "actionPerformed");
            HelpView.setVisible(true);
        }
    }

    /**
     * Generic registration with the Mac OS X application menu.
     *
     * Checks the platform, then attempts.
     *
     * @param frame application frame
     */
    public final void macOSXRegistration(final JFrame frame) {
        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {

            // Set application name :
            // system properties must be set before using any Swing component:
            // Hope nothing as already been done...
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", _applicationDataModel.getProgramName());

            // Set the menu bar under Mac OS X
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            final Class<?> osxAdapter = Introspection.getClass("fr.jmmc.jmcs.gui.OSXAdapter");

            if (osxAdapter == null) {
                // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                _logger.severe(
                        "This version of Mac OS X does not support the Apple EAWT. Application Menu handling has been disabled.");
            } else {
                final Method registerMethod = Introspection.getMethod(osxAdapter, "registerMacOSXApplication", new Class<?>[]{JFrame.class});

                if (registerMethod != null) {
                    Introspection.executeMethod(registerMethod, new Object[]{frame});
                }

                // This is slightly gross.  to reflectively access methods with boolean args,
                // use "boolean.class", then pass a Boolean object in as the arg, which apparently
                // gets converted for you by the reflection system.
                final Method prefsEnableMethod = Introspection.getMethod(osxAdapter, "enablePrefs", new Class<?>[]{boolean.class});

                if (prefsEnableMethod != null) {
                    Introspection.executeMethod(prefsEnableMethod, new Object[]{Boolean.TRUE});
                }
            }
        }
    }
}
/*___oOo___*/
