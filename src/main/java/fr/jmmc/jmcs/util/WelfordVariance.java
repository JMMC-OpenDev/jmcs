package fr.jmmc.jmcs.util;

import java.util.Locale;

/**
 * Simple implementation of Welford's algorithm for
 * online-computation of the variance of a stream.
 *
 * see http://jonisalonen.com/2013/deriving-welfords-method-for-computing-variance/
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public final class WelfordVariance {

    private long nSamples;
    private double min, max, squaredError;
    private final ValueError meanValue;

    public WelfordVariance() {
        this.meanValue = new ValueError();
        reset();
    }

    public void reset() {
        nSamples = 0L;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        meanValue.reset();
        squaredError = 0.0;
    }

    public void copy(final WelfordVariance other) {
        nSamples = other.nSamples;
        min = other.min;
        max = other.max;
        meanValue.copy(other.meanValue);
        squaredError = other.squaredError;
    }

    public void add(final double x) {
        if (Double.isFinite(x)) {
            if (x < min) {
                min = x;
            }
            if (x > max) {
                max = x;
            }
            nSamples++;

            final double deltaOldMean = (x - meanValue.value);
            meanValue.add(deltaOldMean / nSamples);
            squaredError += (x - meanValue.value) * deltaOldMean;
        }
    }

    public boolean isSet() {
        return (nSamples != 0L);
    }

    public long nSamples() {
        return nSamples;
    }

    public double min() {
        if (isSet()) {
            return min;
        }
        return Double.NaN;
    }

    public double max() {
        if (isSet()) {
            return max;
        }
        return Double.NaN;
    }

    public double mean() {
        if (isSet()) {
            return meanValue.value;
        }
        return Double.NaN;
    }

    public double variance() {
        if (isSet()) {
            return squaredError / (nSamples - 1L);
        }
        return Double.NaN;
    }

    public double stddev() {
        if (isSet()) {
            return Math.sqrt(variance());
        }
        return Double.NaN;
    }

    public double rms() {
        if (isSet()) {
            return mean() + stddev();
        }
        return Double.NaN;
    }

    public double rawErrorPercent() {
        if (isSet()) {
            return (100.0 * stddev() / mean());
        }
        return Double.NaN;
    }

    public double total() {
        if (isSet()) {
            return mean() * nSamples;
        }
        return Double.NaN;
    }

    @Override
    public String toString() {
        return new StringBuilder(128)
                .append("[").append(nSamples())
                .append(": µ=").append(NumberUtils.format(mean()))
                .append(" σ=").append(NumberUtils.format(stddev()))
                .append(" (").append(NumberUtils.format(rawErrorPercent()))
                .append(" %) min=").append(NumberUtils.format(min()))
                .append(" max=").append(NumberUtils.format(max()))
                .append(" sum=").append(NumberUtils.format(total()))
                .append("]").toString();
    }

    public String toString(final boolean full) {
        return (full) ? toString() : new StringBuilder(64)
                .append("[µ=").append(NumberUtils.format(mean()))
                .append(" σ=").append(NumberUtils.format(stddev()))
                .append(" min=").append(NumberUtils.format(min()))
                .append(" max=").append(NumberUtils.format(max()))
                .append("]").toString();
    }

    public String toStringFull() {
        return String.format("[%d: µ=%.18e σ=%.18e min=%.18e max=%.18e]",
                nSamples, mean(), stddev(), min(), max());
    }

    private final static class ValueError {

        double value;
        double error;

        protected ValueError() {
            reset();
        }

        protected void reset() {
            this.value = 0.0;
            this.error = 0.0;
        }

        protected void copy(final ValueError other) {
            this.value = other.value;
            this.error = other.error;
        }

        protected void add(final double val) {
            final double y = val - error;
            final double t = value + y;
            error = (t - value) - y;
            value = t;
        }
    }

    /**
     * Unit test
     * @param args 
     */
    public static void main(String[] args) {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        double[] values;
        if (false) {
            values = new double[]{1, 2, 2, 2, 3, 3, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 8, 89, 10000, 100001, 00, 101};
            test(values);
            System.out.println("---");
        }

        final int N = 1000 * 1000;
        final int N_HALF = N / 2;

        double nHigh = 1E15;
        double nLow = 1.0;
        values = new double[N];

        for (int i = 0; i < N; i++) {
            values[i] = (i % 2 == 0) ? nHigh : nLow;
        }
        System.out.format("Expected mean  = %.18e\n", (nHigh / 2.0 + nLow / 2.0));
        test(values);
        System.out.println("---");

        nHigh = 1.0;
        nLow = 1E-15;
        values = new double[N];

        for (int i = 0; i < N; i++) {
            values[i] = (i % 2 == 0) ? nHigh : nLow;
        }
        System.out.format("Expected mean  = %.18e\n", (nHigh / 2.0 + nLow / 2.0));
        test(values);
        System.out.println("---");
    }

    private static void test(final double[] values) {
        final WelfordVariance v = new WelfordVariance();
        for (double val : values) {
            v.add(val);
        }
        System.out.format("v.mean()  =      %.18e\n", v.mean());
        System.out.format("v.stdev() =      %.18e\n", v.stddev());
        System.out.println("stats()  =       " + v.toStringFull());

        testSum(values);
    }

    private static void testSum(final double[] values) {
        System.out.format("naiveSum =       %.18e\n", naiveSum(values));
        System.out.format("kahanSum =       %.18e\n", kahanSum(values));
    }

    private static double naiveSum(double[] values) {
        final ValueError v = new ValueError();

        for (int i = 0; i < values.length; i++) {
            v.value += values[i];
        }
        return v.value;
    }

    private static double kahanSum(double[] values) {
        final ValueError v = new ValueError();

        for (int i = 0; i < values.length; i++) {
            v.add(values[i]);
        }
        return v.value;
    }
}
