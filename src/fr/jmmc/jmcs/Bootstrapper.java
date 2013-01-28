/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import ch.qos.logback.classic.Logger;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.util.logging.LoggingService;
import java.util.Date;

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

    public static boolean launch(final App app) throws IllegalStateException {

        boolean launchDone = false;

        app.___internalSingletonInitialization();

        final long start = System.nanoTime();

        // Load jMCS and application data models
        ApplicationDescription.init();
        _jmmcLogger.debug("Application data loaded.");

        try {
            app.___internalStart();
            app.___internalRun();
            launchDone = true;
        } catch (Throwable th) {
            // Show the feedback report (modal)
            app.___internalHideSplashScreen();
            MessagePane.showErrorMessage("An error occured while initializing the application");
            FeedbackReport.openDialog(true, th);
        } finally {
            final double time = 1e-6d * (System.nanoTime() - start);
            _jmmcLogger.info("Application startup done (duration = {} ms).", time);
        }

        return launchDone;
    }

    /** Private constructor */
    private Bootstrapper() {
        // no-op
    }
}
