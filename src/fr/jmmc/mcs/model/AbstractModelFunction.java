/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: AbstractModelFunction.java,v 1.5 2010-05-11 16:09:30 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2010/02/18 15:51:18  bourgesl
 * added parameter argument validation and propagation (illegal argument exception)
 *
 * Revision 1.3  2010/02/16 14:43:06  bourgesl
 * use the model.getParameter(type) instead of ModelManager
 *
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
  protected final static double getParameterValue(final Model model, final String type) {
    final Parameter parameter = model.getParameter(type);
    if (parameter == null) {
      throw new IllegalArgumentException("parameter [" + type + "] not found in the model [" + model.getName() + "] !");
    }
    return parameter.getValue();
  }

  /**
   * Create a parameter validation exception
   * @param type type of the parameter
   * @param model model instance
   * @param message validation message [< 0 for example]
   * @throws IllegalArgumentException
   */
  protected static void createParameterException(final String type, final Model model, final String message) throws IllegalArgumentException {
    final Parameter parameter = model.getParameter(type);

    throw new IllegalArgumentException(parameter.getName() + " [" + parameter.getValue() + "] " + message + " not allowed in the model [" + model.getName() + "] !");
  }

  /* computation utility methods */
  /**
   * shift(ufreq, vfreq, x, y)
   *
   *  Returns complex factor to apply in the Fourier transform at frequencies
   *  (UFREQ,VFREQ) to account for a shift (X,Y) in image space.
   *   X, Y are given in milliarcseconds.
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param x X (mas)
   * @param y Y (mas)
   * @return complex factor
   */
  protected static final Complex shift(final double ufreq, final double vfreq, final double x, final double y) {
    final double phase = 2D * PI * MAS2RAD * (x * ufreq + y * vfreq);
    return new Complex(Math.cos(phase), -Math.sin(phase));
  }

  /**
   * Return the new spatial frequency U
   * transform(ufreq, vfreq, t_ana, t_homo, rotation)
   *
   * Returns the new spatial frequencies when the object has got geometrical
   * transformations, successively a rotation, an anamorphose and a homothetie.
   * (u,v)--> Transpose(Inverse(T))(u,v), with matrix T = HAR;
   * Inverse(R)= |cos(beta) -sin(beta)|
   *             |sin(beta)  cos(beta)|   beta angle in degrees
   *   beta is the trigonometric angle
   *       |y
   *       |
   *    ---|---> x    beta =0 or 180 for y=0, beta = 90 or -90 for x=0)
   *       |
   *       |
   *
   * Inverse(A)= |t_ana 0|
   *             |0     1|  t_ana = ratio of anamorphose, >0
   * Inverse(H)= |t_homo   0|
   *             |0   t_homo|  t_homo = ratio of homothetie >0
   *
   * The angle ROTATION is the astronomical position angle,       |North
   * equal to 0 or 180 for x=0, and  90 or 270 for y=0.           |
   * so, ROTATION = 90 - beta                                  ---|--->East
   * the positive x-semi-axis being the Est direction, and        |
   * the positive y-semi-axis beeing the North direction.         |
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param anamorphoseRatio t_ana = ratio of anamorphose, >0
   * @param homothetieRatio t_homo = ratio of homothetie >0
   * @param rotation rotation angle in degrees
   * @return new spatial frequency UFREQ
   */
  protected static final double transformU(final double ufreq, final double vfreq,
                                           final double anamorphoseRatio, final double homothetieRatio, final double rotation) {

    final double angle = DEG2RAD * (90D - rotation);

    return ufreq * (Math.cos(angle) * anamorphoseRatio * homothetieRatio) +
            vfreq * (Math.sin(angle) * anamorphoseRatio * homothetieRatio);
  }

  /**
   * Return the new spatial frequency V
   * transform(ufreq, vfreq, t_ana, t_homo, rotation)
   *
   * Returns the new spatial frequencies when the object has got geometrical
   * transformations, successively a rotation, an anamorphose and a homothetie.
   * (u,v)--> Transpose(Inverse(T))(u,v), with matrix T = HAR;
   * Inverse(R)= |cos(beta) -sin(beta)|
   *             |sin(beta)  cos(beta)|   beta angle in degrees
   *   beta is the trigonometric angle
   *       |y
   *       |
   *    ---|---> x    beta =0 or 180 for y=0, beta = 90 or -90 for x=0)
   *       |
   *       |
   *
   * Inverse(A)= |t_ana 0|
   *             |0     1|  t_ana = ratio of anamorphose, >0
   * Inverse(H)= |t_homo   0|
   *             |0   t_homo|  t_homo = ratio of homothetie >0
   *
   * The angle ROTATION is the astronomical position angle,       |North
   * equal to 0 or 180 for x=0, and  90 or 270 for y=0.           |
   * so, ROTATION = 90 - beta                                  ---|--->East
   * the positive x-semi-axis being the Est direction, and        |
   * the positive y-semi-axis beeing the North direction.         |
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param homothetieRatio t_homo = ratio of homothetie >0
   * @param rotation rotation angle in degrees
   * @return new spatial frequency VFREQ
   */
  protected static final double transformV(final double ufreq, final double vfreq,
                                           final double homothetieRatio, final double rotation) {

    final double angle = DEG2RAD * (90D - rotation);

    return -ufreq * (Math.sin(angle) * homothetieRatio) + vfreq * (Math.cos(angle) * homothetieRatio);
  }
}
