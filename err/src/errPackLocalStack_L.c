/*******************************************************************************
* JMMC project
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.7  2005/06/01 13:23:49  gzins
* Changed logExtDbg to logTrace
*
* Revision 1.6  2005/02/15 08:09:35  gzins
* Added file description
*
* Revision 1.5  2005/01/27 14:11:27  gzins
* Changed errERROR to errERROR_STACK
* Added isErrUser parameter
*
* Revision 1.4  2005/01/24 14:49:18  gzins
* Used CVS log as modification history
*
* gzins     17-Jun-2004  completed implementation
* berezne   02-Jun-2004  created
*
*-----------------------------------------------------------------------------*/

/**
 * \file
 * Definition of errPackLocalStack function.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: errPackLocalStack_L.c,v 1.8 2006-01-10 14:40:39 mella Exp $"; 


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
 * Re-initialize the error structure to start a new error stack.
 * 
 * \param  error Error structure to be reset.
 */
mcsCOMPL_STAT errPackLocalStack(errERROR_STACK *error, char *buffer,
                                mcsUINT32 bufSize)
{
    mcsINT32     i;
    mcsUINT32    bufLen;

    logTrace("errPackLocalStack()");

    /* If error stack is not initialised, do it */
    if (error->thisPtr != error)
    {
        errResetLocalStack(error);
    } 

    memset(buffer, '\0', bufSize);
    bufLen = 0;
    
    /* For each error message */
    for ( i = 0; i < error->stackSize; i++)
    {
        /* Format error message */
        char log[errMSG_MAX_LEN];
        char logBuf[512];

        sprintf(logBuf,"%s %d %d %c %s",
                error->stack[i].location,
                error->stack[i].errorId,
                error->stack[i].isErrUser,
                error->stack[i].severity,
                error->stack[i].runTimePar);

        /* Check length */
        if (strlen(logBuf)  > errMSG_MAX_LEN)
        {
            logBuf[errMSG_MAX_LEN] = '\0';
        }

        memset(log, '\0', sizeof(log));
        sprintf(log,"%s - %s %s %.200s\n",
                error->stack[i].timeStamp, error->stack[i].moduleId,
                error->stack[i].procName, logBuf);

        /* Store message into buffer */
        if ((bufSize - bufLen) > strlen(log))
        {
            strncpy(&buffer[bufLen], log, strlen(log));
            bufLen += strlen(log);
        }
        else
        {
            logWarning("errStoreExtrBuffer() - buffer too small "
                       "(current size: %d  requested size: %d)",
                       bufSize, bufLen + strlen(log));
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/*___oOo___*/
