/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSLogger.java,v 1.3 2006-10-10 09:09:35 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/12 14:15:10  lafrasse
 * Added doxygen documentation
 *
 * Revision 1.1  2006/03/27 11:59:58  lafrasse
 * Added new experimental Java GUI
 *
 ******************************************************************************/
package jmmc.mcs.log;

import java.util.logging.Logger;


/**
 * MCSLogger is the main object to use for logging facilities.
 */
public class MCSLogger
{
    /**
     * Application-wide shared instance logger.
     */
    static Logger myLogger = Logger.getLogger("fr.jmmc.mcs");

    /**
     * Give back the logger shared instance
     *
     * @return the logger shared instance
     */
    static public Logger getLogger()
    {
        return myLogger;
    }

    /**
     * Output the given string at the error log level
     *
     * @param log the string to be logged
     */
    public static void error(String log)
    {
        myLogger.severe(log);
    }

    /**
     * Output the given string at the warning log level
     *
     * @param log the string to be logged
     */
    public static void warning(String log)
    {
        myLogger.warning(log);
    }

    /**
     * Output the given string at the info log level
     *
     * @param log the string to be logged
     */
    public static void info(String log)
    {
        myLogger.info(log);
    }

    /**
     * Output the given string for test log level
     *
     * @param log the string to be logged
     */
    public static void test(String log)
    {
        myLogger.fine(log);
    }

    /**
     * Output the given string at the most verbose log level
     *
     * @param log the string to be logged
     */
    public static void debug(String log)
    {
        myLogger.finest(log);
    }

    /**
     * Output the calling method and class names
     */
    public static void trace()
    {
        Throwable         t      = new Throwable();
        StackTraceElement caller = t.getStackTrace()[1];
        myLogger.entering(caller.getClassName(), caller.getMethodName());
    }
}
/*___oOo___*/
