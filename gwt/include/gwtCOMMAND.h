#ifndef gwtCOMMAND_H
#define gwtCOMMAND_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCOMMAND.h,v 1.2 2005-02-15 12:17:52 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     08-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * gwtCOMMAND class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "fnd.h"

/*
 * Class declaration
 */

/**
 * Super class for widgets which could be called back for action 
 */
class gwtCOMMAND
{

public:
    /**
     * Typedef for the callback method 
     */
    typedef mcsCOMPL_STAT (fndOBJECT::*CB_METHOD)(void *);    
    // Brief description of the constructor
    gwtCOMMAND();

    // Brief description of the destructor
    virtual ~gwtCOMMAND();

    virtual void AttachCB(fndOBJECT *obj,  CB_METHOD method);
    virtual mcsCOMPL_STAT ExecuteCB(void * userData);

protected:

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
     gwtCOMMAND(const gwtCOMMAND&);
     gwtCOMMAND& operator=(const gwtCOMMAND&);
    
     fndOBJECT *_cbObj;
     CB_METHOD _cbMethod;
};

#endif /*!gwtCOMMAND_H*/

/*___oOo___*/
