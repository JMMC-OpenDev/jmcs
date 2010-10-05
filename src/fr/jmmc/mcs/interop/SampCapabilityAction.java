/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampCapabilityAction.java,v 1.3 2010-10-05 12:56:22 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/10/05 10:01:54  bourgesl
 * fixed warnings / javadoc
 * fixed exception handling / logs
 * fixed member visibility
 *
 * Revision 1.1  2010/10/04 23:31:04  lafrasse
 * First revision.
 *
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.RegisteredAction;
import java.util.Map;
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

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** label for broadcast to all */
    private final static String BROADCAST_MENU_LABEL = "All";

    /* members */

    /** Always point to (this) for inner class access */
    private final SampCapabilityAction _action;

    /** SAMP capability to send */
    private final SampCapability _capability;

    /** mType of SAMP capability to send */
    private final String _mType;

    /** Capable clients for the registered capability */
    private final SubscribedClientListModel _capableClients;

    /** Reference to the JMenu entry linked to the current action */
    private JMenu _menu = null;

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

        _action = this;

        _capability = capability;
        _mType = _capability.mType();

        // Get a dynamic list of SAMP clients able to respond to the specified capability.
        try {
            _capableClients = new SubscribedClientListModel(SampManager.getGuiHubConnector(), _mType);
        } catch (SampException se) {
          // TODO : handle correctly this exception : what to do
          throw new IllegalStateException("unable to get Samp clients", se);
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
                    if (_logger.isLoggable(Level.WARNING)) {
                        _logger.warning("Could not get back menu entry for action '" + _action + "'.");
                    }
                    return;
                }

                // First clear sub-menus (if any)
                _menu.removeAll();

                // If no client is able to handle specified capability
                final int nbOfClients = _capableClients.getSize();
                if (nbOfClients <= 0) {
                    // Disable both action and JMenu instance
                    _action.setEnabled(false);
                    _menu.setEnabled(false);

                    if (_logger.isLoggable(Level.INFO)) {
                        _logger.info("No SAMP client available for capability '" + _mType + "'.");
                    }
                    return;
                }

                // Otherwise, enable both
                _action.setEnabled(true);
                _menu.setEnabled(true);

                // Add generic "All" entry to broadcast message to all capable clients at once
                final JMenuItem broadcastMenuItem = new JMenuItem(_action);
                broadcastMenuItem.setText(BROADCAST_MENU_LABEL);
                _menu.add(broadcastMenuItem);

                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("Added '" + BROADCAST_MENU_LABEL + "' broadcast menu entry for capability '" + _mType + "'.");
                }

                _menu.addSeparator();

                // Add each individal client
                for (int i = 0; i < nbOfClients; i++) {
                    final Client client = (Client) _capableClients.getElementAt(i);
                    final String clientName = client.toString();
                    final String clientId = client.getId();

                    final JMenuItem individualMenuItem = new JMenuItem(_action);
                    individualMenuItem.setText(clientName);
                    individualMenuItem.setActionCommand(clientId);

                    _menu.add(individualMenuItem);

                    if (_logger.isLoggable(Level.FINER)) {
                        _logger.finer("Added '" + clientName + "' (" + clientId + ") menu entry for capability '" + _mType + "'.");
                    }
                }

                _menu.setEnabled(true);
                _menu.setVisible(true);

                // force to repaint the menu :
                _menu.revalidate();
            }
        });

        // Disabled until a client for the given capabily registers to the hub
        setEnabled(false);
    }

    // @TODO : Monitor hub for registration for our capability to enable back the action

    /** 
     * Should return the message you want to send
     * @return Samp message parameters as a map
     */
    public abstract Map<?,?> composeMessage();

    /**
     * This method automatically sends the message returned by composeMessage()
     * to user selected client(s). Children classes should not overwrite this
     * method or must call super implementation to keep samp message management.
     *
     * @param e actionEvent comming from swing objects. It contains in its
     * command the name of the destination.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        _logger.entering("SampCapabilityAction", "actionPerformed");

        StatusBar.show("Sending data through SAMP ...");

        // Get the user clicked menu label
        final String command = e.getActionCommand();

        // Delegate message forging to app-specific code
        final Map<?,?> parameters = composeMessage();

        try {
            // If the 'All' menu was used
            if (command.equals(BROADCAST_MENU_LABEL)) {
                // Broadcast the forged message to all capable clients
                SampManager.broadcastMessage(_mType, parameters);

                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("Broadcasted SAMP message to '" + _mType + "' capable clients.");
                }

            } else {
                // Otherwise only send forged message to he selected client
                SampManager.sendMessageTo(_mType, command, parameters);

                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info("Sent '" + _mType + "' SAMP message to '" + command + "' client.");
                }

            }
        } catch (SampException se) {
           _logger.log(Level.SEVERE, "Samp message send failure", se);

            StatusBar.show("Sending data through SAMP ... failed.");
            return;
        }

        StatusBar.show("Sending data through SAMP ... done.");
    }
}
