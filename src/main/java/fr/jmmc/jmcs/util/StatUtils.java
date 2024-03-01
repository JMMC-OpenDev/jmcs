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
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

    private final static boolean USE_CACHE = true;

    /** local folder storing cached files */
    public final static String FOLDER_CACHE_JMCS = FileUtils.getPlatformCachesPath() + "jmcs" + File.separatorChar;

    /** supersampling factor: x1 (normal), x16 (high quality) */
    private final static int SUPER_SAMPLING = 1;
    /** number of dimensios */
    public final static int N_DIMS = 2;
    /** number of samples */
    public final static int N_SAMPLES = Integer.getInteger("StatsUtils.samples", 1024 * SUPER_SAMPLING);

    private final static int RATIO_GEN = 3;

    private final static int MIN_ITER = 20;
    private final static int MAX_ITER = 10 * MIN_ITER;
    private final static int STALE_ITER = (3 * MIN_ITER) / 2;

    private final static int RND_ITER_MAX = 250; // 500,000 doubles

    /** max error on squared mean / variance */
    private final static double GOOD_THRESHOLD = 0.3;
    /** convergence threshold on distribution quality */
    private final static double QUALITY_THRESHOLD = 0.01;

    public final static double QUALITY_SCORE_WEIGHT_VAR = 16.0;

    /** normalization factor = 1/N_SAMPLES */
    public final static double SAMPLING_FACTOR_MEAN = 1d / N_SAMPLES;
    /** normalization factor for variance = 1 / (N_SAMPLES - 1) (bessel correction) */
    public final static double SAMPLING_FACTOR_VARIANCE = 1d / (N_SAMPLES - 1);

    public final static int MOMENT_MEAN = 0;
    public final static int MOMENT_VARIANCE = 1;
    public final static int MOMENT_ASYMETRY = 2;
    public final static int MOMENT_KURTOSIS = 3;

    public final static int QUALITY_SCORE = 2;

    private final static int ANG_STEP = 15; // deg

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
        if (count > cache.size()) {
            final long start = System.nanoTime();

            // assert that parent directory exist
            new File(FOLDER_CACHE_JMCS).mkdirs();

            final File cacheFile = new File(FOLDER_CACHE_JMCS, StatUtils.class.getSimpleName() + ".ser");

            logger.debug("prepare: cacheFile = '{}'", cacheFile);

            if (USE_CACHE && cacheFile.canRead()) {
                @SuppressWarnings("unchecked")
                final ArrayList<ComplexDistribution> loadedCache = (ArrayList<ComplexDistribution>) FileUtils.readObject(cacheFile);

                if ((loadedCache != null) && (count <= loadedCache.size())) {
                    // Check array dimensions:
                    for (final Iterator<ComplexDistribution> it = loadedCache.iterator(); it.hasNext();) {
                        final ComplexDistribution d = it.next();

                        if (!d.checkDims()) {
                            logger.debug("prepare: bad dimensions: {}", d);
                            it.remove();
                        }
                    }

                    if (count <= loadedCache.size()) {
                        // loaded cache is valid => adopt it:
                        cache.clear();
                        cache.addAll(loadedCache);

                        logger.info("prepare: {} loaded distributions", cache.size());
                    }
                }
            }

            if (count > cache.size()) {
                logger.info("prepare: {} needed distributions ({} samples)", count, N_SAMPLES);

                ComplexDistribution.create(count, cache);

                // update cache:
                if (!cacheFile.exists() || cacheFile.canWrite()) {
                    FileUtils.writeObject(cacheFile, cache);
                }
            }

            // finally log test details:
            for (final ComplexDistribution d : cache) {
                d.test(null, true);
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

    public static final class ComplexDistribution implements Serializable {

        private static final long serialVersionUID = 1L;

        private final static int NUM_ANGLES;

        private final static double[][] ANGLES_COS_SIN;

        private static int countIteration = 0;

        private static int startIter = 0;

        private static Random createRandom(final Random prevRandom) {
            final int rndIter = countIteration - startIter;

            if ((prevRandom != null) && (rndIter < RND_ITER_MAX)) {
                return prevRandom;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Create new Random after iter: {}", rndIter);
            }
            /* create a new random generator to have different seed (single thread) */
            startIter = countIteration;
            return new Random();
        }

        static {
            logger.debug("N_SAMPLES:         {}", N_SAMPLES);

            logger.debug("MIN_ITER:          {}", MIN_ITER);
            logger.debug("MAX_ITER:          {}", MAX_ITER);
            logger.debug("STALE_ITER:        {}", STALE_ITER);
            logger.debug("RND_ITER_MAX:      {}", RND_ITER_MAX);

            logger.debug("GOOD_THRESHOLD:    {}", GOOD_THRESHOLD);
            logger.debug("QUALITY_THRESHOLD: {}", QUALITY_THRESHOLD);

            NUM_ANGLES = 360 / ANG_STEP;

            logger.debug("ANG_STEP:          {}", ANG_STEP);
            logger.debug("NUM_ANGLES:        {}", NUM_ANGLES);

            ANGLES_COS_SIN = new double[NUM_ANGLES][N_DIMS];

            for (int i = 0; i < NUM_ANGLES; i++) {
                final double angRad = Math.toRadians(ANG_STEP * i);

                ANGLES_COS_SIN[i][0] = Math.cos(angRad);
                ANGLES_COS_SIN[i][1] = Math.sin(angRad);
            }
        }

        /* members */
        private int numIter = -1;
        /** samples (re,im) */
        private final double[][] samples = new double[N_DIMS][N_SAMPLES];
        /** moments */
        private final double[] qualityMoments = new double[QUALITY_SCORE + 1];

        private ComplexDistribution() {
            super();
        }

        public boolean checkDims() {
            return (this.samples.length == N_DIMS)
                    && (this.samples[0].length == N_SAMPLES)
                    && (this.samples[1].length == N_SAMPLES)
                    && (this.qualityMoments.length == (QUALITY_SCORE + 1));
        }

        public double[][] getSamples() {
            return this.samples;
        }

        public double[] getQualityMoments() {
            return this.qualityMoments;
        }

        public double[][] getMoments() {
            // not cached, only for debugging purposes:
            final double[][] moments = new double[N_DIMS][MOMENT_KURTOSIS + 1];
            moments(this.samples[0], moments[0]);
            moments(this.samples[1], moments[1]);
            return moments;
        }

        @Override
        public String toString() {
            return "ComplexDistribution[" + this.samples[0].length + "]{"
                    + "numIter=" + numIter
                    + ", diff_mean_sq=" + qualityMoments[MOMENT_MEAN]
                    + ", diff_var=" + qualityMoments[MOMENT_VARIANCE]
                    + ", quality=" + qualityMoments[QUALITY_SCORE]
                    + "}";
        }

        private void generate(final Random random) {
            this.numIter = countIteration++ - startIter;

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

            final int N = nDistribs * RATIO_GEN;

            final ArrayList<ComplexDistribution> iterDistribs = new ArrayList<ComplexDistribution>(N);
            // keep (previous) best distribs:
            iterDistribs.addAll(distributions);

            final Comparator<ComplexDistribution> cmpQual = new Comparator<ComplexDistribution>() {
                @Override
                public int compare(ComplexDistribution d1, ComplexDistribution d2) {
                    return Double.compare(d1.getQualityMoments()[QUALITY_SCORE], d2.getQualityMoments()[QUALITY_SCORE]);
                }
            };

            ComplexDistribution d = new ComplexDistribution();

            int i = 1;
            double minq = Double.MAX_VALUE;
            double lastq = Double.MAX_VALUE;

            Random random = null;
            int nStale = 0;

            do {
                // Iteration: prepare good distributions (first quality pass)

                do {
                    /* create a new random generator to have different seed (single thread) when needed */
                    random = createRandom(random);

                    d.generate(random);

                    if (d.test()) {
                        iterDistribs.add(d);
                        d = new ComplexDistribution();
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
                final double qLo = distributions.get(0).getQualityMoments()[QUALITY_SCORE];
                final double qHi = distributions.get(nDistribs - 1).getQualityMoments()[QUALITY_SCORE];
                final double qLa = iterDistribs.get(N - 1).getQualityMoments()[QUALITY_SCORE];

                final double ratio_q = (lastq - qHi) / lastq;

                if (logger.isDebugEnabled()) {
                    logger.debug("Iteration[{}] qLo: {} qHi: {} lastq: {}  qLa: {} ratio_q: {} nStale: {}",
                            i, qLo, qHi, lastq, qLa, ratio_q, nStale);
                }

                minq = qLo;
                lastq = qHi;

                if (i >= MIN_ITER) {
                    // check stale (0.0) or low progression (below quality threshold):
                    if (ratio_q < QUALITY_THRESHOLD) {
                        if (++nStale == STALE_ITER) {
                            break;
                        }
                    }
                }

                iterDistribs.clear();
                // keep best distribs:
                iterDistribs.addAll(distributions);

            } while (i++ <= MAX_ITER);

            logger.debug("distributions: {}", distributions);

            logger.info("distributions quality: ({} - {})", minq, lastq);

            logger.info("create done: {} iterations.", (countIteration - prevIter));
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
                double sample, ratio;
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
                ratio = (mean_sq > sq_amp) ? (mean_sq / sq_amp) : (sq_amp / mean_sq);
                mean_sq_diff_acc += Math.abs(ratio - 1.0); // versus 1 (normal law)

                ratio = (variance > var_amp) ? (variance / var_amp) : (var_amp / variance);
                var_diff_acc += Math.abs(ratio - 1.0); // versus 1 (normal law)

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

            final double diff_mean2 = mean_sq_diff_acc / NUM_ANGLES;
            final double diff_var = var_diff_acc / NUM_ANGLES;

            // set quality moments:
            this.qualityMoments[MOMENT_MEAN] = diff_mean2;
            this.qualityMoments[MOMENT_VARIANCE] = diff_var;

            // overall quality = sum(squared diff mean, diff variance):
            final double score = diff_mean2 + QUALITY_SCORE_WEIGHT_VAR * diff_var;
            this.qualityMoments[QUALITY_SCORE] = score;

            final boolean good = (score < GOOD_THRESHOLD);

            if (good && log) {
                final double chi2 = mean_chi2_acc / NUM_ANGLES;
                final double mean2 = mean_sq_acc / NUM_ANGLES;
                final double variance = var_acc / NUM_ANGLES;

                final double mean = Math.sqrt(mean2);
                final double stddev = Math.sqrt(variance);

                // relative difference: delta = (x - est) / x
                final double ratio_mean = (mean2 > sq_amp) ? (mean2 / sq_amp) : (sq_amp / mean2);
                final double ratio_variance = (variance > var_amp) ? (variance / var_amp) : (var_amp / variance);

                logger.info("Sampling[" + N_SAMPLES + "]"
                        + " snr=" + snr + " (err(re,im)=" + err_dist + ") chi2=" + chi2
                        + " mean=" + mean + " norm=" + ref_amp
                        + " stddev=" + stddev + " err_norm=" + err_amp
                        + " diff_mean=" + (mean - ref_amp) + " diff_stddev=" + (stddev - err_amp)
                        + " ratio_mean=" + Math.sqrt(ratio_mean) + " ratio_stddev=" + Math.sqrt(ratio_variance)
                        + " diff_mean2=" + diff_mean2 + " diff_var=" + diff_var
                        + " max_abs_diff=" + Math.max(Math.abs(mean - ref_amp), Math.abs(stddev - err_amp))
                        + " quality=" + score
                        + " good: " + good);
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
        moments[MOMENT_MEAN] = mean;
        moments[MOMENT_VARIANCE] = variance;
        moments[MOMENT_ASYMETRY] = asymetry;
        moments[MOMENT_KURTOSIS] = kurtosis;
    }

    // --- TEST ---
    public static void main(String[] args) throws IOException {
        final boolean TEST_SUM = false;
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

                final double[][] moments = d.getMoments();
                System.out.println("moments(re): " + Arrays.toString(moments[0]));
                System.out.println("moments(im): " + Arrays.toString(moments[1]));

                final File file = new File("dist_" + N_SAMPLES + "_" + n + ".txt");
                saveComplexDistribution(file, distRe, distIm);

                // dump all test samples:
                System.out.println("quality moments: " + Arrays.toString(d.getQualityMoments()));
                d.test(data, true);
            }
        }

        final double[][] means = new double[N_DIMS][INITIAL_CAPACITY];
        final double[][] vars = new double[N_DIMS][INITIAL_CAPACITY];

        for (int n = 0; n < INITIAL_CAPACITY; n++) {
            ComplexDistribution d = StatUtils.getInstance().get();
            System.out.println("get(): " + d);

            final double[][] moments = d.getMoments();
            System.out.println("moments(re): " + Arrays.toString(moments[0]));
            System.out.println("moments(im): " + Arrays.toString(moments[1]));

            means[0][n] = moments[0][MOMENT_MEAN];
            means[1][n] = moments[1][MOMENT_MEAN];

            vars[0][n] = moments[0][MOMENT_VARIANCE];
            vars[1][n] = moments[1][MOMENT_VARIANCE];
        }

        System.out.println("moments(mean) (re): " + Arrays.toString(moments(means[0])));
        System.out.println("moments(mean) (im): " + Arrays.toString(moments(means[1])));

        System.out.println("moments(variance) (re): " + Arrays.toString(moments(vars[0])));
        System.out.println("moments(variance) (im): " + Arrays.toString(moments(vars[1])));
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
