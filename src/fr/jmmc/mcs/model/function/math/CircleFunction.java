/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: CircleFunction.java,v 1.1 2010-05-17 16:05:30 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.model.function.math;

/**
 * This class computes the Fourier transform at frequencies (UFREQ,VFREQ) of a circle object.
 * @author bourgesl
 */
public class CircleFunction extends PunctFunction {

  /** diameter */
  protected double diameter;

  /**
   * Public constructor
   */
  public CircleFunction() {
    super();
  }

  public void setDiameter(final double diameter) {
    this.diameter = diameter;
  }

  /**
   * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @return Fourier transform value
   */
  @Override
  public double computeWeight(final double ufreq, final double vfreq) {
    return Functions.computeCircle(ufreq, vfreq, flux_weight, diameter);
  }
}
