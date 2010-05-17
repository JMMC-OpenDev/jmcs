/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RingModelFunction.java,v 1.2 2010-05-17 16:03:08 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/05/11 16:10:06  bourgesl
 * added new models + javadoc
 *
 */
package fr.jmmc.mcs.model.function;

import fr.jmmc.mcs.model.AbstractModelFunction;
import fr.jmmc.mcs.model.function.math.RingFunction;
import fr.jmmc.mcs.model.targetmodel.Model;

/**
 * This ModelFunction implements the ring model
 * 
 * @author bourgesl
 */
public final class RingModelFunction extends AbstractModelFunction<RingFunction> {

  /* Model constants */
  /** ring model description */
  private static final String MODEL_RING_DESC = "lpb_ring(ufreq, vfreq, flux_weight, x, y, diameter, width) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform ring with internal \n" +
          "diameter DIAMETER  (milliarcsecond) and external diameter DIAMETER+WIDTH \n" +
          "centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER or WIDTH are negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and of dims dimsof(UFREQ,VFREQ). \n";
  /** elongated ring model description */
  private static final String MODEL_ERING_DESC = "lpb_elong_ring(ufreq, vfreq, flux_weight, x, y, minor_internal_diameter, \n" +
          "elong_ratio, width, major_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform elongated ring centered at coordinates (X,Y) (milliarcsecond). \n" +
          "The sizes of the function in two orthogonal directions are given by the \n" +
          "narrowest internal diameter (MINOR_INTERNAL_DIAMETER) and by the ratio \n" +
          "ELONG_RATIO between the widest internal diameter and MINOR_INTERNAL_DIAMETER, \n" +
          "in the same way as for an ellipse: \n\n" +
          "ELONG_RATIO = MAJOR_INTERNAL_DIAMETER / MINOR_INTERNAL_DIAMETER. \n" +
          "In the direction of MINOR_INTERNAL_DIAMETER, the external diameter is \n" +
          "MINOR_INTERNAL_DIAMETER + WIDTH. In the direction of the widest internal diameter, \n" +
          "the width is magnified by the ratio ELONG_RATIO, so that the external \n" +
          "diameter is the elongated MAJOR_INTERNAL_DIAMETER + WIDTH * ELONG_RATIO. \n" +
          "MAJOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n" +
          "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MAJOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MINOR_INTERNAL_DIAMETER is negative or if ELONG_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** flattened ring model description */
  private static final String MODEL_FRING_DESC = "lpb_flatten_ring(ufreq, vfreq, flux_weight, x, y, major_internal_diameter, \n" +
          "flatten_ratio, width, minor_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform flattened ring centered at coordinates (X,Y) (milliarcsecond). \n" +
          "The sizes of the function in two orthogonal directions are given by the \n" +
          "widest internal diameter (MAJOR_INTERNAL_DIAMETER) and by the ratio \n" +
          "FLATTEN_RATIO between MAJOR_INTERNAL_DIAMETER and the narrowest internal diameter, \n" +
          "in the same way as for an ellipse (the flattening is along the minor axis): \n\n" +
          "FLATTEN_RATIO = MAJOR_INTERNAL_DIAMETER / MINOR_INTERNAL_DIAMETER. \n" +
          "In the direction of MAJOR_INTERNAL_DIAMETER, the external diameter is \n" +
          "MAJOR_INTERNAL_DIAMETER + WIDTH. In the direction of the narrowest internal diameter, \n" +
          "the width is decreased by the ratio FLATTEN_RATIO, so that the external \n" +
          "diameter is the flattened MINOR_INTERNAL_DIAMETER + WIDTH / FLATTEN_RATIO. \n" +
          "MINOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n" +
          "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MAJOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MAJOR_INTERNAL_DIAMETER is negative or if FLATTEN_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** Parameter type for the parameter width */
  public final static String PARAM_WIDTH = "width";

  /* specific parameters for elongated ring */
  /** Parameter type for the parameter minor_internal_diameter */
  public final static String PARAM_MINOR_INTERNAL_DIAMETER = "minor_internal_diameter";

  /* specific parameters for flattened ring */
  /** Parameter type for the parameter major_internal_diameter */
  public final static String PARAM_MAJOR_INTERNAL_DIAMETER = "major_internal_diameter";

  /* members */
  /** model variant */
  private final ModelVariant variant;

  /**
   * Constructor
   */
  public RingModelFunction() {
    this(ModelVariant.Standard);
  }

  /**
   * Constructor for the given variant
   * @param variant the model variant
   */
  public RingModelFunction(final ModelVariant variant) {
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
        return MODEL_RING;
      case Elongated:
        return MODEL_ERING;
      case Flattened:
        return MODEL_FRING;
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
        return MODEL_RING_DESC;
      case Elongated:
        return MODEL_ERING_DESC;
      case Flattened:
        return MODEL_FRING_DESC;
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
        addPositiveParameter(model, PARAM_DIAMETER);
        addPositiveParameter(model, PARAM_WIDTH);
        break;
      case Elongated:
        addPositiveParameter(model, PARAM_MINOR_INTERNAL_DIAMETER);
        addRatioParameter(model, PARAM_ELONG_RATIO);
        addPositiveParameter(model, PARAM_WIDTH);
        addAngleParameter(model, PARAM_MAJOR_AXIS_ANGLE);
        break;
      case Flattened:
        addPositiveParameter(model, PARAM_MAJOR_INTERNAL_DIAMETER);
        addRatioParameter(model, PARAM_FLATTEN_RATIO);
        addPositiveParameter(model, PARAM_WIDTH);
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
  protected RingFunction createFunction(final Model model) {
    final RingFunction function = new RingFunction();

    // Get parameters to fill the context (includes parameter validation) :
    function.setX(getParameterValue(model, PARAM_X));
    function.setY(getParameterValue(model, PARAM_Y));
    function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

    // Variant specific code :
    switch (this.variant) {
      default:
      case Standard:
        function.setDiameter(getParameterValue(model, PARAM_DIAMETER));
        break;
      case Elongated:
        function.setDiameter(getParameterValue(model, PARAM_MINOR_INTERNAL_DIAMETER));
        function.setAxisRatio(getParameterValue(model, PARAM_ELONG_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MAJOR_AXIS_ANGLE));
        break;
      case Flattened:
        function.setDiameter(getParameterValue(model, PARAM_MAJOR_INTERNAL_DIAMETER));
        function.setAxisRatio(1d / getParameterValue(model, PARAM_FLATTEN_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MINOR_AXIS_ANGLE));
        break;
    }
    function.setWidth(getParameterValue(model, PARAM_WIDTH));

    return function;
  }
}
