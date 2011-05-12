/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
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
    /** launcher instance */
    private static edu.stanford.ejalbert.BrowserLauncher _launcher = null;

    /**
     * Return the BrowserLauncher instance
     * @return BrowserLauncher instance
     */
    private static edu.stanford.ejalbert.BrowserLauncher getLauncher()
    {
        if (_launcher == null) {
            try {
                _launcher = new edu.stanford.ejalbert.BrowserLauncher();

            } catch (UnsupportedOperatingSystemException uose) {
                _logger.log(Level.WARNING, "Cannot initialize browser launcher : ", uose);
            } catch (BrowserLaunchingInitializingException bie) {
                _logger.log(Level.WARNING, "Cannot initialize browser launcher : ", bie);
            }
        }
        return _launcher;
    }

    /**
     * Open in web browser the url passed in argument.
     *
     * @param url url to open in web browser.
     */
    public static void openURL(String url)
    {
        final edu.stanford.ejalbert.BrowserLauncher launcher = getLauncher();
        if (launcher == null) {
            _logger.log(Level.WARNING, "Cannot open '" + url + "' in web browser");
        } else {
            launcher.openURLinBrowser(url);

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("URL '" + url + "' opened in web browser");
            }
        }
    }

    /**
     * Private constructor
     */
    private BrowserLauncher()
    {
        super();
    }
}
/*___oOo___*/
