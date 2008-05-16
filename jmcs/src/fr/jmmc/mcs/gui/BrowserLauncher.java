/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: BrowserLauncher.java,v 1.1 2008-05-16 12:33:30 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.util.logging.*;


/** Use the BrowserLauncher of edu.stanford.ejalbert package. */
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
