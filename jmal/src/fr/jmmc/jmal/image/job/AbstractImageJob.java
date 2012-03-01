/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image.job;

import fr.jmmc.jmcs.util.concurrent.InterruptedJobException;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Job dedicated to image i.e. float[][] processing
 * 
 * TODO: do no more use chunks but let thread work interlaced: see FloatFFT_2D ...
 * 
 * @param <V> the result type of method <tt>call</tt>
 *
 * @author bourgesl
 */
public abstract class AbstractImageJob<V> implements Callable<V> {

    /** Class logger */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractImageJob.class.getName());
    /** default threshold = 65536 */
    public final static int DEFAULT_THRESHOLD = 256 * 256;
    /** Jmcs Parallel Job executor */
    private static final ParallelJobExecutor jobExecutor = ParallelJobExecutor.getInstance();

    /* members */
    /** job name */
    protected final String _jobName;
    /* input */
    /** data array (2D) [rows][cols] */
    protected final float[][] _array2D;
    /** image width */
    protected final int _width;
    /** image height */
    protected final int _height;
    /* job boundaries */
    /** index of first line (inclusive) */
    private final int _lineStart;
    /** index of last line (exclusive) */
    private final int _lineEnd;
    /* output */
    /** result object */
    protected final V _result;

    /**
     * Create the image Job
     *
     * @param jobName job name used when throwing an exception
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     */
    public AbstractImageJob(final String jobName, final float[][] array, final int width, final int height) {
        this(jobName, array, width, height, 0, height);
    }

    /**
     * Create the image Job
     *
     * @param jobName job name used when throwing an exception
     * @param array data array (2D)
     * @param width image width
     * @param height image height
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     */
    public AbstractImageJob(final String jobName, final float[][] array, final int width, final int height,
                            final int lineStart, final int lineEnd) {
        this._jobName = jobName;
        this._array2D = array;
        this._width = width;
        this._height = height;
        this._lineStart = lineStart;
        this._lineEnd = lineEnd;
        // define result object:
        this._result = initializeResult();
    }

    /**
     * Create the image Job given a parent job
     *
     * @param parentJob parent Job producing same result
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     */
    protected AbstractImageJob(final AbstractImageJob<V> parentJob,
                               final int lineStart, final int lineEnd) {

        this._jobName = parentJob._jobName;
        this._array2D = parentJob._array2D;
        this._width = parentJob._width;
        this._height = parentJob._height;
        this._lineStart = lineStart;
        this._lineEnd = lineEnd;
        // define result object:
        this._result = initializeResult();
    }

    /**
     * 
     * @return result object
     * @throws InterruptedJobException if the current thread is interrupted (cancelled)
     */
    @SuppressWarnings("unchecked")
    public final V forkAndJoin() throws InterruptedJobException {

        V result = null;

        // Start the computations :
        final long start = System.nanoTime();

        // Should split the computation in parts ?
        // i.e. enough big compute task ?

        if (jobExecutor.isEnabled() && shouldForkJobs()) {
            // split model image in parts for parallel threads:

            final int nJobs = jobExecutor.getMaxParallelJob();

            final AbstractImageJob<V>[] jobs = new AbstractImageJob[nJobs];

            final int end = _height;
            final int step = end / nJobs;

            int pixStart = 0;
            int pixEnd = step;
            for (int i = 0; i < nJobs; i++) {
                // ensure last job goes until lineEnd:
                jobs[i] = initializeChildJob(pixStart, ((i == (nJobs - 1)) || (pixEnd > end)) ? end : pixEnd);

                pixStart += step;
                pixEnd += step;
            }

            // execute jobs in parallel:
            final Future<V>[] futures = (Future<V>[]) jobExecutor.fork(jobs);

            logger.debug("wait for jobs to terminate ...");

            final List<V> partialResults = (List<V>) jobExecutor.join(_jobName, futures);

            merge(partialResults);

            result = _result;

        } else {
            // single processor: use this thread to compute the complete model image:
            result = call();
        }

        // fast interrupt :
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedJobException(_jobName + ": interrupted");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("compute : duration = {} ms.", 1e-6d * (System.nanoTime() - start));
        }

        return result;
    }

    /**
     * Execute the task i.e. performs computations
     * 
     * @return result object or null if interrupted
     */
    @Override
    public final V call() {
        if (logger.isDebugEnabled()) {
            logger.debug("AbstractImageJob: start [{} - {}]", _lineStart, _lineEnd);
        }
        // Copy members to local variables:
        /* input */
        final int width = _width;
        /* job boundaries */
        final int lineStart = _lineStart;
        final int lineEnd = _lineEnd;

        /** Get the current thread to check if the computation is interrupted */
        final Thread currentThread = Thread.currentThread();

        // this step indicates when the thread.isInterrupted() is called in the for loop
        final int stepInterrupt = Math.min(16, 1 + (lineEnd - lineStart) / 32);

        float[] row;
        // iterate on rows:
        for (int i, j = lineStart; j < lineEnd; j++) {
            row = _array2D[j];

            // iterate on cols:
            for (i = 0; i < width; i++) {
                processValue(i, j, row[i]);
            }

            // fast interrupt:
            if (j % stepInterrupt == 0 && currentThread.isInterrupted()) {
                logger.debug("AbstractImageJob: cancelled (vis)");
                return null;
            }
        } // line by line

        // Compute done.
        if (logger.isDebugEnabled()) {
            logger.debug("AbstractImageJob: end   [{} - {}]", _lineStart, _lineEnd);
        }
        return _result;
    }

    /**
     * Initialize a new child job
     * @param lineStart index of first line (inclusive)
     * @param lineEnd index of last line (exclusive)
     * @return child job
     */
    protected abstract AbstractImageJob<V> initializeChildJob(final int lineStart, final int lineEnd);

    /**
     * Initialize the result object (one per job)
     * @return result Object
     */
    protected abstract V initializeResult();

    /**
     * Merge partial result objects to produce the final result object
     * @param partialResults partial result objects
     */
    protected abstract void merge(final List<V> partialResults);

    /**
     * Process the given value at the given row and column index
     * 
     * @param col row index
     * @param row column index
     * @param value value at the given row and column
     */
    protected abstract void processValue(final int col, final int row, final float value);

    /**
     * Return true if the job should be forked in smaller jobs
     * @return true if the job should be forked in smaller jobs 
     */
    public boolean shouldForkJobs() {
        return _width * _height > DEFAULT_THRESHOLD;
    }
}
