#ifndef gwtCHOICE_H
#define gwtCHOICE_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtCHOICE.h,v 1.1 2004-12-01 08:57:20 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtCHOICE class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtCHOICE.
 */
class gwtCHOICE : public gwtWIDGET
{
public:
    gwtCHOICE();
    gwtCHOICE(string help);
    ~gwtCHOICE();
    virtual string GetXmlBlock();
    virtual mcsCOMPL_STAT Add(string item);

protected:
        
private:    
    vector<string> _items;
    
};




#endif /*!gwtCHOICE_H*/

/*___oOo___*/
