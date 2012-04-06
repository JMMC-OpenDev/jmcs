/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.concurrent;

/**
 * This runtime exception indicates that the job has been interrupted i.e. the current thread is interrupted:
 * - an InterruptedException has been caught
 * - Thread.currentThread().isInterrupted() is true
 * @author bourgesl
 */
public final class InterruptedJobException extends RuntimeException {

    /** serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;

    /** 
     * Constructs a new InterruptedJobException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InterruptedJobException(final String message) {
        super(message);
    }

    /** 
     * Constructs a new InterruptedJobException with the specified message and cause
     *
     * @param message the detail message
     * @param e cause of this exception
     */
    public InterruptedJobException(final String message, final Exception e) {
        super(message, e);
    }
}
