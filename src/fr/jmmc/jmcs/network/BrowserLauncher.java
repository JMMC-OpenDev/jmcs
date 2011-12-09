/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network;

import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a function to open a web page
 * with the default web browser of the user system.
 *
 * It uses <b>BrowserLauncher</b>.
 * 
 * @author Brice COLUCCI, Laurent BOURGES.
 */
public class BrowserLauncher {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(BrowserLauncher.class.getName());
    /** launcher instance */
    private static edu.stanford.ejalbert.BrowserLauncher _launcher = null;

    /**
     * Return the BrowserLauncher instance
     * @return BrowserLauncher instance
     */
    private static edu.stanford.ejalbert.BrowserLauncher getLauncher() {
        if (_launcher == null) {
            try {
                _launcher = new edu.stanford.ejalbert.BrowserLauncher();

            } catch (UnsupportedOperatingSystemException uose) {
                _logger.warn("Cannot initialize browser launcher : ", uose);
            } catch (BrowserLaunchingInitializingException bie) {
                _logger.warn("Cannot initialize browser launcher : ", bie);
            }
        }
        return _launcher;
    }

    /**
     * Open in web browser the url passed in argument.
     *
     * @param url url to open in web browser.
     */
    public static void openURL(String url) {
        final edu.stanford.ejalbert.BrowserLauncher launcher = getLauncher();
        if (launcher == null) {
            _logger.warn("Cannot open '{}' in web browser", url);
        } else {
            launcher.openURLinBrowser(url);

            _logger.debug("URL '{}' opened in web browser", url);
        }
    }

    /**
     * Private constructor
     */
    private BrowserLauncher() {
        super();
    }
}
/*___oOo___*/
