/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: TaskRegistry.java,v 1.1 2011-02-04 16:22:31 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $ 
 *
 */
package fr.jmmc.mcs.gui.task;

/**
 * This interface defines the methods to be implemented by a task registry
 * @author bourgesl
 */
public interface TaskRegistry {

    /**
     * Return the number of tasks
     * @return number of tasks
     */
    public int getTaskCount();
}
