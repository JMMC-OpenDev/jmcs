/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.ImmutableComplex;
import java.util.Random;

/**
 * This class performs complex visibility conversion to amplitude or phase and optionaly add error noise
 * @author bourgesl
 */
public abstract class VisConverter {

    /** image mode (amplitude, phase or square visibility) */
    protected final ImageMode mode;
    /** true to add noise to data */
    protected final boolean doNoise;
    /** optional noise service to compute noisy data before conversion */
    protected final VisNoiseService noiseService;

    /**
     * Return a new VisConverter instance for the given image mode 
     * @param mode image mode (amplitude, phase or square visibility)
     * @param noiseService optional noise service to compute noisy data before conversion
     * @return new VisConverter instance
     */
    public static VisConverter create(final ImageMode mode, final VisNoiseService noiseService) {
        switch (mode) {
            default:
            case AMP:
                return new VisAmpConverter(mode, noiseService);
            case PHASE:
                return new VisPhiConverter(mode, noiseService);
            case SQUARE:
                return new Vis2Converter(mode, noiseService);
        }
    }

    /**
     * Constructor
     * @param mode image mode (amplitude, phase or square visibility)
     * @param noiseService optional noise service to compute noisy data before conversion
     */
    VisConverter(final ImageMode mode, final VisNoiseService noiseService) {
        this.mode = mode;
        this.doNoise = (noiseService != null && noiseService.isEnabled());
        this.noiseService = (this.doNoise) ? noiseService : null;
    }

    /**
     * Convert the given real and imaginary parts of the complex visibility to amplitude or phase
     * @param re real part of the complex visibility
     * @param im imaginary part of the complex visibility
     * @param threadRandom random instance dedicated to the current thread
     * @return amplitude or phase
     */
    public abstract float convert(double re, double im, final Random threadRandom);

    /**
     * Converter that returns visibility phase (rad)
     */
    private final static class VisPhiConverter extends VisConverter {

        /**
         * Constructor
         * @param mode image mode (amplitude, phase or square visibility)
         * @param noiseService optional noise service to compute noisy data before conversion
         */
        VisPhiConverter(final ImageMode mode, final VisNoiseService noiseService) {
            super(mode, noiseService);
        }

        /**
         * Convert the given real and imaginary parts of the complex visibility to phase
         * @param re real part of the complex visibility
         * @param im imaginary part of the complex visibility
         * @param threadRandom random instance dedicated to the current thread
         * @return phase
         */
        @SuppressWarnings("AssignmentToMethodParameter")
        @Override
        public float convert(double re, double im, final Random threadRandom) {
            if (this.doNoise) {
                final double amp = ImmutableComplex.abs(re, im);
                final double err = this.noiseService.computeVisComplexErrorValue(amp);

                re += VisNoiseService.gaussianNoise(threadRandom, err);
                im += VisNoiseService.gaussianNoise(threadRandom, err);
            }

            return (float) ImmutableComplex.getArgument(re, im);
        }
    }

    /**
     * Converter that returns visibility amplitude
     */
    private final static class VisAmpConverter extends VisConverter {

        /**
         * Constructor
         * @param mode image mode (amplitude, phase or square visibility)
         * @param noiseService optional noise service to compute noisy data before conversion
         */
        VisAmpConverter(final ImageMode mode, final VisNoiseService noiseService) {
            super(mode, noiseService);
        }

        /**
         * Convert the given real and imaginary parts of the complex visibility to amplitude
         * @param re real part of the complex visibility
         * @param im imaginary part of the complex visibility
         * @param threadRandom random instance dedicated to the current thread
         * @return amplitude
         */
        @SuppressWarnings("AssignmentToMethodParameter")
        @Override
        public float convert(double re, double im, final Random threadRandom) {
            if (this.doNoise) {
                final double amp = ImmutableComplex.abs(re, im);
                final double err = this.noiseService.computeVisComplexErrorValue(amp);

                re += VisNoiseService.gaussianNoise(threadRandom, err);
                im += VisNoiseService.gaussianNoise(threadRandom, err);

                final double noisyAmp = ImmutableComplex.abs(re, im);
                // Invalid data when amp > SQRT(2) x sigma (SNR < 1) and noisy amp > amp:
                if (VisNoiseService.VIS_CPX_TO_VIS_AMP_ERR * err > amp && noisyAmp > amp) {
                    // discard too noisy data:
                    return 0f;
                }
                return (float) noisyAmp;
            }

            return (float) ImmutableComplex.abs(re, im);
        }
    }

    /**
     * Converter that returns square visibility
     */
    private final static class Vis2Converter extends VisConverter {

        /**
         * Constructor
         * @param mode image mode (amplitude, phase or square visibility)
         * @param noiseService optional noise service to compute noisy data before conversion
         */
        Vis2Converter(final ImageMode mode, final VisNoiseService noiseService) {
            super(mode, noiseService);
        }

        /**
         * Convert the given real and imaginary parts of the complex visibility to amplitude
         * @param re real part of the complex visibility
         * @param im imaginary part of the complex visibility
         * @param threadRandom random instance dedicated to the current thread
         * @return amplitude
         */
        @SuppressWarnings("AssignmentToMethodParameter")
        @Override
        public float convert(double re, double im, final Random threadRandom) {
            if (this.doNoise) {
                final double amp = ImmutableComplex.abs(re, im);
                final double err = this.noiseService.computeVis2Error(amp);

                final double vis2 = amp * amp;
                final double noisyVis2 = vis2 + VisNoiseService.gaussianNoise(threadRandom, err);

                if (false) {
                    // Invalid data when V2 > sigma (SNR < 1) and noisy V2 > V2:
                    if (err > vis2 && noisyVis2 > vis2) {
                        // discard too noisy data:
                        return 0f;
                    }
                }
                
                // TODO: decide if discard or take abs negative values due to noise:
                if (noisyVis2 < 0d) {
                    return (float)-noisyVis2;
                }

                return (float) noisyVis2;
            }

            return (float) (re * re + im * im);
        }
    }
}
