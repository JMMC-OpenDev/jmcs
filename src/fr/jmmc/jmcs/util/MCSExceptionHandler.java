/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.gui.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides Java 5 Thread uncaught exception handlers
 *
 * see http://stuffthathappens.com/blog/2007/10/07/programmers-notebook-uncaught-exception-handlers/
 *
 * see http://stuffthathappens.com/blog/2007/10/15/one-more-note-on-uncaught-exception-handlers/
 *
 * JNLP issues :
 * - Thread.defaultUncaughtExceptionHandler never used
 * => do not use this default UncaughtExceptionHandler to have the same behaviour using standard java runtime
 *
 * - main thread (starting the application) use a general try/catch (throwable) and opens a JNLP error dialog
 * => do not set the UncaughtExceptionHandler to this thread
 * => Be sure to catch all exceptions in main() and use the feeback report manually
 * 
 * @author Laurent BOURGES.
 */
public final class MCSExceptionHandler {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(MCSExceptionHandler.class.getName());
    /** flag indicating to use the default UncaughtExceptionHandler (true for jdk 1.7.0) */
    private static final boolean USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = true;
    /** flag indicating to set the UncaughtExceptionHandler to the current thread (main) (false because of JNLP) */
    private static final boolean SET_HANDLER_TO_CURRENT_THREAD = false;
    /** uncaughtException handler singleton */
    private static volatile Thread.UncaughtExceptionHandler exceptionHandler = null;

    /**
     * Disable the security manager to be able to use System.setProperty ...
     */
    private static void disableSecurityManager() {
        try {
            // Disable security checks :
            System.setSecurityManager(null);
        } catch (SecurityException se) {
            // This case occurs with java netx and
            // OpenJDK Runtime Environment (IcedTea6 1.6) (rhel-1.13.b16.el5-x86_64)
            _logger.warn("Can't set security manager to null", se);
        }
    }

    /**
     * Public method to initialize the exception handler singleton with the LoggingExceptionHandler
     */
    public static void installLoggingHandler() {
        disableSecurityManager();

        setExceptionHandler(new LoggingExceptionHandler());
    }

    /**
     * Public method to initialize the exception handler singleton with the SwingExceptionHandler
     */
    public static void installSwingHandler() {
        disableSecurityManager();

        // AWT exception handler for modal dialogs :
        System.setProperty("sun.awt.exception.handler", MCSExceptionHandler.class.getName());

        setExceptionHandler(new SwingExceptionHandler());
    }

    /**
     * Public method to apply the exception handler singleton to the given thread
     * @param thread thread to use
     */
    public static void installThreadHandler(final Thread thread) {
        final Thread.UncaughtExceptionHandler handler = getExceptionHandler();
        if (handler != null) {
            applyUncaughtExceptionHandler(thread, handler);
        } else {
            _logger.debug("No UncaughtExceptionHandler defined !");
        }
    }

    /**
     * Return the exception handler singleton
     * @return exception handler singleton or null if undefined
     */
    private static Thread.UncaughtExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Define the exception handler singleton and apply it to the JVM.
     * If the singleton is already defined, this method has no effect.
     *
     * @see #applyUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
     *
     * @param handler handler to set
     */
    private static synchronized void setExceptionHandler(final Thread.UncaughtExceptionHandler handler) {
        if (handler != null) {
            exceptionHandler = handler;

            applyUncaughtExceptionHandler(handler);
        }
    }

    /**
     * Apply the given UncaughtExceptionHandler to the JVM :
     * - define as default if USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER is enabled
     * - define it to the current thread if SET_HANDLER_TO_CURRENT_THREAD is enabled
     * - define it to EDT if the given handler is a SwingExceptionHandler
     *
     * @see #applyUncaughtExceptionHandler(java.lang.Thread, java.lang.Thread.UncaughtExceptionHandler)
     *
     * @param handler handler to set
     */
    private static void applyUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler handler) {
        _logger.debug("New UncaughtExceptionHandler: {}", handler);

        if (USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Current Default UncaughtExceptionHandler: {}", Thread.getDefaultUncaughtExceptionHandler());
            }

            Thread.setDefaultUncaughtExceptionHandler(handler);

            if (_logger.isDebugEnabled()) {
                _logger.debug("Updated Default UncaughtExceptionHandler: {}", Thread.getDefaultUncaughtExceptionHandler());
            }
        }

        if (SET_HANDLER_TO_CURRENT_THREAD) {
            applyUncaughtExceptionHandler(Thread.currentThread(), handler);
        }

        if (handler instanceof SwingExceptionHandler) {
            try {
                // Using invokeAndWait to be in sync with this thread :
                // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
                SwingUtils.invokeAndWaitEDT(new Runnable() {

                    /**
                     * Add my handler to the Event-Driven Thread.
                     */
                    @Override
                    public void run() {
                        applyUncaughtExceptionHandler(Thread.currentThread(), handler);
                    }
                });
            } catch (IllegalStateException ise) {
                _logger.error("exception occured: ", ise);
            }
        }
    }

    /**
     * Define the UncaughtExceptionHandler to the given thread
     * @param thread thread to use
     * @param handler handler to set
     */
    private static void applyUncaughtExceptionHandler(final Thread thread, final Thread.UncaughtExceptionHandler handler) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Current Thread = {} in group = {}", thread, thread.getThreadGroup());
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Current Thread UncaughtExceptionHandler: {}", thread.getUncaughtExceptionHandler());
        }

        // Adding my handler to this thread (may be unnecessary) :
        thread.setUncaughtExceptionHandler(handler);

        if (_logger.isDebugEnabled()) {
            _logger.debug("Updated Thread UncaughtExceptionHandler: {}", thread.getUncaughtExceptionHandler());
        }
    }

    /**
     * Return true if the given exception must be ignored (filtered).
     * For example : ThreadDeath are ignored.
     * @param e
     * @return true if the given exception must be ignored
     */
    private static boolean isFilteredException(final Throwable e) {
        if (e instanceof ThreadDeath) {
            return true;
        }
        return false;
    }

    /**
     * Log the exception
     * @param t the thread
     * @param e the exception
     */
    private static void logException(final Thread t, final Throwable e) {
        _logger.error("An unexpected exception occured in thread {}", t.getName(), e);
    }

    /**
     * Report the exception to the user via Swing using EDT :
     * - display an error message with the exception message
     * - open a modal feedback report
     * @param t the thread
     * @param e the exception
     */
    private static void showException(final Thread t, final Throwable e) {
        MessagePane.showErrorMessage("An unexpected exception occured", e);

        // Show the feedback report (modal) :
        FeedbackReport.openDialog(true, e);
    }

    /**
     * Public constructor used by reflection
     */
    public MCSExceptionHandler() {
        super();
    }

    /**
     * AWT exception handler useful for exceptions occuring in modal dialogs
     *
     * @param e the exception
     */
    public void handle(final Throwable e) {
        showException(Thread.currentThread(), e);
    }

    /**
     * Logging exception handler that delegates exception handling to logException(thread, throwable)
     */
    private final static class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {

        /**
         * Method invoked when the given thread terminates due to the
         * given uncaught exception.
         * <p>Any exception thrown by this method will be ignored by the
         * Java Virtual Machine.
         * @param thread the thread
         * @param e the exception
         */
        @Override
        public void uncaughtException(final Thread thread, final Throwable e) {
            if (!isFilteredException(e)) {
                logException(thread, e);
            }
        }
    }

    /**
     * Swing exception handler that delegates exception handling to showException(thread, throwable)
     * using EDT
     */
    private final static class SwingExceptionHandler implements Thread.UncaughtExceptionHandler {

        /**
         * Method invoked when the given thread terminates due to the
         * given uncaught exception.
         * <p>Any exception thrown by this method will be ignored by the
         * Java Virtual Machine.
         * @param thread the thread
         * @param e the exception
         */
        @Override
        public void uncaughtException(final Thread thread, final Throwable e) {
            if (!isFilteredException(e)) {
                SwingUtils.invokeEDT(new Runnable() {

                    @Override
                    public void run() {
                        showException(thread, e);
                    }
                });
            }
        }
    }
}