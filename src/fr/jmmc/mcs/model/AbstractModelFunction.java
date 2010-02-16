/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: AbstractModelFunction.java,v 1.3 2010-02-16 14:43:06 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2010/02/12 15:52:05  bourgesl
 * refactoring due to changed generated classes by xjc
 *
 * Revision 1.1  2010/01/29 15:52:45  bourgesl
 * Beginning of the Target Model Java implementation = ModelManager and ModelFunction implementations (punct, disk)
 *
 */
package fr.jmmc.mcs.model;

import fr.jmmc.mcs.model.targetmodel.Model;
import fr.jmmc.mcs.model.targetmodel.Parameter;
import org.apache.commons.math.complex.Complex;

/**
 *
 * @author bourgesl
 */
public abstract class AbstractModelFunction implements ModelFunction {

  /** Class Name */
  private static final String className_ = "fr.jmmc.mcs.model.ModelFunction";
  /** Class logger */
  protected static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          className_);
  /** milli arc second Unit */
  public final static String UNIT_MAS = "mas";

  /* double floating precision constants (java) */
  /**
   * @see Double.MIN_NORMAL
   * A constant holding the smallest positive normal value of type
   * {@code double}, 2<sup>-1022</sup>.  It is equal to the
   * hexadecimal floating-point literal {@code 0x1.0p-1022} and also
   * equal to {@code Double.longBitsToDouble(0x0010000000000000L)}.
   *
   * @since 1.6
   */
  public static final double DBL_MIN_NORMAL = 0x1.0p-1022; // 2.2250738585072014E-308
  /**
   * @see Double.MIN_VALUE
   * A constant holding the smallest positive nonzero value of type
   * <code>double</code>, 2<sup>-1074</sup>. It is equal to the
   * hexadecimal floating-point literal
   * <code>0x0.0000000000001P-1022</code> and also equal to
   * <code>Double.longBitsToDouble(0x1L)</code>.
   */
  public static final double DBL_MIN_VALUE = 0x0.0000000000001P-1022; // 4.9e-324
  /* mathematical constants */
  /** _LPB_PI = value of the variable PI, to avoid any corruption */
  public final static double PI = 3.141592653589793238462643383279503D;
  /** _LPB_DEG2RAD = degree to radian conversion factor */
  public final static double DEG2RAD = PI / 180D;
  /** _LPB_MAS2RAD = milliarcsecond to radian conversion factor */
  public final static double MAS2RAD = DEG2RAD / 3600D / 1000D;

  /**
   * Constructor
   */
  public AbstractModelFunction() {
    super();
  }

  /**
   * Return a new template Model instance with its default parameters.
   *
   * This method must be overriden by child classes to define the model type and specific parameters
   *
   * @return new Model instance
   */
  public Model newModel() {

    final Model model = new Model();

    // common parameters :
    Parameter param;

    param = new Parameter();
    param.setName(PARAM_FLUX_WEIGHT);
    param.setType(PARAM_FLUX_WEIGHT);
    param.setMinValue(0D);
    param.setValue(1D);
    model.getParameters().add(param);

    param = new Parameter();
    param.setName(PARAM_X);
    param.setType(PARAM_X);
    param.setValue(0D);
    param.setUnits(UNIT_MAS);
    model.getParameters().add(param);

    param = new Parameter();
    param.setName(PARAM_Y);
    param.setType(PARAM_Y);
    param.setValue(0D);
    param.setUnits(UNIT_MAS);
    model.getParameters().add(param);

    return model;
  }

  /**
   * Return the parameter value of the given type among the parameters of the given model
   * @param type type of the parameter
   * @param model model to use
   * @return parameter value
   * @throws IllegalArgumentException if the parameter type is invalid for the given model
   */
  public final double getParameterValue(final Model model, final String type) {
    return model.getParameter(type).getValue();
  }

  /* computation utility methods */
  /**
   * shift(ufreq, vfreq, x, y)
   *
   *  Returns complex factor to apply in the Fourier transform at frequencies
   *  (UFREQ,VFREQ) to account for a shift (X,Y) in image space.
   *   X, Y are given in milliarcseconds.
   */
  protected final Complex shift(final double ufreq, final double vfreq, final double x, final double y) {
    final double phase = 2D * PI * MAS2RAD * (x * ufreq + y * vfreq);
    return new Complex(Math.cos(phase), -Math.sin(phase));
  }
}
