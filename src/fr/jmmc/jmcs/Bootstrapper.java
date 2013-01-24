/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.network.NetworkSettings;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.logging.ApplicationLogSingleton;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This class provides jMCS initialization (logs, SWING, network ...)
 *
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class Bootstrapper {

    /** JMMC LogBack configuration file as one resource file (in class path) */
    public final static String JMMC_LOGBACK_CONFIG_RESOURCE = "jmmc-logback.xml";
    /** JMMC main logger */
    public final static String JMMC_LOGGER = "fr.jmmc";
    /** Flag to avoid reentrance in launch sequence */
    private static boolean _staticBootstrapDone = false;
    /** Class Logger */
    private static final org.slf4j.Logger _logger = LoggerFactory.getLogger(Bootstrapper.class.getName());

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

        // Assume SLF4J is bound to logback in the current environment
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Get our logback configuration file:
        // throws an IllegalStateException if the file is not found
        final URL logConf = FileUtils.getResource(JMMC_LOGBACK_CONFIG_RESOURCE);

        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            // create one dummy context to let configurator execute correctly:
            configurator.setContext(loggerContext);

            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            loggerContext.reset();

            configurator.doConfigure(logConf.openStream());

        } catch (IOException ioe) {
            throw new IllegalStateException("IO Exception occured", ioe);
        } catch (JoranException je) {
            // StatusPrinter will handle this
            StatusPrinter.printInCaseOfErrorsOrWarnings((LoggerContext) LoggerFactory.getILoggerFactory());
        }

        // Remote existing handlers from java.util.logging (JUL) root logger (useful in netbeans)
        final java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
        final java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0, len = handlers.length; i < len; i++) {
            rootLogger.removeHandler(handlers[i]);
        }

        // Call only once during initialization time of your application to configure JUL bridge
        SLF4JBridgeHandler.install();

        // slf4j / logback initialization done

        // Start the application log singleton:
        ApplicationLogSingleton.getInstance();

        // Get the jmmc logger:
        final ch.qos.logback.classic.Logger jmmcLogger = getJmmcLogger();
        jmmcLogger.info("Application log created at {}. Current level is {}.", new Date(), jmmcLogger.getEffectiveLevel());

        // Define swing settings (laf, locale...) before any Swing usage if not called at the first line of the main method:
        SwingSettings.setup();

        // Define default network settings:
        // note: settings must be set before using any URLConnection (loadApplicationData)
        NetworkSettings.defineDefaults();

        jmmcLogger.info("Application bootstrap done.");

        // set reentrance flag
        _staticBootstrapDone = true;
        return true;
    }

    public static boolean launch(final App app) throws IllegalStateException {

        boolean launchDone = false;

        final long start = System.nanoTime();
        final Logger jmmcLogger = getJmmcLogger();

        // Load jMCS and application data models
        ApplicationDescription.init();
        _logger.debug("Application data loaded.");

        try {
            app.start();
            app.run();
            launchDone = true;
        } catch (Throwable th) {
            // Show the feedback report (modal)
            app.hideSplashScreen();
            MessagePane.showErrorMessage("An error occured while initializing the application");
            FeedbackReport.openDialog(true, th);
        } finally {
            final double time = 1e-6d * (System.nanoTime() - start);
            jmmcLogger.info("Application startup done (duration = {} ms).", time);
        }

        return launchDone;
    }

    /** @return the the JMMC logger (top level). */
    public static ch.qos.logback.classic.Logger getJmmcLogger() {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("fr.jmmc");
    }

    /** Private constructor */
    private Bootstrapper() {
        // no-op
    }
}
