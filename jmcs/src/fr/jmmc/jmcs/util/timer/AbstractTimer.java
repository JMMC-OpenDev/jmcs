/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.timer;

/**
 * This class defines an Abstract Timer Object to have statistics on time metrics
 * 
 * @author Laurent BOURGES (voparis).
 */
public abstract class AbstractTimer {
    // Members

    /** category */
    private final String _category;
    /** unit */
    private final TimerFactory.UNIT _unit;
    /** usage counter */
    protected int _usage = 0;

    /**
     * Protected Constructor for AbstractTimer objects : use the factory pattern
     *
     * @see TimerFactory.UNIT
     * @see TimerFactory#getTimer(String)
     * @param pCategory a string representing the kind of operation
     * @param pUnit MILLI_SECONDS or NANO_SECONDS
     */
    protected AbstractTimer(final String pCategory, final TimerFactory.UNIT pUnit) {
        _category = pCategory;
        _unit = pUnit;
    }

    // ~ Methods
    // ----------------------------------------------------------------------------------------------------------
    /**
     * Add a time measure in milliseconds
     *
     * @param start t0
     * @param now t1
     * @see TimerFactory#elapsedMilliSeconds(long, long)
     */
    public final void addMilliSeconds(final long start, final long now) {
        add(TimerFactory.elapsedMilliSeconds(start, now));
    }

    /**
     * Add a time measure in nanoseconds
     *
     * @param start t0
     * @param now t1
     * @see TimerFactory#elapsedNanoSeconds(long, long)
     */
    public final void addNanoSeconds(final long start, final long now) {
        add(TimerFactory.elapsedNanoSeconds(start, now));
    }

    /**
     * Add a time value given in double precision
     *
     * @param time value to add in statistics
     */
    public abstract void add(final double time);

    /**
     * Return the category
     *
     * @return category
     */
    public final String getCategory() {
        return _category;
    }

    /**
     * Return the unit
     *
     * @return usage counter
     */
    public final TimerFactory.UNIT getUnit() {
        return _unit;
    }

    /**
     * Return the usage counter
     *
     * @return usage counter
     */
    public final int getUsage() {
        return _usage;
    }

    /**
     * Return the time statistics
     *
     * @return time statistics
     */
    public abstract StatLong getTimeStatistics();

    /**
     * Return a string representation like "Timer (#unit) [#n]"
     *
     * @return string representation
     */
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(128);
        toString(sb, true);
        return sb.toString();
    }

    /**
     * toString() implementation using string builder
     * 
     * Note: to override in child classes to append their fields
     * 
     * @param sb string builder to append to
     * @param full true to get complete information; false to get main information (shorter)
     */
    public void toString(final StringBuilder sb, final boolean full) {
        sb.append("Timer [").append(_category).append(" - ").append(_unit).append("] [").append(_usage).append("]\t");
    }
}
