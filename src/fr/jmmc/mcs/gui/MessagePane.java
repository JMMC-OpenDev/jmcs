/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MessagePane.java,v 1.4 2010-10-11 14:00:18 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2010/10/11 13:49:51  lafrasse
 * Ensures that error message dialogs are done in EDT.
 *
 * Revision 1.2  2010/09/30 15:13:03  bourgesl
 * added methods for information and confirmation messages
 * complete javadoc
 *
 * Revision 1.1  2010/09/24 15:44:54  bourgesl
 * initial version : utility class to show message dialogs
 *
 */
package fr.jmmc.mcs.gui;

import java.awt.Component;
import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This class provides utility methods to create message panes (message, error) with/without exceptions
 *
 *
 * TODO : javadoc
 *
 * @author bourgesl
 */
public final class MessagePane {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(MessagePane.class.getName());
    /** default title for error messages */
    private final static String TITLE_ERROR = "Error";
    /** default title for information messages */
    private final static String TITLE_INFO = "Information";

    /**
     * Forbidden constructor
     */
    private MessagePane() {
        super();
    }

    // --- ERROR MESSAGES --------------------------------------------------------
    /**
     * Show an error with the given message
     * @param message message to display
     */
    public static void showErrorMessage(final String message) {
        showErrorMessage(message, TITLE_ERROR, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any)
     * and log the exception
     * @param message message to display
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final Throwable th) {
        showErrorMessage(message, TITLE_ERROR, th);
    }

    /**
     * Show an error with the given message and window title
     * @param message message to display
     * @param title window title to use
     */
    public static void showErrorMessage(final String message, final String title) {
        showErrorMessage(message, title, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any)
     * and window title and log the exception
     * @param message message to display
     * @param title window title to use
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final String title, final Throwable th) {

        if (th != null && _logger.isLoggable(Level.SEVERE)) {
            _logger.log(Level.SEVERE, "An exception occured", th);
        }

        final String msg;
        if (th != null && th.getMessage() != null) {
            msg = message + "\n\n" + "Explanation : \"" + th.getMessage() + "\".";
        } else {
            msg = message;
        }

        // update the status bar within EDT :
        if (SwingUtilities.isEventDispatchThread()) {
            showMessageDialog(msg, title);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    showMessageDialog(msg, title);
                }
            });
        }
    }

    /**
     * Show an error with the given message
     * @param message message to display
     * @param title window title to use
     */
    private static void showMessageDialog(final String message, final String title) {
        JOptionPane.showMessageDialog(getApplicationFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    // --- INFO MESSAGES ---------------------------------------------------------
    /**
     * Show an information with the given message
     * @param message message to display
     */
    public static void showMessage(final String message) {
        showMessage(message, TITLE_INFO);
    }

    /**
     * Show an information with the given message and window title
     * @param message message to display
     * @param title window title to use
     */
    public static void showMessage(final String message, final String title) {
        JOptionPane.showMessageDialog(getApplicationFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- CONFIRM MESSAGES ------------------------------------------------------
    /**
     * Show a confirmation dialog to ask if the user wants to overwrite the file with the same name
     * @param fileName file name
     * @return true if the user answers yes
     */
    public static boolean showConfirmFileOverwrite(final String fileName) {
        return showConfirmMessage("File \'" + fileName + "\' already exists.\nDo you want to overwrite this file ?");
    }

    /**
     * Show a confirmation dialog to ask the given question
     * @param message message to ask
     * @return true if the user answers yes
     */
    public static boolean showConfirmMessage(final String message) {
        return showConfirmMessage(getApplicationFrame(), message);
    }

    /**
     * Show a confirmation dialog to ask the given question
     * @param parentComponent Parent component or null
     * @param message message to ask
     * @return true if the user answers yes
     */
    public static boolean showConfirmMessage(final Component parentComponent, final String message) {
        final int answer = JOptionPane.showConfirmDialog(getParent(parentComponent), message);

        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Return a parent component / owner for a dialog window
     * @param com component argument
     * @return given component argument or the application frame if the given component is null
     */
    public final static Frame getOwner(final Frame com) {
        return (Frame) getParent(com);
    }

    /**
     * Return a parent component / owner for a dialog window
     * @param com component argument
     * @return given component argument or the application frame if the given component is null
     */
    public final static Component getParent(final Component com) {
        Component owner = com;
        if (owner == null) {
            owner = getApplicationFrame();
        }
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("dialog owner = " + owner);
        }
        return owner;
    }

    /**
     * Return the shared application frame
     * @return application frame
     */
    private final static JFrame getApplicationFrame() {
        return App.getFrame();
    }
}
