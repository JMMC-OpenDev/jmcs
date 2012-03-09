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
     * @return complex visiblity error or NaN if the error can not be computed
     */
    public double computeVisComplexErrorValue(final double visAmp);

    /**
     * Get gaussian noise value given its error (= standard deviation)
     * @param visErr complex visiblity error or NaN if the error can not be computed
     * @return gaussian noise value
     */
    public double getNoise(final double visErr);
}
