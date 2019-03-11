/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.ImmutableComplex;
import java.util.Random;
import net.jafama.FastMath;

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

                // Re/Im are two independent variables:
                re += err * threadRandom.nextGaussian();
                im += err * threadRandom.nextGaussian();
            }

            return (float) FastMath.toDegrees(ImmutableComplex.getArgument(re, im));
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
                // use complex visibility error:
                final double amp = ImmutableComplex.abs(re, im);
                final double err = this.noiseService.computeVisComplexErrorValue(amp);

                // Re/Im are two independent variables:
                re += err * threadRandom.nextGaussian();
                im += err * threadRandom.nextGaussian();

                final double noisyAmp = ImmutableComplex.abs(re, im);

                // Very noisy data when amp < err (ie SNR < 1):
                if ((noisyAmp > amp) && (err > amp)) {
                    // use 0 to ignore visually such pixels (use another blanking value ?)
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
                final double err = this.noiseService.computeVisComplexErrorValue(amp);

                // Re/Im are two independent variables:
                re += err * threadRandom.nextGaussian();
                im += err * threadRandom.nextGaussian();

                final double noisyAmp = ImmutableComplex.abs(re, im);

                // Very noisy data when amp < err (ie SNR < 1):
                if ((noisyAmp > amp) && (err > amp)) {
                    // use 0 to ignore visually such pixels (use another blanking value ?)
                    return 0f;
                }
                return (float) (noisyAmp * noisyAmp);
            }

            return (float) (re * re + im * im);
        }
    }
}
