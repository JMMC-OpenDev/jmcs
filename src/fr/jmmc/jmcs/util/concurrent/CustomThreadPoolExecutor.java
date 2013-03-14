/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom ThreadPoolExecutor to add extensions
 * @author Laurent BOURGES (voparis).
 */
public final class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(CustomThreadPoolExecutor.class.getName());
    /** debug flag to log thread activity */
    public static final boolean DO_DEBUG = false;
    // Members
    /** thread pool name */
    private final String _name;

    /**
     * Single constructor allowed
     * 
     * @param pPoolName thread pool name
     * @param corePoolSize the number of threads to keep in the
     * pool, even if they are idle.
     * @param maximumPoolSize the maximum number of threads to allow in the
     * pool.
     * @param keepAliveTime when the number of threads is greater than
     * the core, this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     * @param unit the time unit for the keepAliveTime
     * argument.
     * @param workQueue the queue to use for holding tasks before they
     * are executed. This queue will hold only the <tt>Runnable</tt>
     * tasks submitted by the <tt>execute</tt> method.
     * @param threadFactory the factory to use when the executor
     * creates a new thread.
     */
    public CustomThreadPoolExecutor(final String pPoolName,
            final int corePoolSize,
            final int maximumPoolSize,
            final long keepAliveTime,
            final TimeUnit unit,
            final BlockingQueue<Runnable> workQueue,
            final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory);
        _name = pPoolName;
    }

    /**
     * Return the thread pool name
     * @return thread pool name
     */
    public String getPoolName() {
        return _name;
    }

    /**
     * Called before a task is run
     * @param t thread used to run the task
     * @param r runnable task
     */
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        _logger.debug("{}.beforeExecute : runnable: {}", _name, r);

        if (DO_DEBUG) {
            _logger.warn("{}.beforeExecute : runnable: {}", _name, r);
        }
    }

    /**
     * Called after the task has completed
     * @param r the runnable that has completed.
     * @param th the exception that caused termination, or null if
     * execution completed normally.
     */
    @Override
    protected void afterExecute(final Runnable r, final Throwable th) {
        if (th == null) {
            _logger.debug("{}.afterExecute : runnable: {}", _name, r);

            if (DO_DEBUG) {
                _logger.warn("{}.afterExecute : runnable: {}", _name, r);
            }
        } else {
            _logger.error("{}.afterExecute : uncaught exception: {}", _name, th);
        }
    }

    /**
     * Method invoked when the Executor has terminated.  Default
     * implementation does nothing. Note: To properly nest multiple
     * overriding, subclasses should generally invoke
     * <tt>super.terminated</tt> within this method.
     */
    @Override
    protected void terminated() {
        _logger.debug("{}.terminated.", _name);
    }
}
