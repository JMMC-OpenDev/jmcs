/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarResolverWidget.java,v 1.3 2009-10-23 15:38:20 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2009/10/09 08:05:59  lafrasse
 * Made constructor public (!).
 * Refined documentation.
 *
 * Revision 1.1  2009/10/08 14:31:20  lafrasse
 * First release.
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import fr.jmmc.mcs.gui.*;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * Store informations relative to a star.
 */
public class StarResolverWidget extends SearchField implements Observer
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolverWidget");

    /** Container to store retrieved star properties */
    private Star _star = null;

    /**
     * Creates a new StarResolverWidget object.
     *
     * @param star DOCUMENT ME!
     */
    public StarResolverWidget(Star star)
    {
        super("Simbad");

        _star = star;
        _star.addObserver(this);

        addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String starName = e.getActionCommand();

                    if (starName.length() > 0)
                    {
                        _logger.info("Searching CDS Simbad data for star '" +
                            starName + "'.");
                        StatusBar.show("searching CDS Simbad data for star '" +
                            starName +
                            "'... (please wait, this may take a while)");

                        StarResolver resolver = new StarResolver(starName, _star);
                        resolver.resolve();
                    }
                }
            });
    }

    /**
     * Automatically called on attached QueryModel changes.
     */
    public void update(Observable o, Object arg)
    {
        String errorMessage = _star.consumeCDSimbadErrorMessage();

        if (errorMessage != null) // An error occured
        {
            JOptionPane.showMessageDialog(null,
                "CDS Simbad problem :\n" + errorMessage, "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        else // Simbad querying went fine
        {
            StatusBar.show("CDS Simbad star resolution done.");
        }
    }

    /**
     * Main - for StarResolverWidget demonstration and test only.
     */
    public static void main(String[] args)
    {
        // Resolver initialization
        final Star star = new Star();
        star.addObserver(new Observer()
            {
                public void update(Observable o, Object arg)
                {
                    System.out.println("Star changed:\n" + star);
                }
            });

        // GUI initialization
        JFrame frame = new JFrame();
        frame.setTitle("StarResolverWidget Demo");

        Container          container   = frame.getContentPane();
        JPanel             panel       = new JPanel();
        StarResolverWidget searchField = new StarResolverWidget(star);
        panel.add(searchField);
        container.add(panel);
        panel.setVisible(true);

        frame.pack();
        frame.setVisible(true);
    }
}
/*___oOo___*/
