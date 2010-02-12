/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelFunction.java,v 1.2 2010-02-12 15:52:05 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/01/29 15:52:46  bourgesl
 * Beginning of the Target Model Java implementation = ModelManager and ModelFunction implementations (punct, disk)
 *
 */
package fr.jmmc.mcs.model;

import fr.jmmc.mcs.model.targetmodel.Model;
import org.apache.commons.math.complex.Complex;

/**
 * This interface represents a taregt Model to compute the complex visibility for a given UV coordinates
 * @author bourgesl
 */
public interface ModelFunction {

  /* Model types */
  /** punct model type */
  public String MODEL_PUNCT = "punct";
  /** disk model type */
  public String MODEL_DISK = "disk";

  /* Parameter constants */
  /** Parameter type for the parameter flux_weight */
  public static String PARAM_FLUX_WEIGHT = "flux_weight";
  /** Parameter type for the parameter x */
  public static String PARAM_X = "x";
  /** Parameter type for the parameter y */
  public static String PARAM_Y = "y";

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
   * Compute the model function for the given U, V arrays and model parameters.
   *
   * Note : the visibility array is given to add this model contribution to the total visibility 
   *
   * @param u U array in rad-1
   * @param v V array in rad-1
   * @param model model instance
   * @param complex visibility array
   */
  public void compute(final double[] u, final double[] v, final Model model, final Complex[] vis);
}
