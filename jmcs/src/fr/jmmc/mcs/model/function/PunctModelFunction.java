/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.function.math.PunctFunction;
import fr.jmmc.mcs.model.targetmodel.Model;

/**
 * This ModelFunction implements the punct model
 * 
 * @author bourgesl
 */
public final class PunctModelFunction extends AbstractModelFunction<PunctFunction> {

  /* Model constants */
  /** model description */
  private final static String MODEL_DESC =
          "Returns the Fourier transform of a punctual object (Dirac function) at coordinates (X,Y) \n" +
          "(milliarcsecond). \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1.";

  /**
   * Constructor
   */
  public PunctModelFunction() {
    super();
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    return MODEL_PUNCT;
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

    return model;
  }

  /**
   * Create the computation function for the given model :
   * Get model parameters to fill the function context
   * @param model model instance
   * @return model function
   */
  protected PunctFunction createFunction(final Model model) {
    final PunctFunction function = new PunctFunction();

    // Get parameters to fill the context :
    function.setX(getParameterValue(model, PARAM_X));
    function.setY(getParameterValue(model, PARAM_Y));
    function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

    return function;
  }
}