/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import ch.qos.logback.classic.Level;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.util.IntrospectionUtils;
import fr.jmmc.jmcs.util.logging.LogbackGui;
import fr.jmmc.jmcs.util.logging.LoggingService;
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
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton that represents an application.
 *
 * In order to use functionalities provided by jMCS,
 * extend your application from this class.
 *
 * @author Brice COLUCCI, Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class App {

    /** Class Logger */
    private static final Logger _logger = LoggerFactory.getLogger(App.class.getName());
    /** Singleton reference */
    private static App _instance;
    /** flag indicating if the application started properly and is ready (visible) */
    private static boolean _applicationReady = false;
    /** Main frame of the application (singleton) */
    private static JFrame _applicationFrame = null;
    // Members
    /** Store a proxy to the shared ActionRegistrar facility */
    private final ActionRegistrar _actionRegistrar = ActionRegistrar.getInstance();
    /** Command-line argument meta data */
    private final List<LongOpt> _longOpts = new ArrayList<LongOpt>();
    /** Temporary store the command line arguments (long opt = value) */
    private Map<String, String> _cliArguments = null;
    /** Temporary store the file name argument for the open action */
    private String _fileArgument = null;
    /** Command-line arguments */
    protected final String[] _args;

    /**
     * Static jMCS environment startup.
     */
    static {
        Bootstrapper.bootstrap();
    }

    /**
     * Creates a new App object.
     *
     * @param args command-line arguments.
     */
    protected App(String[] args) {
        _args = args;
        _logger.debug("App object instantiated and logger created.");
    }

    final void ___internalSingletonInitialization() {
        // Set shared instance
        _instance = this;
    }

    final void ___internalStart() {
        // Interpret arguments
        interpretArguments(_args);

        // Build Acknowledgment, ShowRelease and ShowHelp Actions
        // (the creation must be done after applicationModel instanciation)
        _actionRegistrar.createAllInternalActions();
    }

    /**
     * Return command line arguments.
     * @return command line arguments. 
     */
    protected final Map<String, String> getCommandLineArguments() {
        return _cliArguments;
    }

    /**
     * Hook to override in your class to add custom command line argument(s) using:
     * @see  #addCustomCommandLineArgument(java.lang.String, boolean)
     */
    protected void addCustomCommandLineArguments() {
        // noop
    }

    /** 
     * Hook to override in your class to show custom command line argument help.
     */
    protected void showCustomArgumentsHelp() {
        // noop
    }

    /**
     * Add custom command line argument.
     * @param name option name.
     * @param hasArgument true if an argument is required, false otherwise.
     */
    protected final void addCustomCommandLineArgument(final String name, final boolean hasArgument) {
        _longOpts.add(new LongOpt(name, (hasArgument) ? LongOpt.REQUIRED_ARGUMENT : LongOpt.NO_ARGUMENT, null, 'c')); // 'c' means custom
    }

    /**
     * Interpret command line arguments.
     * @param args arguments.
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

        final ApplicationDescription applicationDescription = ApplicationDescription.getInstance();

        // Instantiate the getopt object
        final Getopt getOpt = new Getopt(applicationDescription.getProgramName(), args, "hv:", longOptArray, true);

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
                    System.out.println(applicationDescription.getProgramNameWithVersion());

                    // Exit the application
                    Bootstrapper.stopApp(0);
                    break;

                // Display the LogGUI panel
                case 2:
                    LogbackGui.showLogConsole();
                    break;

                // Open the given file
                case 3:
                    // get the file path argument and store it temporarly :
                    _fileArgument = getOpt.getOptarg();
                    _logger.info("Should open '{}'.", _fileArgument);
                    break;

                // Set the logger level
                case 'v':
                    arg = getOpt.getOptarg();

                    if (arg != null) {
                        _logger.info("Set logger level to '{}'.", arg);

                        final ch.qos.logback.classic.Logger jmmcLogger = LoggingService.getJmmcLogger();
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
                    Bootstrapper.stopApp(-1);
                    break;
            }
        }

        _logger.debug("Application arguments interpreted");
    }

    /** Show command arguments help. */
    private void showArgumentsHelp() {
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
        Bootstrapper.stopApp(0);
    }

    /**
     * Initialize services before the GUI
     */
    protected abstract void initServices();

    /**
     * Hook to override in your App, to initialize user interface in EDT.
     *
     * The actions which are present in menu bar must be instantiated in this method.
     */
    protected abstract void setupGui();

    /**
     * Hook to override in your App, to declare SAMP capabilities (if any).
     */
    protected void declareInteroperability() {
        _logger.debug("Empty App.declareInteroperability() handler called.");
    }

    /**
     * Hook to override in your App, to execute application body.
     */
    protected abstract void execute();
    /**
     * Describe the life cycle of the application.
     */
    final void ___internalRun() {

        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            /**
             * Initializes Splash Screen in EDT
             */
            @Override
            public void run() {

                // If running under Mac OS X
                if (SystemUtils.IS_OS_MAC_OSX) {
                    // Set application name :
                    // system properties must be set before using any Swing component:
                    // Hope nothing as already been done...
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", ApplicationDescription.getInstance().getProgramName());
                }

                // Delegate initialization to daughter class through abstract setupGui() call
                setupGui();

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

        // If any file argument exists, open that file using the registered open action
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

        ResizableTextViewFactory.showUnsupportedJdkWarning();
    }

    /**
     * Return the application frame (singleton).
     * @return application frame.
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
     * Concrete applications must be later re-factored to initialize correctly the GUI using getFrame().
     *
     * @param frame application frame.
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
                InternalActionFactory.quitAction().actionPerformed(null);
            }
        });
    }

    /**
     * Show the application frame and bring it to front.
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
     * Return the application frame panel.
     * @return application frame panel.
     */
    public static Container getFramePanel() {
        return getFrame().getContentPane();
    }

    /**
     * Return App shared instance.
     * @return shared instance.
     */
    public static App getInstance() {
        return _instance;
    }

    /**
     * Return true if the Application is ready.
     * @return true if the Application is ready.
     */
    public static boolean isReady() {
        return _applicationReady;
    }

    /**
     * Hook to override in your App, to return whether the application can be terminated or not.
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
     * Hook to override in your App, to handle SAMP hub destiny before closing application.
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
        _logger.info("Empty App.silentlyKillSampHubOnQuit() handler called.");

        return false;
    }

    /**
     * Hook to override in your App, to handle operations before exit time.
     * @see App#exit(int)
     */
    protected abstract void cleanup();


    static void ___internalSingletonCleanup() {
        _instance = null;
        _applicationFrame = null;
    }

    /**
     * Generic registration with the Mac OS X application menu (if needed).
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
