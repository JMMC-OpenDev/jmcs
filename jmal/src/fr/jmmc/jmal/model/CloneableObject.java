/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supports the Cloneable interface and declare the clone() method (no deep copy)
 * 
 * @author Laurent BOURGES.
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

  /**
   * Return a deep "copy" of the list of objects (recursive call to clone() on each object instance)
   * @param <K> CloneableObject child class
   * @param list list of objects to clone
   * @return deep "copy" of the list
   */
  @SuppressWarnings("unchecked")
  public static <K extends CloneableObject> List<K> deepCopyList(final List<K> list) {
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
