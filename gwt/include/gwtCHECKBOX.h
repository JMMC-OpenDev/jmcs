#ifndef gwtCHECKBOX_H
#define gwtCHECKBOX_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCHECKBOX.h,v 1.2 2005-02-15 12:17:52 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * gwtCHECKBOX class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif
/* 
 * System Headers 
 */
#include <iostream>
#include <vector>
using namespace std;

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtCHECKBOX.
 */
class gwtCHECKBOX : public gwtWIDGET
{
public:
    gwtCHECKBOX();
    gwtCHECKBOX(string help);
    ~gwtCHECKBOX();
    virtual void SetWidgetId(string id);
    virtual string GetXmlBlock();
    virtual void Changed(string value);
    virtual mcsCOMPL_STAT SetValue(mcsLOGICAL flag);
    virtual mcsLOGICAL GetValue();
    
protected:
        
private:    
    std::vector<string> _items;
    mcsLOGICAL _value;
    
};




#endif /*!gwtCHECKBOX_H*/

/*___oOo___*/
