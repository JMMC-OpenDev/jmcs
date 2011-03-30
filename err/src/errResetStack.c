/*******************************************************************************
* JMMC project
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.4  2005/06/01 13:23:49  gzins
* Changed logExtDbg to logTrace
*
* Revision 1.3  2005/02/15 08:05:47  gzins
* Corrected documentation
*
* Revision 1.2  2005/01/24 14:45:09  gzins
* Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
* Used CVS log as modification history
*
* gzins     17-Jun-2004  completed implementation
* berezne   02-Jun-2004  created
*
*-----------------------------------------------------------------------------*/

/**
 * \file
 * Definition of errResetStack function.
 */
static char *rcsId __attribute__ ((unused)) = "@(#) $Id: errResetStack.c,v 1.5 2006-01-10 14:40:39 mella Exp $"; 


/* 
 * System Headers
 */
#include <stdio.h>
#include <string.h>

/*
 * Common Software Headers
 */
#include "mcs.h"
#include "log.h"

/*
 * Local Headers
 */
#include "err.h"
#include "errPrivate.h"

/**
 * Logs errors and resets the global error structure.
 *
 * It reset the error stack; i.e. removed all errors from the stack. This has to
 * be done when the error has been handled by application or it is simply
 * ignored. 
 *
 * \return mcsSUCCESS or mcsFAILURE if an error occured.
 *
 * \sa errAdd, errResetStack
 */
mcsCOMPL_STAT errResetStack()
{
    logTrace("errResetStack()");

    return (errResetLocalStack(&errGlobalStack));
}

/*___oOo___*/
