/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

/**
 * This interface refines the toString() implementation with string builder alternatives
 * @author Laurent BOURGES.
 */
public interface ToStringable {

    /**
     * toString() implementation using string builder
     * 
     * Note: to be override in child classes to append their fields
     * 
     * @param sb string builder to append to
     * @param full true to get complete information; false to get main information (shorter)
     */
    public void toString(final StringBuilder sb, final boolean full);
}
