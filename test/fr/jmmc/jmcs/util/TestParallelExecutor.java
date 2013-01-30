/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import org.ivoa.util.concurrent.ThreadExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bourgesl
 */
public class TestParallelExecutor {

    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(TestFileUtils.class.getName());

    public static void main(String[] args) {

        // invoke App method to initialize logback now:
        Bootstrapper.getApplicationState();

        /** jMCS Parallel Job executor */
        final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

        for (int nIter = 1; nIter <= 100 * 1000; nIter *= 10) {

            System.gc();
            ThreadExecutors.sleep(100l);

            final int nJobs = jobExecutor.getMaxParallelJob() * nIter;

            logger.info("Test: {} jobs");

            final long start = System.nanoTime();

            // computation tasks:
            final Runnable[] jobs = new Runnable[nJobs];

            // create tasks:
            for (int i = 0; i < nJobs; i++) {

                jobs[i] = new Runnable() {
                    @Override
                    public void run() {
                        // nothing to do
                        Thread.currentThread().isInterrupted();
                    }
                };
            }

            // execute jobs in parallel or using current thread if only one job (throws InterruptedJobException if interrupted):
            jobExecutor.forkAndJoin("TestParallelExecutor.main", jobs);

            logger.info("TestParallelExecutor.main: {} iterations - duration = {} / {} ms.", nIter, 1e-6d * (System.nanoTime() - start));
        }

        logger.info("TestParallelExecutor.main: shutdown");

        jobExecutor.shutdown();
    }
}
