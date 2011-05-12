/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui.task;

import fr.jmmc.mcs.gui.FeedbackReport;
import java.util.concurrent.ExecutionException;

/**
 * This class extends SwingWorker backport for Java 5 to :
 * - define related task to cancel easily the task and its child tasks
 * - simplify debugging / logging.
 * Requires library :
 * swing-worker-1.2.jar (org.jdesktop.swingworker.SwingWorker)
 *
 * @author bourgesl
 *
 * @param <T> the result type returned by this {@code TaskSwingWorker}
 */
public abstract class TaskSwingWorker<T> extends org.jdesktop.swingworker.SwingWorker<T, Void>
{

    /** Class logger */
    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            TaskSwingWorker.class.getName());
    /** flag to log debugging information */
    protected final static boolean DEBUG_FLAG = false;

    /* members */
    /** related task */
    private final Task task;
    /** log prefix using the format 'SwingWorker[" + task.name + "]" + logSuffix + "@hashcode' used by debugging statements */
    protected final String logPrefix;

    /**
     * Create a new TaskSwingWorker instance
     * @param task related task
     */
    public TaskSwingWorker(final Task task)
    {
        this(task, "");
    }
    
    /**
     * Create a new TaskSwingWorker instance
     * @param task related task
     * @param logSuffix complementary suffix for log prefix
     */
    public TaskSwingWorker(final Task task, final String logSuffix)
    {
        this.task = task;
        this.logPrefix = (DEBUG_FLAG) ? ("SwingWorker[" + task.getName() + "]" + logSuffix + "@" + Integer.toHexString(hashCode())) : "SwingWorker[" + task.getName() + "]";
    }

    /**
     * Schedules this {@code TaskSwingWorker} for execution on a <i>worker</i>
     * thread.
     * @see TaskSwingWorkerExecutor#executeTask(TaskSwingWorker)
     */
    public final void executeTask()
    {
        // increment running worker :
        TaskSwingWorkerExecutor.incRunningWorkerCounter();

        // Cancel other observability task and execute this new task :
        TaskSwingWorkerExecutor.executeTask(this);
    }

    /**
     * Return the task related to this SwingWorker
     * @return related task
     */
    public final Task getTask()
    {
        return task;
    }

    @Override
    public final String toString()
    {
        return this.logPrefix;
    }

    /**
     * Do some computation in background
     * @return data computed data
     */
    @Override
    public final T doInBackground()
    {
        if (DEBUG_FLAG) {
            logger.info(logPrefix + ".doInBackground : START");
        }

        T data = null;

        // compute the data :
        data = this.computeInBackground();

        if (isCancelled()) {
            if (DEBUG_FLAG) {
                logger.info(logPrefix + ".doInBackground : CANCELLED");
            }
            // no result if task was cancelled :
            data = null;
        } else {
            if (DEBUG_FLAG) {
                logger.info(logPrefix + ".doInBackground : DONE");
            }
        }
        return data;
    }

    /**
     * Call the refreshGUI with result if not cancelled.
     * This code is executed by the Swing Event Dispatcher thread (EDT)
     */
    @Override
    public final void done()
    {
        // check if the worker was cancelled :
        if (isCancelled()) {
            if (DEBUG_FLAG) {
                logger.info(logPrefix + ".done : CANCELLED");
            }
        } else {
            try {
                // Get the computed results :
                final T data = get();

                if (data == null) {
                    if (DEBUG_FLAG) {
                        logger.info(logPrefix + ".done : NO DATA");
                    }
                } else {
                    if (DEBUG_FLAG) {
                        logger.info(logPrefix + ".done : UI START");
                    }

                    // refresh UI with data :
                    this.refreshUI(data);

                    if (DEBUG_FLAG) {
                        logger.info(logPrefix + ".done : UI DONE");
                    }
                }

            } catch (InterruptedException ignore) {
            } catch (ExecutionException ee) {
                handleException(ee);
            }
        }
        // decrement running worker :
        TaskSwingWorkerExecutor.decRunningWorkerCounter();
    }

    /**
     * Compute operation invoked by a Worker Thread (not Swing EDT) in background
     * Called by @see #doInBackground()
     * @return computed data
     */
    public abstract T computeInBackground();

    /**
     * Refresh GUI invoked by the Swing Event Dispatcher Thread (Swing EDT)
     * Called by @see #done()
     * @param data computed data
     */
    public abstract void refreshUI(final T data);

    /**
     * Handle the execution exception that occured in the compute operation : @see #computeInBackground()
     * This default implementation opens the feedback report (modal and do not exit on close).
     *
     * @param ee execution exception
     */
    public void handleException(final ExecutionException ee)
    {
        // Show feedback report (modal and do not exit on close) :
        new FeedbackReport(true, ee.getCause());
    }
}
