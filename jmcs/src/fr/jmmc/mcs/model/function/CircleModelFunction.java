/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CircleModelFunction.java,v 1.1 2010-02-18 15:49:41 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function;

import cern.jet.math.Bessel;
import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.targetmodel.Model;
import fr.jmmc.mcs.model.targetmodel.Parameter;
import org.apache.commons.math.complex.Complex;

/**
 * This ModelFunction implements the circle model
 * 
 * @author bourgesl
 */
public final class CircleModelFunction extends AbstractModelFunction {

  /* Model constants */
  /** model description */
  private static final String MODEL_DESC = "lpb_circle_diameter(ufreq, vfreq, flux_weight, x, y, diameter) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform circle of diameter \n" +
          "DIAMETER (milliarcsecond) and centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER is negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and of dims dimsof(UFREQ,VFREQ). \n";

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
    model.setName(MODEL_CIRCLE);
    model.setType(MODEL_CIRCLE);
    model.setDesc(MODEL_DESC);

    Parameter param;

    param = new Parameter();
    param.setName(PARAM_DIAMETER);
    param.setType(PARAM_DIAMETER);
    param.setMinValue(0D);
    param.setValue(0D);
    param.setUnits(UNIT_MAS);
    model.getParameters().add(param);

    return model;
  }

  /**
   * Compute the model function for the given Ufreq, Vfreq arrays and model parameters
   *
   * Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ)
   * given in 1/rad, of a normalized uniform circle of diameter at coordinates
   * (X,Y) given in milliarcsecond.
   *
   * Note : the visibility array is given to add this model contribution to the total visibility
   *
   * @param ufreq U frequencies in rad-1
   * @param vfreq V frequencies in rad-1
   * @param model model instance
   * @param complex visibility array
   * @throws IllegalArgumentException if a parameter value is invalid !
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
    final double diameter = getParameterValue(model, PARAM_DIAMETER);

    if (diameter < 0d) {
      createParameterException(PARAM_DIAMETER, model, "< 0");
    }

    // Compute :
    for (int i = 0; i < size; i++) {
      vis[i] = vis[i].add(compute_circle(ufreq[i], vfreq[i], flux_weight, x, y, diameter));

      // fast interrupt :
      if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
        return;
      }
    }
  }

  /**
   * Compute the circle model function for a single UV point
   *
   * return flux_weight * shift(ufreq, vfreq, x, y) * bessJ0(PI x diameter x norm(uv))
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param x x coordinate of the punctual object given in milliarcsecond
   * @param y y coordinate of the punctual object given in milliarcsecond
   * @param diameter diameter of the uniform disk object given in milliarcsecond
   * @return complex Fourier transform value
   */
  private final static Complex compute_circle(final double ufreq, final double vfreq, final double flux_weight,
                                              final double x, final double y, final double diameter) {

    // norm of uv :
    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double d = PI * MAS2RAD * diameter * normUV;

    double g;
    if (d == 0D) {
      g = 1D;
    } else {
      g = Bessel.j0(d);
    }

    return shift(ufreq, vfreq, x, y).multiply(flux_weight * g);
  }
}
