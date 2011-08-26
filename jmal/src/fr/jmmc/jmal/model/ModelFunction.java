/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.model.targetmodel.Model;
import org.apache.commons.math.complex.Complex;

/**
 * This interface represents a taregt Model to compute the complex visibility for a given UV coordinates
 * 
 * @author Laurent BOURGES.
 */
public interface ModelFunction extends ModelDefinition {

  /**
   * Return the model type
   * @return model type
   */
  public String getType();

  /**
   * Return the model description
   * @return model description
   */
  public String getDescription();

  /**
   * Return a new Model instance with its parameters and default values
   * @return new Model instance
   */
  public Model newModel();

  /**
   * Check the model parameters against their min/max bounds.
   * @param model model to check
   * @throws IllegalArgumentException
   */
  public void validate(final Model model);

  /**
   * Compute the model function for the given U, V arrays and model parameters.
   *
   * Note : the visibility array is given to add this model contribution to the total visibility 
   *
   * @param u U array in rad-1
   * @param v V array in rad-1
   * @param model model instance
   * @param vis complex visibility array
   * @throws IllegalArgumentException if a parameter value is invalid !
   */
  public void compute(final double[] u, final double[] v, final Model model, final Complex[] vis);
}
