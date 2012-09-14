#ifndef gwtSUBPANEL_H
#define gwtSUBPANEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtSUBPANEL class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtCONTAINER.h"
#include "gwtCOMMAND.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtSUBPANEL.
 */
class gwtSUBPANEL : public gwtCONTAINER, public gwtCOMMAND
{
public:
    gwtSUBPANEL();
    gwtSUBPANEL(string text);
    gwtSUBPANEL(string text, string help);
    ~gwtSUBPANEL();
    virtual string GetXmlBlock();
    void SetWidgetId(string id);
    virtual void Changed(string value);
    virtual void SetText(string text);
    
protected:

private:    
    
};

#endif /*!gwtSUBPANEL_H*/

/*___oOo___*/
