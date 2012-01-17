/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.function.math;

/**
 * This class computes the Fourier transform at frequencies (UFREQ,VFREQ) of a ring object.
 *
 * @author Laurent BOURGES.
 */
public class RingFunction extends DiskFunction {

    /** ring width (mas) */
    protected double width;

    /**
     * Public constructor
     */
    public RingFunction() {
        super();
    }

    /**
     * Define the width (mas)
     *
     * @param width width (mas)
     */
    public final void setWidth(double width) {
        this.width = width;
    }

    /**
     * Compute the Fourier transform at frequencies (UFREQ,VFREQ) of this object
     *
     * @param ufreq U frequency in rad-1
     * @param vfreq V frequency in rad-1
     * @return Fourier transform value
     */
    @Override
    public double computeWeight(final double ufreq, final double vfreq) {
        if (axisRatio != 1d) {
            // transform UV coordinates :
            final double t_ufreq = Functions.transformU(ufreq, vfreq, axisRatio, cosBeta, sinBeta);
            final double t_vfreq = Functions.transformV(ufreq, vfreq, cosBeta, sinBeta);

            return Functions.computeRing(t_ufreq, t_vfreq, flux_weight, diameter, width);
        }
        return Functions.computeRing(ufreq, vfreq, flux_weight, diameter, width);
    }
}
