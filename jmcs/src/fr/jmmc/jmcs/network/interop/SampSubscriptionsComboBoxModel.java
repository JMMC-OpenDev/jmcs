/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.gui.SubscribedClientListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a combo box model that contains the up-to-date list of SAMP applications capable of a given SAMP capability.
 * @author Sylvain Lafrasse, Guillaume Mella
 */
public class SampSubscriptionsComboBoxModel extends DefaultComboBoxModel {

    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(SampSubscriptionsComboBoxModel.class.getName());
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /* members */
    /** Contains the list of capable application for a given mType */
    private final SubscribedClientListModel _clientListModel;

    /**
     * Constructor.
     * @param sampCapability SAMP capability against which the combo box model should be sync.
     */
    public SampSubscriptionsComboBoxModel(final SampCapability sampCapability) {

        _clientListModel = SampManager.createSubscribedClientListModel(sampCapability.mType());
        _clientListModel.addListDataListener(new ListDataListener() {
            @Override
            public void contentsChanged(final ListDataEvent e) {
                _logger.trace("ListDataListener.contentsChanged");
                updateModelOnHubEvent();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                _logger.trace("ListDataListener.intervalAdded");
                updateModelOnHubEvent();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                _logger.trace("ListDataListener.intervalRemoved");
                // note: this event is never invoked by JSamp code (1.3) !
                updateModelOnHubEvent();
            }
        });
    }

    /** Update the combo box model content with the refreshed list of capable applications. */
    private void updateModelOnHubEvent() {
        // First flush the combo box model
        removeAllElements();

        // Then fill it with the current list of capable clients
        final int size = _clientListModel.getSize();
        for (int i = 0; i < size; i++) {
            addElement((Client) _clientListModel.getElementAt(i));
        }
    }
}
