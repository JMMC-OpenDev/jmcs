/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.log;

import java.util.*;
import java.util.logging.*;


/**
 * MCSLogger is the main object to use for logging facilities.
 * 
 * @author Sylvain LAFRASSE.
 */
public class MCSLogger
{
    /**
     * Application-wide shared instance logger.
     */
    static Logger _logger = Logger.getLogger("fr.jmmc.mcs");

    /**
     * Application-wide shared instance console handler.
     */
    static ConsoleHandler _handler = new ConsoleHandler();

    /**
     * Set the logger level.
     *
     * @warning Must at least be called once to enable STDOUT logging.
     *
     * "0" :  all logs turned OFF.
     * "1" : 'error' logs only.
     * "2" : 'error' and 'warning' logs.
     * "3" : 'error', 'warning' and 'info' logs.
     * "4" : 'error', 'warning', 'info' and 'test' logs.
     * "5" :  all logs turned ON ('error','warning','info','test' and 'debug').
     *
     * @param the logger level, as a String amongst values described above.
     */
    static public void setLevel(String stringLevel)
    {
        MCSLogger.trace();

        // Define 'string to Level' conversion table
        Hashtable levels = new Hashtable();
        levels.put("0", java.util.logging.Level.OFF);
        levels.put("1", java.util.logging.Level.SEVERE);
        levels.put("2", java.util.logging.Level.WARNING);
        levels.put("3", java.util.logging.Level.INFO);
        levels.put("4", java.util.logging.Level.FINE);
        levels.put("5", java.util.logging.Level.ALL);

        // Convert the given string in the corresponding Level object
        Level level = (Level) levels.get(stringLevel);

        // If the given string was recognized
        if (level != null)
        {
            System.out.println("Log level set to '" + stringLevel + "' (" +
                level + ").");

            // Set the logger level
            _logger.setLevel(level);

            // Ensures that log message are logged on STDOUT
            _logger.setUseParentHandlers(false);
            _handler.setLevel(level);
            _logger.addHandler(_handler);
        }
        else
        {
            MCSLogger.error("Bad log level value '" + stringLevel + "'.");
        }
    }

    /**
     * Output the given string at the error log level
     *
     * @param log the string to be logged
     */
    public static void error(String log)
    {
        _logger.severe(log);
    }

    /**
     * Output the given string at the warning log level
     *
     * @param log the string to be logged
     */
    public static void warning(String log)
    {
        _logger.warning(log);
    }

    /**
     * Output the given string at the info log level
     *
     * @param log the string to be logged
     */
    public static void info(String log)
    {
        _logger.info(log);
    }

    /**
     * Output the given string for test log level
     *
     * @param log the string to be logged
     */
    public static void test(String log)
    {
        _logger.fine(log);
    }

    /**
     * Output the given string at the most verbose log level
     *
     * @param log the string to be logged
     */
    public static void debug(String log)
    {
        _logger.finest(log);
    }

    /**
     * Output the calling method and class names
     */
    public static void trace()
    {
        Throwable         t      = new Throwable();
        StackTraceElement caller = t.getStackTrace()[1];
        _logger.entering(caller.getClassName(), caller.getMethodName());
    }
}
/*___oOo___*/
