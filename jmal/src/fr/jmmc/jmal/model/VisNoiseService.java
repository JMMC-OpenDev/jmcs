/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import java.util.Random;

/**
 * This interface provides methods used to apply noise on complex visibility
 * @author bourgesl
 */
public abstract class VisNoiseService {

    /** VisAmpErr to CplxVisErr(re/im) coefficient = 1 / SQRT(2) */
    public final static double VIS_AMP_TO_VIS_CPX_ERR = 1d / Math.sqrt(2d);
    /** CplxVisErr(re/im) to VisAmpErr coefficient = 1 / SQRT(2) */
    public final static double VIS_CPX_TO_VIS_AMP_ERR = Math.sqrt(2d);

    /**
     * Return true if this service is enabled
     * @return true if this service is enabled 
     */
    public abstract boolean isEnabled();

    /**
     * Compute error on complex visibility given its amplitude.
     * It returns Double.NaN if the error can not be computed
     *
     * @param visAmp visibility amplitude
     * @return complex visiblity error or NaN if the error can not be computed
     */
    public abstract double computeVisComplexErrorValue(final double visAmp);

    /**
     * Compute error on square visibility. It returns NaN if the error can not be computed
     *
     * @param visAmp visibility amplitude
     * @return square visiblity error or NaN if the error can not be computed
     */
    public abstract double computeVis2Error(final double visAmp);

    /**
     * Get gaussian noise value given its error (= standard deviation)
     * @param random Random number generator
     * @param visErr complex visiblity error or NaN if the error can not be computed
     * @return gaussian noise value
     */
    public static double gaussianNoise(final Random random, final double visErr) {
        return visErr * random.nextGaussian();
    }
}
