/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Editable.java,v 1.1 2010-02-19 16:02:52 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.gui;

import fr.jmmc.mcs.model.targetmodel.Model;

/**
 *
 * @author bourgesl
 */
public interface Editable {

  /**
   * Return the model
   */
  Model getModel();

  /**
   * Indicates if the parameter is a shared parameter
   */
  boolean isShared();

  /**
   * Indicates if the parameter is a position
   */
  boolean isPosition();

  /* Parameter methods */

  /* --- read-only parameter attributes ------------------------------------- */
  /**
   * Gets the value of the name property.
   */
  String getName();

  /**
   * Gets the value of the type property.
   */
  String getType();

  /**
   * Gets the value of the units property.
   */
  String getUnits();

  /* --- writable parameter attributes -------------------------------------- */
  /**
   * Gets the value of the value property.
   */
  double getValue();

  /**
   * Gets the value of the minValue property.
   */
  Double getMinValue();

  /**
   * Gets the value of the maxValue property.
   */
  Double getMaxValue();

  /**
   * Gets the value of the scale property.
   */
  Double getScale();

  /**
   * Gets the value of the hasFixedValue property.
   */
  boolean isHasFixedValue();

  /**
   * Sets the value of the value property.
   *
   */
  void setValue(double value);

  /**
   * Sets the value of the minValue property.
   */
  void setMinValue(Double value);

  /**
   * Sets the value of the maxValue property.
   */
  void setMaxValue(Double value);

  /**
   * Sets the value of the scale property.
   */
  void setScale(Double value);

  /**
   * Sets the value of the hasFixedValue property.
   */
  void setHasFixedValue(boolean value);
}
