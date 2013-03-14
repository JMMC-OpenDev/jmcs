/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Thread Factory for ThreadExecutors to create threads
 * 
 * @see ThreadFactory
 * @author Laurent BOURGES (voparis).
 */
public final class CustomThreadFactory implements ThreadFactory {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CustomThreadFactory.class.getName());
    // Members
    /** thread pool name */
    private final String _name;
    /** thread priority */
    private final int _priority;
    /** global thread counter */
    private final AtomicInteger _threadNumber = new AtomicInteger(1);
    /** thread name prefix */
    private final String _namePrefix;

    /**
     * Constructor with the given thread pool name and use the normal thread priority
     * 
     * @param pPoolName thread pool name
     */
    public CustomThreadFactory(final String pPoolName) {
        this(pPoolName, Thread.NORM_PRIORITY);
    }

    /**
     * Constructor with the given thread pool name and thread priority
     * 
     * @param pPoolName thread pool name
     * @param pPriority thread priority to set on created thread
     */
    public CustomThreadFactory(final String pPoolName, final int pPriority) {
        _name = pPoolName;
        _priority = pPriority;
        _namePrefix = pPoolName + "-thread-";
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Creates a new Thread (PoolThread) with the name [thread pool name]-thread-[number] and set its
     * priority
     * 
     * @param r Runnable task
     * @return new thread created
     */
    @Override
    public Thread newThread(final Runnable r) {
        _logger.debug("CustomThreadFactory.newThread : enter with task: {}", r);

        final Thread t = new PoolThread(r, _namePrefix + _threadNumber.getAndIncrement());
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != _priority) {
            t.setPriority(_priority);
        } else {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        _logger.debug("CustomThreadFactory.newThread : exit with thread {} for task: {}", t, r);
        return t;
    }

    /**
     * Return the thread pool name
     * @return thread pool name
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the thread priority (Thread.NORM_PRIORITY by default)
     * @see Thread#NORM_PRIORITY
     * @return thread priority
     */
    public int getPriority() {
        return _priority;
    }

    /**
     * Return the global thread counter
     * @return global thread counter
     */
    public AtomicInteger getThreadNumber() {
        return _threadNumber;
    }
}
