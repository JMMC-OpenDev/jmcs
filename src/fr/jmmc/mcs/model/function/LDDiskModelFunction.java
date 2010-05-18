/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LDDiskModelFunction.java,v 1.1 2010-05-18 15:34:48 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 */
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.function.math.LDDiskFunction;
import fr.jmmc.mcs.model.targetmodel.Model;
import fr.jmmc.mcs.model.targetmodel.Parameter;

/**
 * This ModelFunction implements the limb darkened disk model (quadratic)
 * 
 * @author bourgesl
 */
public final class LDDiskModelFunction extends AbstractModelFunction<LDDiskFunction> {

  /* Model constants */
  /** model description */
  private static final String MODEL_DESC = "lpb_limb_quadratic(ufreq, vfreq, flux_weight, x, y, diameter, a1_coeff, a2_coeff) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a center-to-limb darkened disk of diameter \n" +
          "DIAMETER (milliarcsecond) centered at coordinates (X,Y) (milliarcsecond). \n\n" +
          "The brightness distribution o, if expressed versus mu, \n" +
          "the cosine of the azimuth of a surface element of the star, follows \n" +
          "a quadratic law of coefficients A1_COEFF, A2_COEFF ([-1,1]), and is normalized \n" +
          "for mu = 1 (center of the star). \n" +
          "o(mu) = 1 -A1_COEFF(1-mu) - A2_COEFF(1-mu)^2. \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER is negative or \n" +
          "if A1_COEFF or A2_coeff outside bounds [-1,1].\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** Parameter type for the parameter a1_coeff */
  public final static String PARAM_A1 = "a1_coeff";
  /** Parameter type for the parameter a2_coeff */
  public final static String PARAM_A2 = "a2_coeff";

  /**
   * Constructor
   */
  public LDDiskModelFunction() {
    super();
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    return MODEL_LDDISK;
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

    Parameter param;

    param = new Parameter();
    param.setNameAndType(PARAM_A1);
    param.setMinValue(-1D);
    param.setValue(0D);
    param.setMaxValue(1D);
    model.getParameters().add(param);

    param = new Parameter();
    param.setNameAndType(PARAM_A2);
    param.setMinValue(-1D);
    param.setValue(0D);
    param.setMaxValue(1D);
    model.getParameters().add(param);

    return model;
  }

  /**
   * Create the computation function for the given model :
   * Get model parameters to fill the function context
   * @param model model instance
   * @return model function
   */
  protected LDDiskFunction createFunction(final Model model) {
    final LDDiskFunction function = new LDDiskFunction();

    // Get parameters to fill the context :
    function.setX(getParameterValue(model, PARAM_X));
    function.setY(getParameterValue(model, PARAM_Y));
    function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

    function.setDiameter(getParameterValue(model, PARAM_DIAMETER));
    function.setA1(getParameterValue(model, PARAM_A1));
    function.setA2(getParameterValue(model, PARAM_A2));

    return function;
  }
}
