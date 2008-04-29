/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: HelpView.java,v 1.4 2008-04-29 14:28:58 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.net.URL;

import java.util.logging.*;

import javax.help.*;


/** Show the help window */
public class HelpView
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(HelpView.class.getName());

    /** Show the help window */
    public HelpView()
    {
        try
        {
            // Get the helpset file and create the help broker
            URL        url        = HelpSet.findHelpSet(null, "documentation.hs");
            HelpSet    helpSet    = new HelpSet(null, url);
            HelpBroker helpBroker = helpSet.createHelpBroker();

            // Show the window
            helpBroker.setDisplayed(true);
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Cannot instantiate HelpView object", ex);
        }
    }
}
/*___oOo___*/
