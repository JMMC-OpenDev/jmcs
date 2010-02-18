/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PunctModelFunction.java,v 1.5 2010-02-18 15:51:18 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
import fr.jmmc.mcs.model.targetmodel.Model;
import org.apache.commons.math.complex.Complex;

/**
 * This ModelFunction implements the punct model
 * 
 * @author bourgesl
 */
public final class PunctModelFunction extends AbstractModelFunction {

  /* Model constants */
  /** model description */
  private final static String MODEL_DESC = "lpb_punct(ufreq, vfreq, flux_weight, x, y) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a punctual object (Dirac function) at coordinates \n" +
          "(X,Y) given in milliarcsecond. \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and of dims dimsof(UFREQ,VFREQ). \n";

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
    model.setName(MODEL_PUNCT);
    model.setType(MODEL_PUNCT);
    model.setDesc(MODEL_DESC);

    return model;
  }

  /**
   * Compute the model function for the given Ufreq, Vfreq arrays and model parameters
   *
   * Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ)
   * given in 1/rad, of a punctual object (Dirac function) at coordinates
   * (X,Y) given in milliarcsecond.
   *
   * Note : the visibility array is given to add this model contribution to the total visibility
   *
   * @param ufreq U frequencies in rad-1
   * @param vfreq V frequencies in rad-1
   * @param model model instance
   * @param complex visibility array
   */
  public void compute(final double[] ufreq, final double[] vfreq, final Model model, final Complex[] vis) {

    /** Get the current thread to check if the computation is interrupted */
    final Thread currentThread = Thread.currentThread();

    final int size = ufreq.length;

    // this step indicates when the thread.isInterrupted() is called in the for loop
    final int stepInterrupt = 1 + size / 25;

    // Get parameters :
    final double flux_weight = getParameterValue(model, PARAM_FLUX_WEIGHT);
    final double x = getParameterValue(model, PARAM_X);
    final double y = getParameterValue(model, PARAM_Y);

    // Compute :
    for (int i = 0; i < size; i++) {
      vis[i] = vis[i].add(compute_punct(ufreq[i], vfreq[i], flux_weight, x, y));

      // fast interrupt :
      if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
        return;
      }
    }
  }

  /**
   * Compute the punct model function for a single UV point
   *
   * return flux_weight * shift(ufreq, vfreq, x, y);
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param x x coordinate of the punctual object given in milliarcsecond
   * @param y y coordinate of the punctual object given in milliarcsecond
   * @return complex Fourier transform value
   */
  private final static Complex compute_punct(final double ufreq, final double vfreq, final double flux_weight, final double x, final double y) {
    return shift(ufreq, vfreq, x, y).multiply(flux_weight);
  }
}
