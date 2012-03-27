/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.astrogrid.samp.Client;
import org.astrogrid.samp.gui.SubscribedClientListModel;

/**
 * Provide a combo box model that contains the up-to-date list of SAMP applications capable of a given SAMP capability.
 * @author Sylvain Lafrasse, Guillaume Mella
 */
public class SampSubscriptionsComboBoxModel extends DefaultComboBoxModel {

    /** Logger */
    private final static Logger _logger = Logger.getLogger(SampSubscriptionsComboBoxModel.class.getName());
    /** Contains the list of capable application for a given mType */
    private final SubscribedClientListModel _clientListModel;

    /**
     * Constructor.
     * @param sampCapability SAMP capability against which the combo box model should be sync.
     */
    public SampSubscriptionsComboBoxModel(SampCapability sampCapability) {

        _clientListModel = SampManager.createSubscribedClientListModel(sampCapability.mType());
        _clientListModel.addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                _logger.entering("ListDataListener", "contentsChanged");
                updateModelOnHubEvent();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalAdded");
                updateModelOnHubEvent();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                _logger.entering("ListDataListener", "intervalRemoved");
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
