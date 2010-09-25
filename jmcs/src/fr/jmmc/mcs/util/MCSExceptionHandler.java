/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSExceptionHandler.java,v 1.5 2010-09-25 13:54:07 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2010/09/25 13:41:14  bourgesl
 * new method installThreadHandler(thread) to set uncaughtException handler for new threads
 *
 * Revision 1.3  2010/09/25 12:17:42  bourgesl
 * more logs about threads, exception handler to inspect JNLP context
 *
 * Revision 1.2  2010/09/24 15:43:32  bourgesl
 * removed unused import
 *
 * Revision 1.1  2010/09/23 19:36:05  bourgesl
 * new generic exception handler (awt, thread) for uncaught exceptions
 *
 */
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.gui.FeedbackReport;
import java.lang.reflect.InvocationTargetException;
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
  /** uncaughtException handler singleton */
  private static volatile Thread.UncaughtExceptionHandler exceptionHandler = null;

  public static void installLoggingHandler() {
    setExceptionHandler(new LoggingExceptionHandler());
  }

  public static void installSwingHandler() {
    // AWT exception handler for modal dialogs :
    System.setProperty("sun.awt.exception.handler", MCSExceptionHandler.class.getName());

    setExceptionHandler(new SwingExceptionHandler());
  }

  public static void installThreadHandler(final Thread thread) {
    final Thread.UncaughtExceptionHandler handler = getExceptionHandler();
    if (handler != null) {
      applyUncaughtExceptionHandler(thread, handler);
    } else {
      _logger.info("No UncaughtExceptionHandler defined !");
    }
  }

  private static Thread.UncaughtExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  private static synchronized void setExceptionHandler(final Thread.UncaughtExceptionHandler handler) {
    if (handler != null) {
      // Force security checks
      System.setSecurityManager(null);

      exceptionHandler = handler;

      applyUncaughtExceptionHandler(handler);
    }
  }

  private static void applyUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler handler) {

    _logger.info("New UncaughtExceptionHandler = " + handler);

    _logger.info("Current Default UncaughtExceptionHandler = " + Thread.getDefaultUncaughtExceptionHandler());

    Thread.setDefaultUncaughtExceptionHandler(handler);

    _logger.info("Updated Default UncaughtExceptionHandler = " + Thread.getDefaultUncaughtExceptionHandler());

    applyUncaughtExceptionHandler(Thread.currentThread(), handler);

    if (handler instanceof SwingExceptionHandler) {
      try {
        // Adding my handler to the Event-Driven Thread.
        SwingUtilities.invokeAndWait(new Runnable() {

          public void run() {
            applyUncaughtExceptionHandler(Thread.currentThread(), handler);
          }
        });
      } catch (InterruptedException ie) {
        _logger.log(Level.SEVERE, "interrupted", ie);
      } catch (InvocationTargetException ite) {
        _logger.log(Level.SEVERE, "exception", ite.getCause());
      }
    }
  }

  private static void applyUncaughtExceptionHandler(final Thread thread, final Thread.UncaughtExceptionHandler handler) {

    _logger.info("Current Thread = " + thread + " in group = " + thread.getThreadGroup());

    final Thread.UncaughtExceptionHandler threadHandler = thread.getUncaughtExceptionHandler();

    _logger.info("Current Thread UncaughtExceptionHandler = " + threadHandler);

    // Adding my handler to this thread (may be unnecessary) :
    thread.setUncaughtExceptionHandler(getExceptionHandler());

    _logger.info("Updated Thread UncaughtExceptionHandler = " + thread.getUncaughtExceptionHandler());
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
  private final static void showException(final Throwable e) {
    // Show feedback report (modal and do not exit on close) :
    // Note : FeedbackReport already logs the exception
    new FeedbackReport(true, e);
  }

  /**
   * Public constructor used by reflection
   */
  public MCSExceptionHandler() {
    super();
  }

  /**
   * AWT exception handler useful for exceptions occuring in modal dialogs
   *
   * @param e the exception
   */
  public void handle(final Throwable e) {
    showException(e);
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
