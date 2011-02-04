/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: JmcsTaskRegistry.java,v 1.1 2011-02-04 16:23:44 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.gui.task;

/**
 * This class describes the Jmcs tasks associated to SwingWorker and their ordering
 * @author bourgesl
 */
public final class JmcsTaskRegistry implements TaskRegistry {

    /* Jmcs tasks */
    /** Observability task */
    public final static Task TASK_FEEDBACK_REPORT;
    /** task count */
    public final static int TASK_COUNT;
    /** task registry singleton */
    private final static JmcsTaskRegistry instance;

    /**
     * Static initializer to define tasks (id, name) and child tasks
     */
    static {
        int n = 0;

        // create tasks :
        TASK_FEEDBACK_REPORT = new Task(n++, "Feedback report");

        final Task[] tasks = new Task[]{
            TASK_FEEDBACK_REPORT};

        TASK_COUNT = tasks.length;

        instance = new JmcsTaskRegistry();
    }

    /**
     * Singleton pattern for the registry itself
     * @return registry instance
     */
    public static TaskRegistry getInstance() {
        return instance;
    }

    /**
     * Private constructor
     */
    private JmcsTaskRegistry() {
        // no-op
    }

    /**
     * Return the number of tasks
     * @return number of tasks
     */
    public int getTaskCount() {
        return TASK_COUNT;
    }
}
