/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.function.math;

/**
 * This class is the base class of all functions computing the Fourier transform at frequencies (UFREQ,VFREQ) of an object.
 *
 * @author Laurent BOURGES.
 */
public class PunctFunction {

    /** x coordinate of the object (mas) */
    protected double x = 0d;
    /** y coordinate of the object (mas) */
    protected double y = 0d;
    /** intensity coefficient of the object */
    protected double flux_weight;
    /** flag to indicate that x = 0 and y = 0 */
    protected boolean zero;

    /**
     * Public constructor
     */
    public PunctFunction() {
        super();
    }

    /**
     * Return true when x = 0 and y = 0
     *
     * @return true when x = 0 and y = 0
     */
    public final boolean isZero() {
        return zero;
    }

    /**
     * Update the zero flag i.e. true when x = 0 and y = 0
     */
    private void updateZero() {
        zero = (x == 0d) && (y == 0d);
    }

    /**
     * Return the x coordinate of the object (mas)
     *
     * @return x coordinate of the object (mas)
     */
    public final double getX() {
        return x;
    }

    /**
     * Define the x coordinate of the object (mas)
     *
     * @param x x coordinate of the object (mas)
     */
    public final void setX(final double x) {
        this.x = x;
        updateZero();
    }

    /**
     * Return the y coordinate of the object (mas)
     *
     * @return y coordinate of the object (mas)
     */
    public final double getY() {
        return y;
    }

    /**
     * Define the y coordinate of the object (mas)
     *
     * @param y coordinate of the object (mas)
     */
    public final void setY(final double y) {
        this.y = y;
        updateZero();
    }

    /**
     * Define the intensity coefficient of the object
     *
     * @param fluxWeight intensity coefficient of the object
     */
    public final void setFluxWeight(final double fluxWeight) {
        this.flux_weight = fluxWeight;
    }

    /**
     * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
     *
     * @param ufreq U frequency in rad-1
     * @param vfreq V frequency in rad-1
     * @return Fourier transform value
     */
    public double computeWeight(final double ufreq, final double vfreq) {
        return Functions.computePunct(flux_weight);
    }
}
