/**
 * *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 *****************************************************************************
 */
package fr.jmmc.jmcs.data.preference;

/**
 * PreferencesException can be thrown by Preferences if anything went wrong.
 *
 * @author Sylvain LAFRASSE.
 */
public class PreferencesException extends Exception {

    /**
     * default serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new PreferencesException object.
     *
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public PreferencesException(final String message) {
        super(message);
    }

    /**
     * Creates a new PreferencesException object.
     *
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public PreferencesException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
