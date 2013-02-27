package org.ivoa.util.timer;

import fr.jmmc.jmcs.util.NumberUtils;

/**
 * Special Timer with a threshold to separate low & high values
 *
 * @author Laurent BOURGES (voparis), Gerard LEMSON (mpe).
 */
public final class ThresholdTimer extends AbstractTimer {
    // ~ Members
    // ----------------------------------------------------------------------------------------------------------

    /**
     * Timer instance for the low values
     */
    private final Timer low;
    /**
     * Timer instance for the high values
     */
    private final Timer high;
    /**
     * High-value threshold
     */
    private final double threshold;

    // ~ Constructors
    // -----------------------------------------------------------------------------------------------------
    /**
     * Protected Constructor for ThresholdTimer objects : use the factory pattern
     *
     * @see TimerFactory.UNIT
     * @see TimerFactory#getTimer(String)
     * @param pCategory a string representing the kind of operation
     * @param pUnit MILLI_SECONDS or NANO_SECONDS
     * @param th threshold to detect an high value
     */
    protected ThresholdTimer(final String pCategory, final TimerFactory.UNIT pUnit, final double th) {
        super(pCategory, pUnit);
        low = new Timer(pCategory, pUnit);
        high = new Timer(pCategory, pUnit);
        threshold = th;
    }

    // ~ Methods
    // ----------------------------------------------------------------------------------------------------------
    /**
     * Add a time value given in double precision
     *
     * @param time value to add in statistics
     */
    @Override
    public void add(final double time) {
        if (time > 0d) {
            this.usage++;
            if (time > threshold) {
                high.add(time);
            } else {
                low.add(time);
            }
        }
    }

    /**
     * Return the Timer instance for the high values
     *
     * @return Timer instance for the high values
     */
    public Timer getTimerHigh() {
        return high;
    }

    /**
     * Return the Timer instance for the low values
     *
     * @return Timer instance for the low values
     */
    public Timer getTimerLow() {
        return low;
    }

    /**
     * Return the time statistics
     *
     * @return time statistics
     */
    @Override
    public StatLong getTimeStatistics() {
        return this.getTimerHigh().getTimeStatistics();
    }

    /**
     * toString() implementation using string builder
     * 
     * Note: to be overriden in child classes to append their fields
     * 
     * @param sb string builder to append to
     * @param full true to get complete information; false to get main information (shorter)
     */
    @Override
    public void toString(final StringBuilder sb, final boolean full) {
        super.toString(sb, full);

        sb.append("(threshold = ").append(NumberUtils.trimTo5Digits(threshold)).append(' ').append(getUnit()).append(") {\n  Low  : ");
        low.toString(sb, full);
        sb.append("\n  High : ");
        high.toString(sb, full);
        sb.append("\n}");
    }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

