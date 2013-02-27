package org.ivoa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commons-logging & Log4J Utility class.<br/>
 *
 * This class is the first loaded class (singleton) to provide logging API.<br/>
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class LogUtil {
    // ~ Constants
    // --------------------------------------------------------------------------------------------------------

    /** internal diagnostic FLAG : use System out */
    public static final boolean LOGGING_DIAGNOSTICS = false;
    /** Main logger = org.ivoa */
    public static final String LOGGER_MAIN = "org.ivoa";
    /** singleton instance (java 5 memory model) */
    private static volatile LogUtil instance = null;
    /** shutdown flag to avoid singleton to be defined (java 5 memory model) */
    private static volatile boolean isShutdown = false;
    // ~ Members
    // ----------------------------------------------------------------------------------------------------------
    /**
     * Main logger
     * @see #LOGGER_MAIN
     */
    Logger logger;

    // ~ Constructors
    // -----------------------------------------------------------------------------------------------------
    /**
     * Private Constructor : use getInstance() method
     */
    private LogUtil() {
        /* no-op */
    }

    // ~ Methods
    // ----------------------------------------------------------------------------------------------------------
    /**
     * Return the singleton instance
     * 
     * @return LogUtil singleton instance
     */
    private static LogUtil getInstance() {
        if (instance == null) {
            final LogUtil l = new LogUtil();
            l.init();
            if (isShutdown) {
                // should not be possible :
                l.logger.error("LogUtil.getInstance : shutdown detected : ", new Throwable());
                return l;
            }
            instance = l;

            instance.logger.debug("LogUtil.getInstance : new singleton: {}", instance);
        }

        return instance;
    }

    /**
     * PUBLIC: onExit method : TODO release all ClassLoader references due to SLF4J
     * NOTE : <b>This method must be called in the context of a web application via
     * ServletContextListener.contextDestroyed(ServletContextEvent)</b>
     */
    public static void onExit() {
        isShutdown = true;
        if (instance != null) {
            // TODO: Classloader unload problem with SLF4J ??
            // force GC :
            instance.logger = null;

            // free singleton :
            instance = null;
        }
    }

    /**
     * Return true if shutdown flag is not set
     * 
     * @return true if shutdown flag is not set
     */
    private static boolean isRunning() {
        if (LOGGING_DIAGNOSTICS && isShutdown) {
            System.err.println("LogUtil.isRunning : shutdown detected");
        }

        return !isShutdown;
    }

    /**
     * Return the Main logger :
     * @see #LOGGER_MAIN
     * 
     * @return Logger instance or null if shutdown flag is set
     */
    public static Logger getLogger() {
        Logger l = null;
        if (isRunning()) {
            l = getInstance().getLog();
        }
        return l;
    }

    /**
     * Return a logger for the given key (category) for a special category.<br/>
     * Use this method only for special categories that are not covered by the other getLogger[...]()
     * methods
     * 
     * @see #getLogger()
     * @param key logger name defined in the log4j configuration file
     * @return Logger instance or null if shutdown flag is set
     */
    public static Logger getLogger(final String key) {
        Logger l = null;
        if (isRunning()) {
            l = getInstance().getLog(key);
        }
        return l;
    }

    /**
     * Return a logger dedicated to the given class.<br/>
     * Warning : it creates a category per class.
     * 
     * @param clazz class instance
     * @return Logger instance dedicated to the given class or null if shutdown flag is set
     */
    public static Logger getLogger(final Class<?> clazz) {
        Logger l = null;
        if (isRunning()) {
            l = getInstance().getLog(clazz.getName());
        }
        return l;
    }

    /**
     * Initialize loggers and checks if Log4J is well configured
     * 
     * @throws IllegalStateException if the logger is not a Log4JLogger
     */
    private void init() {
        this.logger = this.getLog(LOGGER_MAIN);

        this.logger.debug("LogUtil : logging enabled now.");
    }

    /**
     * Return a logger for the given key
     * 
     * @param key logger name in the log4j configuration file
     * @return Log
     * @throws IllegalStateException if the LogFactory returns no logger for the given key
     */
    private Logger getLog(final String key) {
        Logger l = LoggerFactory.getLogger(key);

        if (l == null) {
            throw new IllegalStateException("LogUtil : Logs are not initialized correctly : missing logger [" + key + "] !");
        }

        return l;
    }

    /**
     * Return the Main logger
     * 
     * @return Main Log
     */
    private Logger getLog() {
        return this.logger;
    }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

