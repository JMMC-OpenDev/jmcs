/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampManager.java,v 1.3 2010-09-24 12:07:37 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/09/14 14:31:42  lafrasse
 * Added TODOs
 *
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
import org.astrogrid.samp.gui.*;

import fr.jmmc.mcs.gui.*;

/**
 * SampManager singleton class.
 *
 * @author lafrasse
 */
public class SampManager {

    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.interop.SampManager");
    /** Singleton instance */
    private static SampManager _instance = null;
    /** Singleton instance */
    private static GuiHubConnector _connector = null;

    /** Return the singleton instance */
    public static final synchronized SampManager getInstance() throws SampException {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new SampManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /** Hidden constructor */
    protected SampManager() throws SampException {
        // @TODO : init JSamp env.
        ClientProfile profile = DefaultClientProfile.getProfile();
        _connector = new GuiHubConnector(profile);
        System.out.println("_connector = '" + _connector + "'.");

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
    public static void sendMessageTo(String mType, String recipient, Message msg) throws SampException {
        SampManager.getInstance();

        System.out.println("sendMessage()");
    }

    public static void broadcastMessage(String mType, Map parameters) throws SampException {
        SampManager.getInstance();

        Message msg = new Message(mType, parameters);
        _connector.getConnection().notifyAll(msg);

        System.out.println("Sent Message '" + msg + "'.");
    }

    public static void broadcastMessage(SampCapability capability, Map parameters) throws SampException {
        broadcastMessage(capability.mType(), parameters);
    }

    public static void registerCapability(MessageHandler handler) throws SampException {
        SampManager.getInstance();

        System.out.println("registerCapability(" + handler + ")");
        _connector.addMessageHandler(handler);

        // This step required even if no custom message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(10);
    }

    public static String[] capableRecipients(String mType) throws SampException {
        SampManager.getInstance();

        return (String[])_connector.getConnection().getSubscribedClients(mType).keySet().toArray(new String[0]);
    }

    public static String[] capableRecipients(SampCapability capability) throws SampException {
        return capableRecipients(capability.mType());
    }

    public static GuiHubConnector getGuiHubConnector() throws SampException {
        SampManager.getInstance();

        return _connector;
    }
}
