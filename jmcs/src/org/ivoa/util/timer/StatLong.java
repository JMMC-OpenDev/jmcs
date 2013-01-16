package org.ivoa.util.timer;

import fr.jmmc.jmcs.util.NumberUtils;
import fr.jmmc.jmcs.util.ToStringable;

/**
 * Utility Class to store statistics : accumulated, average, accumulated delta,
 * stddev
 *
 * WARNING : Synchronization for coherence must be done OUTSIDE in the calling class !
 *
 * @author Laurent BOURGES (voparis), Gerard LEMSON (mpe).
 */
public final class StatLong implements ToStringable {
    // ~ Constants
    // --------------------------------------------------------------------------------------------------------

    /** default value for the average threshold */
    public final static int DEFAULT_THRESHOLD_AVG = 5;
    /** average threshold used to start considering that the average value is correct */
    private static int THRESHOLD_AVG;
    /** stddev threshold used to start computing the standard deviation : 2 x THRESHOLD_AVG */
    private static int THRESHOLD_STDDEV;
    /**
     * Fixed Divisor for stddev : THRESHOLD_STDDEV - THRESHOLD_AVG + 1
     */
    private static int THRESHOLD_STDDEV_N;

    /**
     * Define default threshold constants
     */
    static {
        defineThreshold(DEFAULT_THRESHOLD_AVG);
    }

    /**
     * Define the occurence thresholds to compute average, standard deviation ...
     * @param thresholdAverage
     */
    public static void defineThreshold(final int thresholdAverage) {
        if (thresholdAverage > 0) {
            THRESHOLD_AVG = thresholdAverage;
            THRESHOLD_STDDEV = 2 * THRESHOLD_AVG;
            THRESHOLD_STDDEV_N = 1 + THRESHOLD_STDDEV - THRESHOLD_AVG;
        }
    }
    // ~ Members
    // ----------------------------------------------------------------------------------------------------------
    /** occurence counter */
    private int counter;
    /** accumulator */
    private double acc;
    /** average */
    private double average;
    /** minimum value */
    private double min;
    /** maximum value */
    private double max;
    /** high occurence counter */
    private int counterHigh;
    /** delta accumulator (higher values in compare to the average value) */
    private double accDeltaHigh;
    /** low occurence counter */
    private int counterLow;
    /** delta accumulator (lower values in compare to the average value) */
    private double accDeltaLow;

    // ~ Constructors
    // -----------------------------------------------------------------------------------------------------
    /**
     * Creates a new StatLong object.
     */
    public StatLong() {
        reset();
    }

    // ~ Methods
    // ----------------------------------------------------------------------------------------------------------
    /**
     * reset values
     */
    public void reset() {
        this.counter = 0;
        this.acc = 0d;
        this.average = 0d;
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        this.counterHigh = 0;
        this.accDeltaHigh = 0d;
        this.counterLow = 0;
        this.accDeltaLow = 0d;
    }

    /**
     * Merge the given statistics in this instance
     *
     * @param stat statistics to add in this instance
     */
    public void add(final StatLong stat) {
        this.counter += stat.getCounter();
        this.acc += stat.getAccumulator();
        this.average = this.acc / this.counter;
        if (stat.getMin() < this.min) {
            this.min = stat.getMin();
        }
        if (stat.getMax() > this.max) {
            this.max = stat.getMax();
        }
        this.counterHigh += stat.getCounterHigh();
        this.accDeltaHigh += stat.getDeltaAccumulatorHigh();
        this.counterLow += stat.getCounterLow();
        this.accDeltaLow += stat.getDeltaAccumulatorLow();
    }

    /**
     * Add the given value in statistics
     *
     * @param value integer value to add in statistics
     */
    public void add(final int value) {
        add((double) value);
    }

    /**
     * Add the given value in statistics
     *
     * @param value long value to add in statistics
     */
    public void add(final long value) {
        add((double) value);
    }

    /**
     * Add the given value in statistics
     *
     * @param value double value to add in statistics
     */
    public void add(final double value) {
        if (value < this.min) {
            this.min = value;
        }

        if (value > this.max) {
            this.max = value;
        }

        final int count = ++this.counter;

        this.acc += value;
        this.average = this.acc / count;

        if (count >= THRESHOLD_AVG) {
            /**
             * X-       =     (1/n) * Sum (Xn)
             * stdDev^2 = (1/(n-1)) * Sum [ (Xn - * X-)^2 ]
             */
            // the standard deviation is estimated with a clipping algorithm :
            final double delta = this.average - value;
            if (delta > 0) {
                this.counterLow++;
                // Sum of delta square :
                this.accDeltaLow += delta * delta;
            } else {
                this.counterHigh++;
                // Sum of delta square :
                this.accDeltaHigh += delta * delta;
            }
        }
    }

    /**
     * Return the occurence counter
     *
     * @return occurence counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Return the accumulator value
     *
     * @return accumulator value
     */
    public double getAccumulator() {
        return this.acc;
    }

    /**
     * Return the high occurence counter
     *
     * @return high occurence counter
     */
    public int getCounterHigh() {
        return counterHigh;
    }

    /**
     * Return the delta accumulator value
     *
     * @return delta accumulator value
     */
    public double getDeltaAccumulatorHigh() {
        return this.accDeltaHigh;
    }

    /**
     * Return the low occurence counter
     *
     * @return low occurence counter
     */
    public int getCounterLow() {
        return counterLow;
    }

    /**
     * Return the delta accumulator value
     *
     * @return delta accumulator value
     */
    public double getDeltaAccumulatorLow() {
        return this.accDeltaLow;
    }

    /**
     * Return the average value
     *
     * @return average value
     */
    public double getAverage() {
        return this.average;
    }

    /**
     * Return the minimum value
     *
     * @return minimum value
     */
    public double getMin() {
        return this.min;
    }

    /**
     * Return the maximum value
     *
     * @return maximum value
     */
    public double getMax() {
        return this.max;
    }

    /**
     * Return the standard deviation (estimated)
     *
     * @return standard deviation
     */
    public double getStdDev() {
        double stddev = 0d;
        if (this.counter >= THRESHOLD_STDDEV) {
            stddev = Math.sqrt((this.accDeltaHigh + this.accDeltaLow) / (this.counter - THRESHOLD_STDDEV_N));
        }

        return stddev;
    }

    /**
     * Return the standard deviation (estimated)
     *
     * @return standard deviation
     */
    public double getStdDevHigh() {
        double stddev = 0d;
        if (this.counterHigh >= THRESHOLD_STDDEV) {
            stddev = Math.sqrt(this.accDeltaHigh / (this.counterHigh - THRESHOLD_STDDEV_N));
        }

        return stddev;
    }

    /**
     * Return the standard deviation (estimated)
     *
     * @return standard deviation
     */
    public double getStdDevLow() {
        double stddev = 0d;
        if (this.counterLow >= THRESHOLD_STDDEV) {
            stddev = Math.sqrt(this.accDeltaLow / (this.counterLow - THRESHOLD_STDDEV_N));
        }

        return stddev;
    }

    /**
     * toString() implementation using string builder
     * 
     * Note: to be overriden in child classes to append their fields
     * 
     * @param sb string builder to append to
     * @param full true to get complete information; false to get main information (shorter)
     */
    public void toString(final StringBuilder sb, final boolean full) {
        sb.append("{num = ").append(counter);
        sb.append(" :\tmin = ").append(NumberUtils.trimTo5Digits(min));
        sb.append(",\tavg = ").append(NumberUtils.trimTo5Digits(average));
        sb.append(",\tmax = ").append(NumberUtils.trimTo5Digits(max));

        if (full) {
            sb.append(",\tacc = ").append(NumberUtils.trimTo5Digits(acc));

            double v = getStdDev();
            if (v != 0d) {
                sb.append(",\tstd = ").append(NumberUtils.trimTo5Digits(v));
            }
            v = getStdDevLow();
            if (v != 0d) {
                sb.append(",\tstd low = ").append(NumberUtils.trimTo5Digits(v));
                sb.append(" [").append(counterLow).append(']');
            }
            v = getStdDevHigh();
            if (v != 0d) {
                sb.append(",\tstd high = ").append(NumberUtils.trimTo5Digits(v));
                sb.append(" [").append(counterHigh).append(']');
            }
        }
        sb.append('}');
    }
}
