/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui.task;

/**
 * This class describes the Jmcs tasks associated with SwingWorker(s)
 * @author bourgesl
 */
public final class JmcsTaskRegistry extends TaskRegistry
{

    /** task registry singleton */
    private final static JmcsTaskRegistry instance;

    /* JMCS tasks */
    /** feedback Report task */
    public final static Task TASK_FEEDBACK_REPORT;

    /**
     * Static initializer to define tasks and their child tasks
     */
    static {
        // create the task registry singleton :
        instance = new JmcsTaskRegistry();

        // create tasks :
        TASK_FEEDBACK_REPORT = new Task("FeedbackReport");

        // register tasks :
        instance.addTask(TASK_FEEDBACK_REPORT);
    }

    /**
     * Singleton pattern for the registry itself
     * @return registry instance
     */
    public static TaskRegistry getInstance()
    {
        return instance;
    }

    /**
     * Protected constructor
     */
    protected JmcsTaskRegistry()
    {
        super();
    }
}
