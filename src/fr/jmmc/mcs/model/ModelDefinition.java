/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelDefinition.java,v 1.1 2010-02-18 09:59:37 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
