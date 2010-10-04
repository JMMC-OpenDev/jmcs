/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampManager.java,v 1.5 2010-10-04 23:37:32 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2010/10/04 23:35:44  lafrasse
 * Added "Interop" menu handling.
 *
 * Revision 1.3  2010/09/24 12:07:37  lafrasse
 * Added preliminary support for message sending and broadcasting, plus SampCapability management.
 *
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
import org.astrogrid.samp.gui.*;

import fr.jmmc.mcs.gui.*;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.astrogrid.samp.xmlrpc.HubMode;

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

    /** Hook to the "Interop" menu */
    private static JMenu _menu = null;

    /** JMenu to Action relations */
    private static HashMap<SampCapabilityAction,JMenu> _map = null;

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
        _map = new HashMap<SampCapabilityAction, JMenu>();

        // @TODO : init JSamp env.
        ClientProfile profile = DefaultClientProfile.getProfile();
        _connector = new GuiHubConnector(profile);

        // Try to start an external SAMP hub if none available
        Action act = _connector.createHubAction(true, HubMode.CLIENT_GUI);
        act.actionPerformed(null);
        if (_connector.isConnected() == false) {
            StatusBar.show("Could not connect to nor start a SAMP hub.");
        }

        // Build application metadata
        Metadata meta = new Metadata();
        ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
        // @TODO : create some JmcsException !!!

        String applicationName = applicationDataModel.getProgramName();
        meta.setName(applicationName);

        String applicationURL = applicationDataModel.getMainWebPageURL();
        meta.setDescriptionText("More info at " + applicationURL);

        /* @TODO : Add App metadata (cf. Aladin : icon, url, author, ... )
        String iconURL = applicationDataModel.getLogoURL();
        meta.setIconUrl(iconURL);
         */
        _connector.declareMetadata(meta);
        _connector.addConnectionListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _logger.info("SAMP Hub connection status changed: " + e);
                // @TODO : Refresh menu to populate it accordinal on connection
            }
        });
    }

    public static GuiHubConnector getGuiHubConnector() throws SampException {
        SampManager.getInstance();

        return _connector;
    }

    /** Link SampManager instance to the "Interop" menu */
    public static void hookMenu(JMenu menu) {
        _menu = menu;

        // If some capabilities are registered
        if (! _map.isEmpty()) {
            // Make the "Interop" menu visible
            _menu.setVisible(true);
        }
    }

    /** Registerd an app-specific capability */
    public static void registerCapability(SampMessageHandler handler) throws SampException {
        SampManager.getInstance();

        _connector.addMessageHandler(handler);
        _logger.fine("Registered SAMP capability for mType '" + handler.handledMType() + "'.");

        // This step required even if no custom message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(10);
    }

    /** Link a menu entry to its action */
    public static void addMenu(JMenu menu, SampCapabilityAction action) {
        _map.put(action, menu);
    }

    /** Get a menu entry from its action */
    public static JMenu getMenu(SampCapabilityAction action) {
        return _map.get(action);
    }

    /** Sends a given message to a client */
    public static void sendMessageTo(String mType, String recipient, Map parameters) throws SampException {
        SampManager.getInstance();

        Message msg = new Message(mType, parameters);
        _connector.getConnection().notify(recipient, msg);

        _logger.info("Sent '" + mType + "' SAMP message to '" + recipient + "' client.");
    }

    public static void broadcastMessage(String mType, Map parameters) throws SampException {
        SampManager.getInstance();

        Message msg = new Message(mType, parameters);
        _connector.getConnection().notifyAll(msg);

        _logger.info("Broadcasted SAMP message to '" + mType + "' capable clients.");
    }
}
