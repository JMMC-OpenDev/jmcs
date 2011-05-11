/*******************************************************************************
 * JMMC project
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2005/02/15 12:37:36  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.7  2005/01/28 18:50:52  gzins
 * Added CVS log as modification history
 *
 * gzins     15-Jun-2004  Created
 * lafrasse  01-Dec-2004  Added MCS environment name management
 * lafrasse  08-Dec-2004  Corrected a bug that caused an empty env. name instead
 *                        of a mcsUNKNOWN_ENV env. name when $MCSENV was reset
 *                        to "" after being used with another value
 *
 *----------------------------------------------------------------------------*/

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mcs.c,v 1.9 2006-01-10 14:40:39 mella Exp $"; 


/* 
 * System Headers
 */
#include <string.h>
#include <stdlib.h>

/* 
 * Local Headers
 */
#include "mcs.h"
#include "mcsPrivate.h"

/*
 * Local variables 
 */
static mcsPROCNAME mcsProcName = mcsUNKNOWN_PROC;
static mcsENVNAME  mcsEnvName  = mcsUNKNOWN_ENV;
/*
 * Local functions
 */
static mcsCOMPL_STAT mcsStoreProcName (const char *procName);
static mcsCOMPL_STAT mcsStoreEnvName  (const char *envName);

/**
 * \mainpage mcs : MCS services 
 * 
 * \n
 * \section description Description
 * This module is provided mcsInit() function which has to be used to register
 * process and environment to the MCS services. A process and its environment
 * have to be registered before calling a MCS services (log, msg, ...), as shown
 * in the following example.
 * 
 * \section example Code example
 * \code
 *  #include "mcs.h"
 *  ...
 *   
 *  int main(int argc, char *argv[])
 *  {
 *      if (mcsInit(argv[0]) == mcsFAILURE)
 *      {
 *          Process Error condition
 *          exit (EXIT_FAILURE);
 *      }
 *      ...
 *
 *      mcsExit();
 *
 *      exit (EXIT_SUCCESS);
 *  }
 *
 * \endcode
 */

/**
 * Initializes the MCS services.
 * It register application and environment to the MCS services. If process is
 * not identified, then default name is mcsUNKNOWN_PROC. If the $MCSENV
 * environment variable is not defined, then default environment name is
 * mcsUNKNOWN_ENV.
 *
 * \param procName name of the process.
 *
 * \warning This routine must be called ONCE, normally in the initialisation
 * phase of the applications, BEFORE any call to a MCS function (log, msg,...)
 * can be performed.
 *
 * \return mcsSUCCESS
 *
 * \sa mcsGetProcName, mcsGetEnvName.
 */
mcsCOMPL_STAT mcsInit(const mcsPROCNAME  procName)
{
    /* Store the application name */
    mcsStoreProcName(procName);

    /* Store the environment name */
    /* If the $MCS_ENV_NAME environment variable is defined */
    char* envValue = getenv("MCSENV");
    if ((envValue != NULL) && (strlen(envValue) != 0))
    {
        /* Copy the environment variable content in mcsEnvName */
        mcsStoreEnvName(envValue);
    }

    return mcsSUCCESS;
}

/**
 * Returns the process name.
 *
 * It returns the name which has been used to register to the process using
 * mcsInit().
 *
 * \return the process name.
 *
 * \sa mcsInit
 */
const char *mcsGetProcName()
{
    return ((const char*)mcsProcName);
}

/**
 * Returns the environment name.
 *
 * \return the environment name.
 *
 * \sa mcsInit
 */
const char *mcsGetEnvName()
{
    return ((const char*)mcsEnvName);
}


/**
 * Closes MCS services. 
 *
 * It performs the necessary clean up for applications making use of MCS.
 *
 * \warning Any application program, which has previously used mcsInit() must
 * call mcsExit() before terminating.
 *
 * \sa mcsInit
 */
void mcsExit()
{
    /* Store the application name */
    mcsStoreProcName(mcsUNKNOWN_PROC);
    mcsStoreEnvName(mcsUNKNOWN_ENV);
}

/*
 * Local functions
 */
/**
 * Stores the curent process name.
 *
 * Strips off from the process name the possible path, which could have been
 * specified to invoke the process.  For example  : "../bin/myProg" becomes
 * only "myProg". If the parameter given to mcsStoreProcName() is NULL, the name
 * of the process is set to mcsUNKNOWN_PROC.
 *
 * THIS FUNCTION IS FOR INTERNAL USE ONLY.
 *
 * \return mcsSUCCESS
 *
 * \sa mcsInit
 */
mcsCOMPL_STAT mcsStoreProcName (const char *procName)
{
    char *pchar, *path;
    pchar = (char *) procName;
    path  = strrchr(procName,'/');

    if (path != (char *) NULL)  
    { 
        pchar = path + 1; 
    }   

    strncpy((char *)mcsProcName, pchar, (sizeof(mcsProcName)-1));

    return mcsSUCCESS;
}

/**
 * Stores the curent environment name.
 *
 * If the parameter given to mcsStoreEnvName() is NULL, the name of the
 * environment is set to mcsUNKNOWN_ENV.
 *
 * THIS FUNCTION IS FOR INTERNAL USE ONLY.
 *
 * \return mcsSUCCESS
 *
 * \sa mcsInit
 */
mcsCOMPL_STAT mcsStoreEnvName (const char *envName)
{
    if (envName == (char *) NULL)
    {
        return mcsSUCCESS;
    }

    strncpy((char *)mcsEnvName, envName, (sizeof(mcsEnvName)-1));

    return mcsSUCCESS;
}

/*___oOo___*/