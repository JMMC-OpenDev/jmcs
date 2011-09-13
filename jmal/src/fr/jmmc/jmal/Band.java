/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import java.util.logging.Level;

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
    I("I", 0.869625d, 0.31176, -1.94d, 0.75d),
    /** J (Near Infrared) */
    J("J", 1.2365d, 0.426d, -2.5d, 0.77d),
    /** H (Near Infrared) */
    H("H", 1.679625d, 0.46425d, -2.94d, 0.84d),
    /** K (Near Infrared) */
    K("K", 2.365625d, 0.912d, -3.4d, 0.93d),
    /** L (Near Infrared) */
    L("L", 3.45875d, 1.2785, -4.15d, 0.972d),
    /** M (Mid Infrared) */
    M("M", 6.4035d, 4.615d, -4.69d, 0.985d),
    /** N (Mid Infrared) */
    N("N", 11.63d, 5.842d, -5.91d, 0.996d),
    /** Q (Mid Infrared) */
    Q("Q", 16.575d, 4.05, -7.17d, 0.999d);
    /** Class logger */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Band.class.getName());

    /**
     * Find the band corresponding to the given wavelength
     * @param waveLength wave length in microns
     * @return corresponding band
     * @throws IllegalArgumentException if no band found
     */
    public static Band findBand(final double waveLength) throws IllegalArgumentException {
        for (Band b : values()) {
            if (Math.abs(waveLength - b.getLambda()) <= b.getBandWidth() / 2d) {
                return b;
            }
        }
        throw new IllegalArgumentException("no band found for the wave length = " + waveLength);
    }

    /**
     * Compute the strehl ratio. see le louarn et al (1998, mnras 295, 756), and amb-igr-011 p.5
     * @param magnitude object magnitude
     * @param waveLength wave length in microns
     * @param diameter telescope diameter in meters
     * @param seeing seeing in arc sec
     * @param nbOfActuators
     * @return strehl ratio
     */
    public static double strehl(final double magnitude, final double waveLength,
            final double diameter, final double seeing, final int nbOfActuators) {

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("magnitude     = " + magnitude);
            logger.fine("waveLength    = " + waveLength);
            logger.fine("diameter      = " + diameter);
            logger.fine("seeing        = " + seeing);
            logger.fine("nbOfActuators = " + nbOfActuators);
        }

        final double lambdaV = 0.55d;

        final Band band = findBand(waveLength);

        final double r0 = 0.251d * lambdaV / seeing * Math.pow(waveLength / lambdaV, 6d / 5d);

        final double doverr0 = diameter / r0;

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("r0            = " + r0);
            logger.fine("doverr0       = " + doverr0);
        }

        final double sigmaphi2_alias = 0.87d * Math.pow((double) nbOfActuators, -5d / 6d) * Math.pow(doverr0, 5d / 3d);

        // ??? (doverr0 * doverr0)**2 = doverr0**4 ou bien erreur ??
        final double sigmaphi2_phot = 1.59e-8d * Math.pow(doverr0, 4d) * Math.pow(waveLength / lambdaV, -2d)
                * (double) nbOfActuators * Math.pow(10d, 0.4d * magnitude);
        final double sigmaphi2_fixe = -Math.log(band.getStrehlMax());
        final double sigmaphi2 = sigmaphi2_alias + sigmaphi2_phot + sigmaphi2_fixe;
        final double strehl = Math.exp(-sigmaphi2) + (1 - Math.exp(-sigmaphi2)) / (1 + doverr0 * doverr0);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("strehl        = " + strehl);
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
     * @return band name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the central wave length in microns
     * @return central wave length in microns
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Return the band width in microns
     * @return band width in microns
     */
    public double getBandWidth() {
        return bandWidth;
    }

    /**
     * Return the log10 zero magnitude flux at band in W/m^2/m
     * @return log10 zero magnitude flux at band in W/m^2/m
     */
    public double getLogFluxZero() {
        return logFluxZero;
    }

    /**
     * Return the maximum strehl ratio
     * @return maximum strehl ratio
     */
    public double getStrehlMax() {
        return strehlMax;
    }
}
