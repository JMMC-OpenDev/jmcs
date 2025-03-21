/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs;

import fr.jmmc.jmcs.App.ApplicationState;
import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.SessionSettingsPreferences;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.MainMenuBar;
import fr.jmmc.jmcs.gui.SplashScreen;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.task.TaskSwingWorkerExecutor;
import fr.jmmc.jmcs.gui.util.MacOSXAdapter;
import fr.jmmc.jmcs.gui.util.MacOSXQuitCallback;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.jmcs.logging.LoggingService;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.service.XmlFactory;
import fr.jmmc.jmcs.util.IntrospectionUtils;
import fr.jmmc.jmcs.util.JVMUtils;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import fr.jmmc.jmcs.util.runner.LocalLauncher;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.JFrame;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;

/**
 * This class ordinate App life-cycle.
 *
 * Starting with jMCS initialization (logs, SWING, network ...);
 * Followed in this order:
 * - App.initServices()
 * - App.setupGui()
 * - App.execute()
 * - App.cleanup()
 *
 * @see App
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class Bootstrapper {

    /** JMMC Logger */
    private final static Logger _jmmcLogger = LoggingService.getJmmcLogger();
    /** User defined Locale before setting Locale.US */
    private static Locale _userLocale = null;
    /** User defined time zone before setting GMT */
    private static TimeZone _userTimeZone = null;
    /** Flag to avoid reentrance in launch sequence */
    private static boolean _staticBootstrapDone = false;
    /** Store whether application should be quit when main frame close box clicked. */
    private static boolean _exitApplicationWhenClosed = false;
    /** Flag to prevent calls to System.exit() */
    private static boolean _avoidSystemExit = false;
    /** Flag indicating if the application started properly and is ready (visible) */
    private static ApplicationState _applicationState = ApplicationState.JAVA_LIMB;
    /** The application  instance */
    private static App _application = null;
    /** Flag to indicate headless mode */
    private static int _isHeadless = -1;
    /** Flag to indicate if the MAC OS X integration was done successfully */
    private static boolean macIntegrationDone = false;

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
        // Avoid reentrance
        if (_staticBootstrapDone) {
            return true;
        }

        // Disable security checks
        disableSecurityManager();

        // Set System properties
        setSystemProperties();

        // Initialize Locale.US
        initializeLocale();

        // Start the application log singleton
        LoggingService.getInstance();
        _jmmcLogger.info("jMCS log created at {}. Current level is {}.", new Date(),
                LoggingService.getLoggerEffectiveLevel(_jmmcLogger));
        _jmmcLogger.info("jMCS environment bootstrapping...");
        setState(ApplicationState.ENV_BOOTSTRAP);

        // Early load common preferences (jmcs):
        CommonPreferences.getInstance();

        // Define swing settings (laf, defaults...) before any Swing usage
        SwingSettings.setup();

        // Define default network settings
        NetworkSettings.defineDefaults();

        // Set reentrance flag
        _staticBootstrapDone = true;
        return true;
    }

    /**
     * Defines in code some System.properties to force text anti-aliasing and Mac OS features ...
     * Called by bootstrap() before anything
     * @see MacOSXAdapter
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void setSystemProperties() {
        if (SystemUtils.IS_OS_MAC_OSX) {
            // Always use screen menuBar on MacOS X
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    /**
     * Disable the security manager to be able to use System.setProperty, XSLT without secure processing feature ...
     */
    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToPrintStackTrace"})
    private static void disableSecurityManager() {
        if (!SystemUtils.isJavaVersionAtLeast(24.0f)) {
            try {
                // Disable security checks:
                System.setSecurityManager(null);
            } catch (SecurityException | UnsupportedOperationException e) {
                // This case occurs with java netx and
                // OpenJDK Runtime Environment (IcedTea6 1.6) (rhel-1.13.b16.el5-x86_64)
                // Note: 2025.03: Java 24 throws UnsupportedOperationException when calling deprecated setSecurityManager()
                // note: logger are not yet initialized:
                System.err.println("Can't set security manager to null");
                e.printStackTrace();
            }
        }
        // Anyway disable JAXP limits (jdk 24+):
        XmlFactory.disableJAXPLimitsUsingSystemProperties();
    }

    /**
     * Initialize default locale and default time zone.
     */
    private static void initializeLocale() {
        // Backup user settings
        _userLocale = Locale.getDefault();
        _userTimeZone = TimeZone.getDefault();

        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        // Set the default timezone to GMT to handle properly the date in UTC
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Return the user Locale defined before using Locale.US
     * @return user Locale
     */
    public static Locale getUserLocale() {
        return _userLocale;
    }

    /**
     * Return the user TimeZone defined before using GMT
     * @return user TimeZone
     */
    public static TimeZone getUserTimeZone() {
        return _userTimeZone;
    }

    /**
     * @return Flag to indicate headless mode
     */
    public static boolean isHeadless() {
        if (_isHeadless == -1) {
            // Check headless mode ONCE (may be a costly operation):
            _isHeadless = (GraphicsEnvironment.isHeadless()) ? 1 : 0;
        }

        return (_isHeadless == 1);
    }

    /**
     * Force the headless mode from API
     */
    public static void forceHeadless() {
        _jmmcLogger.info("force Headless mode.");
        _isHeadless = 1;
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
        final boolean shouldShowSplashScreen = CommonPreferences.getInstance().getPreferenceAsBoolean(CommonPreferences.SHOW_STARTUP_SPLASHSCREEN);
        return launchApp(application, waitBeforeExecution, exitWhenClosed, shouldShowSplashScreen);
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
     * @throws IllegalStateException TBD
     */
    public static boolean launchApp(final App application, final boolean waitBeforeExecution, final boolean exitWhenClosed,
                                    final boolean shouldShowSplashScreen) throws IllegalStateException {

        return ___internalLaunch(application, waitBeforeExecution, exitWhenClosed, shouldShowSplashScreen);
    }

    /**
     * Internal: launch given application instance
     * @param application application instance to launch
     * @param waitBeforeExecution if true, do not launch App.execute() automatically.
     * @param exitWhenClosed flag indicating if application should be quit when main frame close box clicked.
     * @param shouldShowSplashScreen true to effectively show the splash screen, false otherwise.
     * @return true if launch succeeded; false otherwise
     */
    private static boolean ___internalLaunch(final App application, final boolean waitBeforeExecution,
                                             final boolean exitWhenClosed, final boolean shouldShowSplashScreen) {
        setState(ApplicationState.ENV_INIT);
        final long startTime = System.nanoTime();
        boolean launchDone = false;

        _application = application;
        _exitApplicationWhenClosed = exitWhenClosed;
        _application.___internalSingletonInitialization();

        try {
            // check JVM FIRST:
            if (!JVMUtils.showUnsupportedJdkWarning()) {
                // Exit the application anyway:
                Bootstrapper.stopApp(-1);
            }

            // Load jMCS and application data models
            ApplicationDescription.init();
            _jmmcLogger.debug("Application data loaded.");

            _jmmcLogger.info("{} launching application '{}' ...",
                    ApplicationDescription.getJmcsInstance().getProgramNameWithVersion(),
                    ApplicationDescription.getInstance().getProgramNameWithVersion());

            _application.___internalStart();

            SplashScreen.display(shouldShowSplashScreen && !isHeadless());

            // Build Acknowledgment, ShowRelease and ShowHelp Actions
            // (the creation must be done after applicationModel instanciation)
            ActionRegistrar.getInstance().createAllInternalActions();

            setState(ApplicationState.APP_INIT);
            _application.initServices();

            ___internalRun(waitBeforeExecution);

            launchDone = true;

            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            _jmmcLogger.info("Application startup done (duration = {} ms).", elapsedTime);

            if (!isHeadless()) {
                // Check Application updates
                ApplicationDescription.checkUpdates();
            }

        } catch (Throwable th) {
            final ApplicationState stateOnError = Bootstrapper.getState();

            setState(ApplicationState.APP_BROKEN);

            // Show the feedback report (modal)
            SplashScreen.close();
            MessagePane.showErrorMessage("An error occured while initializing the application");

            // Add last chance tip if this exception appears in an inited state but before being ready. (cf. trac #458)
            final Throwable throwable;
            if (stateOnError.after(ApplicationState.ENV_INIT) && stateOnError.before(ApplicationState.APP_READY)) {
                final String warningMessage = "The application did not start properly. Please try first to start it again from the website:\n"
                        + ApplicationDescription.getInstance().getLinkValue()
                        + "\nIf this operation does not fix the problem, please send us a feedback report!\n\n";

                throwable = new Throwable(warningMessage, th);
            } else {
                throwable = th;
            }
            /* use invokeAndWaitEDT ie blocking the current thread */
            FeedbackReport.openDialog(true, throwable);

        } finally {
            if (isHeadless()) {
                _jmmcLogger.info("Unable to start the GUI application [{}] (headless mode enabled) !",
                        _application.getClass().getSimpleName());

                // Exit the application anyway:
                Bootstrapper.stopApp(-1);
            }
        }
        return launchDone;
    }

    /**
     * Describe the life cycle of the application.
     * @param waitBeforeExecution if true, do not launch App.execute() automatically.
     */
    private static void ___internalRun(final boolean waitBeforeExecution) {
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
                    // Set application name
                    // System properties must be set before using any Swing component:
                    // Hope nothing has already been done...
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                            ApplicationDescription.getInstance().getProgramName());
                }

                // Delegate initialization to daughter class through abstract setupGui() call
                setState(ApplicationState.GUI_SETUP);
                _application.setupGui();

                if (!isHeadless() && !waitBeforeExecution) {
                    // Disabled SAMP in shell mode: (runHub needs GUI):
                    // Initialize SampManager as needed by MainMenuBar:
                    SampManager.getInstance();
                    // Declare SAMP message handlers first:
                    _application.declareInteroperability();
                    // Perform defered action initialization (SAMP-related actions)
                    ActionRegistrar.getInstance().performDeferedInitialization();

                    // Define the JFrame associated to the application which will get the JMenuBar
                    final JFrame frame = App.getExistingFrame();

                    if (frame != null) {
                        // Define OSXAdapter (menu bar integration)
                        macOSXRegistration();
                        // Create menus including the Interop menu (SAMP required)
                        frame.setJMenuBar(new MainMenuBar());
                        // Set application frame ideal size
                        frame.pack();
                        // Restore, then automatically save window size changes
                        WindowUtils.rememberWindowSize(frame, WindowUtils.MAIN_FRAME_DIMENSION_KEY);
                    }
                }
            }
        });

        if (!waitBeforeExecution) {
            // Delegate execution to daughter class through abstract execute() call
            _application.execute();

            // Process command line:
            _application.___internalProcessCommandLine();

            // Optionally Open given File:
            _application.openCommandLineFile();
        }

        // Indicate that the application is ready (visible)
        setState(ApplicationState.APP_READY);
    }

    /**
     * Generic registration with the Mac OS X application menu (if needed).
     */
    private static void macOSXRegistration() {
        // If running under Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX) {
            final Class<?> osxAdapter = IntrospectionUtils.getClass("fr.jmmc.jmcs.gui.util.MacOSXAdapter");
            if (osxAdapter == null) {
                // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                _jmmcLogger.error("This version of Mac OS X does not support the Apple EAWT. Application Menu handling has been disabled.");
            } else {
                final Method registerMethod = IntrospectionUtils.getMethod(osxAdapter, "registerMacOSXApplication", null);
                if (registerMethod != null) {
                    if (IntrospectionUtils.executeMethod(registerMethod, null)) {
                        macIntegrationDone = true;
                    }
                }
            }
        }
    }

    /**
     * @return true if if the MAC OS X integration was done successfully, false otherwise.
     */
    public static boolean isMacIntegrationDone() {
        return macIntegrationDone;
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
     * - stops application if user is OK.
     * @param evt the triggering event if any, null otherwise.
     */
    public static void quitApp(final ActionEvent evt) {
        _jmmcLogger.info("Quitting the application ...");

        // Mac OS X Quit action callback:
        final MacOSXQuitCallback callback;
        if (evt != null && evt.getSource() instanceof MacOSXQuitCallback) {
            callback = (MacOSXQuitCallback) evt.getSource();
        } else {
            callback = null;
        }

        // Check if user is OK to kill SAMP hub (if any)
        if (!SampManager.getInstance().allowHubKilling()) {
            _jmmcLogger.debug("SAMP cancelled application kill.");
            // Otherwise cancel quit
            if (callback != null) {
                callback.cancelQuit();
            }
            return;
        }

        // If we are ready to stop application execution
        if (_application == null || _application.canBeTerminatedNow()) {
            _jmmcLogger.debug("Application should be killed.");

            // Verify if we are authorized to kill the application or not
            if (shouldExitAppWhenFrameClosed()) {

                setState(ApplicationState.APP_STOP);

                // Max OS X quit
                if (callback != null) {
                    disableSystemExit(true);
                }

                // Exit the application
                stopApp(0);

                // Max OS X quit
                if (callback != null) {
                    callback.performQuit();
                }
                return;

            } else {
                _jmmcLogger.debug("Application frame left opened as required.");
            }
        } else {
            _jmmcLogger.debug("Application quit cancelled.");
        }
        if (callback != null) {
            callback.cancelQuit();
        }
    }

    /**
     * Stop the application properly without user feedback:
     * - calls your App.cleanup() method;
     * - stops all critical jMCS services;
     * - System.exit(statusCode) if so (@see disableSystemExit()).
     * @param statusCode status code to return
     */
    public static void stopApp(final int statusCode) {
        if (isInState(ApplicationState.JAVA_LIMB)) {
            return;
        }
        boolean appCleanupFailed = false;

        // Avoid reentrance if an exception occured (Feedback Report):
        if (_application != null && getState().before(ApplicationState.APP_CLEANUP)) {
            _jmmcLogger.info("Stopping the application ...");
            try {
                setState(ApplicationState.APP_CLEANUP);
                // Dispose Gui using EDT:
                SwingUtils.invokeAndWaitEDT(new Runnable() {
                    @Override
                    public void run() {
                        _application.cleanup();
                    }
                });

            } catch (Throwable th) {
                appCleanupFailed = true;
                setState(ApplicationState.APP_CLEANUP_FAIL);

                /* use invokeAndWaitEDT ie blocking the current thread */
                FeedbackReport.openDialog(true, th);
            }
        }

        // Avoid reentrance if an exception occured (Feedback Report):
        if (appCleanupFailed
                || (!isInState(ApplicationState.APP_CLEANUP_FAIL) && getState().before(ApplicationState.ENV_CLEANUP))) {

            setState(ApplicationState.ENV_CLEANUP);
            // should not throw runtime exception:
            ___internalStop();

            setState(ApplicationState.APP_DEAD);
            App.___internalSingletonCleanup();

            if (!_avoidSystemExit) {
                /* fix status code if an exception occured during application cleanup */
                final int exitCode = (appCleanupFailed) ? -1 : statusCode;

                _jmmcLogger.info("Exiting with status code '{}'.", exitCode);

                System.exit(exitCode);
            }
            setState(ApplicationState.JAVA_LIMB);
        }
    }

    /**
     * Internal: Stop services
     */
    private static void ___internalStop() {
        _jmmcLogger.info("Stopping internal services ...");
        try {
            // Save common settings if needed:
            CommonPreferences.saveToFileIfNeeded();

            // Save session settings if needed:
            SessionSettingsPreferences.saveToFileIfNeeded();

            // Stop the job runner (if any)
            LocalLauncher.shutdown();

            // Stop the task executor (if any)
            TaskSwingWorkerExecutor.shutdown();

            // Stop the parallel job executor (if any)
            ParallelJobExecutor.shutdown();

            // Disconnect from SAMP Hub (if any)
            SampManager.shutdown();

            // Close all HTTP connections (http client) (if any)
            MultiThreadedHttpConnectionManager.shutdownAll();

            // Switch to logging exception handler:
            MCSExceptionHandler.installLoggingHandler();

        } catch (RuntimeException re) {
            // should not happen but anyway log any potential exception:
            _jmmcLogger.warn("A runtime exception occured while stopping services: ", re);
        }
    }

    /**
     * @param state the new application state.
     */
    private static void setState(final ApplicationState state) {
        _jmmcLogger.debug("Change state from '{}' to '{}'.", _applicationState, state);
        _applicationState = state;
    }

    /**
     * @return the application current state.
     */
    public static ApplicationState getState() {
        return _applicationState;
    }

    /**
     * @return true if the application is in the given state, false otherwise.
     * @param givenState the state to check against.
     */
    public static boolean isInState(ApplicationState givenState) {
        return (_applicationState == givenState);
    }

    /** Private constructor */
    private Bootstrapper() {
        // no-op
    }
}
