#ifndef gwtSEPARATOR_H
#define gwtSEPARATOR_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtSEPARATOR.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtSEPARATOR class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtSEPARATOR.
 */
class gwtSEPARATOR : public gwtWIDGET
{
public:
    gwtSEPARATOR();
    ~gwtSEPARATOR();
    virtual string GetXmlBlock();
protected:

private:    
    
};

#endif /*!gwtSEPARATOR_H*/

/*___oOo___*/
