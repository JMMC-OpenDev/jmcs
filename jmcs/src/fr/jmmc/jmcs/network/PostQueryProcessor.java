/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network;

import java.io.IOException;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Generic Post query processor
 * @author lafrasse
 */
public interface PostQueryProcessor {

    /**
     * Process the given post method to define its HTTP input fields
     * @param method post method to complete
     * @throws IOException if any IO error occurs
     */
    public void process(final PostMethod method) throws IOException;
}
