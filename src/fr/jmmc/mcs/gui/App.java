/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.1 2008-04-22 12:31:41 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.io.*;

import java.util.Vector;
import java.util.logging.*;


/**
 * Class description goes here.
 */
public class App
{
    /** Logger */
    private static final Logger _mainLogger = Logger.getLogger("fr.jmmc");

    /**
     * DOCUMENT ME!
     */
    private static App _sharedInstance;

    /** Stream handler which permit us to keep logs report in strings */
    private static StreamHandler _streamHandler = null;

    /** Console handler which permit us to show logs report on the console */
    private static ConsoleHandler _consoleHandler = new ConsoleHandler();

    /** ByteArrayOutputStream which keeps logs report */
    private static ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream();

    /**
     * Constructor
     */
    protected App()
    {
        try
        {
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            _streamHandler = new StreamHandler(_byteArrayOutputStream,
                    simpleFormatter);

            // We add the memory handler created and the console one to the logger
            _mainLogger.addHandler(_consoleHandler);
            _mainLogger.addHandler(_streamHandler);

            _mainLogger.fine(
                "Memory and console handler created and fixed to feedbackLogger");
            _mainLogger.fine("App object instantiated and logger created");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Returns logs report into a unique string
     *
     * @return logs report into a unique string
     */
    public static String getLogOutput()
    {
        // Needed in order to write all logs in the ouput stream buffer
        _streamHandler.flush();

        return _byteArrayOutputStream.toString();
    }
}
/*___oOo___*/
