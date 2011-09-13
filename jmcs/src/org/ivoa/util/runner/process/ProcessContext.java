package org.ivoa.util.runner.process;

import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.JavaUtils;

/**
 * Unix Process Job (command, buffer, process status, unix process wrapper)
 *
 * @author laurent bourges (voparis)
 */
public final class ProcessContext extends RunContext {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1L;
    /** command separator '  ' */
    private static final String DB_SEPARATOR = "  ";
    //~ Members ----------------------------------------------------------------------------------------------------------
    /**
     * Commands [unix command, arguments]
     */
    private String command;
    /**
     * Process status
     */
    private int exitCode = -1;
    /** child UNIX process */
    private Process process = null;

    //~ Constructors -----------------------------------------------------------------------------------------------------
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

        this.command = CollectionUtils.toString(JavaUtils.asList(cmd), DB_SEPARATOR, "", "");
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * this method destroys the child UNIX process
     * @see ProcessRunner#stop(ProcessContext)
     */
    @Override
    public void kill() {
        // java process is killed => unix process is killed :
        ProcessRunner.stop(this);
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
        return command.split(DB_SEPARATOR);
    }

    /**
     * Returns the command string
     *
     * @return command string
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the exit code or -1 if undefined
     *
     * @return exit code or -1 if undefined
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Defines the exit code
     *
     * @param code exit code
     */
    protected void setExitCode(final int code) {
        this.exitCode = code;
    }

    /**
     * Returns the UNIX Process
     *
     * @return UNIX Process
     */
    protected Process getProcess() {
        return process;
    }

    /**
     * Defines the UNIX Process
     *
     * @param process UNIX Process
     */
    protected void setProcess(final Process process) {
        this.process = process;
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
