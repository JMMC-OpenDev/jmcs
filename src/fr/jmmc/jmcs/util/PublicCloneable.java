/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

/**
 * This interface declares a public Object.clone() method
 * @author Laurent BOURGES.
 */
public interface PublicCloneable extends Cloneable {

    /**
     * Return a "deep-copy" of this instance
     * 
     * @return "deep-copy" of this instance
     */
    public Object clone();
}
