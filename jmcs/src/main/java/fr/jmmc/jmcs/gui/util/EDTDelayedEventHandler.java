/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class uses java.swing.Timer to delay event processing (to defer event submitted in burst) and only execute the last given action
 *
 * @author bourgesl
 */
public final class EDTDelayedEventHandler {

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(EDTDelayedEventHandler.class.getName());

    /** component resize timer to avoid repeated calls */
    private final Timer _timer;

    private final ActionListener _listener;

    private Runnable _lastAction = null;

    /**
     * Constructor
     * @param delay milliseconds for the initial and between-event delay
     */
    public EDTDelayedEventHandler(final int delay) {
        _listener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                final Runnable action = _lastAction;

                _logger.debug("actionPerformed(EDT): {}", action);

                if (action != null) {
                    // within EDT:
                    action.run();
                }
            }
        };
        // Prepare timer:
        _timer = new Timer(delay, _listener);
        _timer.setRepeats(false);
    }

    public void cancel() {
        _logger.debug("cancel(EDT)");

        this._lastAction = null;

        // Stop timer:
        if (_timer.isRunning()) {
            _timer.stop();
        }
    }

    public void runLater(final Runnable action) {
        _logger.debug("runLater(EDT): {}", action);

        this._lastAction = action;

        // Start timer once
        if (!_timer.isRunning()) {
            _timer.start();
        } else {
            // Or restart it until there is no more resizing events for at least the timer duration
            _timer.restart();
        }
    }
}
