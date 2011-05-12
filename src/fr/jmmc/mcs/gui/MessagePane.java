/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This class provides utility methods to create message panes (message, error) with/without exceptions
 *
 * @author bourgesl
 */
public final class MessagePane
{

    /** Logger */
    private static final Logger _logger = Logger.getLogger(MessagePane.class.getName());
    /** default title for error messages */
    private final static String TITLE_ERROR = "Error";
    /** default title for information messages */
    private final static String TITLE_INFO = "Information";

    /**
     * Forbidden constructor
     */
    private MessagePane()
    {
        super();
    }

    // --- ERROR MESSAGES --------------------------------------------------------
    /**
     * Show an error with the given message using EDT if needed
     * @param message message to display
     */
    public static void showErrorMessage(final String message)
    {
        showErrorMessage(message, TITLE_ERROR, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any) using EDT if needed
     * and log the exception
     * @param message message to display
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final Throwable th)
    {
        showErrorMessage(message, TITLE_ERROR, th);
    }

    /**
     * Show an error with the given message and window title using EDT if needed
     * @param message message to display
     * @param title window title to use
     */
    public static void showErrorMessage(final String message, final String title)
    {
        showErrorMessage(message, title, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any) using EDT if needed
     * and window title and log the exception
     * @param message message to display
     * @param title window title to use
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final String title, final Throwable th)
    {

        if (_logger.isLoggable(Level.SEVERE)) {
            if (th != null) {
                _logger.log(Level.SEVERE, "An exception occured: " + message, th);
            } else {
                _logger.severe("A problem occured: " + message);
            }
        }

        // try to get cause if possible
        final String cause;
        if (th != null && th.getCause() != null && th.getCause().getMessage() != null) {
            cause = "\n" + "Cause : " + th.getCause().getMessage();
        } else {
            cause = "";
        }

        final String msg;
        if (th != null && th.getMessage() != null) {
            msg = message + "\n\n" + "Explanation : " + th.getMessage() + cause;
        } else {
            msg = message;
        }

        // display the message within EDT :
        if (SwingUtilities.isEventDispatchThread()) {
            showMessageDialog(msg, title, JOptionPane.ERROR_MESSAGE);
        } else {
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    showMessageDialog(msg, title, JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    /**
     * Show the given message.    
     * The frame size is limited so long messages appear in a scrollpane.
     * @param message message to display
     * @param title window title to use
     * @param messageType the type of message to be displayed:
     *          <code>ERROR_MESSAGE</code>,
     *			<code>INFORMATION_MESSAGE</code>,
     *			<code>WARNING_MESSAGE</code>,
     *          <code>QUESTION_MESSAGE</code>,
     *			or <code>PLAIN_MESSAGE</code>
     */
    private static void showMessageDialog(final String message, final String title, final int messageType)
    {
        final JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBackground(null);
        final JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(null);
        // Try not to display windows bigger than screen for huge messages
        // 40px margins are here to avoid some scrollbars...
        final Dimension dims = new Dimension(Math.min(textArea.getMinimumSize().width + 50, 600),
                Math.min(textArea.getMinimumSize().height + 50, 500));
        sp.setMaximumSize(dims);
        sp.setPreferredSize(dims);
        JOptionPane.showMessageDialog(getApplicationFrame(), sp, title, messageType);
    }

    // --- INFO MESSAGES ---------------------------------------------------------
    /**
     * Show an information with the given message
     * @param message message to display
     */
    public static void showMessage(final String message)
    {
        showMessage(message, TITLE_INFO);
    }

    /**
     * Show an information with the given message and window title
     * @param message message to display
     * @param title window title to use
     */
    public static void showMessage(final String message, final String title)
    {
        showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- CONFIRM MESSAGES ------------------------------------------------------
    /**
     * Show a confirmation dialog to ask if the user wants to overwrite the file with the same name
     * @param fileName file name
     * @return true if the user wants the file replaced, false otherwise.
     */
    public static boolean showConfirmFileOverwrite(final String fileName)
    {
        final String message = "\"" + fileName + "\" already exists. Do you want to replace it ?\n\n"
                + "A file or folder with the same name already exists in the current folder.\n"
                + "Replacing it will overwrite its current contents.";

        // Ask the user if he wants to save modifications
        final Object[] options = {"Cancel", "Replace"};
        final int result = JOptionPane.showOptionDialog(getApplicationFrame(), message, null, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        // If the user clicked the "Replace" button
        if (result == 1) {
            return true;
        }

        return false;
    }

    /**
     * Show a confirmation dialog to ask the given question
     * @param message message to ask
     * @return true if the user answers yes
     */
    public static boolean showConfirmMessage(final String message)
    {
        return showConfirmMessage(getApplicationFrame(), message);
    }

    /**
     * Show a confirmation dialog to ask the given question
     * @param parentComponent Parent component or null
     * @param message message to ask
     * @return true if the user answers yes
     */
    public static boolean showConfirmMessage(final Component parentComponent, final String message)
    {
        final int answer = JOptionPane.showConfirmDialog(getParent(parentComponent), message);

        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Return a parent component / owner for a dialog window
     * @param com component argument
     * @return given component argument or the application frame if the given component is null
     */
    public final static Component getParent(final Component com)
    {
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
    private final static JFrame getApplicationFrame()
    {
        return App.getFrame();
    }
}
