/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.star;

/**
 * StarResolverListener called with progress messages
 * @author bourgesl
 */
public interface StarResolverProgressListener extends StarResolverListener {

    /**
     * Handle the given progress message (using server mirror, error, done) ...
     * @param message progress message
     */
    public void handleProgressMessage(final String message);

}
