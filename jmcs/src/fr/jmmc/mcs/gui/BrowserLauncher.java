/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: BrowserLauncher.java,v 1.2 2008-06-20 08:41:45 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/05/16 12:33:30  bcolucci
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class provides a function to open a web page
 * with the default web browser of the user system.
 *
 * It uses <b>BrowserLauncher</b>.
 */
public class BrowserLauncher
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(BrowserLauncher.class.getName());

    /** Launcher */
    private static edu.stanford.ejalbert.BrowserLauncher _launcher;

    /**
     * Open in web browser the url passed in argument.
     *
     * @param url url to open in web browser.
     */
    public static void openURL(String url)
    {
        try
        {
            _launcher = new edu.stanford.ejalbert.BrowserLauncher();
            _launcher.openURLinBrowser(url);
            _logger.fine("URL '" + url + "' opened in web browser");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot launch '" + url + "' in web browser", ex);
        }
    }
}
/*___oOo___*/
