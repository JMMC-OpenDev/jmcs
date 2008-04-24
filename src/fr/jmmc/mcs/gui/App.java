/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: App.java,v 1.2 2008-04-24 16:00:02 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/04/22 12:31:41  bcolucci
 * App object creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.io.*;

import java.net.URL;

import java.util.Vector;
import java.util.logging.*;


/**
 * Class description goes here.
 */
public class App
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc");

    /** Singleton reference */
    private static App _sharedInstance;

    /** Stream handler which permit us to keep logs report in strings */
    private static StreamHandler _streamHandler = null;

    /** Console handler which permit us to show logs report on the console */
    private static ConsoleHandler _consoleHandler = new ConsoleHandler();

    /** ByteArrayOutputStream which keeps logs report */
    private static ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream();

    /** Shared application data model */
    private static ApplicationDataModel _applicationDataModel;

    /**
     * Constructor
     */
    protected App()
    {
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        _streamHandler = new StreamHandler(_byteArrayOutputStream,
                simpleFormatter);

        // We add the memory handler created and the console one to the logger
        _logger.addHandler(_consoleHandler);
        _logger.addHandler(_streamHandler);

        _logger.fine(
            "Memory and console handler created and fixed to feedbackLogger");
        _logger.fine("App object instantiated and logger created");

        // Init _applicationDataModel
        Package p = getClass().getPackage();

        // Replace '.' by '/' of package name
        String packageName = p.getName().replace(".", "/");
        String xmlLocation = packageName + "/ApplicationData.xml";
        _logger.fine("xmlLocation='" + xmlLocation + "'");

        try
        {
            // Open XML file at path
            URL xmlURL = getClass().getClassLoader().getResource(xmlLocation);
            _logger.fine("xmlURL='" + xmlURL + "'");
            _applicationDataModel = new ApplicationDataModel(xmlURL);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot retrieve application data from xml", ex);
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

    /**
     * Show about box
     */
    public static void showAboutBox()
    {
        new AboutBox(_applicationDataModel);
    }

    /**
     * DOCUMENT ME!
     */
    public static void showFeedbackReport()
    {
        new FeedbackReport(_applicationDataModel);
    }

    /**
     * DOCUMENT ME!
     */
    public static void showSplashScreen()
    {
        try
        {
            // Show splash Screen window
            SplashScreen splashScreen = new SplashScreen(_applicationDataModel);
            Thread.sleep(8000);

            splashScreen.dispose();
            _logger.fine("Hide splashscreen");
        }
        catch (Exception ex)
        {
            _logger.severe("Cannot instantiate main object");
        }
    }
}
/*___oOo___*/
