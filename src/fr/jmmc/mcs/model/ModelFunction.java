/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelFunction.java,v 1.5 2010-05-17 16:01:03 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2010/02/18 15:51:18  bourgesl
 * added parameter argument validation and propagation (illegal argument exception)
 *
 * Revision 1.3  2010/02/18 09:59:37  bourgesl
 * new ModelDefinition interface to gather model and parameter types
 *
 * Revision 1.2  2010/02/12 15:52:05  bourgesl
 * refactoring due to changed generated classes by xjc
 *
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
