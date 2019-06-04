/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal;

import fr.jmmc.jmcs.util.NumberUtils;
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
    /** L (Near Infrared) (MATISSE) [2.8 - 4.2] */
    L("L", 3.5d, 1.4d, -4.154d, 0.972d),
    // was L("L", 3.45875d, 1.2785d, -4.15d, 0.972d),
    /** M (Mid Infrared) (MATISSE) [4.2 - 8] */
    M("M", 6.1d, 3.8d, -4.568d, 0.985d),
    // was M("M", 6.4035d, 4.615d, -4.69d, 0.985d),
    /** N (Mid Infrared) (MATISSE) [8 - 13] */
    N("N", 10.5d, 5.0d, -6.0d, 0.996d),
    // was N("N", 11.63d, 5.842d, -5.91d, 0.996d),
    /** Q (Mid Infrared) */
    Q("Q", 16.575d, 4.05d, -7.17d, 0.999d);
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(Band.class.getName());
    /** Planck's constant in standard units (6.6262e-34) */
    public final static double H_PLANCK = 6.62606896e-34d;
    /** Speed of light (2.99792458e8) */
    public final static double C_LIGHT = 2.99792458e8d;

    /**
     * Find the band corresponding to the given wavelength
     *
     * @param waveLength wave length in microns
     * @return corresponding band
     * @throws IllegalStateException if no band found
     */
    public static Band findBand(final double waveLength) throws IllegalStateException {
        final Band[] bands = values();
        final int len = bands.length;

        for (int i = len - 1; i >= 0; i--) {
            final Band b = bands[i];
            if (Math.abs(waveLength - b.getLambda()) <= (0.5d * b.getBandWidth())) {
                return b;
            }
        }
        // Return the nearest band:
        if (waveLength <= bands[0].getLambda()) {
            return bands[0];
        }
        if (waveLength >= bands[len - 1].getLambda()) {
            return bands[len - 1];
        }
        // Handle discontinuity between bands:
        Band p = bands[0];
        for (int i = 1; i < len; i++) {
            final Band b = bands[i];
            if ((p.getLambda() <= waveLength) && (waveLength <= b.getLambda())) {
                // wavelength is close to p or b bands:
                if (Math.abs(waveLength - p.getLambda()) <= Math.abs(b.getLambda() - waveLength)) {
                    return p;
                }
                return b;
            }
            p = b;
        }
        return p;
    }

    /** r0 factor = 1.22*1E-6/a and a = PI / ( 180 * 3600 ) */
    private static final double R0_FACTOR = 1.22e-6 * (180 * 3600) / Math.PI;

    /**
     * Compute the strehl ratio. see le louarn et al (1998, mnras 295, 756), and amb-igr-011 p.5
     *
     * @param aoBand band of the AO system (V by default)
     * @param magnitude object magnitude in AO's band
     * @param waveLengths wave lengths in meters
     * @param diameter telescope diameter in meters
     * @param seeing seeing in arc sec
     * @param nbSubPupils number of sub-pupils (interesting pixels on the camera)
     * @param td detector time (ms)
     * @param t0 coherence time (ms)
     * @param quantumEfficiency Detector quantum efficiency
     * @param ron Detector readout noise
     * @param elevation target elevation in degrees [0; 90]
     * @return strehl ratio
     */
    public static double[] strehl(final Band aoBand, final double magnitude, final double[] waveLengths,
                                  final double diameter, final double seeing, final int nbSubPupils,
                                  final double td, final double t0,
                                  final double quantumEfficiency, final double ron,
                                  final double elevation) {

        final double lambdaV = 0.5; // seeing is given at 500 nm

        final double lambdaAO = (aoBand != Band.V) ? aoBand.getLambda() : lambdaV;

        // r0(e)=cos(90-e)^(3/5) * r0
        final double r0_corr = R0_FACTOR * FastMath.pow(FastMath.cos(FastMath.toRadians(90.0 - elevation)), 3.0 / 5.0);

        final double td_over_t0 = td / t0;

        // size of a square sub pupil:
        final double ds2 = Math.PI * FastMath.pow(0.5 * diameter, 2.0) / nbSubPupils;
        final double ds = Math.sqrt(ds2);

        if (logger.isDebugEnabled()) {
            logger.debug("elevation     = {}", elevation);
            logger.debug("lambdaAO      = {}", lambdaAO);
            logger.debug("magnitude     = {}", magnitude);
            logger.debug("diameter      = {}", diameter);
            logger.debug("seeing        = {}", seeing);
            logger.debug("nbSubPupils   = {}", nbSubPupils);
            logger.debug("(td/t0)       = {}", td_over_t0);
            logger.debug("ds              = {}", ds);
        }

        // NbPhot(AO) per DIT per sub aperture:
        final double n0_per_subPupil = aoBand.getNbPhotZero(lambdaV * 1E-6) * aoBand.getBandWidth() * (1E-6 * 1E-3) * td;

        // flux_per_subap=0.25*f*10.^(-0.4*mag)*ds^2
        // LBO: remove 0.25
        final double nphot_per_subPupil = quantumEfficiency * n0_per_subPupil * FastMath.pow(10.0, -0.4 * magnitude) * ds2;

        final int nWLen = waveLengths.length;
        final double[] strehlPerChannel = new double[nWLen];

        double lambdaObs, lambdaRatio;
        double r0, d_over_r0, ds_over_r0;

        double sigmaphi2_alias_fit, sigmaphi2_phot, sigmaphi2_sensor, sigmaphi2_fixed, sigmaphi2, e_sigmaphi2;

        final double sigmaphi2_bw = 0.962 * FastMath.pow(td_over_t0, 5.0 / 3.0);

        for (int i = 0; i < nWLen; i++) {
            lambdaObs = waveLengths[i] * 1e6; // microns

            // explication formule r0:
            // seeing=angular FWHM of seeing in V= 1.22 lambdaV/(r0) r0=fried coherence length.
            // to have seeing in arcsec and all wavelengths in microns, we have
            // seeing * a = 1.22 * lambdaV * 1e-6 / r0 with a=1 arcsec in RD=PI/180*3600
            // thus r0 = 1.22*1E-6 / a * seeing = 0.251 * lambdaV / seeing
            // R0_FACTOR = 0.251...
            // use lambdaV as seeing is given for V:
            lambdaRatio = (lambdaObs / lambdaV);

            // r0 at lambda AO:
            r0 = r0_corr * (lambdaV / seeing) * FastMath.pow(lambdaRatio, 6.0 / 5.0);
            d_over_r0 = diameter / r0; // Math.max(1.0, diameter / r0);
            ds_over_r0 = ds / r0;

            // constant was 0.87 = AMD-REP 001 p32 (related to AO system)
            // MATISSE uses 0.54, adopted in 2018.11
            sigmaphi2_alias_fit = 0.54 * FastMath.pow(ds_over_r0, 5.0 / 3.0);

            // use lambdaAO as magAO corresponds to this AO band:
            lambdaRatio = (lambdaObs / lambdaAO);

            // photon error:
            // (4.*(!DPI^2)/3.)*(lambda_ao/lambda_sc)^2/Nphot_ao_ds
            sigmaphi2_phot = (4.0 / 3.0 * Math.PI * Math.PI) * FastMath.pow(lambdaRatio, -2.0) / nphot_per_subPupil;

            // sensor error:
            // (4.*(!DPI^2)/3.)*(lambda_ao/lambda_sc)^2/Nphot_ao_ds
            sigmaphi2_sensor = (8.0 / 9.0 * Math.PI * Math.PI) * FastMath.pow(lambdaRatio, -2.0) * FastMath.pow(ron / nphot_per_subPupil, 2.0);
            sigmaphi2_sensor *= FastMath.pow(1.0 + FastMath.pow(lambdaRatio, 12.0 / 5.0) * FastMath.pow(ds_over_r0, 2.0), 2.0);
            sigmaphi2_sensor *= FastMath.pow(lambdaRatio, -12.0 / 5.0);
            sigmaphi2_sensor *= FastMath.pow(ds_over_r0, -2.0);

            sigmaphi2_fixed = -Math.log(findBand(lambdaObs).getStrehlMax());

            sigmaphi2 = sigmaphi2_alias_fit + sigmaphi2_bw + sigmaphi2_phot + sigmaphi2_sensor + sigmaphi2_fixed;

            e_sigmaphi2 = FastMath.exp(-sigmaphi2);

            strehlPerChannel[i] = e_sigmaphi2 + (1.0 - e_sigmaphi2) / (1.0 + d_over_r0 * d_over_r0);

            if (logger.isDebugEnabled()) {
                logger.debug("lambda          = {}", lambdaObs);
                logger.debug("r0              = {}", r0);
                logger.debug("nphot_per_subPupil = {}", nphot_per_subPupil);
                logger.debug("sigmaphi2_alias = {}", sigmaphi2_alias_fit);
                logger.debug("sigmaphi2_phot  = {}", sigmaphi2_phot);
                logger.debug("sigmaphi2_det   = {}", sigmaphi2_bw);
                logger.debug("sigmaphi2_fixed = {}", sigmaphi2_fixed);
                logger.debug("sigmaphi2       = {}", sigmaphi2);
                logger.debug("strehl          = {}", strehlPerChannel[i]);
            }
        }

        return strehlPerChannel;
    }

    /**
     * Compute the strehl ratio. see le louarn et al (1998, mnras 295, 756), and amb-igr-011 p.5
     *
     * @param magnitude object magnitude in AO's band
     * @param waveLengths wave lengths in meters
     * @param diameter telescope diameter in meters
     * @param seeing seeing in arc sec
     * @param nbOfActuators number of actuators
     * @param elevation target elevation in degrees [0; 90]
     * @return strehl ratio
     */
    public static double[] strehlOLD(final double magnitude, final double[] waveLengths,
                                     final double diameter, final double seeing, final int nbOfActuators,
                                     final double elevation) {

        // r0(e)=cos(90-e)^(3/5) * r0
        // r0_corr in [0; 0.251]
        final double r0_corr = R0_FACTOR * FastMath.pow(FastMath.cos(FastMath.toRadians(90.0 - elevation)), 3.0 / 5.0);

        if (logger.isDebugEnabled()) {
            logger.debug("elevation     = {}", elevation);
            logger.debug("magnitude     = {}", magnitude);
            logger.debug("waveLength    = {}", waveLengths);
            logger.debug("diameter      = {}", diameter);
            logger.debug("seeing        = {}", seeing);
            logger.debug("nbOfActuators = {}", nbOfActuators);
            logger.debug("r0_corr       = {}", r0_corr);
        }

        final double n_act = (double) nbOfActuators;

        final double lambdaV = 0.55;

        final int nWLen = waveLengths.length;
        final double[] strehlPerChannel = new double[nWLen];

        double waveLength, lambdaRatio;
        double r0, d_over_r0;

        Band band;
        double sigmaphi2_alias, sigmaphi2_phot, sigmaphi2_fixe, sigmaphi2, e_sigmaphi2;

        for (int i = 0; i < nWLen; i++) {
            waveLength = waveLengths[i] * 1e6d; // microns
            lambdaRatio = waveLength / lambdaV;

            // explication formule r0:
            // seeing=angular FWHM of seeing in V= 1.22 lambdaV/(r0) r0=fried coherence length.
            // to have seeing in arcsec and all wavelengths in microns, we have
            // seeing * a = 1.22 * lambdaV * 1e-6 / r0 with a=1 arcsec in RD=PI/180*3600
            // thus r0 = 1.22*1E-6 / a * seeing = 0.251 * lambdaV / seeing
            // R0_FACTOR = 0.251...
            r0 = r0_corr * (lambdaV / seeing) * FastMath.pow(lambdaRatio, 6d / 5d);
            d_over_r0 = diameter / r0;

            // 0.87 = AMD-REP 001 p32 (related to AO system)
            sigmaphi2_alias = 0.87d * FastMath.pow(n_act, -5d / 6d) * FastMath.pow(d_over_r0, 5d / 3d);

            sigmaphi2_phot = 1.59e-8d * (d_over_r0 * d_over_r0) * FastMath.pow(lambdaRatio, -2d)
                    * n_act * FastMath.pow(10d, 0.4d * magnitude);

            band = findBand(waveLength);

            sigmaphi2_fixe = -Math.log(band.getStrehlMax());

            sigmaphi2 = sigmaphi2_alias + sigmaphi2_phot + sigmaphi2_fixe;

            e_sigmaphi2 = FastMath.exp(-sigmaphi2);

            strehlPerChannel[i] = e_sigmaphi2 + (1.0 - e_sigmaphi2) / (1.0 + d_over_r0 * d_over_r0);

            if (logger.isDebugEnabled()) {
                logger.debug("lambda          = {}", waveLength);
                logger.debug("r0              = {}", r0);
                logger.debug("doverr0         = {}", d_over_r0);
                logger.debug("sigmaphi2_alias = {}", sigmaphi2_alias);
                logger.debug("sigmaphi2_phot  = {}", sigmaphi2_phot);
                logger.debug("sigmaphi2_fixe  = {}", sigmaphi2_fixe);
                logger.debug("sigmaphi2       = {}", sigmaphi2);
                logger.debug("strehl          = {}", strehlPerChannel[i]);
            }
        }

        return strehlPerChannel;
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

    /**
     * Return the number of photons in m^-2.s^-1.m^-1 for an object at magnitude 0
     * @param wavelength wavelength in meters
     * @return nb of photons in m^-2.s^-1.m^-1 for an object at magnitude 0
     */
    public double getNbPhotZero(final double wavelength) {
        // nb of photons m^-2.s^-1.m^-1 for an object at magnitude 0:
        // note: fzero depends on the spectral band:
        return FastMath.pow(10d, getLogFluxZero()) * wavelength / (H_PLANCK * C_LIGHT);
    }

    public static void main(String[] args) {

        /*
            Fix MATISSE bands:
            L: 2.8 - 4.2    f0= 7e-11 W.m − 2.um-1 (3.5um)
            M: 4.2 - 8.0    f0= 2.7e-11 W.m − 2.um-1 (4.5um)
            N: 8.0 - 13.0   f0= 1e-12 W.m − 2.um-1 (10.5um)
         */
        System.out.println("log(f0) L: " + NumberUtils.trimTo3Digits(Math.log10(1e6 * 7e-11)));
        System.out.println("log(f0) M: " + NumberUtils.trimTo3Digits(Math.log10(1e6 * 2.7e-11)));
        System.out.println("log(f0) N: " + NumberUtils.trimTo3Digits(Math.log10(1e6 * 1e-12)));

        for (Band b : values()) {
            double half = 0.5d * b.getBandWidth();
            double mid = b.getLambda();
            double min = mid - half;
            double max = mid + half;

            final double nzero = b.getNbPhotZero(b.getLambda() * 1e-6);

            System.out.println("Band: " + b.getName()
                    + " min: " + NumberUtils.trimTo3Digits(min)
                    + " mid: " + NumberUtils.trimTo3Digits(mid)
                    + " max: " + NumberUtils.trimTo3Digits(max)
                    + " n0 : " + nzero
            );
        }
        /*
            Band: U min: 0.301 mid: 0.334 max: 0.367 n0 : 6.6937549979784128E16
            Band: B min: 0.421 mid: 0.461 max: 0.502 n0 : 1.46705974779427744E17
            Band: V min: 0.5 mid: 0.556 max: 0.611 n0 : 1.01624433397212688E17
            Band: R min: 0.609 mid: 0.662 max: 0.715 n0 : 7.466365193537544E16
            Band: I min: 0.713 mid: 0.869 max: 1.025 n0 : 5.0263805017315112E16
            Band: J min: 1.023 mid: 1.236 max: 1.449 n0 : 1.9684186281571248E16
            Band: H min: 1.447 mid: 1.679 max: 1.911 n0 : 9.708132068674186E15
            Band: K min: 1.909 mid: 2.365 max: 2.821 n0 : 4.74099226559661E15
            Band: L min: 2.8 mid: 3.5 max: 4.2 n0 : 1.2359229306721938E15
            Band: M min: 4.199 mid: 6.1 max: 8.0 n0 : 8.303346866438889E14
            Band: N min: 8.0 mid: 10.5 max: 13.0 n0 : 5.285823345220047E13
            Band: Q min: 14.549 mid: 16.575 max: 18.599 n0 : 5.64126995424172E12
         */

        double w = 0.1;

        while (w < 20.0) {
            System.out.println("findBand(" + NumberUtils.trimTo3Digits(w) + "): " + findBand(w));

            w += (w < 1.0) ? 0.02 : 0.05;
        }
    }
}
