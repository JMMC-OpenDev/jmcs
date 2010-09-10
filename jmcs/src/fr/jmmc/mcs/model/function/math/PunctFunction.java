/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PunctFunction.java,v 1.2 2010-05-18 15:34:33 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/05/17 16:05:30  bourgesl
 * This function classes only contain parameters and use Functions for computation. Disk and Ring support elongated / flattened variants (i.e. transform UV coordinates)
 *
 */
package fr.jmmc.mcs.model.function.math;

/**
 * This class is the base class of all functions computing the Fourier transform at frequencies (UFREQ,VFREQ) of an object.
 * @author bourgesl
 */
public class PunctFunction {

  /** x coordinate of the object */
  protected double x;
  /** y coordinate of the object */
  protected double y;
  /** intensity coefficient of the object */
  protected double flux_weight;

  /**
   * Public constructor
   */
  public PunctFunction() {
    super();
  }

  public final double getX() {
    return x;
  }

  public final void setX(final double x) {
    this.x = x;
  }

  public final double getY() {
    return y;
  }

  public final void setY(final double y) {
    this.y = y;
  }

  public final void setFluxWeight(final double fluxWeight) {
    this.flux_weight = fluxWeight;
  }

  /**
   * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
   * @param ufreq U frequency in rad-1
   * @param vfreq V frequency in rad-1
   * @return Fourier transform value
   */
  public double computeWeight(final double ufreq, final double vfreq) {
    return Functions.computePunct(flux_weight);
  }
}
