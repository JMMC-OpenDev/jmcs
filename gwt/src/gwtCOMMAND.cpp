/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCOMMAND.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     08-Nov-2004  Created
 *
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtCOMMAND class.
 */

static char *rcsId="@(#) $Id: gwtCOMMAND.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "gwtCOMMAND.h"
#include "gwtPrivate.h"

/**
 * Class constructor
 */
gwtCOMMAND::gwtCOMMAND()
{
    logExtDbg("gwtCOMMAND::gwtCOMMAND()");
    _cbObj=NULL;
    _cbMethod=NULL;
}

/**
 * Class destructor
 */
gwtCOMMAND::~gwtCOMMAND()
{
    logExtDbg("gwtCOMMAND::~gwtCOMMAND()");
}

/*
 * Public methods
 */


/**
 * Attach a callback to the associated widget
 *
 * \param obj class instance pointer. 
 * \param method  method to be executed on condition arrival.
 *
 */
void gwtCOMMAND::AttachCB(fndOBJECT *obj, CB_METHOD method)
{
    logExtDbg("gwtCOMMAND::AttachCB()");
    _cbObj=obj;
    _cbMethod=method;
}


/**
 * Execute the associated callback if it was registered using AttachCB.
 *
 * \param userData a userData pointer.
 */
mcsCOMPL_STAT gwtCOMMAND::ExecuteCB(void * userData)
{
    logExtDbg("gwtCOMMAND::ExecuteCB()");

    if(_cbObj == NULL)
    {
        return FAILURE;
    }
    else if(_cbMethod == NULL)
    {
        return FAILURE;
    }
    else
    {
        logDebug("Calling callback");
        (_cbObj->*(_cbMethod))(userData);   
    }
    return SUCCESS;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
