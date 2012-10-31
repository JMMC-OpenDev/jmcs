/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

/**
 * This interface defines the dispose() method to free ressources or references to itself to
 * avoid memory leak, Observer/Observable or event listener problems
 * @author bourgesl
 */
public interface Disposable {

  /**
   * Free any ressource or reference to this instance
   */
  public void dispose();

}
