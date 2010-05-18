/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DiskModelFunction.java,v 1.9 2010-05-18 15:34:03 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2010/05/17 16:03:08  bourgesl
 * major refactoring to simplify the code and delegate the model computation to a Function class
 *
 * Revision 1.7  2010/05/11 16:10:06  bourgesl
 * added new models + javadoc
 *
 * Revision 1.6  2010/02/18 15:51:18  bourgesl
 * added parameter argument validation and propagation (illegal argument exception)
 *
 * Revision 1.5  2010/02/18 09:59:37  bourgesl
 * new ModelDefinition interface to gather model and parameter types
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
import fr.jmmc.mcs.model.function.math.DiskFunction;
import fr.jmmc.mcs.model.targetmodel.Model;

/**
 * This ModelFunction implements the disk model
 * 
 * @author bourgesl
 */
public final class DiskModelFunction extends AbstractModelFunction<DiskFunction> {

  /* Model constants */
  /** disk model description */
  private static final String MODEL_DISK_DESC = "lpb_disk(ufreq, vfreq, flux_weight, x, y, diameter) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized uniform disk of diameter \n" +
          "DIAMETER (milliarcsecond) and centered at coordinates (X,Y) (milliarcsecond). \n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if DIAMETER is negative.\n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** elongated disk model description */
  private static final String MODEL_EDISK_DESC = "lpb_elong_disk(ufreq, vfreq, flux_weight, x, y, minor_axis_diameter, \n" +
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
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";
  /** flattened disk model description */
  private static final String MODEL_FDISK_DESC = "lpb_flatten_disk(ufreq, vfreq, flux_weight, x, y, major_axis_diameter, \n" +
          "flatten_ratio, minor_axis_pos_angle) \n\n" +
          "Returns the Fourier transform, at spatial frequencies (UFREQ,VFREQ) \n" +
          "given in 1/rad, of a normalized ellipse centered at coordinates (X,Y) (milliarcsecond) \n" +
          "with a ratio FLATTEN_RATIO between the major diameter MAJOR_AXIS_DIAMETER and the minor one, \n" +
          "turned from the positive vertical semi-axis (i.e. North direction) with angle \n" +
          "MINOR_AXIS_POS_ANGLE, in degrees, towards to the positive horizontal semi-axis \n" +
          "(i.e. East direction). (the flattening is along the minor_axis) \n\n" +
          "|North \n" +
          "|               For avoiding degenerescence, the domain of variation \n" +
          "|--->East       of MINOR_AXIS_POS_ANGLE is 180 degrees, \n" +
          "|               for ex. from 0 to 180 degrees. \n\n" +
          "FLATTEN_RATIO = major_axis / minor_axis \n\n" +
          "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n" +
          "The function returns an error if MAJOR_AXIS_DIAMETER is negative or if FLATTEN_RATIO \n" +
          "is smaller than 1. \n\n" +
          "UFREQ and VFREQ must be conformable. The returned array is always \n" +
          "complex and with dimensions dimsof(UFREQ,VFREQ). \n";

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
