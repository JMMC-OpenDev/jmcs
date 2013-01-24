/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import ch.qos.logback.classic.Level;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.SplashScreen;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.util.IntrospectionUtils;
import fr.jmmc.jmcs.util.logging.ApplicationLogSingleton;
import fr.jmmc.jmcs.util.logging.LogbackGui;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <b>ApplicationDescription</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to access to these informations.
 *
 * @author Brice COLUCCI, Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class App {

    /** Class Logger */
    private static final Logger _logger = LoggerFactory.getLogger(App.class.getName());
    /** flag to avoid calls to System.exit() (JUnit) */
    private static boolean _avoidSystemExit = false;
    /** Singleton reference */
    private static App _sharedInstance;
    /** flag indicating if the application started properly and is ready (visible) */
    private static boolean _applicationReady = false;
    /** Main frame of the application (singleton) */
    private static JFrame _applicationFrame = null;
    /** If it's true, exit the application after the exit method */
    private static boolean _exitApplicationWhenClosed = true;
    // Members
    /** Store a proxy to the shared ActionRegistrar facility */
    private final ActionRegistrar _actionRegistrar = ActionRegistrar.getInstance();
    /** Show the splash screen ? */
    private boolean _showSplashScreen = true;
    /** Splash screen */
    private SplashScreen _splashScreen = null;
    /** Command-line argument meta data */
    private final List<LongOpt> _longOpts = new ArrayList<LongOpt>();
    /** Temporary store the command line arguments (long opt = value) */
    private Map<String, String> _cliArguments = null;
    /** Temporary store the file name argument for the open action */
    private String _fileArgument = null;
    /**  */
    protected final String[] _args;

    /**
     * Static Logger initialization and Network settings
     */
    static {
        Bootstrapper.bootstrap();
    }

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
        // Start application with splashscreen
        this(args, waitBeforeExecution, exitWhenClosed, CommonPreferences.getInstance().getPreferenceAsBoolean(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN));
    }

    /**
     * Constructor with possibility to specify if the application should be
     * stopped when the exit method is called
     *
     * @param args command-line arguments
     * @param waitBeforeExecution if true, do not launch run() automatically
     * @param exitWhenClosed if true, the application will close when exit method is called
     * @param shouldShowSplashScreen show startup splash screen if true, nothing otherwise
     */
    protected App(String[] args, boolean waitBeforeExecution, boolean exitWhenClosed, boolean shouldShowSplashScreen) {
        _args = args;
        // Attributes affectations
        _exitApplicationWhenClosed = exitWhenClosed;
        // Check in common preferences whether startup splashscreen should be shown or not
        _showSplashScreen = shouldShowSplashScreen;
        // Set shared instance
        _sharedInstance = this;
        _logger.debug("App object instantiated and logger created.");
    }

    final void start() {
        // Interpret arguments
        interpretArguments(_args);

        // Build Acknowledgment, ShowRelease and ShowHelp Actions
        // (the creation must be done after applicationModel instanciation)
        _actionRegistrar.createAllInternalActions();
    }

    /**
     * Return command line arguments
     * @return command line arguments 
     */
    protected final Map<String, String> getCommandLineArguments() {
        return _cliArguments;
    }

    /**
     * To be override by child classes to add custom command line argument(s) using:
     * @see  #addCustomCommandLineArgument(java.lang.String, boolean)
     */
    protected void addCustomCommandLineArguments() {
        // noop
    }

    /** 
     * To be override by child classes to show custom command line argument help 
     */
    protected void showCustomArgumentsHelp() {
        // noop
    }

    /**
     * Add custom command line argument
     * @param name option name
     * @param hasArgument true if an argument is required; false otherwise 
     */
    protected final void addCustomCommandLineArgument(final String name, final boolean hasArgument) {
        _longOpts.add(new LongOpt(name, (hasArgument) ? LongOpt.REQUIRED_ARGUMENT : LongOpt.NO_ARGUMENT, null, 'c')); // 'c' means custom
    }

    /**
     * Interpret command line arguments
     *
     * @param args arguments
     */
    private void interpretArguments(final String[] args) {
        // List received arguments
        if (_logger.isDebugEnabled()) {
            for (int i = 0; i < args.length; i++) {
                _logger.debug("args[{}] = '{}'.", i, args[i]);
            }
        }

        // Just leave method if no argument has been given
        if (args == null) {
            return;
        }

        // Define default arguments (help & version):
        _longOpts.clear();
        _longOpts.add(new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'));
        _longOpts.add(new LongOpt("version", LongOpt.NO_ARGUMENT, null, 1));
        _longOpts.add(new LongOpt("loggui", LongOpt.NO_ARGUMENT, null, 2));
        _longOpts.add(new LongOpt("open", LongOpt.REQUIRED_ARGUMENT, null, 3));

        // Application needs custom arguments:
        addCustomCommandLineArguments();

        final LongOpt[] longOptArray = new LongOpt[_longOpts.size()];
        _longOpts.toArray(longOptArray);

        // Instantiate the getopt object
        final Getopt getOpt = new Getopt(ApplicationDescription.getInstance().getProgramName(), args, "hv:", longOptArray, true);

        int c; // argument key
        String arg; // argument value

        // While there is a argument key
        while ((c = getOpt.getopt()) != -1) {
            _logger.debug("opt = {}", c);

            switch (c) {
                // Show the arguments help
                case 'h':
                    showArgumentsHelp();
                    break;

                // Show the name and the version of the program
                case 1:
                    // Show the application name on the shell
                    System.out.println(ApplicationDescription.getInstance().getProgramName() + " v" + ApplicationDescription.getInstance().getProgramVersion());

                    // Exit the application
                    App.exit(0);
                    break;

                // Display the LogGUI panel
                case 2:
                    showLogConsole();
                    break;

                // Open the given file
                case 3:
                    // get the file path argument and store it temporarly :
                    this._fileArgument = getOpt.getOptarg();

                    _logger.info("Should open '{}'.", this._fileArgument);
                    break;

                // Set the logger level
                case 'v':
                    arg = getOpt.getOptarg();

                    if (arg != null) {
                        _logger.info("Set logger level to '{}'.", arg);

                        final ch.qos.logback.classic.Logger jmmcLogger = Bootstrapper.getJmmcLogger();
                        if (arg.equals("0")) {
                            jmmcLogger.setLevel(Level.OFF);
                        } else if (arg.equals("1")) {
                            jmmcLogger.setLevel(Level.ERROR);
                        } else if (arg.equals("2")) {
                            jmmcLogger.setLevel(Level.WARN);
                        } else if (arg.equals("3")) {
                            jmmcLogger.setLevel(Level.INFO);
                        } else if (arg.equals("4")) {
                            jmmcLogger.setLevel(Level.DEBUG);
                        } else if (arg.equals("5")) {
                            jmmcLogger.setLevel(Level.ALL);
                        } else {
                            showArgumentsHelp();
                        }
                    }
                    break;

                // Show the arguments help
                case '?':
                    showArgumentsHelp();
                    break;

                case 'c':
                    // custom argument case:
                    if (_cliArguments == null) {
                        _cliArguments = new LinkedHashMap<String, String>();
                    }
                    _cliArguments.put(_longOpts.get(getOpt.getLongind()).getName(), (getOpt.getOptarg() != null) ? getOpt.getOptarg() : "");
                    break;

                default:
                    System.out.println("Unknow command");

                    // Exit the application
                    App.exit(-1);
                    break;
            }
        }

        _logger.debug("Application arguments interpreted");
    }

    /** Show command arguments help */
    protected final void showArgumentsHelp() {
        System.out.println("------------- Arguments help --------------------------------------------");
        System.out.println("| Key          Value           Description                              |");
        System.out.println("|-----------------------------------------------------------------------|");
        System.out.println("| [-h]                         Show the options help                    |");
        System.out.println("| [-loggui]                    Show the logging tool                    |");
        System.out.println("| [-v]         [0|1|2|3|4|5]   Define console logging level             |");
        System.out.println("| [-version]                   Show application name and version        |");
        System.out.println("| [-h|-help]                   Show arguments help                      |");
        System.out.println("|-----------------------------------------------------------------------|");

        showCustomArgumentsHelp();

        System.out.println("LEVEL : 0=OFF, 1=SEVERE, 2=WARNING, 3=INFO, 4=FINE, 5=ALL\n");

        // Exit the application
        App.exit(0);
    }

    /**
     * Initialize application objects
     *
     * The actions which are present in menu bar must be instantiated in this method.
     */
    protected abstract void init();

    /**
     * Prepare interoperability (SAMP message handlers)
     */
    protected void declareInteroperability() {
        _logger.debug("Default App.declareInteroperability() handler called.");
    }

    /**
     * Execute application body
     */
    protected abstract void execute();

    /**
     * Hook to return whether the application can be terminated or not.
     *
     * This method is automatically triggered when the application "Quit" menu
     * has been used. Thus, you have a chance to do things like saves before the
     * application really quits.
     *
     * The default implementation lets the application silently quit without further ado.
     *
     * @warning This method should be overridden to handle quit as you intend to.
     * In its default behavior, all changes that occurred during application life will be lost.
     *
     * @return should return true if the application can exit, or false to cancel exit.
     */
    public boolean canBeTerminatedNow() {
        _logger.info("Default App.canBeTerminatedNow() handler called.");
        return true;
    }

    /**
     * @return true if should exit when closed, false otherwise.
     */
    public boolean shouldExitWhenClosed() {
        return _exitApplicationWhenClosed;
    }

    /**
     * Hook to handle SAMP hub destiny before closing application.
     *
     * This method is automatically triggered when the application "Quit" menu
     * has been used. Thus, you have a chance to bypass SAMP warning message.
     *
     * The default implementation asks the user if he really wants to shutdown hub.
     *
     * @warning This method should be overridden to handle SAMP hub die as you intend to.
     * In its default behavior, the SAMP warning message will be shown.
     *
     * @return should return true if the SAMP hub should be silently killed, false otherwise
     * to ask user permission.
     */
    public boolean shouldSilentlyKillSampHubOnQuit() {
        _logger.info("Default App.silentlyKillSampHubOnQuit() handler called.");

        return false;
    }

    /**
     * Hook to handle operations when at exit time.
     * @see App#exit(int)
     */
    public void cleanup() {
        // Disconnect from SAMP Hub
        SampManager.shutdown();

        // Close all HTTP connections (http client)
        MultiThreadedHttpConnectionManager.shutdownAll();
    }

    /**
     * Exit the application :
     * - calls onFinish()
     * - System.exit(statusCode)
     * @param statusCode status code to return
     */
    public static void exit(final int statusCode) {
        _logger.info("Killing the application.");

        try {
            final App application = App.getSharedInstance();

            if (application != null) {
                application.cleanup();
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
    public static void setAvoidSystemExit(final boolean flag) {
        _avoidSystemExit = flag;
    }

    /**
     * Callback on exit.
     */
    public static void quit() {
        InternalActionFactory.quitAction().actionPerformed(null);
    }

    /**
     * Describe the life cycle of the application
     */
    final void run() {

        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            // Set application name :
            // system properties must be set before using any Swing component:
            // Hope nothing as already been done...
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", ApplicationDescription.getInstance().getProgramName());
        }

        // Show splash screen if we have to
        if (_showSplashScreen) {
            // Using invokeAndWait to be in sync with this thread :
            // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
            SwingUtils.invokeAndWaitEDT(new Runnable() {
                /**
                 * Initializes Splash Screen in EDT
                 */
                @Override
                public void run() {
                    showSplashScreen();
                }
            });
        }

        // Delegate initialization to daughter class through abstract init() call
        init();

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            /**
             * Initializes swing components in EDT
             */
            @Override
            public void run() {
                // Initialize SampManager as needed by MainMenuBar:
                SampManager.getInstance();

                // declare SAMP message handlers first:
                declareInteroperability();

                // Perform defered action initialization (SAMP-related actions)
                _actionRegistrar.performDeferedInitialization();

                // Define the jframe associated to the application which will get the JmenuBar
                final JFrame frame = getFrame();

                // Use OSXAdapter on the frame
                macOSXRegistration(frame);

                // create menus including the Interop menu (SAMP required)
                frame.setJMenuBar(new MainMenuBar());

                // Set application frame common properties
                frame.pack();
            }
        });

        // Delegate execution to daughter class through abstract execute() call
        execute();

        // Indicate that the application is ready (visible)
        _applicationReady = true;

        // If any file argument exists, open that file using the registered open action :
        if (_fileArgument != null) {
            SwingUtils.invokeLaterEDT(new Runnable() {
                /**
                 * Open the file using EDT :
                 */
                @Override
                public void run() {
                    _actionRegistrar.getOpenAction().actionPerformed(new ActionEvent(_actionRegistrar, 0, _fileArgument));
                    // clear :
                    _fileArgument = null;
                }
            });
        }

        SwingUtils.invokeLaterEDT(new Runnable() {
            /**
             * Display warning if OpenJDK detected:
             */
            @Override
            public void run() {

                final String jvmName = System.getProperty("java.vm.name");
                final String jvmVendor = System.getProperty("java.vm.vendor");
                final String jvmVersion = System.getProperty("java.vm.version");
                final String jvmHome = SystemUtils.getJavaHome().getAbsolutePath();

                boolean shouldWarn = false;
                String message = "<HTML><BODY>";
                if (jvmName != null && jvmName.toLowerCase().contains("openjdk")) {
                    _logger.warn("Detected OpenJDK runtime environment.");
                    shouldWarn = true;
                    message += "<FONT COLOR='RED'>WARNING</FONT> : ";
                    message += "Your Java Virtual Machine is an OpenJDK JVM, which has known bugs (SWING look and feel, fonts, PDF issues...) on several Linux distributions."
                            + "<BR/><BR/>";
                }

                if (SystemUtils.IS_JAVA_1_5) {
                    _logger.warn("Detected JDK 1.5 runtime environment.");
                    shouldWarn = true;
                    message += "<FONT COLOR='RED'>WARNING</FONT> : ";
                    message += "Your Java Virtual Machine is of version 1.5, which has several limitations and is not maintained anymore security-wise."
                            + "<BR/><BR/>";
                }

                if (shouldWarn) {
                    message += "<BR/>"
                            + "<B>JMMC strongly recommends</B> Sun Java Runtime Environments version 1.6 or newer, available at:"
                            + "<BR/><BR/>"
                            + "<CENTER><A HREF='http://java.sun.com/javase/downloads/'>http://java.sun.com/javase/downloads/</A></CENTER>"
                            + "<BR/><BR/>"
                            + "<I>Your current JVM Information :</I><BR/>"
                            + "<TT>"
                            + "java.vm.name = '" + jvmName + "'<BR/>"
                            + "java.vm.vendor = '" + jvmVendor + "'<BR/>"
                            + "java.vm.version = '" + jvmVersion + "'<BR/>"
                            + "Java Home :<BR/>'" + jvmHome + "'"
                            + "</TT>";
                    message += "</BODY></HTML>";

                    ResizableTextViewFactory.createHtmlWindow(message, "Deprecated Java environment detected !", true);
                }
            }
        });
    }

    /**
     * Return true if there is a file name argument for the open action (during startup)
     * @return true if there is a file name argument for the open action
     */
    protected final boolean hasFileArgument() {
        return _fileArgument != null;
    }

    /**
     * Show the logging utility and displays the application log
     */
    public static void showLogConsole() {
        showLogConsole(ApplicationLogSingleton.JMMC_APP_LOG);
    }

    /**
     * Show the logging utility and displays the log corresponding to the given logger path
     * @param loggerPath logger path
     */
    public static void showLogConsole(final String loggerPath) {
        LogbackGui.showWindow(App.getFrame(), ApplicationDescription.getInstance().getProgramName() + " Log Console", loggerPath);
    }

    /** Show the splash screen */
    void showSplashScreen() {
        _logger.debug("Show splash screen");

        // Instantiate the splash screen :
        _splashScreen = new SplashScreen();

        // Show the splash screen :
        _splashScreen.display();
    }

    /** Show the splash screen */
    void hideSplashScreen() {
        // In order to see the error window
        if (_splashScreen != null) {
            if (_splashScreen.isVisible()) {
                _splashScreen.setVisible(false);
            }
            // cleanup (helps GC):
            _splashScreen = null;
        }
    }

    /**
     * Tell if the application is a beta version or not.
     * This flag is given searching one 'b' in the program version number.
     *
     * @return true if it is a beta, false otherwise.
     */
    public static boolean isBetaVersion() {
        return ApplicationDescription.getInstance().getProgramVersion().contains("b");
    }

    /**
     * Tell if the application is an alpha version or not.
     * This flag is given searching one 'a' in the program version number.
     * If one b is present the version is considered beta.
     *
     * @return true if it is a alpha, false otherwise.
     */
    public static boolean isAlphaVersion() {
        final String v = ApplicationDescription.getInstance().getProgramVersion();
        if (v.contains("b")) {
            return false;
        }
        return v.contains("a");
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
     * Define the application frame (singleton).
     *
     * TODO : workaround to let App create the frame (getFrame)...
     * Concrete applications must be later re-factored to initialize correctly the GUI using getFrame()
     *
     * @param frame application frame
     */
    public static void setFrame(final JFrame frame) {
        _applicationFrame = frame;

        // previous adapter manages the windowClosing(event) :
        _applicationFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Properly quit the application when main window close button is clicked
        _applicationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                // Callback on exit
                App.quit();
            }
        });

    }

    /**
     * Show the application frame and bring it to front
     */
    public static void showFrameToFront() {
        final JFrame frame = getFrame();

        // ensure window is visible (not iconified):
        if (frame.getState() == Frame.ICONIFIED) {
            frame.setState(Frame.NORMAL);
        }

        // force the frame to be visible and bring it to front
        frame.setVisible(true);
        frame.toFront();
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
    public static boolean isReady() {
        return _applicationReady;
    }

    /**
     * Generic registration with the Mac OS X application menu.
     *
     * Checks the platform, then attempts.
     *
     * @param frame application frame
     */
    private void macOSXRegistration(final JFrame frame) {
        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {

            // Set the menu bar under Mac OS X
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            final Class<?> osxAdapter = IntrospectionUtils.getClass("fr.jmmc.jmcs.gui.util.MacOSXAdapter");

            if (osxAdapter == null) {
                // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                _logger.error("This version of Mac OS X does not support the Apple EAWT. Application Menu handling has been disabled.");
            } else {
                final Method registerMethod = IntrospectionUtils.getMethod(osxAdapter, "registerMacOSXApplication", new Class<?>[]{JFrame.class});

                if (registerMethod != null) {
                    IntrospectionUtils.executeMethod(registerMethod, new Object[]{frame});
                }
            }
        }
    }
}
/*___oOo___*/
