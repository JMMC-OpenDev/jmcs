/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Fixed Thread pool executor that clears interrupted flag in afterExecute()
 * to avoid JDK 1.5 creating new threads
 *
 * @author Laurent BOURGES.
 */
public class FixedThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Create the Fixed Thread pool executor
     * @param nThreads the number of threads in the pool
     * @param threadFactory the factory to use when creating new threads
     */
    protected FixedThreadPoolExecutor(final int nThreads, final ThreadFactory threadFactory) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        // Create thread(s) now:
        prestartAllCoreThreads();
    }

    /**
     * Method invoked upon completion of execution of the given Runnable:
     * Clears interrupted flag in afterExecute() to avoid JDK 1.5 creating new threads
     *
     * @param r the runnable that has completed.
     * @param t the exception that caused termination, or null if execution
     * completed normally.
     */
    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        // clear interrupt flag:
        // this avoid JDK 1.5 ThreadPoolExecutor to kill current thread and create new threads
        Thread.interrupted();
    }
}
