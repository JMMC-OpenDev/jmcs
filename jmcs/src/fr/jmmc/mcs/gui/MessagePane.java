/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MessagePane.java,v 1.1 2010-09-24 15:44:54 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.gui;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class provides utility methods to create message panes (message, error) with/without exceptions
 * @author bourgesl
 */
public final class MessagePane {

  /** Logger */
  private static final Logger _logger = Logger.getLogger(MessagePane.class.getName());
  /** default title for error messages */
  private final static String TITLE_ERROR = "Error";

  /**
   * Forbidden constructor
   */
  private MessagePane() {
    super();
  }

  public static void showErrorMessage(final String message) {
    showErrorMessage(message, TITLE_ERROR, null);
  }

  public static void showErrorMessage(final String message, final Throwable th) {
    showErrorMessage(message, TITLE_ERROR, th);
  }

  public static void showErrorMessage(final String message, final String title) {
    showErrorMessage(message, title, null);
  }

  public static void showErrorMessage(final String message, final String title, final Throwable th) {

    if (th != null && _logger.isLoggable(Level.SEVERE)) {
      _logger.log(Level.SEVERE, "An exception occured", th);
    }

    final String msg;
    if (th != null && th.getMessage() != null) {
      msg = message + "\n\n(" + th.getMessage() + ")";
    } else {
      msg = message;
    }

    JOptionPane.showMessageDialog(getApplicationFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Return a not null parent Frame for a dialog window
   * @param frame given frame argument
   * @return given frame or application frame if the given frame is null
   */
  public final static Frame getOwner(final Frame frame) {
    Frame owner = frame;
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
