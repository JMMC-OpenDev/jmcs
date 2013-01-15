/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

/**
 * This interface defines the dispose() method to free resources or references to itself to
 * avoid memory leak, Observer/Observable or event listener problems
 * @author Laurent BOURGES.
 */
public interface Disposable {

    /**
     * Free any resource or reference to this instance
     */
    public void dispose();
}
