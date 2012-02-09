/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmcs.util.concurrent;

import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gathers one thread pool dedicated to execute parallel computation jobs
 *
 * @author bourgesl
 */
public final class ParallelJobExecutor {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ParallelJobExecutor.class.getName());
    /** singleton pattern */
    private static volatile ParallelJobExecutor instance = null;
    /* members */
    /** number of available processors */
    private final int cpuCount;
    /** maximum number of running parallel job */
    private int maxParallelJob;
    /** thread pool dedicated to this computation */
    private final ThreadPoolExecutor parallelExecutor;

    /**
     * Return the singleton instance
     *
     * @return ParallelJobExecutor instance
     */
    public static synchronized ParallelJobExecutor getInstance() {
        if (instance == null) {
            instance = new ParallelJobExecutor();
        }
        return instance;
    }

    /**
     * Shutdown the thread pool immediately.
     */
    public static synchronized void shutdown() {
        if (instance != null) {
            instance.getParallelExecutor().shutdownNow();
            instance = null;
            logger.info("ParallelJobExecutor stopped.");
        }
    }

    /**
     * Private constructor
     */
    private ParallelJobExecutor() {
        super();
        this.cpuCount = Runtime.getRuntime().availableProcessors();
        this.maxParallelJob = cpuCount;

        // create any the thread pool even if there is only 1 CPU:
        parallelExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cpuCount, new JobWorkerThreadFactory());

        // create threads now:
        parallelExecutor.prestartAllCoreThreads();

        logger.info("ParallelJobExecutor ready with {} threads", parallelExecutor.getMaximumPoolSize());
    }

    /**
     * Return true if this machine has more than 1 CPU
     *
     * @return true if this machine has more than 1 CPU
     */
    public boolean isEnabled() {
        return this.maxParallelJob > 1;
    }

    /**
     * Return the maximum number of running parallel job
     *
     * @return maximum number of running parallel job
     */
    public int getMaxParallelJob() {
        return maxParallelJob;
    }

    /**
     * Define the maximum number of running parallel job
     *
     * @param maxParallelJob maximum number of running parallel job
     */
    public void setMaxParallelJob(final int maxParallelJob) {
        this.maxParallelJob = maxParallelJob;
    }

    /**
     * Return the number of available processors
     *
     * @return number of available processors
     */
    public int getCpuCount() {
        return cpuCount;
    }

    /**
     * Return the thread pool dedicated to this computation
     *
     * @return thread pool dedicated to this computation
     */
    private ThreadPoolExecutor getParallelExecutor() {
        return parallelExecutor;
    }

    /**
     * Submit the given job to immediate execution and returns its Future object to wait for or cancel job
     *
     * @param job runnable job
     * @return Future object to wait for or cancel jobs
     */
    public Future<?> fork(final Runnable job) {
        if (job != null) {
            // start job:
            final Future<?> future = parallelExecutor.submit(job);

            logger.debug("started job: {}", future);

            return future;
        }
        // illegal state ?
        return null;
    }

    /**
     * Submit the given jobs to immediate execution and returns Future objects to wait for or cancel jobs
     *
     * @param jobs runnable jobs
     * @return Future objects to wait for or cancel jobs
     */
    public Future<?>[] fork(final Runnable[] jobs) {
        if (jobs != null) {
            final int len = jobs.length;
            final Future<?>[] futures = new Future<?>[len];

            // start jobs:
            for (int i = 0; i < len; i++) {
                final Future<?> future = parallelExecutor.submit(jobs[i]);

                logger.debug("started job: {}", future);

                futures[i] = future;
            }
            return futures;
        }
        // illegal state ?
        return null;
    }

    /**
     * Submit the given jobs to immediate execution and returns Future objects to wait for or cancel jobs
     *
     * @param jobs callable jobs i.e. jobs that return results
     * @return Future objects to wait for or cancel jobs
     */
    public Future<?>[] fork(final Callable<?>[] jobs) {
        if (jobs != null) {
            final int len = jobs.length;
            final Future<?>[] futures = new Future<?>[len];

            // start jobs:
            for (int i = 0; i < len; i++) {
                final Future<?> future = parallelExecutor.submit(jobs[i]);

                logger.debug("started job: {}", future);

                futures[i] = future;
            }
            return futures;
        }
        // illegal state ?
        return null;
    }

    /**
     * Waits for all threads to complete computation.
     * If the current thread is interrupted (cancelled), then futures are cancelled too.
     *
     * @param jobName job name used when throwing an exception
     * @param futures Future objects to wait for
     *
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     * @throws RuntimeException if any exception occured during the computation
     */
    public void join(final String jobName, final Future<?>[] futures) throws InterruptedJobException, RuntimeException {
        final int len = futures.length;

        int done = 0;
        boolean doCancel = false;
        Future<?> future;
        try {
            // Wait on running job:
            for (int j = 0; j < len; j++) {
                future = futures[j];

                logger.debug("wait for job: {}", future);

                future.get();
                done++;
            }
        } catch (ExecutionException ee) {
            doCancel = true;
            throw new RuntimeException(jobName + ": failed:", ee.getCause());
        } catch (InterruptedException ie) {
            logger.debug("join: waiting thread cancelled:", ie);
            doCancel = true;
            throw new InterruptedJobException(jobName + ": interrupted", ie);
        } finally {
            if (doCancel) {
                // Cancel and interrupt any running job:
                for (int j = done; j < len; j++) {
                    future = futures[j];

                    logger.debug("cancel job: {}", future);

                    future.cancel(true);
                }

                // Anyway: interrupt this thread again:
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Custom ThreadFactory implementation
     */
    private static final class JobWorkerThreadFactory implements ThreadFactory {

        /** thread count */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        /**
         * Constructs a new {@code Thread}.
         *
         * @param r a runnable to be executed by new thread instance
         * @return constructed thread, or {@code null} if the request to create
         * a thread is rejected
         */
        @Override
        public Thread newThread(final Runnable r) {
            final StringBuilder name = new StringBuilder("JobWorker-pool-");
            name.append(threadNumber.getAndIncrement());

            final Thread thread = new Thread(r, name.toString());
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }

            // define UncaughtExceptionHandler :
            MCSExceptionHandler.installThreadHandler(thread);

            logger.debug("new thread: {}", thread.getName());

            return thread;
        }
    }
}
