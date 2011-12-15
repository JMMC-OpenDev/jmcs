package org.ivoa.util.concurrent;

import org.ivoa.util.LogUtil;
import org.slf4j.Logger;

/**
 * This class extends {@link java.lang.Thread} to add the logging system and logs on interrupt and
 * start methods
 * 
 * @see CustomThreadFactory
 * @see org.ivoa.bean.LogSupport
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class PoolThread extends Thread {
    // ~ Constants
    // --------------------------------------------------------------------------------------------------------

    /**
     * Logger for the base framework
     * @see org.ivoa.bean.LogSupport
     */
    private static Logger logger = LogUtil.getLogger();

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Allocates a new <code>Thread</code> object.
     * 
     * @param target the object whose <code>run</code> method is called.
     * @param name the name of the new thread.
     * @see java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     */
    public PoolThread(final Runnable target, final String name) {
        super(target, name);
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Log and Interrupt this thread.
     */
    @Override
    public void interrupt() {
        if (logger.isDebugEnabled()) {
            logger.debug("{} : interrupt", getName());
        }
        super.interrupt();
    }

    /**
     * Log and start this thread.
     */
    @Override
    public synchronized void start() {
        if (logger.isDebugEnabled()) {
            logger.debug("{} : start", getName());
        }
        super.start();
    }

    /**
     * Run method overridden to add logs and clean up
     */
    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("{} : before run()", getName());
        }
        try {
            super.run();
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("{} : after run()", getName());
            }
        }

    }
    // ~ End of file
    // --------------------------------------------------------------------------------------------------------
}
