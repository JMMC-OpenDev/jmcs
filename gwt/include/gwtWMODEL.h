#ifndef gwtWMODEL_H
#define gwtWMODEL_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWMODEL.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtWMODEL class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtWMODEL.
 */
class gwtWMODEL : public gwtWIDGET
{
public:
    gwtWMODEL();
    gwtWMODEL(string help);
    ~gwtWMODEL();
    virtual string GetXmlBlock();
protected:

private:    
    
};




#endif /*!gwtWMODEL_H*/

/*___oOo___*/
