/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampCapabilityAction.java,v 1.7 2010-10-06 09:42:24 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2010/10/06 09:09:00  mella
 * Build interop menu entry if it does not always exists
 * Set menu and action state according current client state
 *
 * Revision 1.5  2010/10/05 15:48:35  bourgesl
 * do not send message if composeMessage returns null
 *
 * Revision 1.4  2010/10/05 14:52:31  bourgesl
 * removed SampException in several method signatures
 *
 * Revision 1.3  2010/10/05 12:56:22  mella
 * Add javadoc
 *
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
import java.awt.event.ActionEvent;
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
    /** SAMP capability to send */
    private final SampCapability _capability;
    /** mType of SAMP capability to send */
    private final String _mType;
    /** Capable clients for the registered capability */
    private final SubscribedClientListModel _capableClients;

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

            public void contentsChanged(final ListDataEvent e) {
                _logger.entering("ListDataListener", "contentsChanged");
                updateMenuAndActionAfterSubscribedClientChange();
            }

            public void intervalAdded(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalAdded");
                updateMenuAndActionAfterSubscribedClientChange();
            }

            public void intervalRemoved(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalRemoved");
                updateMenuAndActionAfterSubscribedClientChange();
            }
        });

        // but do one first test if one registered app already handle such capability
        updateMenuAndActionAfterSubscribedClientChange();
    }

    /**
     * Updates linked JMenu entry to offer all capable clients, plus broadcast.
     */
    private void updateMenuAndActionAfterSubscribedClientChange() {

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
            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("No SAMP client available for capability '" + _mType + "'.");
            }
            return;
        }

        // Otherwise, enable action (and menu...)
        setEnabled(true);

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
     * method or must call super implementation to keep samp message management.
     *
     * @param e actionEvent comming from swing objects. It contains in its
     * command the name of the destination.
     */
    public final void actionPerformed(final ActionEvent e) {
        _logger.entering("SampCapabilityAction", "actionPerformed");

        // Delegate message forging to app-specific code
        final Map<?, ?> parameters = composeMessage();

        if (parameters != null) {
            StatusBar.show("Sending data through SAMP ...");

            try {
                // Get the user clicked menu label
                final String command = e.getActionCommand();

                // If the 'All' menu was used
                if (BROADCAST_MENU_LABEL.equals(command)) {
                    // Broadcast the forged message to all capable clients
                    SampManager.broadcastMessage(_mType, parameters);

                    if (_logger.isLoggable(Level.INFO)) {
                        _logger.info("Broadcasted SAMP message to '" + _mType + "' capable clients.");
                    }

                } else {
                    // Otherwise only send forged message to the selected client
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
}
