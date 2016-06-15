/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

/** Service statuses */
public enum StarResolverStatus {

    /** in progress (default) */
    IN_PROGRESS,
    /** fatal network error (no connection) */
    ERROR_IO,
    /** server error */
    ERROR_SERVER,
    /** query or response parsing error */
    ERROR_PARSING,
    /** ok */
    OK

}
