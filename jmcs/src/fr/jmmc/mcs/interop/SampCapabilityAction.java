/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampCapabilityAction.java,v 1.1 2010-10-04 23:31:04 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.RegisteredAction;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.SubscribedClientListModel;

/**
 * Generic action dedicated to SAMP capability handling.
 *
 * Your action should extends SampCapabilityAction, implementing composeMessage() to forge the message to be sent by SAMP.
 */
public abstract class SampCapabilityAction extends RegisteredAction {
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.interop.SampCapabilityAction");

    /** SAMP capability to send */
    SampCapability _capability = null;

    /** mType of SAMP capability to send */
    String _mType = null;

    /** Capable clients for the registered capability */
    SubscribedClientListModel _capableClients = null;

    /** Message to send */
    HashMap _parameters = null;

    /** Reference to the JMenu entry linked to the current action */
    JMenu _menu = null;

    /** Always point to (this) for inner class access */
    SampCapabilityAction _action = null;

    /** Always point to (this) for inner class access */
    private final static String BROADCAST_MENU_LABEL = "All";

    /**
     * Constructor.
     *
     * @sa RegisteredAction for first two parameters
     *
     * @param capability the SAMP mType to be sent.
     */
    public SampCapabilityAction(String classPath, String fieldName, SampCapability capability) {
        super(classPath, fieldName);

        _action = this;

        _capability = capability;
        _mType = _capability.mType();

        // Get a dynamic list of SAMP clients able to respond to the specified capability.
        try {
            _capableClients = new SubscribedClientListModel(SampManager.getGuiHubConnector(), _mType);
        } catch (SampException ex) {
            Logger.getLogger(SampCapabilityAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Monitor any modification to the capable clients list
        _capableClients.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                _logger.entering("ListDataListener", "contentsChanged");
                handleChange();
            }

            public void intervalAdded(ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalAdded");
                handleChange();
            }

            public void intervalRemoved(ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalRemoved");
                handleChange();
            }

            // Updates linked JMenu entry to offer all capable clients, plus broadcast.
            public void handleChange() {
                _logger.entering("ListDataListener", "handleChange");

                // Retrieve the JMenu entry for the current capablity
                _menu = SampManager.getMenu(_action);
                if (_menu == null) {
                    _logger.warning("Could not get back menu entry for action '" + _action + "'.");
                    return;
                }

                // First clear sub-menus (if any)
                _menu.removeAll();

                // If no client is able to handle specified capability
                int nbOfClients = _capableClients.getSize();
                if (nbOfClients <= 0) {
                    // Disable both action and JMenu instance
                    _action.setEnabled(false);
                    _menu.setEnabled(false);
                    _logger.info("No SAMP client available for capability '" + _mType + "'.");
                    return;
                }

                // Otherwise, enable both
                _action.setEnabled(true);
                _menu.setEnabled(true);

                // Add generic "All" entry to broadcast message to all capable clients at once
                JMenuItem broadcastMenuItem = new JMenuItem(_action);
                broadcastMenuItem.setText(BROADCAST_MENU_LABEL);
                _menu.add(broadcastMenuItem);
                _logger.finest("Added '" + BROADCAST_MENU_LABEL + "' broadcast menu entry for capability '" + _mType + "'.");

                _menu.addSeparator();

                // Add each individal client
                for (int i = 0; i < nbOfClients; i++) {
                    Client client = (Client) _capableClients.getElementAt(i);
                    String clientName = client.toString();
                    String clientId = client.getId();

                    JMenuItem individualMenuItem = new JMenuItem(_action);
                    individualMenuItem.setText(clientName);
                    individualMenuItem.setActionCommand(clientId);
                    _menu.add(individualMenuItem);
                    _logger.finer("Added '" + clientName + "' (" + clientId + ") menu entry for capability '" + _mType + "'.");
                }

                _menu.setEnabled(true);
                _menu.setVisible(true);
                _menu.repaint();
                _menu.revalidate();
            }
        });

        // Disabled until a client for the given capabily registers to the hub
        setEnabled(false);
    }

    // @TODO : Monitor hub for registration for our capability to enable back the action

    /** Should return the message you went to send */
    public abstract HashMap composeMessage();

    // Automatically sends the message returned by composeMessage() to user selected client(s)
    public void actionPerformed(java.awt.event.ActionEvent e) {
        _logger.entering("SampCapabilityAction", "actionPerformed");

        StatusBar.show("Sending data through SAMP ...");

        // Get the user clicked menu label
        String command = e.getActionCommand();

        // Delegate message forging to app-specific code
        _parameters = composeMessage();

        try {
            // If the 'All' menu was used
            if (command.equals(BROADCAST_MENU_LABEL)) {
                // Broadcast the forged message to all capable clients
                SampManager.broadcastMessage(_mType, _parameters);
                _logger.info("Broadcasted SAMP message to '" + _mType + "' capable clients.");
            } else {
                // Otherwise only send forged message to he selected client
                SampManager.sendMessageTo(_mType, command, _parameters);
                _logger.info("Sent '" + _mType + "' SAMP message to '" + command + "' client.");
            }
        } catch (SampException ex) {
            StatusBar.show("Sending data through SAMP ... failed.");
            ex.printStackTrace();
            return;
        }

        StatusBar.show("Sending data through SAMP ... done.");
    }
}
