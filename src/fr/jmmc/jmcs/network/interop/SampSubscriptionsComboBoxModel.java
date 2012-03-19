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
 * Synchronized comboBoxModel with samp applications associated to a given samp capability.
 * @author Sylvain Lafrasse, Guillaume Mella
 */
public class SampSubscriptionsComboBoxModel extends DefaultComboBoxModel {

    /** logger */
    private final static Logger logger = Logger.getLogger(SampSubscriptionsComboBoxModel.class.getName());
    private final SubscribedClientListModel clientListModel;

    /**
     * Build one new comboBoxModel and link it the the applications supporting given capability.
     * @param sampCapability capability used to filter applications 
     */
    public SampSubscriptionsComboBoxModel(SampCapability sampCapability) {

        clientListModel = SampManager.createSubscribedClientListModel(sampCapability.mType());
        clientListModel.addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                logger.entering("ListDataListener", "contentsChanged");
                handleHubEvent();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                logger.entering("ListDataListener", "intervalAdded");
                handleHubEvent();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                logger.entering("ListDataListener", "intervalRemoved");
                // note: this event is never invoked by JSamp code (1.3) !
                handleHubEvent();
            }
        });
    }

    /**
     * Perform sync operation between list of applications and combobox model.
     */
    private void handleHubEvent() {
        // clean combobox model
        this.removeAllElements();

        // and fill with list of capable client
        final int size = clientListModel.getSize();
        final Client[] clients = new Client[size];
        for (int i = 0; i < size; i++) {
            this.addElement((Client) clientListModel.getElementAt(i));
        }
    }
}
