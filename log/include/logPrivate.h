#ifndef logLOG_PRIVATE_H
#define logLOG_PRIVATE_H
/*******************************************************************************
*  JMMC Project
*  
*  "@(#) $Id: logPrivate.h,v 1.8 2004-11-10 22:07:54 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-May-2004  Created
* lafrasse  03-Aug-2004  Moved local functions logGetTimeStamp  and
*                        logGetHostName declaration in
*                        Added logManagerHostName and logManagerPortNumber to
*                        logRULE to store logManager host name and port number
*                        Added logMANAGER_DEFAULT_PORT_NUMBER constant
*                        Added logDisplayMessage and logDisplayError local
*                        error message handling functions
* lafrasse  10-Aug-2004  Moved logGetTimeStamp back in log.h
* gzins     10-Nov-2004  Renamed logDisplayMessage to logPrintErrMessage
*                        Removed logDisplayError
*
*******************************************************************************/

/**
 * \file
 * Private log module header file, holding the MODULE_NAME definition, logRULE
 * structure definition, contants and local-to-module functions declarations.
 */

/* The following piece of code alternates the linkage type to C for all
functions declared within the braces, which is necessary to use the
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

/*
 * MCS Headers 
 */
#include "mcs.h"


/*
 * Local Headers 
 */
#include "log.h"


/*
 * Constants
 */
#define MODULE_ID "log"
#define logTEXT_LEN 64

/**
 * logManager default listened network port number.
 */
#define logMANAGER_DEFAULT_PORT_NUMBER 8791

/*
 * Define logging definition structure 
 */
typedef struct {
        mcsBYTES256 logManagerHostName;
        mcsUINT32   logManagerPortNumber;
        mcsLOGICAL  log;
        mcsLOGICAL  verbose;
        logLEVEL    logLevel;
        logLEVEL    verboseLevel;
        logLEVEL    actionLevel;
        mcsLOGICAL  printDate;
        mcsLOGICAL  printFileLine;
} logRULE;

/*
 * Local Functions
 */
mcsCOMPL_STAT logGetHostName(char *, mcsUINT32);
void          logPrintErrMessage(const char *, ...);

#ifdef __cplusplus
};
#endif
  
#endif /*!logLOG_PRIVATE_H*/

/*___oOo___*/
