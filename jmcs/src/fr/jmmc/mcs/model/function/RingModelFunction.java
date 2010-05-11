/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RingModelFunction.java,v 1.1 2010-05-11 16:10:06 bourgesl Exp $"
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
 * This ModelFunction implements the ring model
 * 
 * @author bourgesl
 */
public final class RingModelFunction extends AbstractModelFunction {

  /* Model constants */
  /** model description */
  private static final String MODEL_DESC = "lpb_ring(ufreq, vfreq, flux_weight, x, y, diameter, width) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform ring with internal \n" +
          "diameter DIAMETER  (milliarcsecond) and external diameter DIAMETER+WIDTH \n" +
          "centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER or WIDTH are negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and of dims dimsof(UFREQ,VFREQ). \n";
  /** Parameter type for the parameter width */
  public static String PARAM_WIDTH = "width";

  /**
   * Constructor
   */
  public RingModelFunction() {
    super();
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    return MODEL_RING;
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
    model.setName(MODEL_RING);
    model.setType(MODEL_RING);
    model.setDesc(MODEL_DESC);

    Parameter param;

    param = new Parameter();
    param.setName(PARAM_DIAMETER);
    param.setType(PARAM_DIAMETER);
    param.setMinValue(0D);
    param.setValue(0D);
    param.setUnits(UNIT_MAS);
    model.getParameters().add(param);

    param = new Parameter();
    param.setName(PARAM_WIDTH);
    param.setType(PARAM_WIDTH);
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
   * @param vis complex visibility array
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
    final double width = getParameterValue(model, PARAM_WIDTH);

    if (diameter < 0d) {
      createParameterException(PARAM_DIAMETER, model, "< 0");
    }

    if (width < 0d) {
      createParameterException(PARAM_WIDTH, model, "< 0");
    }

    // Compute :
    for (int i = 0; i < size; i++) {
      vis[i] = vis[i].add(compute_ring(ufreq[i], vfreq[i], flux_weight, x, y, diameter, width));

      // fast interrupt :
      if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
        return;
      }
    }
  }

  /**
   * Compute the ring model function for a single UV point
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param x x coordinate of the object given in milliarcsecond
   * @param y y coordinate of the object given in milliarcsecond
   * @param diameter diameter of the ring model given in milliarcsecond
   * @param width width of the ring model given in milliarcsecond
   * @return complex Fourier transform value
   */
  private final static Complex compute_ring(final double ufreq, final double vfreq, final double flux_weight,
                                            final double x, final double y, final double diameter, final double width) {

    if (width == 0d) {
      // Returns the Fourier transform of infinitely thin ring, i.e. a circle.
      return CircleModelFunction.compute_circle(ufreq, vfreq, flux_weight, x, y, diameter);
    }
    if (diameter == 0d) {
      // Returns the Fourier transform of a disk of radius width.
      return DiskModelFunction.compute_disk(ufreq, vfreq, flux_weight, x, y, 2d * width);
    }

    // norm of uv :
    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double alpha = 1d + width * diameter / 2d;

    final double r = PI * MAS2RAD * diameter * normUV / 2d;

    double g;
    if (r == 0D) {
      g = 1D;
    } else {
      g = ((alpha * Bessel.j1(2d * alpha * r) / r) - (Bessel.j1(2d * r) / r)) / (alpha * alpha - 1d);
    }
    g *= flux_weight;

    return shift(ufreq, vfreq, x, y).multiply(flux_weight * g);
  }
}
