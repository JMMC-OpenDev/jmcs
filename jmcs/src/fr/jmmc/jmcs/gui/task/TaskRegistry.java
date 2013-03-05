/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.task;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a simple task registry (add / get tasks and define child tasks for a particular task).
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public class TaskRegistry {

    /** Class _logger */
    protected static final Logger _logger = LoggerFactory.getLogger(TaskRegistry.class.getName());
    /* members */
    /**registered tasks keyed by task name */
    private final Map<String, Task> _registeredTasks = new HashMap<String, Task>();

    /**
     * Protected constructor
     */
    protected TaskRegistry() {
        // no-op
    }

    /**
     * Add the given task in the task registry
     * @param task task to add
     */
    public final void addTask(final Task task) {
        if (_registeredTasks.containsKey(task.getName())) {
            _logger.warn("task already registered : {}", task);
        }
        _registeredTasks.put(task.getName(), task);
    }

    /**
     * Return the task given its name (unique)
     * @param name task name
     * @return task or null if not found
     */
    public final Task getTask(final String name) {
        return _registeredTasks.get(name);
    }

    /**
     * Define the child tasks of the given task
     * @param task task to modify
     * @param childTasks child tasks to set
     */
    public final void setChildTasks(final Task task, final Task[] childTasks) {
        task.setChildTasks(childTasks);
    }
}
