/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: GaussianModelFunction.java,v 1.2 2010-05-18 15:34:03 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/05/18 12:43:06  bourgesl
 * added Gaussian Models
 *
 *
 */
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.function.math.GaussianFunction;
import fr.jmmc.mcs.model.targetmodel.Model;

/**
 * This ModelFunction implements the gaussian model
 * 
 * @author bourgesl
 */
public final class GaussianModelFunction extends AbstractModelFunction<GaussianFunction> {

  /* Model constants */
  /** gaussian model description */
  private static final String MODEL_GAUSS_DESC = "lpb_gaussian(ufreq, vfreq, flux_weight, x, y, fwhm) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized gaussian with given FWHM \n" +
          "(milliarcsecond) centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if FWHM is negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** elongated gaussian model description */
  private static final String MODEL_EGAUSS_DESC = "lpb_elong_gaussian(ufreq, vfreq, flux_weight, x, y, minor_axis_fwhm, \n" +
          "elong_ratio, major_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized elongated gaussian centered at coordinates (X,Y) (milliarcsecond). \n" +
          "The sizes of the function in two orthogonal directions are given by the \n" +
          "narrowest FWHM (MINOR_AXIS_FWHM) and by the ratio \n" +
          "ELONG_RATIO between the largest FWHM (MAJOR_AXIS_FWHM) and the MINOR_AXIS_FWHM, \n" +
          "in the same way as for an ellipse (the elongation is along the major_axis): \n\n" +
          "ELONG_RATIO = MAJOR_AXIS_FWHM / MINOR_AXIS_FWHM. \n" +
          "MAJOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n" +
          "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MAJOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MINOR_AXIS_FWHM is negative or if ELONG_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** flattened gaussian model description */
  private static final String MODEL_FGAUSS_DESC = "lpb_flatten_gaussian(ufreq, vfreq, flux_weight, x, y, major_axis_fwhm, \n" +
          "flatten_ratio, minor_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized flattened gaussian centered at coordinates (X,Y) (milliarcsecond). \n" +
          "The sizes of the function in two orthogonal directions are given by the \n" +
          "largest FWHM (MAJOR_AXIS_FWHM) and by the ratio \n" +
          "FLATTEN_RATIO between the largest FWHM (MAJOR_AXIS_FWHM) and the MINOR_AXIS_FWHM, \n" +
          "in the same way as for an ellipse (the flattening is along the minor_axis): \n\n" +
          "FLATTEN_RATIO = MAJOR_AXIS_FWHM / MINOR_AXIS_FWHM. \n" +
          "MINOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n" +
          "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MAJOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MAJOR_AXIS_FWHM is negative or if FLATTEN_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";

  /* specific parameters for gaussian */
  /** Parameter type for the parameter fwhm */
  public final static String PARAM_FWHM = "fwhm";

  /* specific parameters for elongated gaussian */
  /** Parameter type for the parameter minor_axis_fwhm */
  public final static String PARAM_MINOR_AXIS_FWHM = "minor_axis_fwhm";

  /* specific parameters for flattened gaussian */
  /** Parameter type for the parameter major_axis_fwhm */
  public final static String PARAM_MAJOR_AXIS_FWHM = "major_axis_fwhm";

  /* members */
  /** model variant */
  private final ModelVariant variant;

  /**
   * Constructor for the standard variant
   */
  public GaussianModelFunction() {
    this(ModelVariant.Standard);
  }

  /**
   * Constructor for the given variant
   * @param variant the model variant
   */
  public GaussianModelFunction(final ModelVariant variant) {
    super();
    this.variant = variant;
  }

  /**
   * Return the model type
   * @return model type
   */
  public String getType() {
    switch (this.variant) {
      default:
      case Standard:
        return MODEL_GAUSS;
      case Elongated:
        return MODEL_EGAUSS;
      case Flattened:
        return MODEL_FGAUSS;
    }
  }

  /**
   * Return the model description
   * @return model description
   */
  public String getDescription() {
    switch (this.variant) {
      default:
      case Standard:
        return MODEL_GAUSS_DESC;
      case Elongated:
        return MODEL_EGAUSS_DESC;
      case Flattened:
        return MODEL_FGAUSS_DESC;
    }
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

    switch (this.variant) {
      default:
      case Standard:
        addPositiveParameter(model, PARAM_FWHM);
        break;
      case Elongated:
        addPositiveParameter(model, PARAM_MINOR_AXIS_FWHM);
        addRatioParameter(model, PARAM_ELONG_RATIO);
        addAngleParameter(model, PARAM_MAJOR_AXIS_ANGLE);
        break;
      case Flattened:
        addPositiveParameter(model, PARAM_MAJOR_AXIS_FWHM);
        addRatioParameter(model, PARAM_FLATTEN_RATIO);
        addAngleParameter(model, PARAM_MINOR_AXIS_ANGLE);
        break;
    }

    return model;
  }

  /**
   * Create the computation function for the given model :
   * Get model parameters to fill the function context
   * @param model model instance
   * @return model function
   */
  protected GaussianFunction createFunction(final Model model) {
    final GaussianFunction function = new GaussianFunction();

    // Get parameters to fill the context :
    function.setX(getParameterValue(model, PARAM_X));
    function.setY(getParameterValue(model, PARAM_Y));
    function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

    // Variant specific code :
    switch (this.variant) {
      default:
      case Standard:
        function.setDiameter(getParameterValue(model, PARAM_FWHM));
        break;
      case Elongated:
        function.setDiameter(getParameterValue(model, PARAM_MINOR_AXIS_FWHM));
        function.setAxisRatio(getParameterValue(model, PARAM_ELONG_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MAJOR_AXIS_ANGLE));
        break;
      case Flattened:
        function.setDiameter(getParameterValue(model, PARAM_MAJOR_AXIS_FWHM));
        function.setAxisRatio(1d / getParameterValue(model, PARAM_FLATTEN_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MINOR_AXIS_ANGLE));
        break;
    }

    return function;
  }
}
