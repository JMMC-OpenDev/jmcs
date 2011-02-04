/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Task.java,v 1.1 2011-02-04 16:21:50 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 */
package fr.jmmc.mcs.gui.task;

/**
 * This class represents a task with identifier, name and child tasks
 * @author bourgesl
 */
public final class Task {

    /* members */
    /** task identifier */
    private final int id;
    /** task name */
    private final String name;
    /** child tasks */
    private Task[] childTasks = new Task[0];

    /**
     * Protected constructor
     * @param id task identifier
     * @param name task name
     */
    protected Task(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Task(" + id + ")[" + name + ']';
    }

    /**
     * Return the task identifier
     * @return task identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Return the task name
     * @return task name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the array of child tasks
     * @return child tasks
     */
    public Task[] getChildTasks() {
        return childTasks;
    }

    /**
     * Define the array of child tasks
     * @param childTasks child tasks
     */
    protected void setChildTasks(final Task[] childTasks) {
        this.childTasks = childTasks;
    }
}
