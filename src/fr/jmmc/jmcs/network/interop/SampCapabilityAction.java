/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import fr.jmmc.jmcs.gui.StatusBar;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.SubscribedClientListModel;

/**
 * Generic action dedicated to SAMP capability handling.
 *
 * Your action should extends SampCapabilityAction, implementing composeMessage() to forge the message to be sent by SAMP.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES, Guillaume MELLA.
 */
public abstract class SampCapabilityAction extends RegisteredAction {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(SampCapabilityAction.class.getName());
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** label for broadcast to all */
    private final static String BROADCAST_MENU_LABEL = "All Applications";

    /* members */
    /** SAMP capability to send */
    private final SampCapability _capability;
    /** mType of SAMP capability to send */
    private final String _mType;
    /** Capable clients for the registered capability */
    private final SubscribedClientListModel _capableClients;
    /** Store whether the action should be enabled once SAMP clients are registered for the given capability */
    private boolean _couldBeEnabled = true;

    /**
     * Constructor.
     *
     * @param classPath the path of the class containing the field pointing to
     * the action, in the form returned by 'getClass().getName();'.
     * @param fieldName the name of the field pointing to the action.
     * @param capability the SAMP mType to be sent.
     */
    public SampCapabilityAction(final String classPath, final String fieldName, final SampCapability capability) {
        super(classPath, fieldName);

        _capability = capability;
        _mType = _capability.mType();

        // Get a dynamic list of SAMP clients able to respond to the specified capability.
        _capableClients = SampManager.createSubscribedClientListModel(_mType);

        // Monitor any modification to the capable clients list
        _capableClients.addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                _logger.entering("ListDataListener", "contentsChanged");
                updateMenuAndActionAfterSubscribedClientChange();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalAdded");
                updateMenuAndActionAfterSubscribedClientChange();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalRemoved");
                updateMenuAndActionAfterSubscribedClientChange();
            }
        });

        // but do one first test if one registered app already handle such capability
        updateMenuAndActionAfterSubscribedClientChange();
    }

    /**
     * (dis)Allow the menu entry to be enabled once SAMP clients are registered.
     *
     * @param flag allow enabling if true, disabling otherwise.
     */
    public void couldBeEnabled(boolean flag) {
        _logger.entering("SampCapabilityAction", "couldBeEnabled(" + flag + ")");
        // Update menus only if needed
        if (flag != _couldBeEnabled) {
            _couldBeEnabled = flag;
            updateMenuAndActionAfterSubscribedClientChange();
        }
    }

    /**
     * Set action text.
     * @param text
     */
    public void setText(String text) {
        _logger.entering("SampCapabilityAction", "setText('" + text + "')");
        putValue(Action.NAME, text);
        updateMenuAndActionAfterSubscribedClientChange();
    }

    /**
     * Updates linked JMenu entry to offer all capable clients, plus broadcast.
     */
    private void updateMenuAndActionAfterSubscribedClientChange() {

        // TODO : remove when code is clean !
        if (!SwingUtilities.isEventDispatchThread()) {
            _logger.log(Level.SEVERE, "invalid thread : use EDT", new Throwable());
        }

        // Disabled until a client for the given capabily registers to the hub
        setEnabled(false);

        // Retrieve the JMenu entry for the current capablity
        // or build a new one if it does not already exists

        JMenu menu = SampManager.getMenu(this);
        if (menu == null) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Could not get back menu entry for action '" + this + "'.");
            }
            menu = new JMenu(this);
            SampManager.addMenu(menu, this);
        }

        // First remove all sub-menus
        menu.removeAll();

        // If no client is able to handle specified capability
        final int nbOfClients = _capableClients.getSize();
        if (nbOfClients <= 0) {
            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("No SAMP client available for capability '" + _mType + "'.");
            }
            // Leave the menu disable with no sub-menus
            return;
        }

        // Otherwise, enable menu (if needed)
        setEnabled(_couldBeEnabled);

        // Add generic "All" entry to broadcast message to all capable clients at once
        final JMenuItem broadcastMenuItem = new JMenuItem(this);
        broadcastMenuItem.setText(BROADCAST_MENU_LABEL);
        menu.add(broadcastMenuItem);
        if (_logger.isLoggable(Level.FINEST)) {
            _logger.finest("Added '" + BROADCAST_MENU_LABEL + "' broadcast menu entry for capability '" + _mType + "'.");
        }

        menu.addSeparator();

        // Add each individal client
        for (int i = 0; i < nbOfClients; i++) {
            final Client client = (Client) _capableClients.getElementAt(i);
            final String clientName = client.toString();
            final String clientId = client.getId();

            final JMenuItem individualMenuItem = new JMenuItem(this);
            individualMenuItem.setText(clientName);
            individualMenuItem.setActionCommand(clientId);

            menu.add(individualMenuItem);

            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Added '" + clientName + "' (" + clientId + ") menu entry for capability '" + _mType + "'.");
            }
        }
    }

    /** 
     * Should return the message you want to send
     * @return Samp message parameters as a map
     */
    public abstract Map<?, ?> composeMessage();

    /**
     * This method automatically sends the message returned by composeMessage()
     * to user selected client(s). Children classes should not overwrite this
     * method or must call super implementation to keep SAMP message management.
     *
     * @param e actionEvent coming from SWING objects. It contains in its
     * command the name of the destination.
     */
    @Override
    public final void actionPerformed(final ActionEvent e) {
        _logger.entering("SampCapabilityAction", "actionPerformed");

        // Delegate message forging to app-specific code
        final Map<?, ?> parameters = composeMessage();

        if (parameters != null) {
            StatusBar.show("Sending data through SAMP ...");

            boolean ok = false;
            try {
                // Get the user clicked menu label
                final String command = e.getActionCommand();

                // If the 'All' menu was used
                if (BROADCAST_MENU_LABEL.equals(command)) {
                    // Broadcast the forged message to all capable clients
                    SampManager.broadcastMessage(_mType, parameters);

                } else {
                    // Otherwise only send forged message to the selected client
                    SampManager.sendMessageTo(_mType, command, parameters);
                }

                ok = true;

            } catch (SampException se) {
                _logger.log(Level.SEVERE, "Samp message send failure", se);
            }

            StatusBar.show(
                    (ok) ? "Sending data through SAMP ... done."
                    : "Sending data through SAMP ... failed.");
        }
    }
}
