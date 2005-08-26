#ifndef gwtPrivate_H
#define gwtPrivate_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtPrivate.h,v 1.5 2005-08-26 13:01:45 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     13-Sep-2004  Created
 *
 ******************************************************************************/

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

#define gwtUNINITIALIZED_WIDGET_NAME "notInitedWidget"

#ifdef __cplusplus
}
#endif


#endif /*!gwtPrivate_H*/

/*___oOo___*/
