/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.gui;

import fr.jmmc.jmal.model.targetmodel.Model;

/**
 * @author Laurent BOURGES.
 */
public interface Editable {

  /**
   * Return the model
   * @return model
   */
  Model getModel();

  /**
   * Indicates if the parameter is a shared parameter
   * @return true if the parameter is a shared parameter
   */
  boolean isShared();

  /**
   * Indicates if the parameter is a position
   * @return true if the parameter is a position
   */
  boolean isPosition();

  /* Parameter methods */

  /* --- read-only parameter attributes ------------------------------------- */
  /**
   * Gets the value of the name property.
   * @return value of the name property
   */
  String getName();

  /**
   * Gets the value of the type property.
   * @return value of the type property
   */
  String getType();

  /**
   * Gets the value of the units property.
   * @return value of the units property
   */
  String getUnits();

  /* --- writable parameter attributes -------------------------------------- */
  /**
   * Gets the value of the value property.
   * @return value property
   */
  double getValue();

  /**
   * Gets the value of the minValue property.
   * @return minValue property
   */
  Double getMinValue();

  /**
   * Gets the value of the maxValue property.
   * @return maxValue property
   */
  Double getMaxValue();

  /**
   * Gets the value of the scale property.
   * @return scale property
   */
  Double getScale();

  /**
   * Gets the value of the hasFixedValue property.
   * @return hasFixedValue property
   */
  boolean isHasFixedValue();

  /**
   * Sets the value of the value property.
   * @param value value to set
   */
  void setValue(double value);

  /**
   * Sets the value of the minValue property.
   * @param value value to set
   */
  void setMinValue(Double value);

  /**
   * Sets the value of the maxValue property.
   * @param value value to set
   */
  void setMaxValue(Double value);

  /**
   * Sets the value of the scale property.
   * @param value value to set
   */
  void setScale(Double value);

  /**
   * Sets the value of the hasFixedValue property.
   * @param value value to set
   */
  void setHasFixedValue(boolean value);
}
