package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Urls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class ShowHelpAction extends AbstractAction
{
    /** Help id associted to the given label.*/
    private String _helpID;

    /** Shared frame to avoid one feedbackreport display (and mail) per
     * missing doc indexes
     */
    private static java.awt.Frame _frame = new java.awt.Frame();

    /**
     * Instanciate one action that will show the help view on the page associated to the given label.
     * The label is used to retrieve one target from the documentationTOC.xml file.
     *
     * @param label the key used to retrieve the documentation page.
     */
    public ShowHelpAction(String label)
    {
        // Set Icon (without additional label)
        String icon = "/fr/jmmc/mcs/gui/help.png";
        this.putValue(SMALL_ICON,
            new ImageIcon(Urls.fixJarURL(getClass().getResource(icon))));

        // If help is available, then try to get the HelpID that ends with given label
        boolean helpIsAvailable = HelpView.isAvailable();
        setEnabled(helpIsAvailable);

        if (helpIsAvailable)
        {
            _helpID = HelpView.getHelpID(label);

            // If no helpID found, then show one feedback report and disable action
            if (_helpID == null)
            {
                new FeedbackReport(_frame, false, new Exception(
                        "Documentation problem:\nNo helpID found for label '" +
                        label +
                        "'\nPlease send this feedback to improve documentation."));
                setEnabled(false);

                return;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e)
    {
        HelpView.show(_helpID);
    }
}
