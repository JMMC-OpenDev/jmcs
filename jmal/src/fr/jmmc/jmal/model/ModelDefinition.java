/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

/**
 * This interface gathers all model and parameter types
 * 
 * @author Laurent BOURGES.
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
  /** elongated ring model type */
  public String MODEL_ERING = "elong_ring";
  /** flattened ring model type */
  public String MODEL_FRING = "flatten_ring";
  /** gaussian model type */
  public String MODEL_GAUSS = "gaussian";
  /** elongated gaussian model type */
  public String MODEL_EGAUSS = "elong_gaussian";
  /** flattened gaussian model type */
  public String MODEL_FGAUSS = "flatten_gaussian";
  /** limb darkened disk model type */
  public String MODEL_LDDISK = "limb_quadratic";

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
}
