/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

/**
 * This interface provides methods used to apply noise on complex visibility
 * @author bourgesl
 */
public interface VisNoiseService {

    /**
     * Return true if this service is enabled
     * @return true if this service is enabled 
     */
    public boolean isEnabled();

    /**
     * Compute error on complex visibility given its amplitude.
     * It returns Double.NaN if the error can not be computed
     *
     * @param visAmp visibility amplitude
     * @param forAmplitude true to compute error for amplitudes (including the photometric error); false to compute error for phases
     * @return complex visiblity error or NaN if the error can not be computed
     */
    public double computeVisComplexErrorValue(final double visAmp, final boolean forAmplitude);

}
