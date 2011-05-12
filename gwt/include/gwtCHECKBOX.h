#ifndef gwtCHECKBOX_H
#define gwtCHECKBOX_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtCHECKBOX class.
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
