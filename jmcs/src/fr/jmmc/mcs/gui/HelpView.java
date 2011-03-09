/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: HelpView.java,v 1.18 2010-01-14 13:20:38 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.17  2009/04/16 15:44:51  lafrasse
 * Jalopization.
 *
 * Revision 1.16  2009/03/23 08:49:09  mella
 * remove println
 *
 * Revision 1.15  2009/03/22 20:56:41  mella
 * Add ShowHelpAction
 *
 * Revision 1.14  2009/02/26 14:17:09  mella
 * fixJarURL has been moved into fr.jmmc.mcs.util.Urls
 *
 * Revision 1.13  2008/12/16 14:52:27  lafrasse
 * Workaround known JVM 1.5.0_16 bug preventig the load of HelpSets embedded in JAR file launched from JNLP.
 *
 * Revision 1.12  2008/11/28 12:54:12  mella
 * Add more information on failure case
 *
 * Revision 1.11  2008/11/21 14:59:53  lafrasse
 * Jalopization.
 *
 * Revision 1.10  2008/11/21 11:15:10  mella
 * Improve html harvesting (especially in webstart mode for use)
 *
 * Revision 1.9  2008/10/16 14:19:34  mella
 * Use new help view handling
 *
 * Revision 1.8  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.7  2008/06/13 08:16:59  bcolucci
 * Check if a null pointer exception was launched from WindowCenterer.
 *
 * Revision 1.6  2008/06/10 08:25:06  bcolucci
 * Center the frame on the screen.
 *
 * Revision 1.5  2008/05/16 12:53:43  bcolucci
 * Removed unecessary try/catch, and added argument checks.
 *
 * Revision 1.4  2008/04/29 14:28:58  bcolucci
 * Added JavaHelp support and automatic documentation generation from HTML.
 *
 * Revision 1.3  2007/02/13 15:35:39  lafrasse
 * Jalopization.
 *
 * Revision 1.2  2007/02/13 13:48:51  lafrasse
 * Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
 *
 * Revision 1.1  2006/11/18 23:13:06  lafrasse
 * Creation.
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Urls;

import java.net.*;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;


/**
 * This class uses the JavaHelp system to show a
 * help window. The informations of the help window have
 * been taken from a file called <b>[module_name]-doc.jar</b>
 * located into the application lib folder and generated by
 * a bash script called <b>jmcsHTML2HelpSet.sh</b>.
 * The programs can ask to show one page.
 * @See ShowHelpAction
 */
public class HelpView
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(HelpView.class.getName());

    /** internal reference to the help broker */
    private static HelpBroker _helpBroker;

    /** instance of help view */
    private static HelpView _instance = null;

    /** inited flag */
    private static boolean _alreadyInited = false;

    /** Show the help window */
    public HelpView()
    {
        _instance = this;
    }

    /**
     * Tell if help set can be used
     *
     * @return true if the help set can be used, false otherwise.
     */
    public static boolean isAvailable()
    {
        if (_instance == null)
        {
            _instance = new HelpView();
        }

        if (_alreadyInited)
        {
            return true;
        }

        URL url = null;

        try
        {
            // Get the helpset file and create the centered help broker 
            url = HelpSet.findHelpSet(null, "documentation.hs");

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("HelpSet.findHelpSet(null, 'documentation.hs') = '" +
                    url + "'.");
            }

            if (url == null)
            {
                url = HelpSet.findHelpSet(null, "/documentation.hs");

                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest(
                        "HelpSet.findHelpSet(null, '/documentation.hs') = '" + url +
                        "'.");
                }
            }

            if (url == null)
            {
                // Works on Mac OS X 10.5 PPC G5 with JVM 1.5.0_16
                // Works on Mac OS X 10.5 Intel with JVM 1.5.0_16
                // Works on Windows XP with JVM 1.6.0_07
                // Works on Linux with JVM 1.5.0_16
                url = _instance.getClass().getClassLoader()
                               .getResource("documentation.hs");

                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest(
                        "_instance.getClass().getClassLoader().getResource('documentation.hs') = '" +
                        url + "'.");
                }
            }

            if (url == null)
            {
                url = _instance.getClass().getClassLoader()
                               .getResource("/documentation.hs");

                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest(
                        "_instance.getClass().getClassLoader().getResource('/documentation.hs') = '" +
                        url + "'.");
                }
            }

            // http://forums.sun.com/thread.jspa?messageID=10522645
            url = Urls.fixJarURL(url);

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("fixJarURL(url) = '" + url + "'.");
            }

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("using helpset url=" + url);
            }

            // check if the url is valid :
            if (url == null) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("No helpset document found.");
                }

                return false;
            }

            HelpSet helpSet = new HelpSet(_instance.getClass().getClassLoader(),
                    url);
            _helpBroker = helpSet.createHelpBroker();
            _helpBroker.setLocation(WindowCenterer.getCenteringPoint(
                    _helpBroker.getSize()));
        }
        catch (Exception ex)
        {
            if (_logger.isLoggable(Level.SEVERE)) {
                _logger.log(Level.SEVERE,
                    "Problem during helpset built (url=" + url + ", classloader=" +
                    _instance.getClass().getClassLoader() + ")", ex);
            }

            return false;
        }

        _alreadyInited = true;

        return true;
    }

    /**
     * Show or hide the help view depending on the value of parameter b.
     *
     * @param b if true, shows this component; otherwise, hides this componentShow or hide help view.
     */
    public static void setVisible(boolean b)
    {
        if (isAvailable())
        {
            // Show the window
            _helpBroker.setDisplayed(b);
        }
    }

    /**
     * Return the first HelpID that ends with given label or null.
     * @param endOfHelpID label or anchor that must be used to search one helpID
     * @return null or full HelpId string
     */
    public static String getHelpID(String endOfHelpID)
    {
        //search helpId into map that ends with label
        Map         m  = _helpBroker.getHelpSet().getCombinedMap();
        Enumeration<?> e  = m.getAllIDs();
        Map.ID      id;

        while (e.hasMoreElements())
        {
            id         = (Map.ID) e.nextElement();

            if (id.getIDString().endsWith(endOfHelpID))
            {
                return id.getIDString();
            }
        }

        return null;
    }

    /**
     * Ask the help view to show the page associated to the given helpID
     * @param helpID
     */
    public static void show(String helpID)
    {
        // show without move if it is already visible
        // one problem is still present : the window is not place in foreground
        // and can be hidden by other windows
        if (_helpBroker.isDisplayed())
        {
            _helpBroker.setViewDisplayed(true);
        }
        else
        {
            _helpBroker.setDisplayed(true);
        }

        _helpBroker.setCurrentID(helpID);
    }
}
/*___oOo___*/
