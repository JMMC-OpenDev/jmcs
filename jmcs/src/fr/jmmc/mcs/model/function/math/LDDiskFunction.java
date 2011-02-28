/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LDDiskFunction.java,v 1.1 2010-05-18 15:34:47 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function.math;

/**
 * This class computes the Fourier transform at frequencies (UFREQ,VFREQ) of a limb darkened disk object.
 *
 * @author bourgesl
 */
public class LDDiskFunction extends CircleFunction {

  /** a1 coefficient [-1;1] */
  protected double a1;
  /** a2 coefficient [-1;1] */
  protected double a2;

  /**
   * Public constructor
   */
  public LDDiskFunction() {
    super();
  }

  public final void setA1(final double a1) {
    this.a1 = a1;
  }

  public final void setA2(final double a2) {
    this.a2 = a2;
  }

  /**
   * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @return Fourier transform value
   */
  @Override
  public double computeWeight(final double ufreq, final double vfreq) {
    return Functions.computeLimbQuadratic(ufreq, vfreq, flux_weight, diameter, a1, a2);
  }
}
