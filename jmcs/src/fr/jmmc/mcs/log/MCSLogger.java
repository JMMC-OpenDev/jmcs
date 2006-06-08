/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSLogger.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.mcs.log;

import java.util.logging.Logger;


/**
 * MCSLogger is the main object to use for logging facilities.
 */
public class MCSLogger
{
    /**
     * DOCUMENT ME!
     */
    static Logger myLogger = Logger.getLogger("fr.jmmc.mcs");

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Logger getLogger()
    {
        return myLogger;
    }

    /**
     * DOCUMENT ME!
     */
    public static void trace()
    {
        Throwable         t      = new Throwable();
        StackTraceElement caller = t.getStackTrace()[1];
        myLogger.entering(caller.getClassName(), caller.getMethodName());
    }
}
/*___oOo___*/
