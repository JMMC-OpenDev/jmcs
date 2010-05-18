/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CircleModelFunction.java,v 1.4 2010-05-18 15:34:03 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2010/05/17 16:03:08  bourgesl
 * major refactoring to simplify the code and delegate the model computation to a Function class
 *
 * Revision 1.2  2010/05/11 16:10:06  bourgesl
 * added new models + javadoc
 *
 * Revision 1.1  2010/02/18 15:49:41  bourgesl
 * new models (circle, elongated disk)
 *
 */
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.function.math.CircleFunction;
import fr.jmmc.mcs.model.targetmodel.Model;

/**
 * This ModelFunction implements the circle model
 * 
 * @author bourgesl
 */
public final class CircleModelFunction extends AbstractModelFunction<CircleFunction> {

  /* Model constants */
  /** model description */
  private static final String MODEL_DESC = "lpb_circle_diameter(ufreq, vfreq, flux_weight, x, y, diameter) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform circle of diameter \n" +
          "DIAMETER (milliarcsecond) and centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER is negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";

  /**
   * Constructor
   */
  public CircleModelFunction() {
    super();
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    return MODEL_CIRCLE;
  }

  /**
   * Return the model description
   * @return model description
   */
  public String getDescription() {
    return MODEL_DESC;
  }

  /**
   * Return a new Model instance with its parameters and default values
   * @return new Model instance
   */
  @Override
  public Model newModel() {
    final Model model = super.newModel();

    model.setNameAndType(getType());
    model.setDesc(getDescription());

    addPositiveParameter(model, PARAM_DIAMETER);

    return model;
  }

  /**
   * Create the computation function for the given model :
   * Get model parameters to fill the function context
   * @param model model instance
   * @return model function
   */
  protected CircleFunction createFunction(final Model model) {
    final CircleFunction function = new CircleFunction();

    // Get parameters to fill the context :
    function.setX(getParameterValue(model, PARAM_X));
    function.setY(getParameterValue(model, PARAM_Y));
    function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));
    
    function.setDiameter(getParameterValue(model, PARAM_DIAMETER));

    return function;
  }
}
