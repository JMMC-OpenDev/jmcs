/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ProcessHandler.java,v 1.1 2007-02-14 10:14:38 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.io.*;

import java.util.logging.*;


/**
 * Give access and control on one external program call. One ProcessManager
 * class can be attach to follow program activity.
 *
 */
public class ProcessHandler {
    static Logger logger = Logger.getLogger("fr.jmmc.mcs.util.ProcessHandler");
    ProcessBuilder processBuilder = null;
    Process process = null;

    /* Listening threads */
    Thread t1;

    /* Reference one observing class */
    ProcessManager manager = null;

    public ProcessHandler(String[] command) {
        processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        OutputHandler outputHandler = new OutputHandler();
        t1 = new Thread(outputHandler);
    }

    public void setProcessManager(ProcessManager manager) {
        this.manager = manager;
    }

    public void start() throws IOException {
        logger.entering("" + this.getClass(), "start");

        if (process != null) {
            logger.warning("process already started");
        } else {
            logger.info("starting new process");
            process = processBuilder.start();

            if (manager != null) {
                manager.processStarted();
            }

            // Starting listening processes
            t1.start();
        }
    }

    public int waitFor() throws InterruptedException, java.io.IOException {
        logger.entering("" + this.getClass(), "waitFor");
        logger.info("waiting for process");

        int retValue = process.waitFor();
        logger.info("process terminated (returned " + retValue + ")");

        // send EOF if requested
        OutputStream out = process.getOutputStream();
        out.flush();
        out.close();

        // wait for output data flux
        t1.join();

        if (manager != null) {
            manager.processTerminated(retValue);
        }

        return retValue;
    }

    public void stop() {
        logger.entering("" + this.getClass(), "stop");

        if (process != null) {
            // try to determine if process has terminated
            try {
                process.exitValue();
                logger.warning("process already terminated");

                // @todo: Would it be interresting to throw one exception ?
            } catch (IllegalThreadStateException e) {
                logger.info("stoping process");
                // Stoping listening processes
                t1.stop();
                process.destroy();

                if (manager != null) {
                    manager.processStoped();
                }
            }
        } else {
            logger.warning("process not yet started");
        }
    }

    public void sendToStdin(String data) throws IOException {
        logger.entering("" + this.getClass(), "sendToStdIn");

        if (process != null) {
            OutputStream out = process.getOutputStream();
            out.write(data.getBytes());
            out.flush();
        } else {
            logger.warning("process not yet started");
        }
    }

    public static void main(String[] args) {
        String usage = "Usage:" + "   jmmc.common.ProcessHandler <command>";
        System.out.println("Hello World!");

        if (args.length < 1) {
            System.out.println(usage);
            System.exit(1);
        }

        // Set Logging level
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINEST);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);

        // Run main application
        try {
            ProcessHandler pm = new ProcessHandler(args);
            pm.start();
            pm.waitFor();
            pm.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class OutputHandler implements Runnable {
        public OutputHandler() {
        }

        public void run() {
            logger.finest("listening process output started");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
            String line;

            try {
                while ((line = in.readLine()) != null) {
                    if (manager != null) {
                        //Exception exc = new Exception(line);
                        //manager.errorOccured(exc);
                        manager.outputOccured(line + "\n");
                    }
                }
            } catch (IOException e) {
                logger.finest(
                    "listening process output encountered one ioexception" +
                    e.getMessage());

                if (manager != null) {
                    manager.errorOccured(e);
                }
            }

            logger.finest("listening process output ended");
        }
    }
}
