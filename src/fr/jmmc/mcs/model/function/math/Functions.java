/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Functions.java,v 1.1 2010-05-17 16:04:01 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function.math;

import cern.jet.math.Bessel;
import org.apache.commons.math.complex.Complex;

/**
 *
 * @author bourgesl
 */
public class Functions {

  /* mathematical constants */
  /** _LPB_PI = value of the variable PI, to avoid any corruption */
  public final static double PI = 3.141592653589793238462643383279503D;
  /** _LPB_DEG2RAD = degree to radian conversion factor */
  public final static double DEG2RAD = PI / 180D;
  /** _LPB_MAS2RAD = milliarcsecond to radian conversion factor */
  public final static double MAS2RAD = DEG2RAD / 3600D / 1000D;

  /**
   * Forbidden constructor
   */
  private Functions() {
    super();
  }

  /**
   * shift(ufreq, vfreq, x, y)
   *
   * Returns the complex value applied in the Fourier transform at frequencies
   * (UFREQ,VFREQ) to account for a shift (X,Y) in image space of the given value.
   * X, Y are given in milliarcseconds.
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param x X (mas)
   * @param y Y (mas)
   * @param value value to shift
   * @return complex factor
   */
  public static final Complex shift(final double ufreq, final double vfreq, final double x, final double y, final double value) {
    final double phase = 2D * PI * MAS2RAD * (x * ufreq + y * vfreq);
    return new Complex(value * Math.cos(phase), -value * Math.sin(phase));
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
  public static final double transformU(final double ufreq, final double vfreq,
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
  public static final double transformV(final double ufreq, final double vfreq,
                                        final double homothetieRatio, final double rotation) {

    final double angle = DEG2RAD * (90D - rotation);

    return -ufreq * (Math.sin(angle) * homothetieRatio) + vfreq * (Math.cos(angle) * homothetieRatio);
  }

  /* Model functions */

  /**
   * Compute the punct model function for a single UV point
   *
   * @param flux_weight intensity coefficient
   * @return Fourier transform value
   */
  public final static double computePunct(final double flux_weight) {
    return flux_weight;
  }

  /**
   * Compute the circle model function for a single UV point
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param diameter diameter of the circle model given in milliarcsecond
   * @return Fourier transform value
   */
  public final static double computeCircle(final double ufreq, final double vfreq, final double flux_weight,
                                           final double diameter) {

    // norm of uv :
    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double d = PI * MAS2RAD * diameter * normUV;

    double g;
    if (d == 0D) {
      g = 1D;
    } else {
      g = Bessel.j0(d);
    }
    g *= flux_weight;

    return g;
  }

  /**
   * Compute the disk model function for a single UV point
   *
   * flux_weight * [ 2 * bessJ1(PI x diameter x norm(uv)) / PI x diameter x norm(uv)]
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param diameter diameter of the uniform disk object given in milliarcsecond
   * @return Fourier transform value
   */
  public final static double computeDisk(final double ufreq, final double vfreq, final double flux_weight,
                                         final double diameter) {

    // norm of uv :
    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double d = PI * MAS2RAD * diameter * normUV;

    double g;
    if (d == 0D) {
      g = 1D;
    } else {
      g = 2D * Bessel.j1(d) / d;
    }
    g *= flux_weight;

    return g;
  }

  /**
   * Compute the ring model function for a single UV point
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param diameter diameter of the uniform ring object given in milliarcsecond
   * @param width width of the uniform ring object given in milliarcsecond
   * @return Fourier transform value
   */
  public final static double computeRing(final double ufreq, final double vfreq, final double flux_weight,
                                         final double diameter, final double width) {

    if (width == 0d) {
      // infinitely thin ring, i.e. a circle.
      return computeCircle(ufreq, vfreq, flux_weight, diameter);
    }
    if (diameter == 0d) {
      // disk of radius width.
      return computeDisk(ufreq, vfreq, flux_weight, 2d * width);
    }

    // norm of uv :
    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double alpha = 1d + width * diameter / 2d;

    final double r = PI * MAS2RAD * diameter * normUV / 2d;

    double g;
    if (r == 0D) {
      g = 1D;
    } else {
      g = ((alpha * Bessel.j1(2d * alpha * r) / r) - (Bessel.j1(2d * r) / r)) / (alpha * alpha - 1d);
    }
    g *= flux_weight;

    return g;
  }
}
