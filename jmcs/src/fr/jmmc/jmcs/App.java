/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.util.CommandLineUtils;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton that represents an application.
 *
 * In order to use functionalities provided by jMCS,
 * extend your application from this class and use:
 * @see Bootstrapper.launch(new YourApp(args), ...);
 *
 * @author Brice COLUCCI, Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class App {

    /** Class Logger */
    private static final Logger _logger = LoggerFactory.getLogger(App.class.getName());
    /** Singleton reference */
    private static App _instance;
    /** Main frame of the application (singleton) */
    private static JFrame _applicationFrame = null;
    // Members
    /** Command-line arguments */
    protected final String[] _args;
    /** Command-line argument meta data */
    private final Map<String, Boolean> _customArgumentsDefinition = new HashMap<String, Boolean>();
    /** Command-line custom help */
    private String _customHelp = null;
    /** Store the custom command line argument values (keyed by name) */
    private Map<String, String> _customArgumentValues = null;

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

    /**
     * @return App shared instance.
     */
    public static App getInstance() {
        return _instance;
    }

    final void ___internalStart() {
        // Interpret arguments
        _customArgumentValues = CommandLineUtils.interpretArguments(_args, _customArgumentsDefinition, _customHelp);
    }

    /**
     * @return command line arguments hash map (argument value keyed by argument name).
     */
    protected final Map<String, String> getCommandLineArguments() {
        return _customArgumentValues;
    }

    /**
     * Hook to override in your class to add custom command line argument(s) and help using:
     * @see  #addCustomCommandLineArgument(java.lang.String, boolean)
     * @see  #addCustomArgumentsHelp(java.lang.String, boolean)
     */
    protected void defineCustomCommandLineArgumentsAndHelp() {
        // noop
    }

    /** 
     * Add custom command line argument help.
     * @param help custom help text.
     */
    protected final void addCustomArgumentsHelp(final String help) {
        _customHelp = help;
    }

    /**
     * Add custom command line argument.
     * @param name option name.
     * @param hasArgument true if an argument is required, false otherwise.
     */
    protected final void addCustomCommandLineArgument(final String name, final boolean hasArgument) {
        _customArgumentsDefinition.put(name, hasArgument);
    }

    /**
     * Initialize services before the GUI
     */
    protected abstract void initServices();

    /**
     * Hook to override in your App, to initialize user interface in EDT.
     * @warning : The actions which are present in menu bar must be instantiated in this method.
     */
    protected abstract void setupGui();

    /**
     * Hook to override in your App, to declare SAMP capabilities (if any).
     */
    protected void declareInteroperability() {
        _logger.debug("Empty App.declareInteroperability() handler called.");
    }

    final void openCommandLineFile() {

        if ((_customArgumentValues == null) || (_customArgumentValues.size() == 0)) {
            return;
        }

        // If any file argument exists, open that file using the registered open action
        final String fileArgument = _customArgumentValues.get(CommandLineUtils.CLI_OPEN_KEY);
        if (fileArgument == null) {
            return;
        }

        SwingUtils.invokeLaterEDT(new Runnable() {
            /**
             * Open the file using EDT :
             */
            @Override
            public void run() {
                final ActionRegistrar actionRegistrar = ActionRegistrar.getInstance();
                final AbstractAction openAction = actionRegistrar.getOpenAction();
                openAction.actionPerformed(new ActionEvent(actionRegistrar, 0, fileArgument));
            }
        });
    }

    /**
     * Hook to override in your App, to execute application body.
     */
    protected abstract void execute();

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
     * @return the application frame (singleton).
     */
    public static JFrame getFrame() {
        if (_applicationFrame == null) {
            _applicationFrame = new JFrame();
        }
        return _applicationFrame;
    }

    /**
     * @return the application frame panel.
     */
    public static Container getFramePanel() {
        return getFrame().getContentPane();
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
     * Hook to override in your App, to return whether the application can be terminated or not.
     *
     * This method is automatically triggered when the application "Quit" menu is used.
     * Thus, you have a chance to do things like saves before the application dies.
     *
     * The default implementation lets the application silently quit without further ado.
     *
     * @warning This method should be overridden to handle quit as you intend to. In its default
     * behavior, all changes that occurred during application life will be lost.
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
     * This method is automatically triggered when the application "Quit" menu is used.
     * Thus, you have a chance to bypass SAMP warning message if needed.
     *
     * The default implementation asks the user if he really wants to kill the hub.
     *
     * @warning This method should be overridden to handle SAMP hub death as you intend to.
     * In its default behavior, the SAMP warning message will be shown to get user's advice.
     *
     * @return should return true if the SAMP hub should be silently killed, false otherwise
     * to ask for user permission.
     */
    public boolean shouldSilentlyKillSampHubOnQuit() {
        _logger.info("Default App.silentlyKillSampHubOnQuit() handler called.");
        return false;
    }

    /**
     * Hook to override in your App, to handle operations before exit time.
     */
    protected abstract void cleanup();

    static void ___internalSingletonCleanup() {
        _instance = null;
        _applicationFrame = null;
    }
}
/*___oOo___*/
