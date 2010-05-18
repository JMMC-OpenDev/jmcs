/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PunctModelFunction.java,v 1.9 2010-05-18 15:34:03 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2010/05/17 16:03:09  bourgesl
 * major refactoring to simplify the code and delegate the model computation to a Function class
 *
 * Revision 1.7  2010/05/12 11:34:42  bourgesl
 * refactoring
 *
 * Revision 1.6  2010/05/11 16:10:06  bourgesl
 * added new models + javadoc
 *
 * Revision 1.5  2010/02/18 15:51:18  bourgesl
 * added parameter argument validation and propagation (illegal argument exception)
 *
 * Revision 1.4  2010/02/16 14:44:14  bourgesl
 * getParameter(mode, type) renamed to getParameterValue(model, type)
 *
 * Revision 1.3  2010/02/12 15:52:05  bourgesl
 * refactoring due to changed generated classes by xjc
 *
 * Revision 1.2  2010/02/03 16:05:46  bourgesl
 * Added fast thread interruption checks for asynchronous uv map computation
 *
 * Revision 1.1  2010/01/29 15:52:46  bourgesl
 * Beginning of the Target Model Java implementation = ModelManager and ModelFunction implementations (punct, disk)
 *
 */
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
  private final static String MODEL_DESC = "lpb_punct(ufreq, vfreq, flux_weight, x, y) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a punctual object (Dirac function) at coordinates \n" +
          "(X,Y) given in milliarcsecond. \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";

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
