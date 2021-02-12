/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2020, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to provide utility statistical functions and samples for circular distributions
 * @author bourgesl
 */
public final class StatUtils {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(StatUtils.class.getName());

    /** supersampling factor: x1 (normal), x16 (high quality) */
    private final static int SUPER_SAMPLING = 1;
    /** number of samples */
    public final static int N_SAMPLES = Integer.getInteger("StatsUtils.samples", 1024 * SUPER_SAMPLING);

    /** precision expected on stddev */
    private final static double EPSILON_VARIANCE = Math.pow(5e-3, 2.0);
    /** precision expected on mean */
    private final static double EPSILON_MEAN = Math.pow(2e-2, 2.0);

    /** complex error scaling on re/im (1/2 1/2) on variances */
    private final static double COMPLEX_ERROR_SCALE = 1.0 / Math.sqrt(2.0);

    /** normalization factor = 1/N_SAMPLES */
    public final static double SAMPLING_FACTOR_MEAN = 1d / N_SAMPLES;
    /** normalization factor for variance = 1 / (N_SAMPLES - 1) (bessel correction) */
    public final static double SAMPLING_FACTOR_VARIANCE = 1d / (N_SAMPLES - 1);

    /** initial cache size = number of baselines (15 for 6 telescopes) */
    private final static int INITIAL_CAPACITY = 15;
    /** singleton */
    private static StatUtils INSTANCE = null;

    /**
     * Return singleton (lazy)
     * @return singleton
     */
    public synchronized static StatUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatUtils();
        }
        return INSTANCE;
    }

    /* members */
    /** current index in the distribution cache */
    private int current = 0;
    /** cached distributions */
    private final ArrayList<ComplexDistribution> cache;

    private StatUtils() {
        this.cache = new ArrayList<ComplexDistribution>(INITIAL_CAPACITY);
        prepare(INITIAL_CAPACITY);
    }

    public synchronized void prepare(final int count) {
        final int needed = count - cache.size();
        if (needed > 0) {
            logger.info("prepare: {} needed distributions", needed);
            final long start = System.nanoTime();

            for (int i = 0; i < needed; i++) {
                cache.add(ComplexDistribution.create());
            }

            logger.info("prepare done: {} ms.", 1e-6d * (System.nanoTime() - start));
        }
    }

    public synchronized ComplexDistribution get() {
        final int idx = current;
        final ComplexDistribution distrib = cache.get(idx);

        this.current = (idx + 1) % cache.size();

        return distrib;
    }

    public static final class ComplexDistribution {

        private final static double ANG_STEP;
        private final static int NUM_ANGLES;

        private final static double[][] ANGLES_COS_SIN;

        static {
            logger.debug("N_SAMPLES: {}", N_SAMPLES);
            logger.debug("EPSILON_VARIANCE: {}", EPSILON_VARIANCE);

            ANG_STEP = 30.0; // 30 deg

            NUM_ANGLES = (int) Math.round(360.0 / ANG_STEP);

            logger.debug("ANG_STEP: {}", ANG_STEP);
            logger.debug("NUM_ANGLES: {}", NUM_ANGLES);

            ANGLES_COS_SIN = new double[NUM_ANGLES][2];

            for (int i = 0; i < NUM_ANGLES; i++) {
                final double angRad = Math.toRadians(ANG_STEP * i);

                ANGLES_COS_SIN[i][0] = Math.cos(angRad);
                ANGLES_COS_SIN[i][1] = Math.sin(angRad);
            }
        }

        /** samples (re,im) */
        private final double[][] samples = new double[2][N_SAMPLES];

        public static ComplexDistribution create() {
            /* create a new random generator to have different seed (single thread) */
            final Random random = new Random();

            final ComplexDistribution distrib = new ComplexDistribution();

            final long start = System.nanoTime();

            int n = 0;
            do {
                distrib.generate(random);
                n++;
            } while (!distrib.test());

            logger.info("done: {} ms ({} iterations).", 1e-6d * (System.nanoTime() - start), n);

            return distrib;
        }

        private ComplexDistribution() {
            super();
        }

        private void generate(final Random random) {
            final double[] distRe = samples[0];
            final double[] distIm = samples[1];

            // bivariate distribution (complex normal):
            for (int n = 0; n < N_SAMPLES; n++) {
                // generate nth sample:
                distRe[n] = random.nextGaussian();
                distIm[n] = random.nextGaussian();
            }
        }

        private boolean test() {
            final double snr = 100.0;

            final double ref_amp = 0.5; // middle of [0-1]
            final double err_amp = ref_amp / snr;

            final double err_dist = err_amp * COMPLEX_ERROR_SCALE;

            final double[] distRe = samples[0];
            final double[] distIm = samples[1];

            double mean_sq_acc = 0.0, var_acc = 0.0;

            for (int i = 0; i < NUM_ANGLES; i++) {
                final double ref_re = ref_amp * ANGLES_COS_SIN[i][0];
                final double ref_im = ref_amp * ANGLES_COS_SIN[i][1];

                double re, im, diff;
                double y, t;

                double re_sum, re_sum_err, im_sum, im_sum_err;
                re_sum = re_sum_err = im_sum = im_sum_err = 0.0;

                // bivariate distribution (complex normal):
                // pass 1: numeric mean:
                for (int n = 0; n < N_SAMPLES; n++) {
                    // update nth sample:
                    re = ref_re + (err_dist * distRe[n]);
                    im = ref_im + (err_dist * distIm[n]);

                    // kahan sum
                    // re_sum += cRe;
                    y = re - re_sum_err;
                    t = re_sum + y;
                    re_sum_err = (t - re_sum) - y;
                    re_sum = t;

                    // im_sum += cIm;
                    y = im - im_sum_err;
                    t = im_sum + y;
                    im_sum_err = (t - im_sum) - y;
                    im_sum = t;
                }

                // true average on averaged complex distribution:
                final double avg_re = SAMPLING_FACTOR_MEAN * re_sum;
                final double avg_im = SAMPLING_FACTOR_MEAN * im_sum;

                // mean(norm):
                final double mean_sq = avg_re * avg_re + avg_im * avg_im; // Use V^2 like variance epsilon

                // pass 2: stddev:
                double re_diff_acc = 0.0;
                double re_sq_diff_acc = 0.0;
                double im_diff_acc = 0.0;
                double im_sq_diff_acc = 0.0;

                for (int n = 0; n < N_SAMPLES; n++) {
                    // update nth sample:
                    re = ref_re + (err_dist * distRe[n]);
                    im = ref_im + (err_dist * distIm[n]);

                    // New: compute stddev on re/im
                    // Compensated-summation variant for better numeric precision:
                    diff = re - avg_re;
                    re_diff_acc += diff;
                    re_sq_diff_acc += diff * diff;

                    diff = im - avg_im;
                    im_diff_acc += diff;
                    im_sq_diff_acc += diff * diff;
                }

                // variance on complex values:
                // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
                final double re_stddev = SAMPLING_FACTOR_VARIANCE * (re_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (re_diff_acc * re_diff_acc)));
                final double im_stddev = SAMPLING_FACTOR_VARIANCE * (im_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (im_diff_acc * im_diff_acc)));

                final double variance = re_stddev + im_stddev; // sum of variances (re,im)

                // average mean/variance estimations:
                mean_sq_acc += mean_sq;
                var_acc += variance;
            }

            final double mean_sq = mean_sq_acc / NUM_ANGLES;
            final double variance = var_acc / NUM_ANGLES;

            // relative difference: delta = (x - est) / x
            final double ratio_mean = mean_sq / (ref_amp * ref_amp);
            final double ratio_variance = variance / (err_amp * err_amp);

            final boolean good = (Math.abs(ratio_mean - 1.0) < EPSILON_VARIANCE) && (Math.abs(ratio_variance - 1.0) < EPSILON_VARIANCE);

            if (good && logger.isDebugEnabled()) {
                logger.debug("Sampling[" + N_SAMPLES + "] snr=" + snr + " (err(re,im)= " + err_dist + ")"
                        + " avg= " + Math.sqrt(mean_sq) + " norm= " + ref_amp + " ratio: " + Math.sqrt(ratio_mean)
                        + " stddev= " + Math.sqrt(variance) + " err(norm)= " + err_amp + " ratio: " + Math.sqrt(ratio_variance)
                        + " good = " + good);
            }
            return good;
        }

        // OLD calibration (deprecated)
        private boolean testV2() {
            final double snr = 100.0;

            final double ref_amp = 0.5; // middle of [0-1]
            final double err_amp = ref_amp / snr;
            // d(v2) = 2v * dv 
            final double errNorm = 2.0 * ref_amp * err_amp;

            final double err_dist = err_amp * COMPLEX_ERROR_SCALE;

            final double norm = ref_amp * ref_amp;

            final double[] distRe = samples[0];
            final double[] distIm = samples[1];

            double mean_acc = 0.0, var_acc = 0.0;

            for (int i = 0; i < NUM_ANGLES; i++) {
                final double ref_re = ref_amp * ANGLES_COS_SIN[i][0];
                final double ref_im = ref_amp * ANGLES_COS_SIN[i][1];

                double re, im, sample, diff;
                double sum = 0.0;
                double sum_err = 0.0, y, t;

                // bivariate distribution (complex normal):
                // pass 1: numeric mean:
                for (int n = 0; n < N_SAMPLES; n++) {
                    // update nth sample:
                    re = ref_re + (err_dist * distRe[n]);
                    im = ref_im + (err_dist * distIm[n]);

                    // compute norm=re^2+im^2:
                    sample = re * re + im * im;

                    // kahan sum
                    y = sample - sum_err;
                    t = sum + y;
                    sum_err = (t - sum) - y;
                    sum = t;
                }

                // mean(norm):
                final double mean = SAMPLING_FACTOR_MEAN * sum;

                // pass 2: stddev:
                double diff_acc = 0.0;
                double sq_diff_acc = 0.0;

                for (int n = 0; n < N_SAMPLES; n++) {
                    // update nth sample:
                    re = ref_re + (err_dist * distRe[n]);
                    im = ref_im + (err_dist * distIm[n]);

                    // compute norm=re^2+im^2:
                    sample = re * re + im * im;

                    // Compensated-summation variant for better numeric precision:
                    diff = sample - mean;
                    diff_acc += diff;
                    sq_diff_acc += diff * diff;
                }

                // variance(norm) to avoid SQRT() in loops:
                // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
                final double variance = (SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))))
                        / (COMPLEX_ERROR_SCALE * COMPLEX_ERROR_SCALE); // single dimension only !

                // average mean/variance estimations:
                mean_acc += mean;
                var_acc += variance;
            }

            final double mean = mean_acc / NUM_ANGLES;
            final double variance = var_acc / NUM_ANGLES;

            final double ratio_mean;
            final double ratio_variance;

            final boolean good;

            if (true) {
                // absolute difference: delta = (x - est)
                ratio_mean = Math.abs(norm - mean);
                ratio_variance = Math.abs(variance - (errNorm * errNorm));

                good = (Math.abs(ratio_mean - 1.0) < EPSILON_MEAN) && (Math.abs(ratio_variance - 1.0) < EPSILON_VARIANCE);
            } else {
                // relative difference: delta = (x - est) / x
                ratio_mean = Math.abs(mean / norm);
                ratio_variance = Math.abs(variance) / (errNorm * errNorm);

                good = (Math.abs(ratio_mean - 1.0) < EPSILON_MEAN) && (Math.abs(ratio_variance - 1.0) < EPSILON_VARIANCE);
            }
            if (good && logger.isDebugEnabled()) {
                logger.debug("Sampling[" + N_SAMPLES + "] snr=" + snr + " (err(re,im)= " + err_dist + ")"
                        + " avg= " + mean + " norm= " + norm + " ratio: " + Math.sqrt(ratio_mean)
                        + " stddev= " + Math.sqrt(variance) + " err(norm)= " + errNorm + " ratio: " + Math.sqrt(ratio_variance)
                        + " good = " + good);
            }
            return good;
        }

        public double[][] getSamples() {
            return samples;
        }

        public double[][] getMoments() {
            final double[][] moments = new double[2][4];
            moments(samples[0], moments[0]);
            moments(samples[1], moments[1]);
            return moments;
        }
    }

    public static double min(final double[] array) {
        double min = Double.POSITIVE_INFINITY;

        for (int n = 0; n < array.length; n++) {
            if (array[n] < min) {
                min = array[n];
            }
        }
        return min;
    }

    public static double max(final double[] array) {
        double max = Double.NEGATIVE_INFINITY;

        for (int n = 0; n < array.length; n++) {
            if (array[n] > max) {
                max = array[n];
            }
        }
        return max;
    }

    public static double mean(final double[] array) {
        double sample, sum = 0.0;
        int ns = 0;
        for (int n = 0; n < array.length; n++) {
            sample = array[n];
            // No Compensated-summation (double):
            if (!Double.isNaN(sample)) {
                sum += sample;
                ns++;
            }
        }
        return (ns != 0) ? (sum / ns) : 0.0;
    }

    public static double[] moments(final double[] array) {
        final double[] moments = new double[4];
        moments(array, moments);
        return moments;
    }

    public static void moments(final double[] array, final double[] moments) {
        final double mean = mean(array);

        double sample, diff;
        double sum_diff = 0.0;
        double sum_diff2 = 0.0;

        for (int n = 0; n < array.length; n++) {
            sample = array[n];
            // Compensated-summation variant for better numeric precision:
            diff = sample - mean;
            sum_diff += diff;
            sum_diff2 += diff * diff;
        }

        // variance(norm):
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        final double variance = (sum_diff2 - (SAMPLING_FACTOR_MEAN * (sum_diff * sum_diff))) / (array.length - 1);

        final double stddev = Math.sqrt(variance);

        // Moments ordre 3 et 4:
        double sum_diff3 = 0.0;
        double sum_diff4 = 0.0;

        for (int n = 0; n < array.length; n++) {
            sample = array[n];
            // Compensated-summation variant for better numeric precision:
            diff = (sample - mean) / stddev;
            sum_diff3 += diff * diff * diff;
            sum_diff4 += diff * diff * diff * diff;
        }

        final double asymetry = sum_diff3 / array.length;
        final double kurtosis = (sum_diff4 / array.length) - 3.0; // normalised

        // output:
        moments[0] = mean;
        moments[1] = variance;
        moments[2] = asymetry;
        moments[3] = kurtosis;
    }

    // --- TEST ---
    public static void main(String[] args) throws IOException {
        final boolean TEST_SUM = false;
        final boolean TEST_DIST = true;
        final boolean DO_DUMP = false;

        // Test kahan sum:
        if (TEST_SUM) {
            final double[] values = new double[10 * 1024 * 1024];
            testSum(values, 1.0e-8);
            testSum(values, 1.0);
            testSum(values, 1.0e8);
        }

        // Precompute distributions:
        StatUtils.getInstance();

        if (DO_DUMP) {
            // dump all distributions:
            for (int n = 0; n < INITIAL_CAPACITY; n++) {
                ComplexDistribution d = StatUtils.getInstance().get();
                System.out.println("get(): " + d);

                // Get the complex distribution for this row:
                final double[] distRe = d.getSamples()[0];
                final double[] distIm = d.getSamples()[1];

                System.out.println("moments(re): " + Arrays.toString(d.getMoments()[0]));
                System.out.println("moments(im): " + Arrays.toString(d.getMoments()[1]));

                final File file = new File("dist_" + N_SAMPLES + "_" + n + ".txt");
                saveComplexDistribution(file, distRe, distIm);
            }
        }

        final double[][] means = new double[2][INITIAL_CAPACITY];
        final double[][] vars = new double[2][INITIAL_CAPACITY];

        for (int n = 0; n < INITIAL_CAPACITY; n++) {
            ComplexDistribution d = StatUtils.getInstance().get();
            System.out.println("get(): " + d);

            System.out.println("moments(re): " + Arrays.toString(d.getMoments()[0]));
            System.out.println("moments(im): " + Arrays.toString(d.getMoments()[1]));

            means[0][n] = d.getMoments()[0][0];
            means[1][n] = d.getMoments()[1][0];

            vars[0][n] = d.getMoments()[0][1];
            vars[1][n] = d.getMoments()[1][1];
        }

        System.out.println("moments(mean) (re): " + Arrays.toString(moments(means[0])));
        System.out.println("moments(mean) (im): " + Arrays.toString(moments(means[1])));

        System.out.println("moments(variance) (re): " + Arrays.toString(moments(vars[0])));
        System.out.println("moments(variance) (im): " + Arrays.toString(moments(vars[1])));

        final double[][] ratios = new double[2][INITIAL_CAPACITY]; // mean, stddev

        for (double snr = 5.0; snr > 0.01;) {
            // fix snr rounding:
            snr = NumberUtils.trimTo3Digits(snr);

            // TODO: check amplitude effect (ie x/y complex position impact on mean /stddev estimations) */
            /* for (double amp = 1.0; amp > 5e-6; amp /= 10.0) */
            final double amp = 0.1;
            {
                System.out.println("--- SNR: " + snr + " @ AMP = " + amp + "---");

                if (false) {
                    System.out.println("VISAMP");

                    final double angle = 3.0;
                    //for (double angle = 3.0 - 90.0; angle < 95.0; angle += 10.0)
                    {
                        for (int i = 0; i < INITIAL_CAPACITY; i++) {
                            ComplexDistribution d = StatUtils.getInstance().get();
                            if (TEST_DIST) {
                                test(angle, amp, snr, true, d.getSamples(), ratios, i);
                            } else {
                                test(amp, snr, true, ratios, i);
                            }
                        }
                        System.out.println("SNR: " + snr + " angle: " + angle + " VISAMP: Ratio mean moments: " + Arrays.toString(moments(ratios[0])));
                        System.out.println("SNR: " + snr + " angle: " + angle + " VISAMP: Ratio err moments : " + Arrays.toString(moments(ratios[1])));
                        System.out.println("SNR: " + snr + " angle: " + angle + " VISAMP: score:\t" + mean(ratios[0]) + "\t" + mean(ratios[1]));
                    }
                }
                if (true) {
                    System.out.println("VIS2:");

                    final double angle = 3.0;
                    //for (double angle = 3.0 - 90.0; angle < 95.0; angle += 10.0)
                    {
                        for (int i = 0; i < INITIAL_CAPACITY; i++) {
                            ComplexDistribution d = StatUtils.getInstance().get();
                            if (TEST_DIST) {
                                test(angle, amp, snr, false, d.getSamples(), ratios, i);
                            } else {
                                test(amp, snr, false, ratios, i);
                            }
                        }
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VIS2 SNR: " + (snr / 2) + " Ratio mean moments: " + Arrays.toString(moments(ratios[0])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VIS2 SNR: " + (snr / 2) + " Ratio err moments : " + Arrays.toString(moments(ratios[1])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VIS2 SNR: " + (snr / 2) + " Score:\t" + mean(ratios[0]) + "\t" + mean(ratios[1]));
                    }
                }
            }

            if (snr > 25) {
                snr -= 10.0;
            } else if (snr > 2.5) {
                snr -= 1.0;
            } else if (snr > 0.25) {
                snr -= 0.2;
            } else {
                snr -= 0.02;
            }
        }
    }

    private static void test(final double angle, final double visRef, final double snr, final boolean amp, double[][] samples, final double[][] ratios, final int pos) {
        if (amp) {
            testV(angle, visRef, snr, true, samples, ratios, pos);
        } else {
            testV2(angle, visRef, snr, false, samples, ratios, pos);
        }
    }

    /*
SNR: 5.0(C) angle: 3.0 VIS2 SNR: 2.5 Score:	1.0004869845893785	1.0100900449645016
SNR: 4.0(C) angle: 3.0 VIS2 SNR: 2.0 Score:	1.000561663990723	1.015680255003912
SNR: 3.0(C) angle: 3.0 VIS2 SNR: 1.5 Score:	1.0006437432385085	1.0276385127216798
SNR: 2.0(C) angle: 3.0 VIS2 SNR: 1.0 Score:	1.0006495572482226	1.0610208995073855
SNR: 1.8(C) angle: 3.0 VIS2 SNR: 0.9 Score:	1.000605295837188	1.0747964754406276
SNR: 1.6(C) angle: 3.0 VIS2 SNR: 0.8 Score:	1.0005183539355007	1.093760356453842
SNR: 1.4(C) angle: 3.0 VIS2 SNR: 0.7 Score:	1.0003564861167475	1.1208404066855626
SNR: 1.2(C) angle: 3.0 VIS2 SNR: 0.6 Score:	1.0000575099625348	1.16132152882296
SNR: 1.0(C) angle: 3.0 VIS2 SNR: 0.5 Score:	0.9994947476067958	1.2255044344613526
SNR: 0.8(C) angle: 3.0 VIS2 SNR: 0.4 Score:	0.9984025572684322	1.3356057241179202
SNR: 0.6(C) angle: 3.0 VIS2 SNR: 0.3 Score:	0.9963609416914155	1.5469332567790361
SNR: 0.399(C) angle: 3.0 VIS2 SNR: 0.1995 Score:	0.998773576275753	2.0369074938722767
SNR: 0.199(C) angle: 3.0 VIS2 SNR: 0.0995 Score:	1.463926592309453	3.6953008579923483
SNR: 0.179(C) angle: 3.0 VIS2 SNR: 0.0895 Score:	1.676712348367298	4.079295009684587
SNR: 0.159(C) angle: 3.0 VIS2 SNR: 0.0795 Score:	1.9845681624006706	4.563125282774453
SNR: 0.139(C) angle: 3.0 VIS2 SNR: 0.0695 Score:	2.4508945662741155	5.189957728385803
SNR: 0.119(C) angle: 3.0 VIS2 SNR: 0.0595 Score:	3.1957329192454744	6.031970005390596
SNR: 0.098(C) angle: 3.0 VIS2 SNR: 0.049 Score:	4.574296650264673	7.291910820521746
SNR: 0.078(C) angle: 3.0 VIS2 SNR: 0.039 Score:	7.1292905333114485	9.129798786733915
SNR: 0.057(C) angle: 3.0 VIS2 SNR: 0.0285 Score:	13.264714629684272	12.458133922618687
SNR: 0.037(C) angle: 3.0 VIS2 SNR: 0.0185 Score:	31.424387299684664	19.155983622874608
SNR: 0.016(C) angle: 3.0 VIS2 SNR: 0.008 Score:	168.25320644878747	44.24792987512209
     */
    private static void testV2(final double angle, final double visRef, final double snr, final boolean amp, double[][] samples, final double[][] ratios, final int pos) {
        if (amp) {
            throw new IllegalStateException("only amp=false supported !");
        }

        System.out.println("-----");
        System.out.println("angle: " + angle);

        final double angRad = Math.toRadians(angle);

        final double cos_angle = Math.cos(angRad);
        final double sin_angle = Math.sin(angRad);

        final double visErr = visRef / snr;
        System.out.println("vis ref: " + visRef);
        System.out.println("circular err: " + visErr);

        // v2 = v * v
        final double exp_ref = visRef * visRef;
        // d(v2) = 2v * dv si dv petit, sinon il faut suivre les lois de distributions !
        // ie abaque (erreur expected) => erreur corrigée sur CVis
        final double exp_err = 2.0 * visRef * visErr;

        final double exp_snr = exp_ref / exp_err;

        System.out.println("expected mean: " + exp_ref + ", stddev: " + exp_err + " SNR = " + exp_snr);

        // add angle:
        final double visRe = visRef * cos_angle;
        final double visIm = visRef * sin_angle;

        final double visCErr = visErr * COMPLEX_ERROR_SCALE;
        System.out.println("vis re: " + visRe + " im: " + visIm + " err: " + visCErr);

        double sample, diff;
        int n;
        double re, im;
        double y, t;

        final double[] re_samples = new double[N_SAMPLES];
        final double[] im_samples = new double[N_SAMPLES];

        double re_sum, re_sum_err, im_sum, im_sum_err, cos_phi, sin_phi;
        re_sum = re_sum_err = im_sum = im_sum_err = 0.0;
        double cRe, cIm;

        // bivariate distribution (complex normal):
        // pass 1: numeric mean:
        for (n = 0; n < N_SAMPLES; n++) {
            // update nth sample:
            re = visRe + visCErr * samples[0][n];
            im = visIm + visCErr * samples[1][n];

            // compute C2=C*C
            // (a + bi)(c + di) = (ac - bd) + (ad + bc)i
            cRe = re * re - im * im;
            cIm = 2.0 * re * im;

            // average complex value:
            re_samples[n] = cRe;
            im_samples[n] = cIm;

            // kahan sum
            // re_sum += cRe;
            y = cRe - re_sum_err;
            t = re_sum + y;
            re_sum_err = (t - re_sum) - y;
            re_sum = t;

            // im_sum += cIm;
            y = cIm - im_sum_err;
            t = im_sum + y;
            im_sum_err = (t - im_sum) - y;
            im_sum = t;
        }

        // mean(vphi):
        final double s_camp_mean = Math.sqrt(re_sum * re_sum + im_sum * im_sum);
        // double s_cphi_mean = (im_sum != 0.0) ? Math.atan2(im_sum, re_sum) : 0.0;
        // System.out.println("angle:"+Math.toDegrees(s_cphi_mean));

        // rotate by -phi:
        cos_phi = re_sum / s_camp_mean;
        sin_phi = im_sum / s_camp_mean;

        // true average on averaged complex samples:
        double avg = SAMPLING_FACTOR_MEAN * s_camp_mean;

        // true average on averaged complex distribution:
        final double avg_re = SAMPLING_FACTOR_MEAN * re_sum;
        final double avg_im = SAMPLING_FACTOR_MEAN * im_sum;

        // pass 2: stddev:
        double diff_acc = 0.0;
        double sq_diff_acc = 0.0;

        double re_diff_acc = 0.0;
        double re_sq_diff_acc = 0.0;
        double im_diff_acc = 0.0;
        double im_sq_diff_acc = 0.0;

        for (n = 0; n < N_SAMPLES; n++) {
            // Correct amplitude by estimated phase:
            // Amp = Re { C * phasor(-phi) }
            sample = re_samples[n] * cos_phi + im_samples[n] * sin_phi; // -phi => + imaginary part in complex mult

            // Compensated-summation variant for better numeric precision:
            diff = sample - avg;
            diff_acc += diff;
            sq_diff_acc += diff * diff;

            // New: compute stddev on re/im
            // Compensated-summation variant for better numeric precision:
            diff = re_samples[n] - avg_re;
            re_diff_acc += diff;
            re_sq_diff_acc += diff * diff;

            diff = im_samples[n] - avg_im;
            im_diff_acc += diff;
            im_sq_diff_acc += diff * diff;
        }

        // standard deviation on amplitude:
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        double stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))))
                / COMPLEX_ERROR_SCALE; // single dimension only !

        // standard deviation on complex values:
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        final double re_stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (re_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (re_diff_acc * re_diff_acc))));
        final double im_stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (im_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (im_diff_acc * im_diff_acc))));

        System.out.println("Complex stddev re: " + re_stddev + " im: " + im_stddev);

        final double est_sigma = Math.sqrt(re_stddev * re_stddev + im_stddev * im_stddev); // sum of variances (re,im)
        System.out.println("Est2 est_sigma = " + est_sigma + " stddev = " + stddev + " ratio: " + (est_sigma / stddev));

        // consider est_sigma is better accuracy:
        stddev = est_sigma;

        ratios[0][pos] = (avg / exp_ref);
        ratios[1][pos] = (stddev / exp_err);

        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] avg= " + avg + " vs expected ref= " + exp_ref + " ratio: " + ratios[0][pos]);
        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] stddev= " + stddev + " vs expected Err= " + exp_err + " ratio: " + ratios[1][pos]);
    }

    /*
SNR: 5.0 angle: 3.0 VIS2: score:	1.000702306935893	1.0111043461977893
SNR: 4.0 angle: 3.0 VIS2: score:	0.9999846971667051	1.0169296951012827
SNR: 3.0 angle: 3.0 VIS2: score:	1.0015427320756956	1.0292592177495035
SNR: 2.0 angle: 3.0 VIS2: score:	0.9999370016537165	1.0632993166288296
SNR: 1.8 angle: 3.0 VIS2: score:	1.0047995677013708	1.077267730541154
SNR: 1.6 angle: 3.0 VIS2: score:	1.0053112063406973	1.0964538862541453
SNR: 1.4 angle: 3.0 VIS2: score:	0.9990201900342046	1.123789479470285
SNR: 1.2 angle: 3.0 VIS2: score:	0.9983930545440053	1.164560346912696
SNR: 1.0 angle: 3.0 VIS2: score:	0.997319456641421	1.2290584473535424
SNR: 0.8 angle: 3.0 VIS2: score:	0.987491751104786	1.3394629221843875
SNR: 0.6 angle: 3.0 VIS2: score:	0.9700366295115521	1.5509593845204808
SNR: 0.399 angle: 3.0 VIS2: score:	0.9858168594530491	2.040562824952632
SNR: 0.199 angle: 3.0 VIS2: score:	0.9936735661993709	3.696215895981706
SNR: 0.179 angle: 3.0 VIS2: score:	0.9922159528454567	4.079513079439166
SNR: 0.159 angle: 3.0 VIS2: score:	0.9902212116720193	4.562457996216046
SNR: 0.139 angle: 3.0 VIS2: score:	1.0	5.188137273392731
SNR: 0.119 angle: 3.0 VIS2: score:	1.0	6.0285959428183
SNR: 0.098 angle: 3.0 VIS2: score:	1.0	7.286209667096372
SNR: 0.078 angle: 3.0 VIS2: score:	0.9623426377175301	9.120704791607277
SNR: 0.057 angle: 3.0 VIS2: score:	1.0	12.442904934682065
SNR: 0.037 angle: 3.0 VIS2: score:	1.0	19.12843248924418
SNR: 0.016 angle: 3.0 VIS2: score:	1.0	44.17429958312818
     */
    private static void testV2WithMeanCorrection(final double angle, final double visRef, final double snr, final boolean amp, double[][] samples, final double[][] ratios, final int pos) {
        if (amp) {
            throw new IllegalStateException("only amp=false supported !");
        }

        System.out.println("-----");
        System.out.println("angle: " + angle);

        final double angRad = Math.toRadians(angle);

        final double cos_angle = Math.cos(angRad);
        final double sin_angle = Math.sin(angRad);

        final double visErr = visRef / snr;
        System.out.println("vis ref: " + visRef);
        System.out.println("circular err: " + visErr);

        // v2 = v * v
        final double exp_ref = visRef * visRef;
        // d(v2) = 2v * dv si dv petit, sinon il faut suivre les lois de distributions !
        // ie abaque (erreur expected) => erreur corrigée sur CVis
        final double exp_err = 2.0 * visRef * visErr;

        // Compute true V2 complex (angle x2):
        final double exp_re = exp_ref * Math.cos(2.0 * angRad);
        final double exp_im = exp_ref * Math.sin(2.0 * angRad);

        System.out.println("expected mean: " + exp_ref + ", stddev: " + exp_err + " SNR = " + (exp_ref / exp_err));

        // add angle:
        final double visRe = visRef * cos_angle;
        final double visIm = visRef * sin_angle;

        final double visCErr = visErr * COMPLEX_ERROR_SCALE;
        System.out.println("vis re: " + visRe + " im: " + visIm + " err: " + visCErr);

        double sample, diff;
        int n;
        double re, im;
        double y, t;

        final double[] re_samples = new double[N_SAMPLES];
        final double[] im_samples = new double[N_SAMPLES];

        double re_sum, re_sum_err, im_sum, im_sum_err, cos_phi, sin_phi;
        re_sum = re_sum_err = im_sum = im_sum_err = 0.0;
        double cRe, cIm;

        // bivariate distribution (complex normal):
        // pass 1: numeric mean:
        for (n = 0; n < N_SAMPLES; n++) {
            // update nth sample:
            re = visRe + visCErr * samples[0][n];
            im = visIm + visCErr * samples[1][n];

            // compute C2=C*C
            // (a + bi)(c + di) = (ac - bd) + (ad + bc)i
            cRe = re * re - im * im;
            cIm = 2.0 * re * im;

            // average complex value:
            re_samples[n] = cRe;
            im_samples[n] = cIm;

            // kahan sum
            // re_sum += cRe;
            y = cRe - re_sum_err;
            t = re_sum + y;
            re_sum_err = (t - re_sum) - y;
            re_sum = t;

            // im_sum += cIm;
            y = cIm - im_sum_err;
            t = im_sum + y;
            im_sum_err = (t - im_sum) - y;
            im_sum = t;
        }

        // mean(vphi):
        final double s_camp_mean = Math.sqrt(re_sum * re_sum + im_sum * im_sum);
        // double s_cphi_mean = (im_sum != 0.0) ? Math.atan2(im_sum, re_sum) : 0.0;
        // System.out.println("angle:"+Math.toDegrees(s_cphi_mean));

        // rotate by -phi:
        cos_phi = re_sum / s_camp_mean;
        sin_phi = im_sum / s_camp_mean;

        // true average on averaged complex samples:
        double avg = SAMPLING_FACTOR_MEAN * s_camp_mean;

        // true average on averaged complex distribution:
        double avg_re = SAMPLING_FACTOR_MEAN * re_sum;
        double avg_im = SAMPLING_FACTOR_MEAN * im_sum;

        System.out.println("expected mean re: " + exp_re + ", avg_re: " + avg_re + " ratio = " + (avg_re / exp_re));
        System.out.println("expected mean im: " + exp_im + ", avg_im: " + avg_im + " ratio = " + (avg_im / exp_im));

        /*
        * This test on mean() is cheap and interesting to detect non gaussian behaviour = mean diverges and sigma too, but less fast !
        * this is useful to FLAG such data anyway (incorrect assumptions) and use theoretical values instead :
        * distribution of C2 samples is NOT gaussian anymore, but C is a good normal complex law, how it T3 = C1.C2.C3 (bad too, I suppose) ?
         */
        if (Math.abs(avg_re / exp_re) > 1.1 || Math.abs(avg_im / exp_im) > 1.1) {
            System.out.println("TODO: correct re/im means and phi");
            avg = exp_ref;
            cos_phi = Math.cos(2.0 * angRad);
            sin_phi = Math.sin(2.0 * angRad);
            avg_re = exp_re;
            avg_im = exp_im;
        }

        // pass 2: stddev:
        double diff_acc = 0.0;
        double sq_diff_acc = 0.0;

        double re_diff_acc = 0.0;
        double re_sq_diff_acc = 0.0;
        double im_diff_acc = 0.0;
        double im_sq_diff_acc = 0.0;

        for (n = 0; n < N_SAMPLES; n++) {
            // Correct amplitude by estimated phase:
            // Amp = Re { C * phasor(-phi) }
            sample = re_samples[n] * cos_phi + im_samples[n] * sin_phi; // -phi => + imaginary part in complex mult

            // Compensated-summation variant for better numeric precision:
            diff = sample - avg;
            diff_acc += diff;
            sq_diff_acc += diff * diff;

            // New: compute stddev on re/im
            // Compensated-summation variant for better numeric precision:
            diff = re_samples[n] - avg_re;
            re_diff_acc += diff;
            re_sq_diff_acc += diff * diff;

            diff = im_samples[n] - avg_im;
            im_diff_acc += diff;
            im_sq_diff_acc += diff * diff;
        }

        // standard deviation on amplitude:
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        double stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))))
                / COMPLEX_ERROR_SCALE; // single dimension only !

        // standard deviation on complex values:
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        final double re_stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (re_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (re_diff_acc * re_diff_acc))));
        final double im_stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (im_sq_diff_acc - (SAMPLING_FACTOR_MEAN * (im_diff_acc * im_diff_acc))));

        System.out.println("Complex stddev re: " + re_stddev + " im: " + im_stddev);

        final double est_sigma = Math.sqrt(re_stddev * re_stddev + im_stddev * im_stddev); // sum of variances (re,im)
        System.out.println("Est2 est_sigma = " + est_sigma + " stddev = " + stddev + " ratio: " + (est_sigma / stddev));

        // consider est_sigma is better accuracy:
        stddev = est_sigma;

        ratios[0][pos] = (avg / exp_ref);
        ratios[1][pos] = (stddev / exp_err);

        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] avg= " + avg + " vs expected ref= " + exp_ref + " ratio: " + ratios[0][pos]);
        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] stddev= " + stddev + " vs expected Err= " + exp_err + " ratio: " + ratios[1][pos]);
    }

    private static void test(final double visRef, final double snr, final boolean amp, final double[][] ratios, final int pos) {
        /* create a new random generator to have different seed (single thread) */
        final Random random = new Random();

        final double[][] samples = new double[2][];
        samples[0] = new double[N_SAMPLES];
        samples[1] = new double[N_SAMPLES];

        for (int n = 0; n < N_SAMPLES; n++) {
            // update nth sample:
            samples[0][n] = random.nextGaussian();
            samples[1][n] = random.nextGaussian();
        }
        test(45.0, visRef, snr, amp, samples, ratios, pos);
    }

    private static void testV(final double angle, final double visRef, final double snr, final boolean amp, double[][] samples, final double[][] ratios, final int pos) {
        if (!amp) {
            throw new IllegalStateException("only amp=true supported !");
        }

        System.out.println("-----");
        System.out.println("angle: " + angle);

        final double angRad = Math.toRadians(angle);

        final double cos_angle = Math.cos(angRad);
        final double sin_angle = Math.sin(angRad);

        final double visErr = visRef / snr;
        System.out.println("vis ref: " + visRef);
        System.out.println("circular err: " + visErr);

        final double exp_ref = visRef;
        final double exp_err = visErr;

        System.out.println("expected mean: " + exp_ref + ", stddev: " + exp_err + " SNR = " + (exp_ref / exp_err));

        // add angle:
        final double visRe = visRef * cos_angle;
        final double visIm = visRef * sin_angle;

        final double visCErr = visErr * COMPLEX_ERROR_SCALE;
        System.out.println("vis re: " + visRe + " im: " + visIm + " err: " + visCErr);

        double sample, diff;
        int n;
        double re, im;
        double y, t;

        double re_sum, re_sum_err, im_sum, im_sum_err, cos_phi, sin_phi;

        final double[] re_samples = new double[N_SAMPLES];
        final double[] im_samples = new double[N_SAMPLES];

        re_sum = re_sum_err = im_sum = im_sum_err = 0.0;

        // bivariate distribution (complex normal):
        // pass 1: numeric mean:
        for (n = 0; n < N_SAMPLES; n++) {
            // update nth sample:
            re = visRe + visCErr * samples[0][n];
            im = visIm + visCErr * samples[1][n];

            // average complex value:
            re_samples[n] = re;
            im_samples[n] = im;

            // kahan sums:
            // re_sum += cRe;
            y = re - re_sum_err;
            t = re_sum + y;
            re_sum_err = (t - re_sum) - y;
            re_sum = t;

            // im_sum += cIm;
            y = im - im_sum_err;
            t = im_sum + y;
            im_sum_err = (t - im_sum) - y;
            im_sum = t;
        }

        // mean(vphi):
        final double s_camp_mean = Math.sqrt(re_sum * re_sum + im_sum * im_sum);
        // double s_cphi_mean = (im_sum != 0.0) ? Math.atan2(im_sum, re_sum) : 0.0;
        // System.out.println("angle:"+Math.toDegrees(s_cphi_mean));

        // rotate by -phi:
        cos_phi = re_sum / s_camp_mean;
        sin_phi = im_sum / s_camp_mean;

        // true average on averaged complex samples:
        double avg = SAMPLING_FACTOR_MEAN * s_camp_mean;
        /*        
        final double est_sigma_c = Math.sqrt(dc.getStddevReal() * dc.getStddevReal() + dc.getStddevIm() * dc.getStddevIm());
            System.out.println("Est C: mu = " + est_mu_c + " sigma = " + est_sigma_c);        
         */

        // pass 2: stddev:
        double diff_acc = 0.0;
        double sq_diff_acc = 0.0;

        for (n = 0; n < N_SAMPLES; n++) {
            // Correct amplitude by estimated phase:
            // Amp = Re { C * phasor(-phi) }
            sample = re_samples[n] * cos_phi + im_samples[n] * sin_phi; // -phi => + imaginary part in complex mult

            // Compensated-summation variant for better numeric precision:
            diff = sample - avg;
            diff_acc += diff;
            sq_diff_acc += diff * diff;
        }

        // standard deviation on amplitude:
        // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
        final double stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))))
                / COMPLEX_ERROR_SCALE; // single dimension only !

        ratios[0][pos] = (avg / exp_ref);
        ratios[1][pos] = (stddev / exp_err);

        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] avg= " + avg + " vs expected ref= " + exp_ref + " ratio: " + ratios[0][pos]);
        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] stddev= " + stddev + " vs expected Err= " + exp_err + " ratio: " + ratios[1][pos]);
    }

    // sum tests
    private static void testSum(final double[] values, final double val) {
        Arrays.fill(values, val);
        values[0] = 1.0;

        final double naive = naiveSum(values);
        System.out.println("naiveSum[1 + " + val + " x " + values.length + "]: " + naive);
        final double kahan = kahanSum(values);
        System.out.println("kahanSum[1 + " + val + " x " + values.length + "]: " + kahan);
        System.out.println("delta: " + (naive - kahan));
    }

    private static double naiveSum(double[] values) {
        final double[] state = new double[1]; // sum
        state[0] = 0.0;
        for (int i = 0; i < values.length; i++) {
            state[0] += values[i];
        }
        return state[0];
    }

    private static double kahanSum(double[] values) {
        final double[] state = new double[2]; // sum | error
        state[0] = 0.0;
        state[1] = 0.0;
        for (int i = 0; i < values.length; i++) {
            final double y = values[i] - state[1];
            final double t = state[0] + y;
            state[1] = (t - state[0]) - y;
            state[0] = t;
        }
        return state[0];
    }

    // --- utility functions ---
    private static void saveComplexDistribution(final File file, final double[] distRe, final double[] distIm) throws IOException {
        if (file != null) {
            final int capacity = (N_SAMPLES + 1) * 40;

            final StringBuilder sb = new StringBuilder(capacity);
            sb.append("# RE\tIM\n");

            for (int i = 0; i < N_SAMPLES; i++) {
                sb.append(distRe[i]).append('\t').append(distIm[i]).append('\n');
            }

            System.out.println("Writing file: " + file.getAbsolutePath());
            writeFile(file, sb.toString());
        }
    }

    private static void writeFile(final File file, final String content) throws IOException {
        final Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        try {
            w.write(content);
        } finally {
            try {
                w.close();
            } catch (IOException ioe) {
                logger.debug("IO close failure.", ioe);
            }
        }
    }
}
