/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSExceptionHandler.java,v 1.6 2010-09-30 13:37:43 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2010/09/25 13:54:07  bourgesl
 * disable security manager
 *
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
import fr.jmmc.mcs.gui.MessagePane;
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
 * JNLP issues :
 * - Thread.defaultUncaughtExceptionHandler never used
 * => do not use this default UncaughtExceptionHandler to have the same behaviour using standard java runtime
 *
 * - main thread (starting the application) use a general try/catch (throwable) and opens a JNLP error dialog
 * => do not set the UncaughtExceptionHandler to this thread
 * => Be sure to catch all exceptions in main() and use the feeback report manually
 *
 * @author bourgesl
 */
public final class MCSExceptionHandler {

  /** Logger */
  private static final Logger _logger = Logger.getLogger(MCSExceptionHandler.class.getName());
  /** flag indicating to use the default UncaughtExceptionHandler (false because of JNLP) */
  private static final boolean USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = false;
  /** flag indicating to set the UncaughtExceptionHandler to the current thread (main) (false because of JNLP) */
  private static final boolean SET_HANDLER_TO_CURRENT_THREAD = false;
  /** uncaughtException handler singleton */
  private static volatile Thread.UncaughtExceptionHandler exceptionHandler = null;

  /**
   * Disable the security manager to be able to use System.setProperty ...
   */
  private static void disableSecurityManager() {
    // Disable security checks
    System.setSecurityManager(null);
  }

  /**
   * Public method to initialize the exception handler singleton with the LoggingExceptionHandler
   */
  public static void installLoggingHandler() {
    disableSecurityManager();

    setExceptionHandler(new LoggingExceptionHandler());
  }

  /**
   * Public method to initialize the exception handler singleton with the SwingExceptionHandler
   */
  public static void installSwingHandler() {
    disableSecurityManager();

    // AWT exception handler for modal dialogs :
    System.setProperty("sun.awt.exception.handler", MCSExceptionHandler.class.getName());

    setExceptionHandler(new SwingExceptionHandler());
  }

  /**
   * Public method to apply the exception handler singleton to the given thread
   * @param thread thread to use
   */
  public static void installThreadHandler(final Thread thread) {
    final Thread.UncaughtExceptionHandler handler = getExceptionHandler();
    if (handler != null) {
      applyUncaughtExceptionHandler(thread, handler);
    } else {
      _logger.fine("No UncaughtExceptionHandler defined !");
    }
  }

  /**
   * Return the exception handler singleton
   * @return exception handler singleton or null if undefined
   */
  private static Thread.UncaughtExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  /**
   * Define the exception handler singleton and apply it to the JVM.
   * If the singleton is already defined, this method has no effect.
   *
   * @see #applyUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
   *
   * @param handler handler to set
   */
  private static synchronized void setExceptionHandler(final Thread.UncaughtExceptionHandler handler) {
    if (handler != null) {
      exceptionHandler = handler;

      applyUncaughtExceptionHandler(handler);
    }
  }

  /**
   * Apply the given UncaughtExceptionHandler to the JVM :
   * - define as default if USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER is enabled
   * - define it to the current thread if SET_HANDLER_TO_CURRENT_THREAD is enabled
   * - define it to EDT if the given handler is a SwingExceptionHandler
   *
   * @see #applyUncaughtExceptionHandler(java.lang.Thread, java.lang.Thread.UncaughtExceptionHandler)
   *
   * @param handler handler to set
   */
  private static void applyUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler handler) {

    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("New UncaughtExceptionHandler = " + handler);
    }

    if (USE_DEFAULT_UNCAUGHT_EXCEPTION_HANDLER) {
      if (_logger.isLoggable(Level.FINE)) {
        _logger.fine("Current Default UncaughtExceptionHandler = " + Thread.getDefaultUncaughtExceptionHandler());
      }

      Thread.setDefaultUncaughtExceptionHandler(handler);

      if (_logger.isLoggable(Level.FINE)) {
        _logger.fine("Updated Default UncaughtExceptionHandler = " + Thread.getDefaultUncaughtExceptionHandler());
      }
    }

    if (SET_HANDLER_TO_CURRENT_THREAD) {
      applyUncaughtExceptionHandler(Thread.currentThread(), handler);
    }

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

  /**
   * Define the UncaughtExceptionHandler to the given thread
   * @param thread thread to use
   * @param handler handler to set
   */
  private static void applyUncaughtExceptionHandler(final Thread thread, final Thread.UncaughtExceptionHandler handler) {

    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("Current Thread = " + thread + " in group = " + thread.getThreadGroup());
    }

    final Thread.UncaughtExceptionHandler threadHandler = thread.getUncaughtExceptionHandler();

    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("Current Thread UncaughtExceptionHandler = " + threadHandler);
    }

    // Adding my handler to this thread (may be unnecessary) :
    thread.setUncaughtExceptionHandler(getExceptionHandler());

    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("Updated Thread UncaughtExceptionHandler = " + thread.getUncaughtExceptionHandler());
    }
  }

  /**
   * Return true if the given exception must be ignored (filtered).
   * For example : ThreadDeath are ignored.
   * @param e
   * @return true if the given exception must be ignored
   */
  private static boolean isFilteredException(final Throwable e) {
    if (e instanceof ThreadDeath) {
      return true;
    }
    return false;
  }

  /**
   * Log the exception
   * @param t the thread
   * @param e the exception
   */
  private static void logException(final Thread t, final Throwable e) {
    _logger.log(Level.SEVERE, "An unexpected exception occured in thread " + t.getName(), e);
  }

  /**
   * Report the exception to the user via Swing using EDT :
   * - display an error message with the exception message
   * - open a modal feedback report
   * @param t the thread
   * @param e the exception
   */
  private final static void showException(final Thread t, final Throwable e) {

    MessagePane.showErrorMessage("An unexpected exception occured", e);

    // Show the feedback report (modal) :
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
    showException(Thread.currentThread(), e);
  }

  /**
   * Logging exception handler that delegates exception handling to logException(thread, throwable)
   */
  private final static class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param thread the thread
     * @param e the exception
     */
    public void uncaughtException(final Thread thread, final Throwable e) {
      if (!isFilteredException(e)) {
        logException(thread, e);
      }
    }
  }

  /**
   * Swing exception handler that delegates exception handling to showException(thread, throwable)
   * using EDT
   */
  private final static class SwingExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param thread the thread
     * @param e the exception
     */
    public void uncaughtException(final Thread thread, final Throwable e) {
      if (!isFilteredException(e)) {
        if (SwingUtilities.isEventDispatchThread()) {
          showException(thread, e);
        } else {
          SwingUtilities.invokeLater(new Runnable() {

            public void run() {
              showException(thread, e);
            }
          });
        }
      }
    }
  }
}
