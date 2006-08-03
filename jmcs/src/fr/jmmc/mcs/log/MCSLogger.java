/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSLogger.java,v 1.2 2006-07-12 14:15:10 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
     * Output the given string at the finest log level
     *
     * @param log the string to be logged
     */
    public static void finest(String log)
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
