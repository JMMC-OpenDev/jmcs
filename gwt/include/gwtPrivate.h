#ifndef gwtPrivate_H
#define gwtPrivate_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtPrivate.h,v 1.3 2004-11-30 12:00:22 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     13-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Brief description of the header file, which ends at this dot.
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif


/*
 * Module name
 */
#define MODULE_ID "gwt"

 

#ifdef __cplusplus
}
#endif


#endif /*!gwtPrivate_H*/

/*___oOo___*/
