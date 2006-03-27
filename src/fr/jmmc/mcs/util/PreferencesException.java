/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesException.java,v 1.1 2006-03-27 11:59:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.mcs.util;


/**
 * PreferencesException can be thrown if some code try to instanciate Prefrences
 * twice.
 *
 */
public class PreferencesException extends Exception
{
    // Constructor only use mother class constructor.
    /**
     * Creates a new PreferencesException object.
     *
     * @param message DOCUMENT ME!
     */
    public PreferencesException(String message)
    {
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
    public PreferencesException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
