/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function.math;

import cern.jet.math.Bessel;
import org.apache.commons.math.complex.Complex;

/**
 * @author Laurent BOURGES.
 */
public class Functions {

  /* mathematical constants */
  /** _LPB_PI = value of the variable PI, to avoid any corruption */
  public final static double PI = 3.141592653589793238462643383279503D;
  /** _LPB_DEG2RAD = degree to radian conversion factor */
  public final static double DEG2RAD = PI / 180D;
  /** _LPB_MAS2RAD = milliarcsecond to radian conversion factor */
  public final static double MAS2RAD = DEG2RAD / 3600D / 1000D;
  /** constant used to compute the gaussian model */
  private final static double GAUSS_CST = 4d * Math.log(2d);

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
   * transform(ufreq, vfreq, t_ana, rotation)
   *
   * Returns the new spatial frequencies when the object has got geometrical
   * transformations, successively a rotation and an anamorphose.
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
   *
   * The angle ROTATION is the astronomical position angle,       |North
   * equal to 0 or 180 for x=0, and  90 or 270 for y=0.           |
   * so, ROTATION = 90 - beta                                  ---|--->East
   * the positive x-semi-axis being the Est direction, and        |
   * the positive y-semi-axis beeing the North direction.         |
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param anamorphoseRatio t_ana = ratio of anamorphose, >0
   * @param rotation rotation angle in degrees
   * @return new spatial frequency UFREQ
   */
  public static final double transformU(final double ufreq, final double vfreq,
                                        final double anamorphoseRatio, final double rotation) {

    final double angle = DEG2RAD * (90D - rotation);

    return ufreq * Math.cos(angle) * anamorphoseRatio + vfreq * Math.sin(angle) * anamorphoseRatio;
  }

  /**
   * Return the new spatial frequency V
   * transform(ufreq, vfreq, t_ana, rotation)
   *
   * Returns the new spatial frequencies when the object has got geometrical
   * transformations, only a rotation.
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
   * The angle ROTATION is the astronomical position angle,       |North
   * equal to 0 or 180 for x=0, and  90 or 270 for y=0.           |
   * so, ROTATION = 90 - beta                                  ---|--->East
   * the positive x-semi-axis being the Est direction, and        |
   * the positive y-semi-axis beeing the North direction.         |
   * @param ufreq UFREQ
   * @param vfreq VFREQ
   * @param rotation rotation angle in degrees
   * @return new spatial frequency VFREQ
   */
  public static final double transformV(final double ufreq, final double vfreq,
                                        final double rotation) {

    final double angle = DEG2RAD * (90D - rotation);

    return -ufreq * Math.sin(angle) + vfreq * Math.cos(angle);
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

    final double radius = 0.5d * diameter;
    
    final double alpha = 1d + width / radius;

    final double r = PI * MAS2RAD * radius * normUV;

    double g;
    if (r == 0D) {
      g = 1D;
    } else {
      g = ((alpha * Bessel.j1(2d * alpha * r) / r) - (Bessel.j1(2d * r) / r)) / (alpha * alpha - 1d);
    }
    g *= flux_weight;

    return g;
  }

  /**
   * Compute the gaussian model function for a single UV point
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param fwhm full width at half maximum of the gaussian object given in milliarcsecond (diameter like)
   * @return Fourier transform value
   */
  public final static double computeGaussian(final double ufreq, final double vfreq, final double flux_weight,
                                             final double fwhm) {

    final double f = PI * MAS2RAD * fwhm;

    final double d = -f * f / GAUSS_CST * (ufreq * ufreq + vfreq * vfreq);

    double g;
    if (d == 0D) {
      g = 1D;
    } else {
      g = Math.exp(d);
    }
    g *= flux_weight;

    return g;
  }

  /**
   * Compute the center-to-limb darkened disk model function for a single UV point :
   * o(mu) = 1 - A1(1-mu) - A2(1-mu)^2.
   *
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @param flux_weight intensity coefficient
   * @param diameter diameter of the disk object given in milliarcsecond
   * @param a1 first coefficient of the quadratic law
   * @param a2 second coefficient of the quadratic law
   * @return Fourier transform value
   */
  public final static double computeLimbQuadratic(final double ufreq, final double vfreq, final double flux_weight,
                                                  final double diameter, final double a1, final double a2) {
    /*
     * 11- Limb darkened Disk
     *     g(u,v) = ( a*(j1/x) + b* sqrt(PI/2)*(J3over2/x**1.5) + 2*c*(j2/x**2) )/s
     *     where
     *       BesselJ[3/2, z] == (Sqrt[2/Pi] ((-z) Cos[z] + Sin[z]))/z^(3/2)
     *       x=pi*diametre*q
     *       j1=J1(x)
     *       j2=J2(x)
     *       j3over2(x)=sqrt((2/PI)/x)*((sin(x)/x)-cos(x))
     *       a=1-cu-cv
     *       b=cu+2*cv
     *       c=-cv
     *       s=a/2+b/3+c/4
     *       q**2 = u**2+v**2
     */

    final double normUV = Math.sqrt(ufreq * ufreq + vfreq * vfreq);

    final double d = PI * MAS2RAD * diameter * normUV;

    double g;
    if (d == 0D) {
      g = 1D;
    } else {
      final double a = 1d - a1 - a2;
      final double b = a1 + 2d * a2;
      final double c = -a2;
      final double s = a / 2d + b / 3d + c / 4d;

      // Note : BesselJ[3/2, z] == (Sqrt[2/Pi] ((-z) Cos[z] + Sin[z]))/z^(3/2)
      // BesselJ[3/2, d] * Sqrt(Pi/2) :
      final double term2 = (Math.sin(d) / d - Math.cos(d)) / Math.sqrt(d);

      g = (a * Bessel.j1(d) / d + b * term2 / Math.pow(d, 1.5d) + 2d * c * Bessel.jn(2, d) / (d * d)) / s;
    }
    g *= flux_weight;

    return g;
  }
}
