#ifndef gwtTEXTFIELD_H
#define gwtTEXTFIELD_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtTEXTFIELD class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtTEXTFIELD.
 */
class gwtTEXTFIELD : public gwtWIDGET
{
public:
    gwtTEXTFIELD();
    gwtTEXTFIELD(string text, string help);
    ~gwtTEXTFIELD();
    virtual string GetXmlBlock();
    virtual void SetText(string text);
    virtual string GetText();
    virtual void Changed(string value);
protected:
    virtual void SetWidgetId(string id);

private:    
    
};




#endif /*!gwtTEXTFIELD_H*/

/*___oOo___*/
