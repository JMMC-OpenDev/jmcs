/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MessagePane.java,v 1.13 2011-03-30 09:07:33 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2011/03/08 12:53:45  bourgesl
 * fixed imports
 * code formating
 *
 * Revision 1.11  2011/02/28 12:37:23  mella
 * Add some margin to avoid some scrollbars in the message dialogs
 * Remove some quotes characters during error display
 *
 * Revision 1.10  2011/02/24 16:20:24  mella
 * Limit size of dialog box for long messages using scrollpane
 *
 * Revision 1.9  2011/02/14 17:09:41  bourgesl
 * removed deprecated / dead code
 *
 * Revision 1.8  2011/02/11 09:59:35  mella
 * Add message of the throwable.getCause() if any in the error dialog box
 *
 * Revision 1.7  2010/10/22 11:00:32  bourgesl
 * javadoc
 *
 * Revision 1.6  2010/10/15 09:26:46  bourgesl
 * fixed typo in confirme overwrite message
 *
 * Revision 1.5  2010/10/15 09:01:54  lafrasse
 * Back-ported the "File Overwriting ?" dialog box from SearchCal to showConfirmFileOverwrite().
 *
 * Revision 1.4  2010/10/11 14:00:18  lafrasse
 * Enhanced error message formating.
 *
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
