package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.Urls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public class ShowHelpAction extends AbstractAction
{
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
            if (_helpID == null && ( ! _alreadyShown || App.isBetaVersion()) )
            {
                if (App.isBetaVersion()) {
                    new FeedbackReport(null, false, new Exception(
                            "Documentation problem:\nNo helpID found for label '" +
                            label +
                            "'\nWe are working on this problem to solve it."));
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Sorry, documentation not found. This case offen "+
                            "occurs \n in java 1.5 version and Java Web Start applications.",
                            "Documentation problem", JOptionPane.ERROR_MESSAGE);

                }
                setEnabled(false);
                _alreadyShown=true;
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
