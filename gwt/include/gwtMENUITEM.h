#ifndef gwtMENUITEM_H
#define gwtMENUITEM_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtMENUITEM.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtMENUITEM class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtMENUITEM.
 */
class gwtMENUITEM : public gwtWIDGET
{
public:
    gwtMENUITEM(string label);
    ~gwtMENUITEM();
    virtual string GetXmlBlock();
protected:

private:    
    
};




#endif /*!gwtMENUITEM_H*/

/*___oOo___*/
