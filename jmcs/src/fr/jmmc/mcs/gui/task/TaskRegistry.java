/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: TaskRegistry.java,v 1.2 2011-02-14 17:10:50 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2011/02/04 16:22:31  mella
 * minimal interface to define Task count
 * 
 *
 */
package fr.jmmc.mcs.gui.task;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines a simple task registry (add / get tasks and define child tasks for a particular task)
 * @author bourgesl
 */
public class TaskRegistry
{

    /** Class logger */
    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            TaskRegistry.class.getName());
    /* members */
    /**registered tasks keyed by task name */
    private final Map<String, Task> registeredTasks = new HashMap<String, Task>();

    /**
     * Protected constructor
     */
    protected TaskRegistry()
    {
        // no-op
    }

    /**
     * Add the given task in the task registry
     * @param task task to add
     */
    public final void addTask(final Task task)
    {
        if (this.registeredTasks.containsKey(task.getName())) {
            logger.warning("task already registered : " + task);
        }
        this.registeredTasks.put(task.getName(), task);
    }

    /**
     * Return the task given its name (unique)
     * @param name task name
     * @return task or null if not found
     */
    public final Task getTask(final String name)
    {
        return this.registeredTasks.get(name);
    }

    /**
     * Define the child tasks of the given task
     * @param task task to modify
     * @param childTasks child tasks to set
     */
    public final void setChildTasks(final Task task, final Task[] childTasks)
    {
        task.setChildTasks(childTasks);
    }
}
