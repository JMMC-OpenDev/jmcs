/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: RingFunction.java,v 1.1 2010-05-17 16:05:30 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function.math;

/**
 * This class computes the Fourier transform at frequencies (UFREQ,VFREQ) of a ring object.
 * @author bourgesl
 */
public class RingFunction extends DiskFunction {

  /** ring width */
  protected double width;

  /**
   * Public constructor
   */
  public RingFunction() {
    super();
  }

  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @return Fourier transform value
   */
  @Override
  public double computeWeight(final double ufreq, final double vfreq) {
    if (axisRatio != 1d) {
      // transform UV coordinates :
      final double t_ufreq = Functions.transformU(ufreq, vfreq, axisRatio, 1d, positionAngle);
      final double t_vfreq = Functions.transformV(ufreq, vfreq, 1d, positionAngle);

      return Functions.computeRing(t_ufreq, t_vfreq, flux_weight, diameter, width);
    }
    return Functions.computeRing(ufreq, vfreq, flux_weight, diameter, width);
  }
}
