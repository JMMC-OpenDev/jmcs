/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function;

import fr.jmmc.jmal.model.AbstractModelFunction;
import fr.jmmc.jmal.model.function.math.DiskFunction;
import fr.jmmc.jmal.model.targetmodel.Model;

/**
 * This ModelFunction implements the disk model
 * 
 * @author Laurent BOURGES.
 */
public final class DiskModelFunction extends AbstractModelFunction<DiskFunction> {

  /* Model constants */
  /** disk model description */
  private static final String MODEL_DISK_DESC = 
          "Returns the Fourier transform of a normalized uniform disk of diameter DIAMETER \n" +
          "(milliarcsecond) and centered at coordinates (X,Y) (milliarcsecond). \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n" +
          "The function returns an error if DIAMETER is negative.";
  /** elongated disk model description */
  private static final String MODEL_EDISK_DESC = 
          "Returns the Fourier transform of a normalized ellipse centered at coordinates (X,Y) \n" +
          "(milliarcsecond) with a ratio ELONG_RATIO between the major diameter and the minor one \n" +
          "MINOR_AXIS_DIAMETER, turned from the positive vertical semi-axis (i.e. North direction) \n" +
          "with angle MAJOR_AXIS_POS_ANGLE, in degrees, towards to the positive horizontal semi-axis \n" +
          "(i.e. East direction). (the elongation is along the major_axis) \n\n" +
          "For avoiding degenerescence, the domain of variation of MAJOR_AXIS_POS_ANGLE is 180 \n" +
          "degrees, for ex. from 0 to 180 degrees. \n\n" +
          "ELONG_RATIO = major_axis / minor_axis \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n" +
          "The function returns an error if MINOR_AXIS_DIAMETER is negative or if ELONG_RATIO is \n" +
          "smaller than 1.";
  /** flattened disk model description */
  private static final String MODEL_FDISK_DESC = 
          "Returns the Fourier transform of a normalized ellipse centered at coordinates (X,Y) \n" +
          "(milliarcsecond) with a ratio FLATTEN_RATIO between the major diameter \n" +
          "MAJOR_AXIS_DIAMETER and the minor one, turned from the positive vertical semi-axis \n" +
          "(i.e. North direction) with angle MINOR_AXIS_POS_ANGLE, in degrees, towards to the \n" +
          "positive horizontal semi-axis (i.e. East direction). (the flattening is along the minor_axis) \n\n" +
          "For avoiding degenerescence, the domain of variation of MINOR_AXIS_POS_ANGLE is 180 \n" +
          "degrees, for ex. from 0 to 180 degrees. \n\n" +
          "FLATTEN_RATIO = major_axis / minor_axis \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n" +
          "The function returns an error if MAJOR_AXIS_DIAMETER is negative or if FLATTEN_RATIO \n" +
          "is smaller than 1.";

  /* specific parameters for elongated disk */
  /** Parameter type for the parameter minor_axis_diameter */
  public final static String PARAM_MINOR_AXIS_DIAMETER = "minor_axis_diameter";

  /* specific parameters for flattened disk */
  /** Parameter type for the parameter minor_axis_diameter */
  public final static String PARAM_MAJOR_AXIS_DIAMETER = "major_axis_diameter";

  /* members */
  /** model variant */
  private final ModelVariant variant;

  /**
   * Constructor for the standard variant
   */
  public DiskModelFunction() {
    this(ModelVariant.Standard);
  }

  /**
   * Constructor for the given variant
   * @param variant the model variant
   */
  public DiskModelFunction(final ModelVariant variant) {
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
        return MODEL_DISK;
      case Elongated:
        return MODEL_EDISK;
      case Flattened:
        return MODEL_FDISK;
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
        return MODEL_DISK_DESC;
      case Elongated:
        return MODEL_EDISK_DESC;
      case Flattened:
        return MODEL_FDISK_DESC;
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
        break;
      case Elongated:
        addPositiveParameter(model, PARAM_MINOR_AXIS_DIAMETER);
        addRatioParameter(model, PARAM_ELONG_RATIO);
        addAngleParameter(model, PARAM_MAJOR_AXIS_ANGLE);
        break;
      case Flattened:
        addPositiveParameter(model, PARAM_MAJOR_AXIS_DIAMETER);
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
  protected DiskFunction createFunction(final Model model) {
    final DiskFunction function = new DiskFunction();

    // Get parameters to fill the context :
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
        function.setDiameter(getParameterValue(model, PARAM_MINOR_AXIS_DIAMETER));
        function.setAxisRatio(getParameterValue(model, PARAM_ELONG_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MAJOR_AXIS_ANGLE));
        break;
      case Flattened:
        function.setDiameter(getParameterValue(model, PARAM_MAJOR_AXIS_DIAMETER));
        function.setAxisRatio(1d / getParameterValue(model, PARAM_FLATTEN_RATIO));
        function.setPositionAngle(getParameterValue(model, PARAM_MINOR_AXIS_ANGLE));
        break;
    }

    return function;
  }
}
