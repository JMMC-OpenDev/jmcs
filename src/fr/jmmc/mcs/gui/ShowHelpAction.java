package fr.jmmc.mcs.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ShowHelpAction extends AbstractAction {

    private String _helpID;

    public ShowHelpAction(String label) {
        // Set Label
        this.putValue(this.NAME, "?");

        // If help is available, then try to get the HelpID that ends with given label
        boolean helpIsAvailable = HelpView.isAvailable();
        setEnabled(helpIsAvailable);
        if (helpIsAvailable) {
            _helpID = HelpView.getHelpID(label);
            // If no helpID found, then show one feedback report and disable action
            if (_helpID == null) {
                new FeedbackReport(
                        new Exception("Documentation problem:\nNo helpID found for label '" +
                        label +
                        "'\nPlease send this feedback to improve documentation."));
                setEnabled(false);
                return;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        HelpView.show(_helpID);
    }
}
