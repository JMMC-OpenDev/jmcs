/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs;

/**
 * Describe current application state in its life-cycle.
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public enum ApplicationState {

    APP_BROKEN(-1),
    ENV_LIMB(0),
    ENV_BOOTSTRAP(1),
    ENV_INIT(2),
    APP_INIT(3),
    GUI_SETUP(4),
    APP_READY(5),
    APP_STOP(6),
    APP_CLEANUP(7),
    ENV_CLEANUP(8),
    APP_DEAD(9);
    // Members
    /** the numerical order of the internal progress */
    private final int _step;

    /**
     * Constructor
     * @param step the numerical order of the internal progress
     */
    ApplicationState(final int step) {
        _step = step;
    }

    /**
     * @return the internal numerical progression.
     */
    public int step() {
        return _step;
    }

    /**
     * Return true if this state is after the given state
     * @param state state to compare with
     * @return true if this state is after the given state 
     */
    public boolean after(final ApplicationState state) {
        return _step > state.step();
    }

    /**
     * Return true if this state is before the given state
     * @param state state to compare with
     * @return true if this state is before the given state 
     */
    public boolean before(final ApplicationState state) {
        return _step < state.step();
    }

    /**
     * For unit testing purpose only.
     * @param args
     */
    public static void main(String[] args) {
        for (ApplicationState s : ApplicationState.values()) {
            System.out.println("State '" + s.step() + "' = [" + s.name() + "'].");
        }
    }
}
