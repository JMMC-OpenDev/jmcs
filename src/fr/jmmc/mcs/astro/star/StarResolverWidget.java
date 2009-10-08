/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarResolverWidget.java,v 1.1 2009-10-08 14:31:20 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import fr.jmmc.mcs.gui.SearchField;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Store informations relative to a star.
 */
public class StarResolverWidget extends SearchField
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolverWidget");

    /**
     * DOCUMENT ME!
     */
    Star _star = null;

    /**
     * Creates a new StarResolverWidget object.
     *
     * @param star DOCUMENT ME!
     */
    StarResolverWidget(Star star)
    {
        super("Simbad");

        _star = star;

        addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String starName = e.getActionCommand();

                    if (starName.length() > 0)
                    {
                        System.out.println("Searching for '" + starName + "'.");

                        StarResolver resolver = new StarResolver(starName, _star);
                        resolver.resolve();
                    }
                }
            });
    }

    /**
     * Main.
     */
    public static void main(String[] args)
    {
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
