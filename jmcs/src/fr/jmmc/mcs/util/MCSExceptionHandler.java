/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSExceptionHandler.java,v 1.2 2010-09-24 15:43:32 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/09/23 19:36:05  bourgesl
 * new generic exception handler (awt, thread) for uncaught exceptions
 *
 */
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.gui.FeedbackReport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * This class provides Java 5 Thread uncaught exception handlers
 *
 * see http://stuffthathappens.com/blog/2007/10/07/programmers-notebook-uncaught-exception-handlers/
 *
 * see http://stuffthathappens.com/blog/2007/10/15/one-more-note-on-uncaught-exception-handlers/
 *
 * TODO : javadoc
 *
 * @author bourgesl
 */
public final class MCSExceptionHandler {

  /** Logger */
  private static final Logger _logger = Logger.getLogger(MCSExceptionHandler.class.getName());

  /**
   * Public constructor used by reflection
   */
  public MCSExceptionHandler() {
    super();
  }

  /**
   * AWT exception handler
   *
   * @param e the exception
   */
  public void handle(Throwable e) {
    showException(e);
  }

  public static void installLoggingHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler());
  }

  public static void installSwingHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new SwingExceptionHandler());

    System.setProperty("sun.awt.exception.handler", MCSExceptionHandler.class.getName());
  }

  private static boolean isFilteredException(final Throwable e) {
    if (e instanceof ThreadDeath) {
      return true;
    }
    return false;
  }

  private static void logException(final Thread t, final Throwable e) {
    _logger.log(Level.SEVERE, "Unexpected Exception occured in thread " + t.getName(), e);
  }

  /**
   * Report the exception to the user via Swing
   * @param e the exception
   */
  private static void showException(final Throwable e) {
    // Show feedback report (modal and do not exit on close) :
    // Note : FeedbackReport already logs the exception
    new FeedbackReport(true, e);
  }

  /**
   * Logging exception handler
   */
  private final static class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    public void uncaughtException(final Thread t, final Throwable e) {
      if (!isFilteredException(e)) {
        logException(t, e);
      }
    }
  }

  /**
   * Swing exception handler
   */
  private final static class SwingExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    public void uncaughtException(final Thread t, final Throwable e) {
      if (!isFilteredException(e)) {
        if (SwingUtilities.isEventDispatchThread()) {
          showException(e);
        } else {
          SwingUtilities.invokeLater(new Runnable() {

            public void run() {
              showException(e);
            }
          });
        }
      }
    }
  }
}
