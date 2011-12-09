package org.ivoa.util;

import java.util.HashMap;
import java.util.Map;
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
    /** Base framework logger = org.ivoa.base */
    public static final String LOGGER_BASE = "org.ivoa.base";
    /** Development logger = org.ivoa.dev */
    public static final String LOGGER_DEV = "org.ivoa.dev";
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
    private Logger log;
    /**
     * Development logger
     * @see #LOGGER_BASE
     */
    private Logger logBase;
    /**
     * Development logger
     * @see #LOGGER_DEV
     */
    private Logger logDev;
    /** all loggers */
    private final Map<String, Logger> logs = new HashMap<String, Logger>();

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
                if (l.log.isWarnEnabled()) {
                    l.log.warn("LogUtil.getInstance : shutdown detected : ", new Throwable());
                }
                return l;
            }
            instance = l;

            if (instance.logBase.isDebugEnabled()) {
                instance.logBase.debug("LogUtil.getInstance : new singleton : " + instance);
            }
        }

        return instance;
    }

    /**
     * PUBLIC: onExit method : TODO release all ClassLoader references due to SLF4J
     * NOTE : <b>This method must be called in the context of a web application via
     * ServletContextListener.contextDestroyed(ServletContextEvent)</b>
     * 
     * @see org.apache.commons.logging.LogFactory#release(ClassLoader)
     */
    public static final void onExit() {
        isShutdown = true;
        if (instance != null) {
            // TODO: Classloader unload problem with SLF4J ??
            // force GC :
            instance.log = null;
            instance.logBase = null;
            instance.logDev = null;
            instance.logs.clear();

            // free singleton :
            instance = null;
        }
    }

    /**
     * Return true if shutdown flag is not set
     * 
     * @return true if shutdown flag is not set
     */
    private static final boolean isRunning() {
        if (LOGGING_DIAGNOSTICS && isShutdown) {
            if (SystemLogUtil.isDebugEnabled()) {
                SystemLogUtil.debug("LogUtil.isRunning : shutdown detected : ");
            }
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
     * Return the Base logger :
     * @see #LOGGER_BASE
     *
     * @return Logger instance or null if shutdown flag is set
     */
    public static Logger getLoggerBase() {
        Logger l = null;
        if (isRunning()) {
            l = getInstance().getLogBase();
        }
        return l;
    }

    /**
     * Return the Development logger :
     * @see #LOGGER_DEV
     * 
     * @return Logger instance or null if shutdown flag is set
     */
    public static Logger getLoggerDev() {
        Logger l = null;
        if (isRunning()) {
            l = getInstance().getLogDev();
        }
        return l;
    }

    /**
     * Return a logger for the given key (category) for a special category.<br/>
     * Use this method only for special categories that are not covered by the other getLogger[...]()
     * methods
     * 
     * @see #getLogger()
     * @see #getLoggerBase()
     * @see #getLoggerDev()
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
        this.log = this.getLog(LOGGER_MAIN);

        if (this.log.isDebugEnabled()) {
            this.log.debug("LogUtil : logging enabled now.");
        }

        this.logBase = this.getLog(LOGGER_BASE);
        this.logDev = this.getLog(LOGGER_DEV);
    }

    /**
     * Return a logger for the given key
     * 
     * @param key logger name in the log4j configuration file
     * @return Log
     * @throws IllegalStateException if the LogFactory returns no logger for the given key
     */
    private Logger getLog(final String key) {
        Logger l = this.logs.get(key);

        if (l == null) {
            l = LoggerFactory.getLogger(key);

            if (l != null) {
                this.addLog(key, l);
            } else {
                throw new IllegalStateException("LogUtil : Logs are not initialized correctly : missing logger [" + key + "] !");
            }
        }

        return l;
    }

    /**
     * Add a Logger into the logs map
     * 
     * @param key alias
     * @param logger Logger to add
     */
    private void addLog(final String key, final Logger logger) {
        this.logs.put(key, logger);
    }

    /**
     * Return the Main logger
     * 
     * @return Main Log
     */
    private final Logger getLog() {
        return this.log;
    }

    /**
     * Return the Base logger
     *
     * @return Base Log
     */
    private final Logger getLogBase() {
        return this.logBase;
    }

    /**
     * Return the Development logger
     *
     * @return Development Log
     */
    private final Logger getLogDev() {
        return this.logDev;
    }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

