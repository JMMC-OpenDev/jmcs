#ifndef mthInterp_H
#define mthInterp_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthInterp.h,v 1.1 2007-07-09 15:29:15 gluck Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
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
 * MCS header
 */
#include "mcs.h"


/* 
 * Constants definition
 */



/* 
 * Macro definition
 */



/*
 * Enumeration type definition
 */



/*
 * Structure type definition
 */



/*
 * Unions type definition
 */



/*
 * Public functions declaration
 */
mcsCOMPL_STAT mthLinInterp(const mcsINT32 nbOfCurvePoints,
                           const mcsDOUBLE * xList, 
                           const mcsDOUBLE * yList, 
                           const mcsINT32 nbOfPointsToInterp, 
                           const mcsDOUBLE * xToInterpList, 
                           mcsDOUBLE * const yInterpolatedList);



#ifdef __cplusplus
}
#endif


#endif /*!mthInterp_H*/

/*___oOo___*/
