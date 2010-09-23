/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MCSObservable.java,v 1.1 2010-09-23 19:36:47 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.util;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a custom implementation of Observable in order
 * to trace interactions with Observers.
 *
 * TODO : implement totally Observable class to have better event handling (logs ...)
 *
 * @author bourgesl
 */
public class MCSObservable extends Observable {

  /** Class name */
  private static final String _className = "fr.jmmc.mcs.util.MCSObservable";
  /** Logger - get from given class name */
  private static final Logger _logger = Logger.getLogger(_className);

  /**
   * Adds an observer to the set of observers for this object, provided
   * that it is not the same as some observer already in the set.
   * The order in which notifications will be delivered to multiple
   * observers is not specified. See the class comment.
   *
   * @param   o   an observer to be added.
   * @throws NullPointerException   if the parameter o is null.
   */
  @Override
  public final void addObserver(final Observer o) {
    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("addObserver : " + toString(o));
    }

    super.addObserver(o);
  }

  /**
   * Deletes an observer from the set of observers of this object.
   * Passing <CODE>null</CODE> to this method will have no effect.
   * @param   o   the observer to be deleted.
   */
  @Override
  public final void deleteObserver(Observer o) {
    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("deleteObserver : " + toString(o));
    }
    super.deleteObserver(o);
  }

  /**
   * If this object has changed, as indicated by the
   * <code>hasChanged</code> method, then notify all of its observers
   * and then call the <code>clearChanged</code> method to indicate
   * that this object has no longer changed.
   * <p>
   * Each observer has its <code>update</code> method called with two
   * arguments: this observable object and the <code>arg</code> argument.
   *
   * @param   arg   any object.
   * @see     java.util.Observable#clearChanged()
   * @see     java.util.Observable#hasChanged()
   * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public final void notifyObservers(final Object arg) {
    if (_logger.isLoggable(Level.FINE)) {
      _logger.fine("notifyObservers : " + toString(arg));
    }
    super.notifyObservers(arg);
  }

  /**
   * Returns a string representation of the object.
   * <blockquote>
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString(hashCode())
   * </pre></blockquote>
   *
   * @param o object reference
   * @return  a string representation of the object.
   */
  public final String toString(final Object o) {
    if (o == null) {
      return "null";
    }
    return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
  }
}
