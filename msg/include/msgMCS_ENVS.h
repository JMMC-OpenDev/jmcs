#ifndef msgMCS_ENVS_H
#define msgMCS_ENVS_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: msgMCS_ENVS.h,v 1.2 2004-12-06 16:35:08 lafrasse Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* lafrasse  03-Dec-2004  Created
* gzins     05-Dec-2004  Changed method prototypes and class members
* gzins     06-Dec-2004  Renamed msgMCS_ENV to msgMCS_ENVS
* lafrasse  03-Dec-2004  Added GetEnvLine() method
*
*
*******************************************************************************/

/**
 * \file
 * msgMCS_ENVS class declaration.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "misc.h"

/*
 * Class declaration
 */

/**
 * Class handling MCS environment.
 *
 * The msgMCS_ENVS class contains methods to get informations (host name
 * and port number) of MCS environments. 
 * 
 * \usedfiles
 * \filename $MCSROOT/etc/mcsEnvList : file containing definition of MCS
 * environments.
 */
class msgMCS_ENVS
{

public:
    // Brief description of the constructor
    msgMCS_ENVS();

    // Brief description of the destructor
    virtual ~msgMCS_ENVS();

    virtual const char*    GetHostName   (const char *envName = NULL);
    virtual const mcsINT32 GetPortNumber (const char *envName = NULL);

protected:
    
private:
    mcsCOMPL_STAT  LoadEnvListFile       (void);
    char*          GetEnvLine            (const char *envName);
    miscDYN_BUF   _envList;           /* Internal buffer containing
                                         environment list */
    mcsLOGICAL    _envListFileLoaded; /* Flag that tell weither the file
                                         containing the environment list
                                         definition has already been loaded or
                                         not */
    mcsSTRING256  _hostName;          /* Instance member to be able to return
                                         its address putside of GetHostName() */

    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    msgMCS_ENVS(const msgMCS_ENVS&);
    msgMCS_ENVS& operator=(const msgMCS_ENVS&);
};


#endif /*!msgMCS_ENVS_H*/

/*___oOo___*/
