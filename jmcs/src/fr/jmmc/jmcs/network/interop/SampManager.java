/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.gui.StatusBar;
import java.io.IOException;
import java.util.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SampManager singleton class.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public final class SampManager {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SampManager.class.getName());
    /** Singleton instance */
    private static volatile SampManager _instance = null;
    /** Hook to the "Interop" menu */
    private static volatile JMenu _menu = null;
    /** JMenu to Action relations */
    private static final Map<SampCapabilityAction, JMenu> _map = Collections.synchronizedMap(new HashMap<SampCapabilityAction, JMenu>(8));

    /* members */
    /** GUI hub connector */
    private final GuiHubConnector _connector;
    /** Store whether we started our own hub instance or used an external on already running */
    private static Hub _internalHub = null;

    /**
     * Return the singleton instance
     * @return singleton instance
     */
    public static synchronized SampManager getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            // _logger.error("SampManager getInstance()", new Throwable());
            _instance = new SampManager();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * @return true if SAMP hub cannot prevent quitting, false otherwise.
     */
    public synchronized boolean allowHubKilling() {

        // If no one else is registered to the hub
        int nbOfConnectedClient = _connector.getClientListModel().getSize();
        if (nbOfConnectedClient < 3) { // 1 for the hub, 1 for us
            _logger.info("No one else but us is registered to SAMP hub, letting application quits.");
            // Let the hub die without prompting confirmation
            return true;
        }

        // If we did not launch the hub ourself
        if ((_internalHub == null) || (!_internalHub.getHubService().isHubRunning())) {
            _logger.info("Application has not launched the SAMP hub internally, letting application quits.");
            // Let the hub die without prompting confirmation
            return true;
        }

        _logger.info("Application has launched the SAMP hub internally, asking user if it should be killed or not.");

        // Ask the user to confirm hub killing
        boolean shouldWeQuit = MessagePane.showConfirmKillHub();
        if (!shouldWeQuit) {
            _logger.info("User dissmissed SAMP hub termination, preventing application from quitting.");
            // Prevent hub dying
            return false;
        }

        _logger.info("User allowed SAMP hub termination, proceeding with application quitting.");
        // Let the hub die
        return true;
    }

    /**
     * Explicitely shut down the hub connector
     */
    public static synchronized void shutdown() {
        if (_instance != null) {
            _instance.shutdownNow();
            _instance = null;
            _internalHub = null;
        }
    }

    /**
     * Hidden constructor
     */
    protected SampManager() {

        // @TODO : init JSamp env.
        final ClientProfile profile = DefaultClientProfile.getProfile();

        _connector = new GuiHubConnector(profile);

        // Build application metadata
        final ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();
        final String applicationName = applicationDataModel.getProgramName();
        Metadata metaData = forgeSampMetaDataFromApplicationDataModel(applicationName, applicationDataModel);
        _connector.declareMetadata(metaData);

        // Monitor hub connections
        _connector.addConnectionListener(new SampConnectionChangeListener());

        // Try to connect
        _connector.setActive(true);
        if (!_connector.isConnected()) {
            // Try to start an internal SAMP hub if none available (JNLP do not support external hub) :
            try {
                _internalHub = Hub.runHub(getInternalHubMode());
            } catch (IOException ioe) {
                _logger.debug("unable to start internal hub (probably another hub is already running)", ioe);
            }

            // Retry connection
            _connector.setActive(true);
        }
        if (_connector.isConnected()) {
            _logger.info("Application ['{}'] connected to the SAMP Hub.", applicationName);
        } else {
            StatusBar.show("Could not connect to an existing hub or start an internal SAMP hub.");
        }

        // Keep a look out for hubs if initial one shuts down
        _connector.setAutoconnect(5);

        // This step required even if no message handlers added.
        _connector.declareSubscriptions(_connector.computeSubscriptions());
    }

    private Metadata forgeSampMetaDataFromApplicationDataModel(final String applicationName, final ApplicationDataModel applicationDataModel) {

        final Metadata meta = new Metadata();

        meta.setName(applicationName);

        final String sampDescription = applicationDataModel.getSampDescription();
        if (sampDescription != null) {
            meta.setDescriptionText(sampDescription);
        }

        final String documentationUrl = applicationDataModel.getDocumetationUrl();
        if (documentationUrl != null) {
            meta.setDocumentationUrl(documentationUrl);
        }

        // @TODO : embbed the icon in each application JAR file
        // meta.setIconUrl("http://apps.jmmc.fr/~sclws/SearchCal/AppIcon.png");

        // Non-standard meatadata
        meta.put("affiliation.name", applicationDataModel.getShortCompanyName() + " (" + applicationDataModel.getLegalCompanyName() + ")");

        meta.put("affiliation.url", applicationDataModel.getMainWebPageURL());

        final String jnlpUrl = applicationDataModel.getJnlpUrl();
        if (jnlpUrl != null) {
            meta.put(SampMetaData.JNLP_URL.id(), jnlpUrl);
        }

        final String userSupportUrl = applicationDataModel.getUserSupportURL();
        if (userSupportUrl != null) {
            meta.put("affiliation.support", userSupportUrl);
        }

        final String lowerCaseApplicationName = applicationName.toLowerCase();
        String authors = applicationDataModel.getAuthors();
        if (authors != null) {
            meta.put(SampMetaData.AUTHORS.id(), "Brought to you by " + authors);
        }

        meta.put(SampMetaData.HOMEPAGE_URL.id(), applicationDataModel.getLinkValue());

        meta.put(SampMetaData.RELEASE_VERSION.id(), applicationDataModel.getProgramVersion());

        meta.put(SampMetaData.RELEASE_DATE.id(), applicationDataModel.getCompilationDate());

        final String newsUrl = applicationDataModel.getHotNewsRSSFeedLinkValue();
        if (newsUrl != null) {
            meta.put(SampMetaData.RSS_URL.id(), newsUrl);
        }

        final String releaseNoteUrl = applicationDataModel.getReleaseNotesLinkValue();
        if (releaseNoteUrl != null) {
            meta.put(SampMetaData.RELEASENOTES_URL.id(), releaseNoteUrl);
        }

        final String faq = applicationDataModel.getFaqLinkValue();
        if (faq != null) {
            meta.put(SampMetaData.FAQ_URL.id(), faq);
        }

        return meta;
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
    private GuiHubConnector getHubConnector() {
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
     * Indicates whether this connector is currently registered with a
     * running hub.
     *
     * @return true if currently connected to a hub
     */
    public static boolean isConnected() {
        return SampManager.getInstance().getHubConnector().isConnected();
    }

    /**
     * Create a list model for the registered clients of the given message type
     * @param mType samp message type
     * @return list model for the registered clients
     */
    public static SubscribedClientListModel createSubscribedClientListModel(final String mType) {
        return new SubscribedClientListModel(SampManager.getGuiHubConnector(), mType);
    }

    /**
     * Create a list model for the registered clients of given message types
     * @param mTypes samp message types
     * @return list model for the registered clients
     */
    public static SubscribedClientListModel createSubscribedClientListModel(final String[] mTypes) {
        return new SubscribedClientListModel(SampManager.getGuiHubConnector(), mTypes);
    }

    /**
     * Returns an action which toggles hub registration.
     *
     * @return registration toggle action
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
     * @return monitor window action
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

        // This step required even if no custom message handlers added.
        connector.declareSubscriptions(connector.computeSubscriptions());

        _logger.info("Registered SAMP capability for mType '{}'.", handler.handledMType());
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
     * Return the meta data corresponding to the given client Id known by the hub
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
     * Return the list of id for a given SAMP client name
     * @param name client
     * @return id list
     */
    public static List<String> getClientIdsForName(final String name) {
        final List<String> clientIdList = new ArrayList<String>();
        final Map clientMap = getGuiHubConnector().getClientMap();
        for (Iterator it = clientMap.keySet().iterator(); it.hasNext();) {
            Client client = (Client) clientMap.get(it.next());
            if (client.getMetadata().getName().matches(name)) {
                clientIdList.add(client.getId());
            }
        }
        return clientIdList;
    }

    /**
     * Send the given message to a client
     * @param mType samp message type
     * @param recipient public-id of client to receive message
     * @param parameters message parameters
     *
     * @throws SampException if any Samp exception occurred
     */
    public static void sendMessageTo(final String mType, final String recipient, final Map<?, ?> parameters) throws SampException {
        final GuiHubConnector connector = getGuiHubConnector();

        final long start = System.nanoTime();

        connector.getConnection().notify(recipient, new Message(mType, parameters));

        if (_logger.isInfoEnabled()) {
            _logger.info("Sent '{}' SAMP message to '{}' client ({} ms)",
                    new Object[]{mType, recipient, 1e-6d * (System.nanoTime() - start)});
        }
    }

    /**
     * Send the given message to all clients supporting the given message type
     * @param mType samp message type
     * @param parameters message parameters
     *
     * @throws SampException if any Samp exception occurred
     */
    public static void broadcastMessage(final String mType, final Map<?, ?> parameters) throws SampException {
        final GuiHubConnector connector = getGuiHubConnector();

        final long start = System.nanoTime();

        connector.getConnection().notifyAll(new Message(mType, parameters));

        if (_logger.isInfoEnabled()) {
            _logger.info("Broadcasted SAMP message to '{}' capable clients ({} ms)",
                    mType, 1e-6d * (System.nanoTime() - start));
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

            _logger.info("SAMP Hub connection status : {}", ((connector.isConnected()) ? "registered" : "unregistered"));
        }
    }
}
