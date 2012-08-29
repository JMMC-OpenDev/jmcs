/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.util.ArrayList;
import org.ivoa.util.CollectionUtils;
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
public final class CliStarter {

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(JnlpStarter.class.getName());
    /** application identifier for LocalLauncher */
    public final static String APP_NAME = "CliStarter";
    /** user for LocalLauncher */
    public final static String USER_NAME = "JMMC";
    /** task identifier for LocalLauncher */
    public final static String TASK_NAME = "CliStarter";

    /** Forbidden constructor */
    private CliStarter() {
    }

    /**
     * Launch the given command-line path application in background.
     * 
     * @param cliPath command-line path to launch the application (simple one without arguments only)
     * @return the job context identifier
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public static Long launch(final String cliPath) throws IllegalStateException {
        return launch(cliPath, new EmptyJobListener());
    }

    /**
     * Launch the given command-line path application in background.
     * 
     * @param cliPath command-line path to launch the application (simple one without arguments only)
     * @param jobListener job event listener (not null)
     * @return the job context identifier
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public static Long launch(final String cliPath, final JobListener jobListener) throws IllegalStateException {

        if (cliPath == null || cliPath.length() == 0) {
            throw new IllegalArgumentException("empty command-line path !");
        }
        if (jobListener == null) {
            throw new IllegalArgumentException("undefined job listener !");
        }

        _logger.info("launch: {}", cliPath);

        // Create the execution context without log file
        final RootContext jobContext = LocalLauncher.prepareMainJob(APP_NAME, USER_NAME, FileUtils.getTempDirPath(), null);

        // TODO : split command-line path to handle arguments ansd so on...
        final String[] cmd = new String[]{cliPath};
        LocalLauncher.prepareChildJob(jobContext, TASK_NAME, cmd);

        // Puts the job in the job queue (can throw IllegalStateException if job not queued)
        LocalLauncher.startJob(jobContext, jobListener);

        return jobContext.getId();
    }

    /*
    private static String[] splitCliPath(final String cliPath) {

        System.out.println("cliPath = " + cliPath);

        final String delimiters = "'\" ";// + SystemUtils.PATH_SEPARATOR;
        System.out.println("delimiters = " + delimiters);

        final ArrayList<String> splittedPath = new ArrayList<String>();

        int tokenBeginning = -1;
        int tokenEnd;
        String currentToken;
        final int lenghtMinusOne = cliPath.length() - 1;
        for (int i = 0; i < lenghtMinusOne; i++) {

            // Fix the beginning of the current token if not yet defined
            if (tokenBeginning < 0) {
                tokenBeginning = i;
                // Jump directly to next char
                continue;
            }

            // If the current char is not a delimiter (and we are not reaching the end of the path)
            final String currentChar = cliPath.substring(i, i + 1);
            if ((!delimiters.contains(currentChar)) && (i < lenghtMinusOne - 1)) {
                // Skip it
                continue;
            }

            // Current char is delimiter, or we are reaching the end of the path !
            // BUG : what about embedded delimiters "" or '' strings ???

            // If previous char is an escape sequence
            final String previousChar = cliPath.substring(i - 1, i);
            if (previousChar.equals("\\")) { // Skip escaped delimiters
                // Skip current char
                // BUG : what about reaching the end of the path ???
                continue;
            }

            // Otherwise we are at the end of a token, so memorize it
            tokenEnd = i + 2;
            currentToken = cliPath.substring(tokenBeginning, tokenEnd);
            splittedPath.add(currentToken);
            tokenBeginning = -1;
            // Restart sequence from scratch at current position for next token
        }

        return splittedPath.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] pathes = {"/usr/X11/bin/xeyes",
            "/Volumes/Backup\\ HD/Perso/Mac\\ OS\\ X\\ Install\\ DVD.dmg",
            "say \"Hello crual world !\"",
            "C:\\Program Files\\Inkscape\\inkscape.exe"};
        for (String string : pathes) {
            System.out.println("array = " + CollectionUtils.toString(splitCliPath(string)));
        }
    }
    */
}
