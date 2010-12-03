/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CloneableObject.java,v 1.3 2010-12-03 16:28:09 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/11/30 13:00:30  bourgesl
 * simpler clone method using generic deepCopyList method
 *
 * Revision 1.1  2010/02/12 15:52:32  bourgesl
 * added cloneable support for target model classes
 * added parameter table model
 *
 */
package fr.jmmc.mcs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class supports the Cloneable interface and declare the clone() method (no deep copy)
 * @author bourgesl
 */
public class CloneableObject implements Cloneable, Serializable {

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

  /**
   * Return a deep "copy" of the list of objects (recursive call to clone() on each object instance)
   * @param <K> CloneableObject child class
   * @param list list of objects to clone
   * @return deep "copy" of the list
   */
  @SuppressWarnings("unchecked")
  public static final <K extends CloneableObject> List<K> deepCopyList(final List<K> list) {
    if (list != null) {
      final List<K> newList = new ArrayList<K>(list.size());
      for (K o : list) {
        newList.add((K) o.clone());
      }
      return newList;
    }
    return null;
  }
}
