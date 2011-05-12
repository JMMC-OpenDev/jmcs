#ifndef gwtBUTTON_H
#define gwtBUTTON_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtBUTTON class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"
#include "gwtCOMMAND.h"


/*
 * Class declaration
 */

/**
 * Constructs a new gwtBUTTON.
 */
class gwtBUTTON: public gwtWIDGET, public gwtCOMMAND
{
public:
    gwtBUTTON();
    gwtBUTTON(string text);
    gwtBUTTON(string text, string help);
    virtual ~gwtBUTTON();
    virtual void SetText(string text);
    virtual string GetXmlBlock();
    virtual void Changed(string widgetId);
    virtual void PlaceAtTop(mcsLOGICAL flag);
protected:
    void SetWidgetId(string id);
private:    
};




#endif /*!gwtBUTTON_H*/

/*___oOo___*/
