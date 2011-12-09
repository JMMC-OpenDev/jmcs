package org.ivoa.util.runner.process;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.ivoa.util.FileUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.concurrent.ThreadExecutors;
import org.slf4j.Logger;

/**
 * Process Runner : manages Unix process (start and kill)
 * 
 * @author laurent bourges (voparis)
 */
public final class ProcessRunner {

    /** logger */
    private static final Logger log = LogUtil.getLoggerDev();
    /** ERROR prefix */
    public final static String ERR_PREFIX = "ERROR";
    /** undefined process status */
    public final static int STATUS_UNDEFINED = -1;
    /** normal process status (ok) */
    public final static int STATUS_NORMAL = 0;
    /** interrupted process status */
    public final static int STATUS_INTERRUPTED = -100;

    /**
     * Forbidden constructor
     */
    private ProcessRunner() {
    }

    /**
     * Runs a job context (UNIX command) and redirects the STD OUT / ERR to the ring buffer associated to the given job context
     * @see StreamRedirector
     * @see RingBuffer
     * @param runCtx job context
     * @return process status (0 to 255) or -1 if undefined
     */
    public static int execute(final ProcessContext runCtx) {
        int status = STATUS_UNDEFINED;
        // params :

        final File workingDir = FileUtils.getDirectory(runCtx.getWorkingDir());
        if (workingDir == null) {
            log.error("ProcessRunner.execute : working directory does not exist : " + runCtx.getWorkingDir());
        } else {
            final String[] args = runCtx.getCommandArray();
            final RingBuffer ring = runCtx.getRing();

            if (log.isInfoEnabled()) {
                log.info("ProcessRunner.execute : starting process : " + Arrays.toString(args) + " in " + workingDir);
            }

            // initialization :
            ring.prepare();

            final StreamRedirector outputRedirect = new StreamRedirector(ring);
            final StreamRedirector errorRedirect = new StreamRedirector(ring, ERR_PREFIX);

            final long start = System.nanoTime();
            try {
                final Process process = exec(workingDir, args);
                // keep reference to allow killing process :
                runCtx.setProcess(process);

                // capture stdout :
                outputRedirect.setInputStream(process.getInputStream());
                // capture stderr :
                errorRedirect.setInputStream(process.getErrorStream());

                Future<?> outputFuture = null;
                Future<?> errorFuture = null;

                // start StreamRedirectors and place in runnable state :
                log.debug("ProcessRunner.execute : starting outputRedirect task ...");

                outputFuture = ThreadExecutors.getGenericExecutor().submit(outputRedirect);

                log.debug("ProcessRunner.execute : starting errorRedirect task ...");

                errorFuture = ThreadExecutors.getGenericExecutor().submit(errorRedirect);

                log.debug("ProcessRunner.execute : waitFor process to end ...");

                // todo use timeout to stop waiting ...
                status = process.waitFor();

                // calls thread.join to be sure that other threads finish before leaving from here :
                // note: this thread is waiting FOR EVER until stdout/stderr streams are closed 
                // by the child process itself

                log.debug("ProcessRunner.execute : join output Redirect ...");

                outputFuture.get();

                log.debug("ProcessRunner.execute : join error Redirect ...");

                errorFuture.get();

            } catch (CancellationException ce) {
                log.error("ProcessRunner.run : execution failure :", ce);
            } catch (ExecutionException ee) {
                log.error("ProcessRunner.run : execution failure :", ee);
            } catch (IllegalStateException ise) {
                log.error("ProcessRunner.execute : illegal state failure :", ise);
            } catch (InterruptedException ie) {
                // occurs when the threadpool shutdowns or interrupts the task (future.cancel) :
                if (log.isInfoEnabled()) {
                    log.info("ProcessRunner.execute : interrupted failure :", ie);
                }
                // Interrupted status :
                status = STATUS_INTERRUPTED;
            } catch (IOException ioe) {
                log.error("ProcessRunner.execute : unable to start process " + Arrays.toString(args) + " : ", ioe);
                ring.add(ERR_PREFIX, ioe.getMessage());
            } finally {
                // in all cases : 
                final double duration = (System.nanoTime() - start) / 1000000L;

                runCtx.setDuration((long) duration);
                runCtx.setExitCode(status);

                // cleanup : free process in whatever state and close streams:
                stop(runCtx, false);

                if (log.isInfoEnabled()) {
                    log.info("ProcessRunner.execute : process status : " + runCtx.getExitCode());
                }
            }
        }

        return status;
    }

    /**
     * Kill a running UNIX Process from the given job context
     * @param runCtx job context
     */
    public static void kill(final ProcessContext runCtx) {
        stop(runCtx, true);
    }

    /**
     * Kill a running UNIX Process from the given job context
     * @param runCtx job context
     * @param kill true to indicate a kill operation
     */
    private static void stop(final ProcessContext runCtx, final boolean kill) {
        final Process process = runCtx.getProcess();
        if (process != null) {
            if (kill) {
                if (log.isInfoEnabled()) {
                    log.info("ProcessRunner.stop : stop process ... " + process);
                }
            } else {
                log.debug("ProcessRunner.stop : stop process ... " + process);
            }

            // workaround to closing bugs:
            FileUtils.closeStream(process.getOutputStream());
            FileUtils.closeStream(process.getErrorStream());
            FileUtils.closeStream(process.getInputStream());

            // kills unix process & close all streams (stdin, stdout, stderr) :
            process.destroy();

            if (kill) {
                log.info("ProcessRunner.stop : process stopped.");
            } else {
                log.debug("ProcessRunner.stop : process stopped.");
            }
            // free killed process :
            runCtx.setProcess(null);
        }
    }

    /**
     * Launches a UNIX command with the given args (command is included in that array) and working directory
     * @see ProcessBuilder
     * @param workingDir process working directory
     * @param args UNIX command array (command + arguments)
     * @return UNIX Process
     * @throws java.io.IOException if the process can not be created
     */
    private static Process exec(final File workingDir, final String[] args) throws IOException {
        return new ProcessBuilder(args).directory(workingDir).start();
    }
}
