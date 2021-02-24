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
import java.util.Collections;
import java.util.Comparator;
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

    /** max error on squared mean / variance */
    private final static double GOOD_THRESHOLD = 1e-2;
    /** convergence threshold on distribution quality */
    private final static double QUALITY_THRESHOLD = 4e-2;

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
            logger.info("prepare: {} needed distributions ({} samples)", needed, N_SAMPLES);
            final long start = System.nanoTime();

            ComplexDistribution.create(needed, cache);

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

        private static int countIteration = 0;

        static {
            logger.debug("N_SAMPLES: {}", N_SAMPLES);
            logger.debug("GOOD_THRESHOLD: {}", GOOD_THRESHOLD);
            logger.debug("QUALITY_THRESHOLD: {}", QUALITY_THRESHOLD);

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

        /* members */
        private int numIter = -1;
        /** samples (re,im) */
        private final double[][] samples = new double[2][N_SAMPLES];
        /** moments */
        private final double[] qualityMoments = new double[3];

        private ComplexDistribution() {
            super();
        }

        public double[][] getSamples() {
            return this.samples;
        }

        public double[] getQualityMoments() {
            return this.qualityMoments;
        }

        public double[][] getMoments() {
            final double[][] moments = new double[2][4];
            moments(this.samples[0], moments[0]);
            moments(this.samples[1], moments[1]);
            return moments;
        }

        @Override
        public String toString() {
            return "ComplexDistribution{" + "numIter=" + numIter + " quality=" + qualityMoments[2] + '}';
        }

        private void generate(final Random random) {
            this.numIter = countIteration++;

            final double[] distRe = this.samples[0];
            final double[] distIm = this.samples[1];

            // bivariate distribution (complex normal):
            for (int n = 0; n < N_SAMPLES; n++) {
                // generate nth sample:
                distRe[n] = random.nextGaussian();
                distIm[n] = random.nextGaussian();
            }
        }

        public static void create(final int nDistribs, final ArrayList<ComplexDistribution> distributions) {
            int prevIter = countIteration;

            /* create a new random generator to have different seed (single thread) */
            final Random random = new Random();

            final int MIN_ITER = 5;
            final int MAX_ITER = 25;
            final int N = nDistribs * 5;

            final ArrayList<ComplexDistribution> iterDistribs = new ArrayList<ComplexDistribution>(N);
            ComplexDistribution d = new ComplexDistribution();

            final Comparator<ComplexDistribution> cmpQual = new Comparator<ComplexDistribution>() {
                @Override
                public int compare(ComplexDistribution d1, ComplexDistribution d2) {
                    return Double.compare(d1.getQualityMoments()[2], d2.getQualityMoments()[2]);
                }
            };

            final long start = System.nanoTime();

            int i = 1;
            double minq = Double.MAX_VALUE;
            double maxq = Double.MAX_VALUE;
            int nStale = 0;

            do {
                // Iteration: prepare good distributions (first quality pass)
                do {
                    d.generate(random);

                    if (d.test()) {
                        iterDistribs.add(d);
                        d = new ComplexDistribution(); // TODO: reuse instances
                    }
                } while (iterDistribs.size() < N);

                // Final scoring:
                Collections.sort(iterDistribs, cmpQual);

                // only keep first distribs (better quality):
                distributions.clear();
                for (int j = 0; j < nDistribs; j++) {
                    distributions.add(iterDistribs.get(j));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Iteration[{}] best distribs: {}", i, distributions);
                }

                // check convergence:
                final double qLo = distributions.get(0).getQualityMoments()[2];
                final double qHi = distributions.get(nDistribs - 1).getQualityMoments()[2];

                final double ratio_q = (maxq - qHi) / maxq;

                if (logger.isDebugEnabled()) {
                    logger.debug("qLo: " + qLo + " qHi: " + qHi + " maxq: " + maxq + " ratio_q: " + ratio_q);
                }

                minq = qLo;
                maxq = qHi;

                if (i >= MIN_ITER) {
                    // check stale or low progression:
                    if ((ratio_q < QUALITY_THRESHOLD) && (++nStale == 2)) {
                        break;
                    }
                }

                // TODO: recycle
                iterDistribs.clear();
                // keep best distribs:
                iterDistribs.addAll(distributions);

            } while (i++ <= MAX_ITER);

            logger.debug("distributions: {}", distributions);

            logger.info("distributions quality: ({} - {})", minq, maxq);

            // log test details:
            for (i = 0; i < nDistribs; i++) {
                d = distributions.get(i);
                d.test(null, true);
            }

            logger.info("done: {} ms ({} iterations).", 1e-6d * (System.nanoTime() - start), (countIteration - prevIter));
        }

        private boolean test() {
            return test(null, false);
        }

        private boolean test(final double[] data, final boolean log) {
            final double snr = 100.0;

            final double ref_amp = 0.5; // middle of [0-1]
            final double err_amp = ref_amp / snr;

            final double err_dist = err_amp; // use circular symetric error

            final double sq_amp = ref_amp * ref_amp;
            final double var_amp = err_amp * err_amp;

            final double[] distRe = this.samples[0];
            final double[] distIm = this.samples[1];

            double mean_chi2_acc = 0.0, mean_sq_acc = 0.0, var_acc = 0.0;
            double mean_sq_diff_acc = 0.0, var_diff_acc = 0.0;

            for (int i = 0; i < NUM_ANGLES; i++) {
                final double cos_phi = ANGLES_COS_SIN[i][0];
                final double sin_phi = ANGLES_COS_SIN[i][1];
                final double ref_re = ref_amp * cos_phi;
                final double ref_im = ref_amp * sin_phi;

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
                double sample;
                double amp_sum_diff = 0.0;
                double amp_sum_diff_square = 0.0;
                double chi2_amp_sum = 0.0;

                for (int n = 0; n < N_SAMPLES; n++) {
                    // update nth sample:
                    re = ref_re + (err_dist * distRe[n]);
                    im = ref_im + (err_dist * distIm[n]);

                    // Correct amplitude by pure phase:
                    // Amp = Re { C * phasor(-phi) }
                    sample = re * cos_phi + im * sin_phi; // -phi => + imaginary part in complex mult
                    if (data != null) {
                        data[n] = sample;
                    }

                    // Compensated-summation variant for better numeric precision:
                    diff = sample - ref_amp;
                    amp_sum_diff += diff;
                    amp_sum_diff_square += diff * diff;

                    // chi2 vs pure mean / error:
                    diff = (sample - ref_amp) / err_amp;
                    chi2_amp_sum += diff * diff;
                }

                // variance on amplitude:
                // note: this algorithm ensures correctness (stable) even if the mean used in diff is wrong !
                final double variance = SAMPLING_FACTOR_VARIANCE * (amp_sum_diff_square - (SAMPLING_FACTOR_MEAN * (amp_sum_diff * amp_sum_diff)));

                final double chi2 = SAMPLING_FACTOR_VARIANCE * chi2_amp_sum;

                // average chi2/mean/variance estimations:
                mean_chi2_acc += chi2;
                mean_sq_acc += mean_sq;
                var_acc += variance;

                // sum of relative delta:
                mean_sq_diff_acc += Math.abs(mean_sq / sq_amp - 1.0); // versus 1 (normal law)
                var_diff_acc += Math.abs(variance / var_amp - 1.0); // versus 1 (normal law)

                if (data != null) {
                    logger.info("quality: mean: " + (mean_sq / sq_amp - 1.0) + " variance: " + (variance / var_amp - 1.0) + " chi2: " + chi2);
                    final File file = new File("data_" + N_SAMPLES + "_it_" + this.numIter + "_ang_" + (ANG_STEP * i) + ".txt");
                    try {
                        saveArray(file, data);
                    } catch (IOException ioe) {
                        logger.info("IO failure: ", ioe);
                    }
                }

            } // loop (angles)

            final double diff_mean_sq = mean_sq_diff_acc / NUM_ANGLES;
            final double diff_var = var_diff_acc / NUM_ANGLES;

            // set quality moments:
            this.qualityMoments[0] = diff_mean_sq; // mean quality
            this.qualityMoments[1] = diff_var; // var quality
            // overall quality = sum(quality moments)
            final double score = diff_mean_sq + diff_var;
            this.qualityMoments[2] = score;

            final boolean good = (score < GOOD_THRESHOLD);

            if (good && log) {
                final double chi2 = mean_chi2_acc / NUM_ANGLES;
                final double mean_sq = mean_sq_acc / NUM_ANGLES;
                final double variance = var_acc / NUM_ANGLES;

                // relative difference: delta = (x - est) / x
                final double ratio_mean = mean_sq / sq_amp;
                final double ratio_variance = variance / var_amp;

                logger.info("Sampling[" + N_SAMPLES + "] snr=" + snr + " (err(re,im)= " + err_dist + ") chi2= " + chi2
                        + " avg= " + Math.sqrt(mean_sq) + " norm= " + ref_amp + " ratio: " + Math.sqrt(ratio_mean)
                        + " stddev= " + Math.sqrt(variance) + " err(norm)= " + err_amp + " ratio: " + Math.sqrt(ratio_variance)
                        + " diff_mean_sq= " + diff_mean_sq + " diff_var= " + diff_var
                        + " quality= " + this.qualityMoments[2]
                        + " good = " + good);
            }
            return good;
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
            final double[] data = new double[N_SAMPLES];

            for (int n = 0; n < INITIAL_CAPACITY; n++) {
                final ComplexDistribution d = StatUtils.getInstance().get();
                System.out.println("get(): " + d);

                // Get the complex distribution for this row:
                final double[] distRe = d.getSamples()[0];
                final double[] distIm = d.getSamples()[1];

                System.out.println("moments(re): " + Arrays.toString(d.getMoments()[0]));
                System.out.println("moments(im): " + Arrays.toString(d.getMoments()[1]));

                final File file = new File("dist_" + N_SAMPLES + "_" + n + ".txt");
                saveComplexDistribution(file, distRe, distIm);

                // dump all test samples:
                System.out.println("quality moments: " + Arrays.toString(d.getQualityMoments()));
                d.test(data, true);
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

        final double[][] ratios = new double[3][INITIAL_CAPACITY]; // mean, stddev, chi2

        for (double snr = 5.0; snr > 0.01;) {
            // fix snr rounding:
            snr = NumberUtils.trimTo3Digits(snr);

            // TODO: check amplitude effect (ie x/y complex position impact on mean /stddev estimations) */
            /* for (double amp = 1.0; amp > 5e-6; amp /= 10.0) */
            final double amp = 0.1;
            {
                System.out.println("--- SNR: " + snr + " @ AMP = " + amp + "---");

                if (true) {
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
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VISAMP Ratio mean moments: " + Arrays.toString(moments(ratios[0])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VISAMP Ratio err moments : " + Arrays.toString(moments(ratios[1])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " VISAMP score:\t" + mean(ratios[0]) + "\t" + mean(ratios[1]) + "\t" + mean(ratios[2]));
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
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " C2 SNR: " + (snr / 2) + " VIS2 Ratio mean moments: " + Arrays.toString(moments(ratios[0])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " C2 SNR: " + (snr / 2) + " VIS2 Ratio err moments : " + Arrays.toString(moments(ratios[1])));
                        System.out.println("SNR: " + snr + "(C) angle: " + angle + " C2 SNR: " + (snr / 2) + " VIS2 Score:\t" + mean(ratios[0]) + "\t" + mean(ratios[1]) + "\t" + mean(ratios[2]));
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
SNR: 5.0(C) angle: 3.0 C2 SNR: 2.5 VIS2 Score:	1.0010045298742483	1.0227438073895267	1.0000000000000002
SNR: 4.0(C) angle: 3.0 C2 SNR: 2.0 VIS2 Score:	1.0012765243198278	1.034138545888004	1.0
SNR: 3.0(C) angle: 3.0 C2 SNR: 1.5 VIS2 Score:	1.0017483277680366	1.057939450242428	1.0000000000000002
SNR: 2.0(C) angle: 3.0 C2 SNR: 1.0 VIS2 Score:	1.0027610574895454	1.122080710029527	1.0000000000000002
SNR: 1.8(C) angle: 3.0 C2 SNR: 0.9 VIS2 Score:	1.0031190712536069	1.1478669415912104	0.9999999999999997
SNR: 1.6(C) angle: 3.0 C2 SNR: 0.8 VIS2 Score:	1.0035809354229324	1.182856118800273	0.9999999999999999
SNR: 1.4(C) angle: 3.0 C2 SNR: 0.7 VIS2 Score:	1.0041981645156703	1.231943562478189	0.9999999999999999
SNR: 1.2(C) angle: 3.0 C2 SNR: 0.6 VIS2 Score:	1.005062062946277	1.303718640326104	0.9999999999999999
SNR: 1.0(C) angle: 3.0 C2 SNR: 0.5 VIS2 Score:	1.0063500714379556	1.414374664150271	1.0000000000000002
SNR: 0.8(C) angle: 3.0 C2 SNR: 0.4 VIS2 Score:	1.0084540433199518	1.5975542543269703	1.0000000000000004
SNR: 0.6(C) angle: 3.0 C2 SNR: 0.3 VIS2 Score:	1.012421073145649	1.9338722493668803	0.9999999999999998
SNR: 0.399(C) angle: 3.0 C2 SNR: 0.1995 VIS2 Score:	1.0221904915301658	2.6741486127911767	0.9999999999999998
SNR: 0.199(C) angle: 3.0 C2 SNR: 0.0995 VIS2 Score:	1.0668942956555465	5.053369593332251	0.9999999999999999
SNR: 0.179(C) angle: 3.0 C2 SNR: 0.0895 VIS2 Score:	1.0803687092150416	5.594313632160225	1.0000000000000004
SNR: 0.159(C) angle: 3.0 C2 SNR: 0.0795 VIS2 Score:	1.0992749662829189	6.273393846276565	1.0000000000000002
SNR: 0.139(C) angle: 3.0 C2 SNR: 0.0695 VIS2 Score:	1.1272617695249763	7.150089149638841	1.0
SNR: 0.119(C) angle: 3.0 C2 SNR: 0.0595 VIS2 Score:	1.1719267154180815	8.324413053932211	1.0000000000000004
SNR: 0.098(C) angle: 3.0 C2 SNR: 0.049 VIS2 Score:	1.2579953189078907	10.09196814804471	0.9999999999999997
SNR: 0.078(C) angle: 3.0 C2 SNR: 0.039 VIS2 Score:	1.4410567104661591	12.719703793211266	1.0
SNR: 0.057(C) angle: 3.0 C2 SNR: 0.0285 VIS2 Score:	1.938981779453928	17.391932669762614	0.9999999999999994
SNR: 0.037(C) angle: 3.0 C2 SNR: 0.0185 VIS2 Score:	3.553717709600981	26.926227203332353	0.9999999999999999
SNR: 0.016(C) angle: 3.0 C2 SNR: 0.008 VIS2 Score:	17.449905852613874	62.13034186209619	1.0
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

        final double visCErr = visErr;
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
        final double avg = SAMPLING_FACTOR_MEAN * s_camp_mean;

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
        final double stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))));

        double chi2_amp_sum = 0.0;

        for (n = 0; n < N_SAMPLES; n++) {
            // Correct amplitude by estimated phase:
            // Amp = Re { C * phasor(-phi) }
            sample = re_samples[n] * cos_phi + im_samples[n] * sin_phi; // -phi => + imaginary part in complex mult

            // chi2 vs pure mean / error:
            diff = (sample - avg) / stddev;
            chi2_amp_sum += diff * diff;
        }

        final double chi2 = SAMPLING_FACTOR_VARIANCE * chi2_amp_sum;

        ratios[0][pos] = (avg / exp_ref);
        ratios[1][pos] = (stddev / exp_err);
        ratios[2][pos] = chi2;

        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] avg= " + avg + " vs expected ref= " + exp_ref + " ratio: " + ratios[0][pos] + " chi2: " + chi2);
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

    /*
SNR: 5.0(C) angle: 3.0 VISAMP score:	1.0004756945672886	1.0002647045562154	1.0000000000000002
SNR: 4.0(C) angle: 3.0 VISAMP score:	1.00059677659021	1.0002644163119558	1.0000000000000002
SNR: 3.0(C) angle: 3.0 VISAMP score:	1.000800498254227	1.000263935931527	1.0000000000000004
SNR: 2.0(C) angle: 3.0 VISAMP score:	1.001215134611274	1.0002629751559866	1.0000000000000004
SNR: 1.8(C) angle: 3.0 VISAMP score:	1.001355477899223	1.000262654859044	1.0000000000000002
SNR: 1.6(C) angle: 3.0 VISAMP score:	1.0015324054720895	1.0002622544383537	0.9999999999999999
SNR: 1.4(C) angle: 3.0 VISAMP score:	1.0017623302941037	1.000261739504399	1.0000000000000002
SNR: 1.2(C) angle: 3.0 VISAMP score:	1.0020731783459524	1.0002610526786972	1.0000000000000002
SNR: 1.0(C) angle: 3.0 VISAMP score:	1.0025165873364066	1.0002600904979662	1.0000000000000002
SNR: 0.8(C) angle: 3.0 VISAMP score:	1.0031996908244962	1.0002586454045561	1.0
SNR: 0.6(C) angle: 3.0 VISAMP score:	1.004386202406063	1.000256230255331	1.0000000000000004
SNR: 0.399(C) angle: 3.0 VISAMP score:	1.0069597147948257	1.0002513270789979	0.9999999999999996
SNR: 0.199(C) angle: 3.0 VISAMP score:	1.0161592581582337	1.0002361509899491	1.0
SNR: 0.179(C) angle: 3.0 VISAMP score:	1.018516657135068	1.000232648466847	1.0
SNR: 0.159(C) angle: 3.0 VISAMP score:	1.0216276444986283	1.000228197616387	1.0
SNR: 0.139(C) angle: 3.0 VISAMP score:	1.0259002361883058	1.000222356135221	0.9999999999999993
SNR: 0.119(C) angle: 3.0 VISAMP score:	1.0320859222598509	1.0002143659513443	0.9999999999999998
SNR: 0.098(C) angle: 3.0 VISAMP score:	1.0423395580589179	1.0002021130262635	0.9999999999999998
SNR: 0.078(C) angle: 3.0 VISAMP score:	1.059564141538027	1.0001836307291063	1.0000000000000004
SNR: 0.057(C) angle: 3.0 VISAMP score:	1.0982020398906587	1.00014642670414	0.9999999999999999
SNR: 0.037(C) angle: 3.0 VISAMP score:	1.208528189048131	1.0000180403701673	1.0000000000000004
SNR: 0.016(C) angle: 3.0 VISAMP score:	1.9534791991245422	0.9997740687377148	1.0000000000000002
     */
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

        final double visCErr = visErr;
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
        final double stddev = Math.sqrt(SAMPLING_FACTOR_VARIANCE * (sq_diff_acc - (SAMPLING_FACTOR_MEAN * (diff_acc * diff_acc))));

        double chi2_amp_sum = 0.0;

        for (n = 0; n < N_SAMPLES; n++) {
            // Correct amplitude by estimated phase:
            // Amp = Re { C * phasor(-phi) }
            sample = re_samples[n] * cos_phi + im_samples[n] * sin_phi; // -phi => + imaginary part in complex mult

            // chi2 vs pure mean / error:
            diff = (sample - avg) / stddev;
            chi2_amp_sum += diff * diff;
        }

        final double chi2 = SAMPLING_FACTOR_VARIANCE * chi2_amp_sum;

        ratios[0][pos] = (avg / exp_ref);
        ratios[1][pos] = (stddev / exp_err);
        ratios[2][pos] = chi2;

        System.out.println("[" + pos + "] Sampling[" + N_SAMPLES + "] avg= " + avg + " vs expected ref= " + exp_ref + " ratio: " + ratios[0][pos] + " chi2: " + chi2);
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
    private static void saveArray(final File file, final double[] array) throws IOException {
        if (file != null) {
            final int len = array.length;
            final int capacity = (len + 1) * 40;

            final StringBuilder sb = new StringBuilder(capacity);
            sb.append("# X\n");

            for (int i = 0; i < len; i++) {
                sb.append(array[i]).append('\n');
            }

            logger.debug("Writing file: {}", file.getAbsolutePath());
            writeFile(file, sb.toString());
        }
    }

    private static void saveComplexDistribution(final File file, final double[] distRe, final double[] distIm) throws IOException {
        if (file != null) {
            final int len = distRe.length;
            final int capacity = (len + 1) * 40;

            final StringBuilder sb = new StringBuilder(capacity);
            sb.append("# RE\tIM\n");

            for (int i = 0; i < len; i++) {
                sb.append(distRe[i]).append('\t').append(distIm[i]).append('\n');
            }

            logger.debug("Writing file: {}", file.getAbsolutePath());
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
