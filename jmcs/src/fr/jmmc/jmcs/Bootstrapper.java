/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import ch.qos.logback.classic.Logger;
import com.apple.eawt.QuitResponse;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.SplashScreen;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.task.TaskSwingWorkerExecutor;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import fr.jmmc.jmcs.util.logging.LoggingService;
import java.awt.event.ActionEvent;
import java.util.Date;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.ivoa.util.runner.LocalLauncher;

/**
 * This class provides jMCS initialization (logs, SWING, network ...)
 *
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class Bootstrapper {

    /** Flag to avoid reentrance in launch sequence */
    private static boolean _staticBootstrapDone = false;
    /** JMMC Logger */
    private final static Logger _jmmcLogger = LoggingService.getJmmcLogger();
    /** Store whether application should be quit when main frame close box clicked. */
    private static boolean _exitApplicationWhenClosed;
    /** Flag to prevent calls to System.exit() */
    private static boolean _avoidSystemExit = false;

    /**
     * Static Logger initialization and Network settings
     */
    static {
        Bootstrapper.bootstrap();
    }

    /**
     * Static minimal service initialization: logger, swing, network ...
     * @throws IllegalStateException if any exception occurs during initialization.
     * @return true if the initialization sequence succeeds, false otherwise.
     */
    static boolean bootstrap() throws IllegalStateException {

        if (_staticBootstrapDone) {
            return true;
        }

        // Start the application log singleton:
        LoggingService.getInstance();
        // Get the jmmc logger:
        _jmmcLogger.info("Application log created at {}. Current level is {}.", new Date(), _jmmcLogger.getEffectiveLevel());

        // Define swing settings (laf, locale...) before any Swing usage if not called at the first line of the main method:
        SwingSettings.setup();

        // Define default network settings:
        // note: settings must be set before using any URLConnection (loadApplicationData)
        NetworkSettings.defineDefaults();

        _jmmcLogger.info("Application bootstrap done.");

        // set reentrance flag
        _staticBootstrapDone = true;
        return true;
    }

    /**
     * Launch an application that will:
     * - execute directly after services initialization and GUI setup;
     * - trap and properly exit on main frame close button click;
     * - show a splash screen during bootstrap sequence.
     * @param application the App object to launch.
     * @return true if all went well, false otherwise.
     * @throws IllegalStateException in case something went really wrong.
     */
    public static boolean launchApp(final App application) throws IllegalStateException {
        return launchApp(application, false);
    }

    /**
     * Launch an application that will:
     * - wait (or not) after services initialization and GUI setup;
     * - trap and properly exit on main frame close button click;
     * - show a splash screen during bootstrap sequence.
     * @param application the App object to launch.
     * @param waitBeforeExecution if true, do not launch App.execute() automatically.
     * @return true if all went well, false otherwise.
     * @throws IllegalStateException in case something went really wrong.
     */
    public static boolean launchApp(final App application, boolean waitBeforeExecution) throws IllegalStateException {
        return launchApp(application, waitBeforeExecution, true);
    }

    /**
     * Launch an application that will:
     * - wait (or not) after services initialization and GUI setup;
     * - trap and properly exit (or not) on main frame close button click;
     * - show a splash screen during bootstrap sequence.
     * @param application the App object to launch.
     * @param waitBeforeExecution if true, do not launch App.execute() automatically.
     * @param exitWhenClosed if true, the application will close when exit method is called.
     * @return true if all went well, false otherwise.
     * @throws IllegalStateException in case something went really wrong.
     */
    public static boolean launchApp(final App application, boolean waitBeforeExecution, boolean exitWhenClosed) throws IllegalStateException {
        return launchApp(application, waitBeforeExecution, exitWhenClosed, CommonPreferences.getInstance().getPreferenceAsBoolean(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN));
    }

    /**
     * Start the application properly:
     * - starts all critical jMCS services;
     * - calls your App.initServices() method to start your services;
     * - calls your App.setupGui() method to setup your graphical interfaces (in EDT);
     * - calls your App.execute() method.
     *
     * @param application your application to start.
     * @param waitBeforeExecution if true, do not launch App.execute() automatically.
     * @param exitWhenClosed if true, the application will close when exit method is called.
     * @param shouldShowSplashScreen show startup splash screen if true, nothing otherwise.
     *
     * @return true on success, false otherwise.
     * @throws IllegalStateException
     */
    public static boolean launchApp(final App application, final boolean waitBeforeExecution, final boolean exitWhenClosed, final boolean shouldShowSplashScreen) throws IllegalStateException {

        final long startTime = System.nanoTime();
        boolean launchDone = false;

        _exitApplicationWhenClosed = exitWhenClosed;
        application.___internalSingletonInitialization();

        // Load jMCS and application data models
        ApplicationDescription.init();
        _jmmcLogger.debug("Application data loaded.");

        try {
            application.___internalStart();
            application.initServices();
            SplashScreen.display(shouldShowSplashScreen);
            application.___internalRun();
            launchDone = true;
        } catch (Throwable th) {
            // Show the feedback report (modal)
            SplashScreen.close();
            MessagePane.showErrorMessage("An error occured while initializing the application");
            FeedbackReport.openDialog(true, th);
        } finally {
            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            _jmmcLogger.info("Application startup done (duration = {} ms).", elapsedTime);
        }

        return launchDone;
    }

    /**
     * @return true if the application should exit when frame is closed, false otherwise.
     */
    public static boolean shouldExitAppWhenFrameClosed() {
        return _exitApplicationWhenClosed;
    }

    /**
     * Define the flag to avoid calls to System.exit().
     * @param flag true to avoid calls to System.exit()
     */
    public static void disableSystemExit(final boolean flag) {
        _avoidSystemExit = flag;
    }

    /**
     * Quit the application properly:
     * - warn user of SAMP shutdown if needed;
     * - prompt user of unsaved data loss;
     * - stops application if everything is ok.
     * @param evt the triggering event if any, null otherwise.
     */
    public static void quitApp(ActionEvent evt) {

        // Mac OS X Quit action handler
        final QuitResponse response;
        if (evt != null && evt.getSource() instanceof QuitResponse) {
            response = (QuitResponse) evt.getSource();
        } else {
            response = null;
        }

        // Check if user is OK to kill SAMP hub (if any)
        if (!SampManager.getInstance().allowHubKilling()) {
            _jmmcLogger.debug("SAMP cancelled application kill.");
            // Otherwise cancel quit
            if (response != null) {
                response.cancelQuit();
            }
            return;
        }

        // If we are ready to stop application execution
        if (App.getInstance().canBeTerminatedNow()) {
            _jmmcLogger.debug("Application should be killed.");

            // Verify if we are authorized to kill the application or not
            if (Bootstrapper.shouldExitAppWhenFrameClosed()) {

                // Max OS X quit
                if (response != null) {
                    Bootstrapper.disableSystemExit(true);
                }

                // Exit the application
                Bootstrapper.stopApp(0);

                // Max OS X quit
                if (response != null) {
                    response.performQuit();
                }
            } else {
                _jmmcLogger.debug("Application left opened as required.");
            }
        } else {
            _jmmcLogger.debug("Application killing cancelled.");
        }
        if (response != null) {
            response.cancelQuit();
        }
    }

    /**
     * Stop the application properly without user feedback:
     * - calls your App.cleanup() method;
     * - stops all critical jMCS services;
     * - System.exit(statusCode) if so.
     * @param statusCode status code to return
     */
    public static void stopApp(final int statusCode) {

        _jmmcLogger.info("Stopping the application.");
        final App application = App.getInstance();

        try {
            if (application != null) {
                application.cleanup();
                ___internalStop();
            }
        } finally {
            App.___internalSingletonCleanup();
            if (!_avoidSystemExit) {
                _jmmcLogger.info("Exiting with status code '{}'.", statusCode);
                System.exit(statusCode);
            }
        }
    }

    private static void ___internalStop() {

        // Stop the job runner
        LocalLauncher.shutdown();

        // Stop the task executor
        TaskSwingWorkerExecutor.shutdown();

        // Stop the parallel job executor
        ParallelJobExecutor.shutdown();

        // Disconnect from SAMP Hub
        SampManager.shutdown();

        // Close all HTTP connections (http client)
        MultiThreadedHttpConnectionManager.shutdownAll();
    }

    /** Private constructor */
    private Bootstrapper() {
        // no-op
    }
}
