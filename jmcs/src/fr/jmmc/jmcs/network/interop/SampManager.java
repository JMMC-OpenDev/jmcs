/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.gui.StatusBar;
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
import org.astrogrid.samp.Client;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.client.ClientProfile;
import org.astrogrid.samp.client.DefaultClientProfile;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.GuiHubConnector;
import org.astrogrid.samp.gui.SubscribedClientListModel;
import org.astrogrid.samp.gui.SysTray;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;

/**
 * SampManager singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
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
     * Explicitely shut down the hub connector
     */
    public static synchronized void shutdown() {
        if (_instance != null) {
            _instance.shutdownNow();
            _instance = null;
        }
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
        meta.put("affiliation.support", "http://www.jmmc.fr/support.htm");

        final String lowerCaseApplicationName = applicationName.toLowerCase();
        meta.put(lowerCaseApplicationName + ".authors", "Brought to you by the JMMC Team");
        meta.put(lowerCaseApplicationName + ".homepage", applicationDataModel.getLinkValue());
        meta.put(lowerCaseApplicationName + ".version", applicationDataModel.getProgramVersion());
        meta.put(lowerCaseApplicationName + ".news", applicationDataModel.getHotNewsRSSFeedLinkValue());
        meta.put(lowerCaseApplicationName + ".compilationdate", applicationDataModel.getCompilationDate());
        meta.put(lowerCaseApplicationName + ".compilatorversion", applicationDataModel.getCompilatorVersion());
        meta.put(lowerCaseApplicationName + ".releasenotes", applicationDataModel.getReleaseNotesLinkValue());
        meta.put(lowerCaseApplicationName + ".faq", applicationDataModel.getFaqLinkValue());

        _connector.declareMetadata(meta);

        _connector.addConnectionListener(new SampConnectionChangeListener());

        // try to connect :
        _connector.setActive(true);

        if (!_connector.isConnected()) {

            // Try to start an internal SAMP hub if none available (JNLP do not support external hub) :
            try {
                Hub.runHub(getInternalHubMode());
            } catch (IOException ioe) {
                if (_logger.isLoggable(Level.FINE)) {
                    _logger.log(Level.FINE, "unable to start internal hub (probably another hub is already running)", ioe);
                }
            }

            // retry to connect :
            _connector.setActive(true);
        }

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(5);

        if (!_connector.isConnected()) {
            StatusBar.show("Could not connect to an existing hub or start an internal SAMP hub.");
        }

        // This step required even if no message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());
    }

    /**
     * Shutdown operations
     */
    private void shutdownNow() {
        // It is good practice to call setActive(false) when this object is finished with;
        // however if it is not called explicitly, any open connection will unregister itself
        // on object finalisation or JVM termination, as long as the JVM shuts down cleanly.

        _connector.setActive(false);
        _logger.info("SAMP Hub connection closed.");
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
     * Return the hub service mode for the internal Hub (CLIENT_GUI if system tray is supported)
     * @return hub mode
     */
    private static HubServiceMode getInternalHubMode() {
        final HubServiceMode internalMode = SysTray.getInstance().isSupported()
                ? HubServiceMode.CLIENT_GUI
                : HubServiceMode.NO_GUI;
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
     * Return the meta data correponding to the given client Id known by the hub
     * @param clientId client id
     * @return meta data or null
     */
    public static Metadata getMetaData(final String clientId) {
        final Client client = (Client) getGuiHubConnector().getClientMap().get(clientId);
        if (client != null) {
            return client.getMetadata();
        }
        return null;
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

        final long start = System.nanoTime();

        connector.getConnection().notify(recipient, new Message(mType, parameters));

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Sent '" + mType + "' SAMP message to '" + recipient + "' client (" + 1e-6d * (System.nanoTime() - start) + " ms)");
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

        final long start = System.nanoTime();

        connector.getConnection().notifyAll(new Message(mType, parameters));

        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Broadcasted SAMP message to '" + mType + "' capable clients (" + 1e-6d * (System.nanoTime() - start) + " ms)");
        }
    }

    /**
     * Samp Hub Connection Change listener
     */
    private final static class SampConnectionChangeListener implements ChangeListener {

        /**
         * Invoked when the hub connection has changed its state i.e.
         * when this connector registers or unregisters with a hub.
         *
         * @param e  a ChangeEvent object
         */
        @Override
        public void stateChanged(final ChangeEvent e) {
            final GuiHubConnector connector = (GuiHubConnector) e.getSource();

            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("SAMP Hub connection status : " + ((connector.isConnected()) ? "registered" : "unregistered"));
            }
        }
    }
}
