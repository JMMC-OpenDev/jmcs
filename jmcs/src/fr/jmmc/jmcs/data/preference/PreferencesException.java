/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

/**
 * PreferencesException can be thrown if some code try to instanciate Prefrences
 * twice.
 * 
 * @author Sylvain LAFRASSE.
 */
public class PreferencesException extends Exception {

    private static final long serialVersionUID = 1L;
    // Constructor only use mother class constructor.

    /**
     * Creates a new PreferencesException object.
     *
     * @param message DOCUMENT ME!
     */
    public PreferencesException(String message) {
        super(message);
    }

    /*
     * Main constructor.
     */
    /**
     * Creates a new PreferencesException object.
     *
     * @param message DOCUMENT ME!
     * @param cause DOCUMENT ME!
     */
    public PreferencesException(String message, Throwable cause) {
        super(message, cause);
    }
}
