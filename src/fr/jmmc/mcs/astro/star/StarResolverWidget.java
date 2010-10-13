/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarResolverWidget.java,v 1.11 2010-10-13 20:56:30 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2010/09/30 13:27:38  bourgesl
 * removed import
 *
 * Revision 1.9  2010/09/24 15:46:15  bourgesl
 * use MessagePane
 *
 * Revision 1.8  2010/04/07 12:21:39  bourgesl
 * logger instead of system.out in main()
 *
 * Revision 1.7  2010/01/21 10:05:18  bourgesl
 * Define the star name when the query is complete
 * StarResolverWidget can be used in netbeans's component palette
 *
 * Revision 1.6  2010/01/14 12:40:19  bourgesl
 * Fix blanking value with white spaces for proper motion and parallax ' ; '
 * StringBuilder and Logger.isLoggable to avoid string.concat
 *
 * Revision 1.5  2009/12/18 14:42:57  bourgesl
 * added serialVersionUID
 *
 * Revision 1.4  2009/12/16 15:53:02  lafrasse
 * Hardened CDS Simbad science star resolution mecanisms while failing.
 * Code, documentation and log refinments.
 *
 * Revision 1.3  2009/10/23 15:38:20  lafrasse
 * Added error (querying and parsing) management.
 *
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

import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.gui.SearchField;
import fr.jmmc.mcs.gui.StatusBar;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Store informations relative to a star.
 */
public class StarResolverWidget extends SearchField implements Observer
{

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolverWidget");

    /** Container to store retrieved star properties */
    private final Star _star;

    /**
     * Creates a new StarResolverWidget object.
     */
    public StarResolverWidget()
    {
      this(new Star());
    }
    
    /**
     * Creates a new StarResolverWidget object.
     *
     * @param star star model
     */
    public StarResolverWidget(final Star star)
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
                        if (_logger.isLoggable(Level.INFO)) {
                          _logger.info("Searching CDS Simbad data for star '" +
                            starName + "'.");
                        }
                        StatusBar.show("searching CDS Simbad data for star '" +
                            starName +
                            "'... (please wait, this may take a while)");

                        // TODO : check concurrency issues (multiple calls running at the same time) :
                        // TODO : reject concurrent calls on the same object :
                        StarResolver resolver = new StarResolver(starName, _star);
                        resolver.resolve();
                    }
                }
            });
    }

  /**
   * Return the star model
   * @return star model
   */
  public Star getStar() {
    return _star;
  }

    /**
     * Automatically called on attached Star changes.
     */
    public void update(Observable o, Object arg)
    {
        Star.Notification notification = Star.Notification.UNKNOWN;

        if (arg != null)
        {
            notification = (Star.Notification) arg;
        }

        switch (notification)
        {
        case QUERY_COMPLETE:
            StatusBar.show("CDS Simbad star resolution done.");

            break;

        case QUERY_ERROR:

            String errorMessage = _star.consumeCDSimbadErrorMessage();

            if (errorMessage != null) // An error occured
            {
                MessagePane.showErrorMessage(
                        "CDS Simbad problem :\n" + errorMessage);
            }

            StatusBar.show("CDS Simbad star resolution failed.");

            break;
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
                    _logger.severe("Star changed:\n" + star);
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
