/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampManager.java,v 1.19 2010-11-14 13:50:39 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.18  2010/11/03 10:05:45  lafrasse
 * Added loads of meta-data exports.
 *
 * Revision 1.17  2010/10/22 11:08:21  bourgesl
 * enable JSamp logs until SAMP integration is production ready
 *
 * Revision 1.16  2010/10/22 11:02:32  bourgesl
 * disable JSamp logs
 *
 * Revision 1.15  2010/10/22 11:01:10  bourgesl
 * added shutdown : close hub connection
 *
 * Revision 1.14  2010/10/14 13:11:09  bourgesl
 * javadoc / comments
 *
 * Revision 1.13  2010/10/11 13:58:28  bourgesl
 * removed overriden disconnect method on GuiHubConnector
 *
 * Revision 1.12  2010/10/06 16:04:45  bourgesl
 * removed TODO
 *
 * Revision 1.11  2010/10/06 09:43:37  bourgesl
 * format
 *
 * Revision 1.10  2010/10/06 09:43:08  bourgesl
 * GuiHubConnector is now a member of SampManager
 *
 * Revision 1.9  2010/10/05 17:32:51  bourgesl
 * define initial subscriptions for Samp clients providing no message handler
 *
 * Revision 1.8  2010/10/05 14:52:09  bourgesl
 * use an internal hub to avoid JNLP issues with external hubs / silently ignore IO exception if another hub is already running
 * no more SampException in several method signatures
 *
 * Revision 1.7  2010/10/05 12:02:39  bourgesl
 * added TODO on create external hub
 * throw an illegal state exception if the hooked menu is already set (signal multiple menu bars)
 *
 * Revision 1.6  2010/10/05 10:17:56  bourgesl
 * fixed warnings / javadoc
 * fixed exception handling / logs
 * fixed member visibility
 *
 * Revision 1.5  2010/10/04 23:37:32  lafrasse
 * Removed unused imports.
 *
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

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.ApplicationDataModel;
import fr.jmmc.mcs.gui.StatusBar;
import java.io.IOException;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.client.ClientProfile;
import org.astrogrid.samp.client.DefaultClientProfile;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.GuiHubConnector;
import org.astrogrid.samp.gui.SubscribedClientListModel;
import org.astrogrid.samp.gui.SysTray;
import org.astrogrid.samp.xmlrpc.HubMode;
import org.astrogrid.samp.xmlrpc.HubRunner;
import org.astrogrid.samp.xmlrpc.XmlRpcKit;

/**
 * SampManager singleton class.
 *
 * @author lafrasse
 */
public final class SampManager {

    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.interop.SampManager");
    /** Singleton instance */
    private static volatile SampManager _instance = null;
    /** Hook to the "Interop" menu */
    private static volatile JMenu _menu = null;
    /** JMenu to Action relations */
    private static final Map<SampCapabilityAction, JMenu> _map = Collections.synchronizedMap(new HashMap<SampCapabilityAction, JMenu>());

    /* members */
    /** Gui hub connector */
    private final GuiHubConnector _connector;

    /**
     * Return the singleton instance
     * @return singleton instance
     */
    public static synchronized SampManager getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new SampManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Hidden constructor
     */
    protected SampManager() {

        // define JSamp log verbosity to warning level (avoid debug messages) :
        Logger.getLogger("org.astrogrid.samp").setLevel(Level.WARNING);

        // @TODO : init JSamp env.
        final ClientProfile profile = DefaultClientProfile.getProfile();

        _connector = new GuiHubConnector(profile);

        // Build application metadata :
        final Metadata meta = new Metadata();

        final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();

        final String applicationName = applicationDataModel.getProgramName();
        meta.setName(applicationName);

        // @TODO : embbed in ApplicationData.xml
        // meta.setDescriptionText("Find Interferometric Calibrators for Optical Observations");

        // @TODO : embbed the real HTML doc URL in ApplicationData.xml
        // meta.setDocumentationUrl(applicationURL);

        // @TODO : embbed the icon in each application JAR file
        // meta.setIconUrl("http://apps.jmmc.fr/~sclws/SearchCal/AppIcon.png");

        // Non-standard meatadata
        meta.put("affiliation.name", "JMMC (Jean-Marie MARIOTTI Center)");
        meta.put("affiliation.url", applicationDataModel.getMainWebPageURL());
        meta.put("affiliation.feedback", "http://jmmc.fr/feedback/");
        meta.put("affiliation.suppport", "http://www.jmmc.fr/support.htm");
        final String lowerCaseApplicationName = applicationName.toLowerCase();
        meta.put(lowerCaseApplicationName + ".authors", "Brought to you by the JMMC Team");
        final String applicationURL = applicationDataModel.getLinkValue();
        meta.put(lowerCaseApplicationName + ".homepage", applicationURL);
        meta.put(lowerCaseApplicationName + ".version", applicationDataModel.getProgramVersion());
        meta.put(lowerCaseApplicationName + ".news", applicationDataModel.getHotNewsRSSFeedLinkValue());
        meta.put(lowerCaseApplicationName + ".compilationdate", applicationDataModel.getCompilationDate());
        meta.put(lowerCaseApplicationName + ".compilatiorversion", applicationDataModel.getCompilatorVersion());
        meta.put(lowerCaseApplicationName + ".releasenotes", applicationDataModel.getReleaseNotesLinkValue());
        meta.put(lowerCaseApplicationName + ".faq", applicationDataModel.getFaqLinkValue());

        _connector.declareMetadata(meta);

        _connector.addConnectionListener(new ChangeListener() {

            public void stateChanged(final ChangeEvent e) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("SAMP Hub connection status changed: " + getHubConnector().isConnected());
                }
            }
        });

        // Try to start an internal SAMP hub if none available
        try {
            // use an internal hub for JNLP issues :
            HubRunner.runHub(getInternalHubMode(), XmlRpcKit.getInstance());
        } catch (IOException ioe) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.log(Level.FINE, "unable to start internal hub (probably another hub is already running)", ioe);
            }
        }

        // try to connect
        _connector.setActive(true);

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(10);

        if (!_connector.isConnected()) {
            StatusBar.show("Could not connect to an existing hub or start an internal SAMP hub.");
        }

        // This step required even if no message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());
    }

    /**
     * Return the JSamp Gui hub connector providing swing actions
     * @return JSamp Gui hub connector providing swing actions
     */
    protected GuiHubConnector getHubConnector() {
        return _connector;
    }

    /* --- STATIC METHODS --------------------------------------------------- */
    /**
     * Explicitely shut down the hub connector
     */
    public static void shutdown() {
        // It is good practice to call setActive(false) when this object is finished with;
        // however if it is not called explicitly, any open connection will unregister itself
        // on object finalisation or JVM termination, as long as the JVM shuts down cleanly.
        _logger.info("SAMP Hub connection closed.");
        
        getGuiHubConnector().setActive(false);
    }

    /**
     * Return the hub mode for the internal Hub (CLIENT_GUI if system tray is supported)
     * @return hub mode
     */
    private static HubMode getInternalHubMode() {
        final HubMode internalMode = SysTray.getInstance().isSupported()
                ? HubMode.CLIENT_GUI
                : HubMode.NO_GUI;
        return internalMode;
    }

    /**
     * Return the JSamp Gui hub connector providing swing actions
     * @return JSamp Gui hub connector providing swing actions
     */
    private static GuiHubConnector getGuiHubConnector() {
        return SampManager.getInstance().getHubConnector();
    }

    /**
     * Create a list model for the registered clients of the given message type
     * @param mType samp message type
     * @return list model for the registered clients
     */
    protected static SubscribedClientListModel createSubscribedClientListModel(final String mType) {
        return new SubscribedClientListModel(SampManager.getGuiHubConnector(), mType);
    }

    /**
     * Returns an action which toggles hub registration.
     *
     * @return   registration toggler action
     */
    public static Action createToggleRegisterAction() {
        final GuiHubConnector connector = getGuiHubConnector();

        final Action[] hubStartActions = new Action[]{
            connector.createHubAction(false, getInternalHubMode())
        };

        return connector.createRegisterOrHubAction(App.getFrame(), hubStartActions);
    }

    /**
     * Returns an action which will display a SAMP hub monitor window.
     *
     * @return   monitor window action
     */
    public static Action createShowMonitorAction() {
        return getGuiHubConnector().createShowMonitorAction();
    }

    /**
     * Register an app-specific capability
     * @param handler message handler
     */
    public static void registerCapability(final SampMessageHandler handler) {
        final GuiHubConnector connector = getGuiHubConnector();

        connector.addMessageHandler(handler);

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Registered SAMP capability for mType '" + handler.handledMType() + "'.");
        }

        // This step required even if no custom message handlers added.
        connector.declareSubscriptions(connector.computeSubscriptions());
    }

    /**
     * Link SampManager instance to the "Interop" menu
     * @param menu interop menu container
     */
    public static synchronized void hookMenu(final JMenu menu) {

        if (_menu != null) {
            throw new IllegalStateException("the interoperability menu is already hooked by SampManager : \n" + _menu + "\n" + menu);
        }

        _menu = menu;

        // If some capabilities are registered
        if (!_map.isEmpty()) {
            // Make the "Interop" menu visible
            _menu.setVisible(true);
        }
    }

    /**
     * Link a menu entry to its action
     * @param menu menu entry
     * @param action samp capability action
     */
    public static void addMenu(final JMenu menu, final SampCapabilityAction action) {
        _map.put(action, menu);
    }

    /**
     * Get a menu entry from its action
     * @param action samp capability action
     * @return menu menu entry
     */
    public static JMenu getMenu(final SampCapabilityAction action) {
        return _map.get(action);
    }

    /**
     * Send the given message to a client
     * @param mType samp message type
     * @param recipient public-id of client to receive message
     * @param parameters message parameters
     *
     * @throws SampException if any Samp exception occured
     */
    public static void sendMessageTo(final String mType, final String recipient, final Map<?, ?> parameters) throws SampException {
        final GuiHubConnector connector = getGuiHubConnector();

        connector.getConnection().notify(recipient, new Message(mType, parameters));

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Sent '" + mType + "' SAMP message to '" + recipient + "' client.");
        }
    }

    /**
     * Send the given message to all clients supporting the given message type
     * @param mType samp message type
     * @param parameters message parameters
     *
     * @throws SampException if any Samp exception occured
     */
    public static void broadcastMessage(final String mType, final Map<?, ?> parameters) throws SampException {
        final GuiHubConnector connector = getGuiHubConnector();

        connector.getConnection().notifyAll(new Message(mType, parameters));

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Broadcasted SAMP message to '" + mType + "' capable clients.");
        }
    }
}
