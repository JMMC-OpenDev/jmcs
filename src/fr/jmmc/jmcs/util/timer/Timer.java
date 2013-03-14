/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.timer;

import fr.jmmc.jmcs.util.ToStringable;

/**
 * This class contains statistics for time metrics.
 *
 * @author Laurent BOURGES (voparis).
 */
public final class Timer extends AbstractTimer implements ToStringable {

    /** statistics for elapsed time */
    private final StatLong _monitorTime = new StatLong();

    /**
     * Protected Constructor for Timer objects : use the factory pattern
     *
     * @see TimerFactory.UNIT
     * @see TimerFactory#getTimer(String)
     * @param pCategory a string representing the kind of operation
     * @param pUnit MILLI_SECONDS or NANO_SECONDS
     */
    protected Timer(final String pCategory, final TimerFactory.UNIT pUnit) {
        super(pCategory, pUnit);
    }

    /**
     * Add a time value given in double precision
     *
     * @param time value to add in statistics
     */
    @Override
    public void add(final double time) {
        if (time > 0d) {
            _usage++;
            _monitorTime.add(time);
        }
    }

    /**
     * Return the time statistics
     *
     * @return time statistics
     */
    @Override
    public StatLong getTimeStatistics() {
        return _monitorTime;
    }

    /**
     * toString() implementation using string builder
     * 
     * Note: to override in child classes to append their fields
     * 
     * @param sb string builder to append to
     * @param full true to get complete information; false to get main information (shorter)
     */
    @Override
    public void toString(final StringBuilder sb, final boolean full) {
        super.toString(sb, full);

        if (_monitorTime.getCounter() != 0) {
            _monitorTime.toString(sb, full);
        }
    }
}
