/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: DiskFunction.java,v 1.1 2010-05-17 16:05:30 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function.math;

/**
 * This class computes the Fourier transform at frequencies (UFREQ,VFREQ) of a disk object.
 *
 * Note on the diameter for the disk model.
 * For the elongated model, the minor axis diameter.
 * For the flattened model, the major axis diameter.
 *
 * @author bourgesl
 */
public class DiskFunction extends CircleFunction {

  /**
   * Axis ratio :
   * For the elongated model, the axis ratio = major axis / minor axis.
   * For the flattened model, the axis ratio = minor axis / major axis.
   * (1 for the basic model)
   */
  protected double axisRatio = 1d;
  /**
   * Position angle :
   * For the elongated model, the angle relative to the major axis.
   * For the flattened model, the angle relative to the minor axis.
   * (0 for the disk model)
   */
  protected double positionAngle = 0d;

  /**
   * Public constructor
   */
  public DiskFunction() {
    super();
  }

  public void setAxisRatio(final double axisRatio) {
    this.axisRatio = axisRatio;
  }

  public void setPositionAngle(final double positionAngle) {
    this.positionAngle = positionAngle;
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

      return Functions.computeDisk(t_ufreq, t_vfreq, flux_weight, diameter);
    }

    return Functions.computeDisk(ufreq, vfreq, flux_weight, diameter);
  }
}
