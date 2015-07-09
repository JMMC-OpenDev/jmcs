/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.star;

import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.SearchField;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * Store informations relative to a star.
 *
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public class StarResolverWidget extends SearchField implements StarResolverProgressListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Menu to choose SIMBAD mirror */
    private final static JPopupMenu _mirrorPopupMenu;

    // Static initialization
    static {
        // init mirrorPopup menu on first display
        _mirrorPopupMenu = new JPopupMenu();

        // Add title
        JMenuItem menuItem = new JMenuItem("Choose Simbad Location:");
        menuItem.setEnabled(false);
        _mirrorPopupMenu.add(menuItem);

        // And populate with StarResolver mirrors
        final Set<String> mirrors = StarResolver.getSimbadMirrors();
        for (final String mirror : mirrors) {
            menuItem = new JMenuItem(mirror);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StarResolver.setSimbadMirror(mirror);
                }
            });
            _mirrorPopupMenu.add(menuItem);
        }
    }

    /* members */
    /** widget listener to get star resolver result */
    private StarResolverListener _childListener = null;
    /** flag indicating if the resolver can resolve multiple identifiers */
    private final boolean _supportMultiple;
    /** star resolver instance */
    private final StarResolver _resolver;
    /** Single future instance used to cancel background requests */
    private Future<StarResolverResult> _future = null;

    /**
     * Creates a new StarResolverWidget object that only supports one single identifier
     */
    public StarResolverWidget() {
        this(false);
    }

    /**
     * Creates a new StarResolverWidget object
     * @param supportMultiple flag indicating if the resolver can resolve multiple identifiers
     */
    public StarResolverWidget(final boolean supportMultiple) {
        super("Simbad", _mirrorPopupMenu);
        this._supportMultiple = supportMultiple;
        this._resolver = new StarResolver(this);

        if (supportMultiple) {
            // fix newline replacement character for copy/paste operations:
            this.setNewLineReplacement(StarResolver.SEPARATOR_SEMI_COLON.charAt(0));
        }

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // note: the action command value is already cleaned by SearchField#cleanText(String)
                final String names = e.getActionCommand();

                final boolean isMultiple = StarResolver.isMultiple(names);

                // Check for multiple identifier support:
                if (!_supportMultiple && isMultiple) {
                    MessagePane.showErrorMessage("Only one identifier expected (remove the ';' character)", "Star resolver problem");
                    return;
                }

                try {
                    // Keep future instance to possibly cancel the job:
                    if (isMultiple) {
                        _future = _resolver.multipleResolve(names);
                    } else {
                        _future = _resolver.resolve(names);
                    }

                    // Disable search field while request processing to avoid concurrent requests:
                    setEnabled(false);

                } catch (IllegalArgumentException iae) {
                    MessagePane.showErrorMessage(iae.getMessage());
                }
            }
        });
    }

    @Override
    protected void performCancel() {
        _logger.debug("performCancel invoked.");
        if (_future != null) {
            // do cancel background requests:
            _future.cancel(true);
        }
    }

    /**
     * @return the widget listener to get star resolver result
     */
    public final StarResolverListener getListener() {
        return _childListener;
    }

    /**
     * @param listener the widget listener to get star resolver result
     */
    public final void setListener(final StarResolverListener listener) {
        this._childListener = listener;
    }

    /**
     * @return flag indicating if the resolver can resolve multiple identifiers
     */
    public boolean isSupportMultiple() {
        return _supportMultiple;
    }

    /**
     * Clean up the current text value before calling action listeners and update the text field.
     * @param text current text value
     * @return cleaned up text value
     */
    @Override
    public String cleanText(final String text) {
        return StarResolver.cleanNames(text);
    }

    /**
     * Handle the given progress message = show it in the StatusBar (EDT)
     * @param message progress message
     */
    @Override
    public void handleProgressMessage(final String message) {
        StatusBar.show(message);
    }

    /**
     * Handle the star resolver result (status, error messages, stars):
     * - show error meassages
     * - enable the text field / focus if any error
     * - anyway: propagate the result to the child listener (EDT)
     * @param result star resolver result
     */
    @Override
    public void handleResult(final StarResolverResult result) {
        _logger.debug("star resolver result:\n{}", result);

        // reset the future instance:
        _future = null;

        SwingUtils.invokeEDT(new Runnable() {
            @Override
            public void run() {
                try {
                    // Handle status & error messages:
                    showResultMessage(result);

                    // Propagate the result to the child listener
                    fireResultToChildListener(result);

                } finally {
                    // Enable search field after request processing done :
                    setEnabled(true);

                    if (result.isErrorStatus()) {
                        requestFocus();
                    }
                }
            }
        });
    }

    public static void showResultMessage(final StarResolverResult result) {
        final String errorMessage;

        // TODO: get both error messages (multiple ?)
        switch (result.getStatus()) {
            case ERROR_SERVER:
                errorMessage = result.getServerErrorMessage();
                break;
            case ERROR_IO:
            case ERROR_PARSING:
                errorMessage = result.getErrorMessage();
                break;

            default:
                errorMessage = null;
        }

        final String warningMessage;

        // Handle multiple matches per identifier:
        if (result.isMultipleMatches()) {
            // TODO: display ambiguous results: let the user select the appropriate star ?
            // Show ambiguous ids for now:
            final List<String> multNames = result.getNamesForMultipleMatches();
            _logger.debug("multNames: {}", multNames);

            final StringBuilder sb = new StringBuilder(256);
            sb.append("Multiple objects found (please refine your query):\n\n");
            for (String name : multNames) {
                sb.append("'").append(name).append("': [ ");
                for (Star star : result.getStars(name)) {
                    String id = star.getId();
                    if (id != null) {
                        sb.append(id);
                    }
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append(" ]\n");
            }
            warningMessage = sb.toString();
        } else {
            warningMessage = null;
        }

        // gather error & warning messages into a single one:
        if (errorMessage != null) {
            String msg = errorMessage;
            if (warningMessage != null) {
                // both messages:
                msg += warningMessage;
            }
            MessagePane.showErrorMessage(msg, "Star resolver problem");
        } else if (warningMessage != null) {
            MessagePane.showWarning(warningMessage, "Star resolver problem");
        }
    }

    void fireResultToChildListener(final StarResolverResult result) {
        if (_childListener != null) {
            _childListener.handleResult(result);
        }
    }

    /**
     * Main - for StarResolverWidget demonstration and test only.
     * @param args unused
     */
    public static void main(final String[] args) {

        // invoke Bootstrapper method to initialize logback now:
        Bootstrapper.getState();
//            LoggingService.setLoggerLevel("fr.jmmc.jmal.star", Level.ALL);
        // GUI initialization (EDT)
        SwingUtils.invokeLaterEDT(new Runnable() {

            @Override
            public void run() {

                // GUI initialization
                final JFrame frame = new JFrame("StarResolverWidget Demo");

                // Force to exit when the frame closes :
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Resolver initialization
                final boolean supportMultiple = true;
                final StarResolverWidget searchField = new StarResolverWidget(supportMultiple);
                searchField.setListener(new StarResolverListener() {

                    @Override
                    public void handleResult(StarResolverResult result) {
                        _logger.info("Result:\n{}", result);
                    }
                });

                final JPanel panel = new JPanel(new BorderLayout());
                panel.add(searchField, BorderLayout.CENTER);

                frame.getContentPane().add(panel);

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
/*___oOo___*/
