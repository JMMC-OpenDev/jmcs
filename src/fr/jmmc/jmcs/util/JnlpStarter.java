/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import org.ivoa.util.runner.EmptyJobListener;
import org.ivoa.util.runner.JobListener;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper on http://code.google.com/p/vo-urp/ task runner.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES
 */
public final class JnlpStarter {

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(JnlpStarter.class.getName());
    /** application identifier for LocalLauncher */
    public final static String APP_NAME = "JnlpStarter";
    /** user for LocalLauncher */
    public final static String USER_NAME = "JMMC";
    /** task identifier for LocalLauncher */
    public final static String TASK_NAME = "JavaWebStart";

    /** Forbidden constructor */
    private JnlpStarter() {
    }

    /**
     * Launch the given Java WebStart application in background.
     * 
     * @param jnlpUrl jnlp url to launch the Jnlp application
     * @return the job context identifier
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public static Long launch(final String jnlpUrl) throws IllegalStateException {
        return launch(jnlpUrl, new EmptyJobListener());
    }

    /**
     * Launch the given Java WebStart application in background.
     * 
     * @param jnlpUrl jnlp url to launch the Jnlp application
     * @param jobListener job event listener (not null)
     * @return the job context identifier
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public static Long launch(final String jnlpUrl, final JobListener jobListener) throws IllegalStateException {

        if (jnlpUrl == null || jnlpUrl.length() == 0) {
            throw new IllegalArgumentException("empty JNLP url !");
        }
        if (jobListener == null) {
            throw new IllegalArgumentException("undefined job listener !");
        }

        _logger.info("launch: {}", jnlpUrl);

        // create the execution context without log file:
        final RootContext jobContext = LocalLauncher.prepareMainJob(APP_NAME, USER_NAME, FileUtils.getTempDirPath(), null);

        // command line: 'javaws -Xnosplash <jnlpUrl>'
        LocalLauncher.prepareChildJob(jobContext, TASK_NAME, new String[]{"javaws", "-verbose", "-Xnosplash", jnlpUrl});

        // puts the job in the job queue :
        // can throw IllegalStateException if job not queued :
        LocalLauncher.startJob(jobContext, jobListener);

        return jobContext.getId();
    }
}
