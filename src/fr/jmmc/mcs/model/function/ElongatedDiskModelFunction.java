/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ElongatedDiskModelFunction.java,v 1.1 2010-02-18 15:49:41 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.targetmodel.Model;
import fr.jmmc.mcs.model.targetmodel.Parameter;
import org.apache.commons.math.complex.Complex;

/**
 * This ModelFunction implements the elongated disk model (ellipse)
 * @author bourgesl
 */
public final class ElongatedDiskModelFunction extends AbstractModelFunction {

  /* Model constants */
  /** model description */
  private static final String MODEL_DESC = "lpb_elong_disk(ufreq, vfreq, flux_weight, x, y, minor_axis_diameter, \n" +
          "elong_ratio, major_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized ellipse centered at coordinates (X,Y) (milliarcsecond) \n" +
          "with a ratio ELONG_RATIO between the major diameter and the minor one MINOR_AXIS_DIAMETER, \n" +
          "turned from the positive vertical semi-axis (i.e. North direction) with angle \n" +
          "MAJOR_AXIS_POS_ANGLE, in degrees, towards to the positive horizontal semi-axis \n" +
          "(i.e. East direction). (the elongation is along the major_axis) \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MAJOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "ELONG_RATIO = major_axis / minor_axis \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MINOR_AXIS_DIAMETER is negative or if ELONG_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and of dims dimsof(UFREQ,VFREQ). \n";

  /**
   * Constructor
   */
  public ElongatedDiskModelFunction() {
    super();
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    return MODEL_EDISK;
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
    model.setName(MODEL_EDISK);
    model.setType(MODEL_EDISK);
    model.setDesc(MODEL_DESC);

    Parameter param;

    param = new Parameter();
    param.setName(PARAM_MINOR_AXIS_DIAMETER);
    param.setType(PARAM_MINOR_AXIS_DIAMETER);
    param.setMinValue(0D);
    param.setValue(0D);
    param.setUnits(UNIT_MAS);
    model.getParameters().add(param);

    param = new Parameter();
    param.setName(PARAM_ELONG_RATIO);
    param.setType(PARAM_ELONG_RATIO);
    param.setMinValue(1D);
    param.setValue(1D);
    model.getParameters().add(param);

    param = new Parameter();
    param.setName(PARAM_MAJOR_AXIS_ANGLE);
    param.setType(PARAM_MAJOR_AXIS_ANGLE);
    param.setMinValue(0D);
    param.setValue(0D);
    param.setMaxValue(180D);
    model.getParameters().add(param);

    return model;
  }

  /**
   * Compute the model function for the given Ufreq, Vfreq arrays and model parameters
   *
   * Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ)
   * given in 1/rad, of a normalized ellipse centered at coordinates (X,Y) given in milliarcsecond.
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
    final double minor_axis_diameter = getParameterValue(model, PARAM_MINOR_AXIS_DIAMETER);
    final double elong_ratio = getParameterValue(model, PARAM_ELONG_RATIO);
    final double major_axis_pos_angle = getParameterValue(model, PARAM_MAJOR_AXIS_ANGLE);

    if (minor_axis_diameter < 0d) {
      createParameterException(PARAM_MINOR_AXIS_DIAMETER, model, "< 0");
    }

    if (elong_ratio < 1d) {
      createParameterException(PARAM_ELONG_RATIO, model, "< 1");
    }

    if (major_axis_pos_angle < 0d) {
      createParameterException(PARAM_MAJOR_AXIS_ANGLE, model, "< 0");
    }

    if (major_axis_pos_angle > 180d) {
      createParameterException(PARAM_MAJOR_AXIS_ANGLE, model, "> 180");
    }

    // Compute :
    for (int i = 0; i < size; i++) {
      vis[i] = vis[i].add(compute_elong_disk(ufreq[i], vfreq[i], flux_weight, x, y, minor_axis_diameter, elong_ratio, major_axis_pos_angle));

      // fast interrupt :
      if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
        return;
      }
    }
  }

  /**
   * Compute the elongated disk model function for a single UV point
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param x x coordinate of the punctual object given in milliarcsecond
   * @param y y coordinate of the punctual object given in milliarcsecond
   * @param minor_axis_diameter minor aixs diameter of the ellipse disk object given in milliarcsecond
   * @param elong_ratio elongation ratio = major_axis_diameter / minor_axis_diameter
   * @param major_axis_pos_angle position angle
   * @return complex Fourier transform value
   */
  private final static Complex compute_elong_disk(final double ufreq, final double vfreq, final double flux_weight, final double x, final double y,
                                                  final double minor_axis_diameter, final double elong_ratio, final double major_axis_pos_angle) {

    // transform UV coordinates :
    final double t_ufreq = transformU(ufreq, vfreq, elong_ratio, 1d, major_axis_pos_angle);
    final double t_vfreq = transformV(ufreq, vfreq, 1d, major_axis_pos_angle);

    return shift(ufreq, vfreq, x, y).multiply(DiskModelFunction.compute_disk_weight(t_ufreq, t_vfreq, flux_weight, minor_axis_diameter));
  }
}
