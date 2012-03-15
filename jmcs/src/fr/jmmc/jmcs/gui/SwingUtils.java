/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * This class is dedicated to EDT invoke methods and simplify GUI code
 * @author bourgesl
 */
public final class SwingUtils {

    /**
     * Forbidden constructor
     */
    private SwingUtils() {
        super();
    }

    /**
     * Returns true if the current thread is the Event Dispatcher Thread (EDT)
     *
     * @return true if the current thread is the Event Dispatcher Thread (EDT)
     */
    public static boolean isEDT() {
        return SwingUtilities.isEventDispatchThread();
    }

    /**
     * Execute the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * @param runnable runnable code dedicated to Swing
     */
    public static void invokeEDT(final Runnable runnable) {
        if (isEDT()) {
            // current Thread is EDT, simply execute runnable:
            runnable.run();
        } else {
            invokeLaterEDT(runnable);
        }
    }

    /**
     * Execute LATER the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * @param runnable runnable code dedicated to Swing
     */
    public static void invokeLaterEDT(final Runnable runnable) {
        // current Thread is NOT EDT, simply invoke later runnable using EDT:
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Execute the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * And waits for completion
     * @param runnable runnable code dedicated to Swing
     * @throws IllegalStateException if any exception occurs while the given runnable code executes using EDT
     */
    public static void invokeAndWaitEDT(final Runnable runnable) throws IllegalStateException {
        if (isEDT()) {
            // current Thread is EDT, simply execute runnable:
            runnable.run();
        } else {
            try {
                // Using invokeAndWait to be in sync with the main thread :
                SwingUtilities.invokeAndWait(runnable);

            } catch (InterruptedException ie) {
                // propagate the exception :
                throw new IllegalStateException("SwingUtils.invokeAndWaitEDT : interrupted while running " + runnable, ie);
            } catch (InvocationTargetException ite) {
                // propagate the internal exception :
                throw new IllegalStateException("SwingUtils.invokeAndWaitEDT : an exception occured while running " + runnable, ite.getCause());
            }
        }
    }
}
