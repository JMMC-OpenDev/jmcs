/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal;

import net.jafama.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Photometry band related information
 *
 * @author Laurent BOURGES, Sylvain LAFRASSE.
 */
public enum Band {

    /** U (ultra violet) */
    U("U", 0.334d, 0.066d, -1.4d, 0.3d),
    /** B (Visible) */
    B("B", 0.461875d, 0.08175d, -1.2d, 0.48d),
    /** V (Visible) */
    V("V", 0.556d, 0.1105d, -1.44d, 0.5d),
    /** R (Visible) */
    R("R", 0.6625d, 0.10651d, -1.65d, 0.65d),
    /** I (Near Infrared) */
    I("I", 0.869625d, 0.31176d, -1.94d, 0.75d),
    /** J (Near Infrared) */
    J("J", 1.2365d, 0.426d, -2.5d, 0.77d),
    /** H (Near Infrared) */
    H("H", 1.679625d, 0.46425d, -2.94d, 0.84d),
    /** K (Near Infrared) */
    K("K", 2.365625d, 0.912d, -3.4d, 0.93d),
    /** L (Near Infrared) */
    L("L", 3.45875d, 1.2785d, -4.15d, 0.972d),
    /** M (Mid Infrared) */
    M("M", 6.4035d, 4.615d, -4.69d, 0.985d),
    /** N (Mid Infrared) */
    N("N", 11.63d, 5.842d, -5.91d, 0.996d),
    /** Q (Mid Infrared) */
    Q("Q", 16.575d, 4.05d, -7.17d, 0.999d);
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(Band.class.getName());
    /** r0 factor = 1.22*1E-6/a and a = PI / ( 180 * 3600 ) */
    private static final double R0_FACTOR = 1.22e-6 * (180 * 3600) / Math.PI;

    /**
     * Find the band corresponding to the given wavelength
     *
     * @param waveLength wave length in microns
     * @return corresponding band
     * @throws IllegalArgumentException if no band found
     */
    public static Band findBand(final double waveLength) throws IllegalArgumentException {
        for (Band b : values()) {
            if (Math.abs(waveLength - b.getLambda()) <= (0.5d * b.getBandWidth())) {
                return b;
            }
        }
        throw new IllegalArgumentException("no band found for the wave length = " + waveLength);
    }

    /**
     * Compute the strehl ratio. see le louarn et al (1998, mnras 295, 756), and amb-igr-011 p.5
     *
     * @param magnitude object magnitude in AO's band
     * @param waveLength wave length in microns
     * @param diameter telescope diameter in meters
     * @param seeing seeing in arc sec
     * @param nbOfActuators
     * @return strehl ratio
     */
    public static double strehl(final double magnitude, final double waveLength,
                                final double diameter, final double seeing, final int nbOfActuators) {

        if (logger.isDebugEnabled()) {
            logger.debug("magnitude     = {}", magnitude);
            logger.debug("waveLength    = {}", waveLength);
            logger.debug("diameter      = {}", diameter);
            logger.debug("seeing        = {}", seeing);
            logger.debug("nbOfActuators = {}", nbOfActuators);
        }

        final double n_act = (double) nbOfActuators;

        final double lambdaV = 0.55;

        final double lambdaRatio = waveLength / lambdaV;

        // explication formule r0:
        // seeing=angular FWHM of seeing in V= 1.22 lambdaV/(r0) r0=fried coherence length.
        // to have seeing in arcsec and all wavelengths in microns, we have
        // seeing * a = 1.22 * lambdaV * 1e-6 / r0 with a=1 arcsec in RD=PI/180*3600
        // thus r0 = 1.22*1E-6 / a * seeing = 0.251 * lambdaV / seeing
        // R0_FACTOR = 0.251...
        final double r0 = R0_FACTOR * lambdaV / seeing * FastMath.pow(lambdaRatio, 6d / 5d);
        final double d_over_r0 = diameter / r0;

        // 0.87 = AMD-REP 001 p32 (related to AO system)
        final double sigmaphi2_alias = 0.87d * FastMath.pow(n_act, -5d / 6d) * FastMath.pow(d_over_r0, 5d / 3d);

        final double sigmaphi2_phot = 1.59e-8d * (d_over_r0 * d_over_r0) * FastMath.pow(lambdaRatio, -2d)
                * n_act * FastMath.pow(10d, 0.4d * magnitude);

        final Band band = findBand(waveLength);

        final double sigmaphi2_fixe = -Math.log(band.getStrehlMax());

        final double sigmaphi2 = sigmaphi2_alias + sigmaphi2_phot + sigmaphi2_fixe;

        final double e_sigmaphi2 = FastMath.exp(-sigmaphi2);

        final double strehl = e_sigmaphi2 + (1 - e_sigmaphi2) / (1 + d_over_r0 * d_over_r0);

        if (logger.isDebugEnabled()) {
            logger.debug("lambda          = {}", waveLength);
            logger.debug("r0              = {}", r0);
            logger.debug("doverr0         = {}", d_over_r0);
            logger.debug("sigmaphi2_alias = {}", sigmaphi2_alias);
            logger.debug("sigmaphi2_phot  = {}", sigmaphi2_phot);
            logger.debug("sigmaphi2_fixe  = {}", sigmaphi2_fixe);
            logger.debug("sigmaphi2       = {}", sigmaphi2);
            logger.debug("strehl          = {}", strehl);
        }

        return strehl;
    }

    /* members */
    /** single char band name (upper case) */
    private final String name;
    /** central wave length in microns */
    private final double lambda;
    /** band width in microns */
    private final double bandWidth;
    /** log10 zero magnitude flux at band in W/m^2/m */
    private final double logFluxZero;
    /** maximum strehl ratio */
    private final double strehlMax;

    /**
     * Custom constructor
     *
     * @param name band name
     * @param lambda central wave length in microns
     * @param bandWidth band width in microns
     * @param logFluxZero log10 zero magnitude flux at band in W/m^2/m
     * @param strehlMax maximum strehl ratio
     */
    private Band(final String name, final double lambda, final double bandWidth, final double logFluxZero, final double strehlMax) {
        this.name = name;
        this.lambda = lambda;
        this.bandWidth = bandWidth;
        this.logFluxZero = logFluxZero;
        this.strehlMax = strehlMax;
    }

    /**
     * Return the band name
     *
     * @return band name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the central wave length in microns
     *
     * @return central wave length in microns
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Return the band width in microns
     *
     * @return band width in microns
     */
    public double getBandWidth() {
        return bandWidth;
    }

    /**
     * Return the log10 zero magnitude flux at band in W/m^2/m
     *
     * @return log10 zero magnitude flux at band in W/m^2/m
     */
    public double getLogFluxZero() {
        return logFluxZero;
    }

    /**
     * Return the maximum strehl ratio
     *
     * @return maximum strehl ratio
     */
    public double getStrehlMax() {
        return strehlMax;
    }
}
