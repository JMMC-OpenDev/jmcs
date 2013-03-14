/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.LoggerFactory;

/**
 * Simple mapping between logger and our custom ByteArrayOutputStreamAppender
 * 
 * Note: this class is only used in this package (inaccessible from outside)
 *
 * @author Laurent BOURGES.
 */
final class AppenderLogMapper {

    /** display name */
    private final String _displayName;
    /** logger path */
    private final String _loggerPath;
    /** logger */
    private final org.slf4j.Logger _logger;
    /** Logback appender which keeps log content */
    private final ByteArrayOutputStreamAppender _logAppender;

    /**
     * Constructor
     * @param displayName display name
     * @param loggerPath logger path
     * @param appenderName appender name
     */
    AppenderLogMapper(final String displayName, final String loggerPath, final String appenderName) {
        _displayName = displayName;
        _loggerPath = loggerPath;

        // Try to get the root logger (logback):
        ch.qos.logback.classic.Logger loggerImpl = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(_loggerPath);

        LoggerFactory.getLogger(LoggingService.JMMC_STATUS_LOG);

        // Get the root logger's appender 'APPLOG':
        final Appender<ILoggingEvent> appender = loggerImpl.getAppender(appenderName);
        // Check if this appender has the correct type (ByteArrayOutputStreamAppender):
        if ((appender != null) && !(appender instanceof ByteArrayOutputStreamAppender)) {
            throw new IllegalStateException("Bad class type [" + appender.getClass() + " - "
                    + ByteArrayOutputStreamAppender.class + "expected] for appender [" + appenderName + "] attached to the " + _loggerPath + " logger !");
        }
        if (appender != null) {
            // use this appender:
            _logAppender = (ByteArrayOutputStreamAppender) appender;
        } else {
            throw new IllegalStateException("Missing appender [" + appenderName + "] attached to the " + _loggerPath + " logger !");
        }

        _logger = loggerImpl;
    }

    /**
     * Return the display name
     * @return display name
     */
    String getDisplayName() {
        return _displayName;
    }

    /**
     * Return the logger path
     * @return logger path
     */
    String getLoggerPath() {
        return _loggerPath;
    }

    /**
     * Return the logger
     * @return logger
     */
    org.slf4j.Logger getLogger() {
        return _logger;
    }

    /**
     * Return the Logback appender which keeps log content
     * @return Logback appender which keeps log content
     */
    ByteArrayOutputStreamAppender getLogAppender() {
        return _logAppender;
    }
}
