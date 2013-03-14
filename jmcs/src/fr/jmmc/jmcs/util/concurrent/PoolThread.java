/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends {@link java.lang.Thread} to add the logging system and logs on interrupt and
 * start methods
 * 
 * @see CustomThreadFactory
 * @author Laurent BOURGES (voparis).
 */
public final class PoolThread extends Thread {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(PoolThread.class.getName());

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

    /**
     * Log and Interrupt this thread.
     */
    @Override
    public void interrupt() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("{} : interrupt", getName());
        }
        super.interrupt();
    }

    /**
     * Log and start this thread.
     */
    @Override
    public synchronized void start() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("{} : start", getName());
        }
        super.start();
    }

    /**
     * Run method overridden to add logs and clean up
     */
    @Override
    public void run() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("{} : before run()", getName());
        }
        try {
            super.run();
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("{} : after run()", getName());
            }
        }

    }
}
