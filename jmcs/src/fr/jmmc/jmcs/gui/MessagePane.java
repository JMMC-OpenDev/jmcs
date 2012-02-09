/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.App;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utility methods to create message panes (message, error) with/without exceptions
 * 
 * @author Laurent BOURGES, Sylvain LAFRASSE, Guillaume MELLA.
 */
public final class MessagePane {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(MessagePane.class.getName());
    /** default title for error messages */
    private final static String TITLE_ERROR = "Error";
    /** default title for warning messages */
    private final static String TITLE_WARNING = "Warning";
    /** default title for information messages */
    private final static String TITLE_INFO = "Information";
    /** save changes dialog options */
    private final static Object[] SAVE_CHANGES_OPTIONS = {"Save", "Cancel", "Don't Save"};
    /** save changes dialog options */
    private final static Object[] KILL_HUB_OPTIONS = {"Cancel", "Quit"};

    /** Save changes before closing results */
    public enum ConfirmSaveChanges {

        /** Save */
        Save,
        /** Cancel */
        Cancel,
        /** Ignore */
        Ignore
    }

    /**
     * Forbidden constructor
     */
    private MessagePane() {
        super();
    }

    // --- ERROR MESSAGES --------------------------------------------------------
    /**
     * Show an error with the given message using EDT if needed
     * @param message message to display
     */
    public static void showErrorMessage(final String message) {
        showErrorMessage(message, TITLE_ERROR, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any) using EDT if needed
     * and log the exception
     * @param message message to display
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final Throwable th) {
        showErrorMessage(message, TITLE_ERROR, th);
    }

    /**
     * Show an error with the given message and window title using EDT if needed
     * @param message message to display
     * @param title window title to use
     */
    public static void showErrorMessage(final String message, final String title) {
        showErrorMessage(message, title, null);
    }

    /**
     * Show an error with the given message plus the exception message (if any) using EDT if needed
     * and window title and log the exception
     * @param message message to display
     * @param title window title to use
     * @param th exception to use
     */
    public static void showErrorMessage(final String message, final String title, final Throwable th) {

        if (th != null) {
            _logger.error("An exception occured: {}", message, th);
        } else {
            _logger.error("A problem occured: {}", message);
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
        SwingUtils.invokeEDT(new Runnable() {

            @Override
            public void run() {
                showMessageDialog(msg, title, JOptionPane.ERROR_MESSAGE);
            }
        });
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
    private static void showMessageDialog(final String message, final String title, final int messageType) {
        final JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);

        final JScrollPane scrollPane = new JScrollPane(textArea);
        // Try not to display windows bigger than screen for huge messages
        // 40px margins are here to avoid some scrollbars...
        final Dimension dims = new Dimension(Math.min(textArea.getMinimumSize().width + 50, 500),
                Math.min(textArea.getMinimumSize().height + 50, 100));
        scrollPane.setMaximumSize(dims);
        scrollPane.setPreferredSize(dims);

        boolean textAreaBackgroundShouldBeOpaque = (textArea.getMinimumSize().width > dims.width) || (textArea.getMinimumSize().height > dims.height);
        textArea.setOpaque(textAreaBackgroundShouldBeOpaque);
        scrollPane.setOpaque(textAreaBackgroundShouldBeOpaque);
        scrollPane.getViewport().setOpaque(textAreaBackgroundShouldBeOpaque);
        if (!textAreaBackgroundShouldBeOpaque) {
            scrollPane.setBorder(null);
        }

        // ensure window is visible (not iconified):
        App.showFrameToFront();

        JOptionPane.showMessageDialog(getApplicationFrame(), scrollPane, title, messageType);
    }

    // --- WARNING MESSAGES ---------------------------------------------------------
    /**
     * Show an information with the given message
     * @param message message to display
     */
    public static void showWarning(final String message) {
        showWarning(message, TITLE_WARNING);
    }

    /**
     * Show an information with the given message and window title
     * @param message message to display
     * @param title window title to use
     */
    public static void showWarning(final String message, final String title) {
        showMessageDialog(message, title, JOptionPane.WARNING_MESSAGE);
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
        showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- CONFIRM MESSAGES ------------------------------------------------------
    /**
     * Show a confirmation dialog to ask if the user wants to overwrite the file with the same name
     * @param fileName file name
     * @return true if the user wants the file replaced, false otherwise.
     */
    public static boolean showConfirmFileOverwrite(final String fileName) {
        final String message = "\"" + fileName + "\" already exists. Do you want to replace it ?\n\n"
                + "A file or folder with the same name already exists in the current folder.\n"
                + "Replacing it will overwrite its current contents.";

        // Ask the user if he wants to save modifications
        final Object[] options = {"Cancel", "Replace"};

        // ensure window is visible (not iconified):
        App.showFrameToFront();

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
    public static boolean showConfirmMessage(final String message) {
        return showConfirmMessage(getApplicationFrame(), message);
    }

    /**
     * Show a confirmation dialog to ask if the user wants to save changes before closing the application
     * @return true if the user wants the file replaced, false otherwise.
     */
    public static ConfirmSaveChanges showConfirmSaveChangesBeforeClosing() {
        return showConfirmSaveChanges("closing");
    }

    /**
     * Show a confirmation dialog to ask if the user wants to save changes before closing the application.
     * 
     * @param beforeMessage part of the message inserted after 'before ' ?
     * @return true if the user wants the file replaced, false otherwise.
     */
    public static ConfirmSaveChanges showConfirmSaveChanges(final String beforeMessage) {

        // ensure window is visible (not iconified):
        App.showFrameToFront();

        // If the data are NOT saved, handle it before loosing any results !!!
        // Ask the user if he wants to save modifications
        final int result = JOptionPane.showOptionDialog(getApplicationFrame(),
                "Do you want to save changes to this document before " + beforeMessage + "?\nIf you don't save, your changes will be definitively lost.\n\n",
                null, JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, SAVE_CHANGES_OPTIONS, SAVE_CHANGES_OPTIONS[0]);

        // Handle user choice
        switch (result) {
            // If the user clicked the "Save" button
            case 0: // options[0] = "Save" button
                // Save the current data if no cancel occured
                return ConfirmSaveChanges.Save;
//                canLostModifications = _saveFileAction.save();

            // If the user clicked the "Don't Save" button
            case 2: // options[2] = "Don't Save" button
                // Exit
                return ConfirmSaveChanges.Ignore;

            // If the user clicked the "Cancel" button or pressed 'esc' key
            case 1: // options[1] = "Cancel" button
            case JOptionPane.CLOSED_OPTION: // 'esc' key
            default: // Any other case
                // Cancel the exit
                return ConfirmSaveChanges.Cancel;
        }
    }

    /**
     * Show a confirmation dialog to ask if the user wants to kill SAMP hub while quitting.
     * 
     * @return true if the user wants the quit nevertheless, false otherwise.
     */
    public static boolean showConfirmKillHub() {

        // ensure window is visible (not iconified):
        App.showFrameToFront();

        final String applicationName = App.getSharedApplicationDataModel().getProgramName();

        // Ask the user if he wants to kill hub
        final int result = JOptionPane.showOptionDialog(getApplicationFrame(),
                "Quitting '" + applicationName + "' will also terminate the shared SAMP hub,\npotentially preventing other applications interoperability until\nanother hub is started elsewhere.\n\n Proceed with quitting nevertheless ?",
                null, JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, KILL_HUB_OPTIONS, KILL_HUB_OPTIONS[0]);

        // Handle user choice
        switch (result) {
            // If the user clicked the "Quit" button
            case 1: // options[0] = "Quit" button
                // Proceed whith quit
                return true;

            // If the user clicked the "Cancel" button or pressed 'esc' key
            case 0: // options[0] = "Cancel" button
            case JOptionPane.CLOSED_OPTION: // 'esc' key
            default: // Any other case
                // Cancel quitting
                return false;
        }
    }

    /**
     * Show a confirmation dialog to ask the given question
     * @param parentComponent Parent component or null
     * @param message message to ask
     * @return true if the user answers yes
     */
    public static boolean showConfirmMessage(final Component parentComponent, final String message) {

        // ensure window is visible (not iconified):
        App.showFrameToFront();

        final int answer = JOptionPane.showConfirmDialog(getParent(parentComponent), message);

        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Return a parent component / owner for a dialog window
     * @param com component argument
     * @return given component argument or the application frame if the given component is null
     */
    public static Component getParent(final Component com) {
        Component owner = com;
        if (owner == null) {
            owner = getApplicationFrame();
        }
        _logger.debug("dialog owner = {}", owner);
        return owner;
    }

    /**
     * Return the shared application frame
     * @return application frame
     */
    private static JFrame getApplicationFrame() {
        return App.getFrame();
    }
}
