/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.runner.process;

import fr.jmmc.jmcs.util.CollectionUtils;
import fr.jmmc.jmcs.util.runner.RootContext;
import fr.jmmc.jmcs.util.runner.RunContext;

/**
 * Unix Process Job (command, buffer, process status, UNIX process wrapper)
 *
 * @author Laurent BOURGES (voparis).
 */
public final class ProcessContext extends RunContext {

    /** serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** command separator '  ' */
    private static final String DB_SEPARATOR = "  ";
    // Members
    /** Commands [UNIX command, arguments] */
    private String _command;
    /** Process status */
    private int _exitCode = -1;
    /** child UNIX process */
    private transient Process _process = null;

    /**
     * Creates a new ProcessContext object for JPA
     */
    public ProcessContext() {
        super();
    }

    /**
     * Creates a new ProcessContext object
     *
     * @param parent root context
     * @param name operation name
     * @param id job identifier
     * @param cmd command array
     */
    public ProcessContext(final RootContext parent, final String name, final Long id, final String[] cmd) {
        super(parent, name, id);

        _command = CollectionUtils.toString(CollectionUtils.asList(cmd), DB_SEPARATOR, "", "");
    }

    /**
     * this method destroys the child UNIX process
     */
    @Override
    public void kill() {
        // java process is killed => unix process is killed :
        ProcessRunner.kill(this);
    }

    /**
     * Simple toString representation : "job[id][state] duration ms. {command} - dir : workDir"
     *
     * @return "job[id][state] duration ms. {command} - dir : workDir"
     */
    @Override
    public String toString() {
        return super.toString() + " " + getCommand();
    }

    /**
     * Returns the command array
     *
     * @return command array
     */
    public String[] getCommandArray() {
        return _command.split(DB_SEPARATOR);
    }

    /**
     * Returns the command string
     *
     * @return command string
     */
    public String getCommand() {
        return _command;
    }

    /**
     * Returns the exit code or -1 if undefined
     *
     * @return exit code or -1 if undefined
     */
    public int getExitCode() {
        return _exitCode;
    }

    /**
     * Defines the exit code
     *
     * @param code exit code
     */
    void setExitCode(final int code) {
        _exitCode = code;
    }

    /**
     * Returns the UNIX Process
     *
     * @return UNIX Process
     */
    Process getProcess() {
        return _process;
    }

    /**
     * Defines the UNIX Process
     *
     * @param process UNIX Process
     */
    void setProcess(final Process process) {
        _process = process;
    }
}
