/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtCOMMAND class.
 */


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
 * \warning Only one callback per widget can be used. Just recall this method to
 * connect a new callback.
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
        // \todo add error code
        return mcsFAILURE;
    }
    else if(_cbMethod == NULL)
    {
        // \todo add error code
        return mcsFAILURE;
    }
    else
    {
        logDebug("Calling callback");
        (_cbObj->*(_cbMethod))(userData);   
    }
    return mcsSUCCESS;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
