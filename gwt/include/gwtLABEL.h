#ifndef gwtLABEL_H
#define gwtLABEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtLABEL class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtLABEL.
 */
class gwtLABEL : public gwtWIDGET
{
public:
    gwtLABEL();
    gwtLABEL(string text, string help);
    ~gwtLABEL();
    virtual string GetXmlBlock();
    virtual void SetText(string text);
    virtual string GetText();
protected:
    virtual void SetWidgetId(string id);

private:    
    
};

#endif /*!gwtLABEL_H*/

/*___oOo___*/
