/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CloneableObject.java,v 1.1 2010-02-12 15:52:32 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model;

/**
 * This class supports the Cloneable interface and declare the clone() method (no deep copy)
 * @author bourgesl
 */
public class CloneableObject implements Cloneable {

  /**
   * Public constructor
   */
  public CloneableObject() {
    super();
  }

  /**
   * Return a "shallow copy" of this instance
   * @return "shallow copy" of this instance
   */
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cnse) {
    }
    return null;
  }
}
