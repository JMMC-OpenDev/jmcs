/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.util.Urls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * Show the help item given a button label
 * 
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class ShowHelpAction extends AbstractAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Help id associted to the given label.*/
    private String _helpID;
    /** flag to display only the first missing do in production */
    private static boolean _alreadyShown = false;

    /**
     * Instanciate one action that will show the help view on the page associated to the given label.
     * The label is used to retrieve one target from the documentationTOC.xml file.
     *
     * @param label the key used to retrieve the documentation page.
     */
    public ShowHelpAction(String label) {
        // Set Icon (without additional label)
        final String icon = "/fr/jmmc/jmcs/resource/help.png";
        this.putValue(SMALL_ICON, new ImageIcon(Urls.fixJarURL(getClass().getResource(icon))));

        // If help is available, then try to get the HelpID that ends with given label
        boolean helpIsAvailable = HelpView.isAvailable();
        setEnabled(helpIsAvailable);

        if (helpIsAvailable) {
            _helpID = HelpView.getHelpID(label);

            // If no helpID found, then show one feedback report and disable action
            if (_helpID == null && (!_alreadyShown || App.isBetaVersion())) {
                if (App.isBetaVersion()) {
                    // Show the feedback report :
                    FeedbackReport.openDialog(new Exception(
                            "Documentation problem:\nNo helpID found for label '"
                            + label
                            + "'\nWe are working on this problem to solve it."));
                } else {
                    MessagePane.showErrorMessage(
                            "Sorry, documentation not found. This case often "
                            + "occurs \n in java 1.5 version and Java Web Start applications.",
                            "Documentation problem");

                }
                setEnabled(false);
                _alreadyShown = true;
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HelpView.show(_helpID);
    }
}
