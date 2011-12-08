/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.LoggerFactory;

/**
 * This class keeps into memory the full application log using the logback root logger 
 * and a custom appender to store log messages into one ByteBuffer (memory)
 * 
 * Usage:
 * Call ApplicationLogSingleton.getInstance() during your application startup
 * 
 * @author bourgesl
 */
public final class ApplicationLogSingleton {

    /** jmmc application log appender name */
    public final static String JMMC_APPLOG_APPENDER = "APPLOG";
    /** singleton instance */
    private volatile static ApplicationLogSingleton instance = null;

    /**
     * Get the singleton instance or create a new one if needed
     * @return singleton instance
     */
    public static synchronized ApplicationLogSingleton getInstance() {
        if (instance == null) {
            instance = new ApplicationLogSingleton();
        }
        return instance;
    }

    /* members */
    /** Logback appender which keeps Application log */
    private final ByteArrayOutputStreamAppender _appLogAppender;

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

        // Try to get the root logger (logback):
        ch.qos.logback.classic.Logger _rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        // Get the root logger's appender 'APPLOG':
        final Appender<ILoggingEvent> appender = _rootLogger.getAppender(JMMC_APPLOG_APPENDER);

        // Check if this appender has the correct type (ByteArrayOutputStreamAppender):
        if ((appender != null) && !(appender instanceof ByteArrayOutputStreamAppender)) {
            throw new IllegalStateException("Bad class type [" + appender.getClass() + " - " + ByteArrayOutputStreamAppender.class
                    + "expected] for appender [" + JMMC_APPLOG_APPENDER + "] attached to the " + Logger.ROOT_LOGGER_NAME + " logger !");
        }
        if (appender != null) {
            // use this appender:
            _appLogAppender = (ByteArrayOutputStreamAppender) appender;
        } else {
            // create a new ByteArrayOutputStreamAppender:
            final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(_rootLogger.getLoggerContext());
            encoder.setPattern("%d{HH:mm:ss.SSS} %-5level [%thread] %logger{60} - %msg%n");
            encoder.start();

            // Logger's byteBuffer stream handler creation:
            final ByteArrayOutputStreamAppender streamAppender = new ByteArrayOutputStreamAppender();
            streamAppender.setContext(_rootLogger.getLoggerContext());
            streamAppender.setEncoder(encoder);
            streamAppender.start();

            // We add the memory handler created to the root logger
            _rootLogger.addAppender(streamAppender);

            // use this new appender:
            _appLogAppender = streamAppender;
        }
    }

    /**
     * Return the complete log as string (THREAD SAFE)
     * @return complete log output
     */
    public LogOutput getLogOutput() {
        return getLogOutput(0);
    }

    /**
     * Return the partial log as string starting at the given argument from (THREAD SAFE)
     * @param from gives the position in the buffer to copy from
     * @return partial log output
     */
    public LogOutput getLogOutput(final int from) {
        return _appLogAppender.getLogOutput(from);
    }    
}
