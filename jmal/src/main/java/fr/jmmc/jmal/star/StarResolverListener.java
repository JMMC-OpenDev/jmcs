/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.star;

/**
 * Listener called with resolver results (error, star)
 * @author bourgesl
 */
public interface StarResolverListener {

    /**
     * Handle the star resolver result (status, error messages, stars) ...
     * @param result star resolver result
     */
    public void handleResult(final StarResolverResult result);

}
