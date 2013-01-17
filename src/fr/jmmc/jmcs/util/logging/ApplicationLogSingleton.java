/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * This class keeps into memory the full application log using the logback root logger 
 * and a custom appender to store log messages into one ByteBuffer (memory)
 * 
 * Usage:
 * Call ApplicationLogSingleton.getInstance() during your application startup
 * 
 * @author Laurent BOURGES.
 */
public final class ApplicationLogSingleton {

    /** singleton instance */
    private volatile static ApplicationLogSingleton _instance = null;
    /* loggers */
    /** JMMC application log */
    public final static String JMMC_APP_LOG = Logger.ROOT_LOGGER_NAME;
    /** JMMC status log */
    public final static String JMMC_STATUS_LOG = "fr.jmmc.jmcs.status";

    /**
     * Get the singleton instance or create a new one if needed
     * @return singleton instance
     */
    public static synchronized ApplicationLogSingleton getInstance() {
        if (_instance == null) {
            _instance = new ApplicationLogSingleton();
        }
        return _instance;
    }

    /* members */
    /** mappers collection keyed by logger path */
    private final Map<String, AppenderLogMapper> mappers = new LinkedHashMap<String, AppenderLogMapper>(8);

    /**
     * Private constructor that gets one ByteArrayOutputStreamAppender:
     * - from appenders attached to the root logger (joran configuration already done / logback.xml)
     * 
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <configuration> 
     * 
     * ...
     * 
     *   <!-- Appenders -->
     *     <appender name="APPLOG" class="fr.jmmc.jmcs.util.logging.ByteArrayOutputStreamAppender">
     *         <encoder>
     *             <pattern>%d{HH:mm:ss.SSS} %-5level [%thread] %logger{60} - %msg%n</pattern>
     *         </encoder>
     *     </appender>
     * ...  
     * 
     *   <!-- Loggers -->
     * ...
     * 
     *   <!-- Root Logger -->
     *   <root level="INFO">
     *         <appender-ref ref="APPLOG" />
     *         ...
     *   </root>
     *  
     * </configuration>
     * 
     * - created here and attached to the root logger
     */
    private ApplicationLogSingleton() {
        super();

        // define Log Mappers:
        addLogMapper("Status history", JMMC_STATUS_LOG, "STATUSLOG");
        addLogMapper("Execution log", JMMC_APP_LOG, "APPLOG");
    }

    /**
     * Return the complete application log as string (THREAD SAFE)
     * @return complete log output
     */
    public LogOutput getLogOutput() {
        return getLogOutput(0);
    }

    /**
     * Return the partial application log as string starting at the given argument from (THREAD SAFE)
     * @param from gives the position in the buffer to copy from
     * @return partial log output
     */
    public LogOutput getLogOutput(final int from) {
        return getLogOutput(JMMC_APP_LOG, from);
    }

    /**
     * Return the partial log for the given logger path as string starting at the given argument from (THREAD SAFE)
     * @param loggerPath logger path
     * @param from gives the position in the buffer to copy from
     * @return partial log output
     */
    public LogOutput getLogOutput(final String loggerPath, final int from) {
        return getLogMapper(loggerPath).getLogAppender().getLogOutput(from);
    }

    /**
     * Return the logger for the given logger path
     * @param loggerPath logger path
     * @return logger for the given logger path
     */
    public Logger getLogger(final String loggerPath) {
        return getLogMapper(loggerPath).getLogger();
    }

    /**
     * Register a new log given its attributes (logger path, appender name)
     * @param displayName display name
     * @param loggerPath logger path
     * @param appenderName appender name
     */
    public void addLogMapper(final String displayName, final String loggerPath, final String appenderName) {
        mappers.put(loggerPath, new AppenderLogMapper(displayName, loggerPath, appenderName));
    }

    /**
     * Return the ordered collection of Log mappers
     * @return collection of Log mappers
     */
    Collection<AppenderLogMapper> getLogMappers() {
        return mappers.values();
    }

    /**
     * Return the mapper associated to the given logger path
     * @param loggerPath logger path
     * @return mapper associated to the given logger path or null
     * @throws IllegalStateException if the logger path is not present in the mappers collection
     */
    private AppenderLogMapper getLogMapper(final String loggerPath) throws IllegalStateException {
        final AppenderLogMapper mapper = mappers.get(loggerPath);
        if (mapper == null) {
            throw new IllegalStateException("Unsupported logger [" + loggerPath + "]");
        }
        return mapper;
    }
}
