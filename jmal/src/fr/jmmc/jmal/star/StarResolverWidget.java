/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.gui.SearchField;
import fr.jmmc.mcs.gui.StatusBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Store informations relative to a star.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public class StarResolverWidget extends SearchField implements Observer {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Container to store retrieved star properties */
    private final Star _star;


    /**
     * Creates a new StarResolverWidget object.
     */
    public StarResolverWidget() {
        this(new Star());
    }

    /**
     * Creates a new StarResolverWidget object.
     *
     * @param star star model
     */
    public StarResolverWidget(final Star star) {
        super("Simbad");

        _star = star;
        _star.addObserver(this);

        addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                final String starName = e.getActionCommand().trim();

                if (starName.length() > 0) {
                    if (_logger.isLoggable(Level.INFO)) {
                        _logger.info("Searching CDS Simbad data for star '" + starName + "'.");
                    }
                    StatusBar.show("searching CDS Simbad data for star '"
                            + starName + "'... (please wait, this may take a while)");

                    // Disable search field while request processing to avoid concurrent calls :
                    setEnabled(false);

                    new StarResolver(starName, _star).resolve();
                }
            }
        });
    }

    /**
     * Return the star model
     * @return star model
     */
    public final Star getStar() {
        return _star;
    }

    /**
     * Automatically called on attached Star changes.
     */
    public final void update(final Observable o, final Object arg) {
        Star.Notification notification = Star.Notification.UNKNOWN;

        if (arg != null) {
            notification = (Star.Notification) arg;
        }

        switch (notification) {
            case QUERY_COMPLETE:
                StatusBar.show("CDS Simbad star resolution done.");
                break;

            case QUERY_ERROR:
                final String errorMessage = _star.consumeCDSimbadErrorMessage();
                if (errorMessage != null) {
                    MessagePane.showErrorMessage("CDS Simbad problem :\n" + errorMessage);
                }

                StatusBar.show("CDS Simbad star resolution failed.");
                break;
        }

        // Enable search field after request processing done :
        setEnabled(true);
    }

    /**
     * Main - for StarResolverWidget demonstration and test only.
     * @param args unused
     */
    public static void main(final String[] args) {
        // GUI initialization
        final JFrame frame = App.getFrame();
        frame.setTitle("StarResolverWidget Demo");

        // Force to exit when the frame closes :
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Resolver initialization
        final Star star = new Star();
        star.addObserver(new Observer() {

            public void update(Observable o, Object arg) {
                _logger.severe("Star changed:\n" + star);
            }
        });

        final JPanel panel = new JPanel();
        panel.add(new StarResolverWidget(star));

        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
    }
}
/*___oOo___*/
