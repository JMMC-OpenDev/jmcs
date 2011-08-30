package org.ivoa.util.runner;

import java.io.Serializable;

import org.ivoa.util.runner.process.RingBuffer;

/**
 * Generic Job state (id, state, dates, duration).
 *
 * Sub classes will extend this class to add specific attributes ...
 *
 * @author laurent bourges (voparis)
 */
public class RunContext implements Serializable, Cloneable {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1L;
    //~ Members ----------------------------------------------------------------------------------------------------------
    /**
     * Job identifier
     */
    private Long id;
    /**
     * Root Context reference (No cascade at all to have unary operation)
     */
    private RootContext parent;
    /**
     * Name of this task (useful to process task events)
     */
    private String name;
    /**
     * Attribute description :
     * A description of this context / job.
     */
    private String description;
    /**
     * Job duration
     */
    private long duration = 0L;
    /**
     * Job state
     */
    private RunState state;
    /**
     * Ring Buffer for logs
     */
    private RingBuffer ring = null;

    /**
     * Creates a new RunContext object for JPA
     */
    public RunContext() {
    }

    /**
     * Creates a new RunContext object
     *
     * @param parent root context
     * @param applicationName operation name
     * @param id job identifier
     */
    public RunContext(final RootContext parent, final String applicationName, final Long id) {
        this.parent = parent;
        this.id = id;
        this.name = applicationName;
        // init :
        this.state = RunState.STATE_UNKNOWN;

        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * Clones this instance via standard java Cloneable support
     *
     * @return cloned instance
     *
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * This method can be used to release resources : clear the ring buffer
     */
    public void close() {
        if (this.ring != null) {
            this.ring.close();
        }
    }

    /**
     * this method stops the execution of that context
     */
    public void kill() {
    }

    /**
     * Returns the process working directory
     *
     * @return process working directory
     */
    public String getWorkingDir() {
        return (getParent() != null) ? getParent().getWorkingDir() : null;
    }

    /**
     * Simple toString representation : "job[id][state] duration ms. - work dir : [workingDir]"
     *
     * @return "job[id][state] duration ms. - work dir : [workingDir]"
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getId() + "][" + getState() + "] "
                + ((getDuration() > 0L) ? (" : " + getDuration() + " ms.") : "")
                + " - work dir : " + getWorkingDir();
    }

    /**
     * Simple toString representation : "job[id][state]"
     *
     * @return "job[id][state]"
     */
    public String shortString() {
        return getClass().getSimpleName() + "[" + getId() + "][" + getState() + "]";
    }

    public final RootContext getParent() {
        return parent;
    }

    /**
     * Returns the job identifier
     *
     * @return identifier
     */
    public final Long getId() {
        return id;
    }

    /**
     * Set the job identifier
     *
     * @param pId identifier
     */
    protected final void setId(final Long pId) {
        id = pId;
    }

    /**
     * Returns the job state
     *
     * @return job state
     */
    public final RunState getState() {
        return state;
    }

    public final boolean isRunning() {
        return getState() == RunState.STATE_RUNNING;
    }

    public final boolean isPending() {
        return getState() == RunState.STATE_PENDING;
    }

    /**
     * Defines the job state and corresponding date
     *
     * @param state to set
     */
    protected final void setState(final RunState state) {
        this.state = state;
    }

    /**
     * Returns the job duration in ms
     *
     * @return job duration
     */
    public final long getDuration() {
        return duration;
    }

    /**
     * Defines the job duration in ms
     *
     * @param duration to set
     */
    public final void setDuration(final long duration) {
        this.duration = duration;
    }

    /**
     * Returns the ring buffer
     *
     * @return ring buffer
     */
    public final RingBuffer getRing() {
        return ring;
    }

    /**
     * Defines the ring buffer
     *
     * @param ring buffer to set
     */
    public final void setRing(final RingBuffer ring) {
        this.ring = ring;
    }

    /**
     * Return the name of this context
     * @return name of this context
     */
    public final String getName() {
        return name;
    }

    /**
     * Return the description of this context
     * @return description of this context
     */
    public final String getDescription() {
        return description;
    }
}
