/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelDefinition.java,v 1.3 2010-05-11 16:09:53 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/02/18 15:51:18  bourgesl
 * added parameter argument validation and propagation (illegal argument exception)
 *
 * Revision 1.1  2010/02/18 09:59:37  bourgesl
 * new ModelDefinition interface to gather model and parameter types
 *
 */
package fr.jmmc.mcs.model;

/**
 * This interface gathers all model and parameter types
 * @author bourgesl
 */
public interface ModelDefinition {

  /* Model types */
  /** punct model type */
  public String MODEL_PUNCT = "punct";
  /** disk model type */
  public String MODEL_DISK = "disk";
  /** elongated disk model type */
  public String MODEL_EDISK = "elong_disk";
  /** flattened disk model type */
  public String MODEL_FDISK = "flatten_disk";
  /** disk model type */
  public String MODEL_CIRCLE = "circle";
  /** ring model type */
  public String MODEL_RING = "ring";

  /* Units */
  /** milli arc second Unit */
  public final static String UNIT_MAS = "mas";
  /** degrees Unit */
  public final static String UNIT_DEG = "degrees";

  /* Parameter types */

  /* common parameters */
  /** Parameter type for the parameter flux_weight */
  public static String PARAM_FLUX_WEIGHT = "flux_weight";
  /** Parameter type for the parameter x */
  public static String PARAM_X = "x";
  /** Parameter type for the parameter y */
  public static String PARAM_Y = "y";

  /* specific parameters */
  /** Parameter type for the parameter diameter */
  public static String PARAM_DIAMETER = "diameter";

  /* specific parameters for elongated models */
  /** Parameter type for the parameter minor_axis_diameter */
  public static String PARAM_MINOR_AXIS_DIAMETER = "minor_axis_diameter";
  /** Parameter type for the parameter elong_ratio */
  public static String PARAM_ELONG_RATIO = "elong_ratio";
  /** Parameter type for the parameter major_axis_pos_angle */
  public static String PARAM_MAJOR_AXIS_ANGLE = "major_axis_pos_angle";

  /* specific parameters for flattened models */
  /** Parameter type for the parameter minor_axis_diameter */
  public static String PARAM_MAJOR_AXIS_DIAMETER = "major_axis_diameter";
  /** Parameter type for the parameter elong_ratio */
  public static String PARAM_FLATTEN_RATIO = "flatten_ratio";
  /** Parameter type for the parameter major_axis_pos_angle */
  public static String PARAM_MINOR_AXIS_ANGLE = "minor_axis_pos_angle";
}
