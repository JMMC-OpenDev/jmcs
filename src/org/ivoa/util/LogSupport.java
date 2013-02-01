package org.ivoa.util;

import org.slf4j.Logger;

/**
 * Log Support class to manage LogUtil references and classLoader issues
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class LogSupport {

    /** 
     * Main Logger for the application
     */
    protected final static Logger logger = LogUtil.getLogger();

    /** Hidden constructor */
    protected LogSupport() {
    }
}
