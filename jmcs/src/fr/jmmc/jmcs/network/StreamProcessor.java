/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network;

import java.io.IOException;
import java.io.InputStream;

/**
 * Generic Stream processor
 * @author Laurent BOURGES.
 */
interface StreamProcessor {

    /**
     * Process the given input stream and CLOSE it anyway (try/finally)
     * @param in input stream to process
     * @throws IOException if any IO error occurs
     */
    public void process(final InputStream in) throws IOException;
}
