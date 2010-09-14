/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampManager.java,v 1.2 2010-09-14 14:31:42 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/09/13 15:57:18  lafrasse
 * First SAMP manager implementation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import java.util.*;

import java.util.logging.*;

import org.astrogrid.samp.*;
import org.astrogrid.samp.client.*;
import org.astrogrid.samp.hub.*;

import fr.jmmc.mcs.gui.*;

/**
 * SampManager singleton class.
 *
 * @author lafrasse
 */
public class SampManager
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.interop.SampManager");

    /** Singleton instance */
    private static SampManager _instance = null;

    /** Singleton instance */
    private static HubConnector _connector = null;

    /** Return the singleton instance */
    public static final synchronized SampManager getInstance() throws SampException
    {
        // DO NOT MODIFY !!!
        if (_instance == null)
        {
            _instance = new SampManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /** Hidden constructor */
    protected SampManager() throws SampException
    {
        // @TODO : init JSamp env.
        ClientProfile profile = DefaultClientProfile.getProfile();
        _connector = new HubConnector(profile);
        System.out.println("_connector = '" + _connector + "'.");

        // @TODO : start internal hub if none alreday running.
        if ( _connector == null )
        {
            // @TODO : marche pas !!!!
            BasicHubService hub = new BasicHubService(new Random());

            // Reconnect to the new hub
            try
            {
                _connector = new HubConnector(profile);
                System.out.println("_connector = '" + _connector + "'.");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        // Build application metadata
        Metadata meta = new Metadata();
        ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
        // @TODO : create some JmcsException !!!

        String applicationName = applicationDataModel.getProgramName();
        meta.setName(applicationName);

        String applicationURL = applicationDataModel.getMainWebPageURL();
        meta.setDescriptionText("More info at " + applicationURL);
/* @TODO
        String iconURL = applicationDataModel.getLogoURL();
        meta.setIconUrl(iconURL);
*/
        _connector.declareMetadata(meta);
    }

    // @TODO : be able to regsiter to some specific capabilities to retrieve a list of capable applications.

    /** Sends a given message to a client */
    public static void sendMessageTo(/*...*/) throws SampException
    {
        SampManager.getInstance();

        System.out.println("sendMessage()");
    }

    public static void broadcastMessage(/*...*/) throws SampException
    {
        SampManager.getInstance();

        System.out.println("sendMessage()");
    }

    /** Hidden constructor */
    public static void registerCapability(MessageHandler handler) throws SampException
    {
        SampManager.getInstance();

        System.out.println("registerCapability(" + handler + ")");
        _connector.addMessageHandler(handler);

        // This step required even if no custom message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(10);
    }
}
