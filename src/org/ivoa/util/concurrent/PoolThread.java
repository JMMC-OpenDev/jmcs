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
    protected static Logger logB = LogUtil.getLoggerBase();

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
        if (logB.isDebugEnabled()) {
            logB.debug(getName() + " : interrupt");
        }
        super.interrupt();
    }

    /**
     * Log and start this thread.
     */
    @Override
    public synchronized void start() {
        if (logB.isDebugEnabled()) {
            logB.debug(getName() + " : start");
        }
        super.start();
    }

    /**
     * Run method overridden to add logs and clean up
     */
    @Override
    public void run() {
        if (logB.isDebugEnabled()) {
            logB.debug(getName() + " : before run() : ");
        }
        try {
            super.run();
        } finally {
            if (logB.isDebugEnabled()) {
                logB.debug(getName() + " : after run() : ");
            }
        }

    }
    // ~ End of file
    // --------------------------------------------------------------------------------------------------------
}
