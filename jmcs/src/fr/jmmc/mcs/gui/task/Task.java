/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Task.java,v 1.2 2011-02-14 17:10:26 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2011/02/04 16:21:50  mella
 * Generic Task definition (id, name, child tasks)
 *
 *
 */
package fr.jmmc.mcs.gui.task;

/**
 * This class represents a task with identifier, name and child tasks
 * @author bourgesl
 */
public final class Task
{

    /* members */
    /** task name */
    private final String name;
    /** child tasks */
    private Task[] childTasks = new Task[0];

    /**
     * Protected constructor
     * @param name task name
     */
    public Task(final String name)
    {
        this.name = name;
    }

    /**
     * Return true only if the given object is a task and task names are equals
     * @param obj other object
     * @return true only if the given object is a task and task names are equals
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * Return the hash code based on the task name
     * @return hash code based on the task name
     */
    @Override
    public int hashCode()
    {
        return (this.name != null ? this.name.hashCode() : 0);
    }

    @Override
    public String toString()
    {
        return "Task[" + getName() + ']';
    }

    /**
     * Return the task name
     * @return task name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return the array of child tasks (read-only)
     * @return child tasks
     */
    public Task[] getChildTasks()
    {
        return childTasks;
    }

    /**
     * Define the array of child tasks.
     * Only visible to this package
     * @param childTasks child tasks
     */
    void setChildTasks(final Task[] childTasks)
    {
        this.childTasks = childTasks;
    }
}
