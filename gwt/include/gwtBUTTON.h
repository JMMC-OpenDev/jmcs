#ifndef gwtBUTTON_H
#define gwtBUTTON_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtBUTTON.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtBUTTON class declaration file.
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
protected:
    void SetWidgetId(string id);
private:    
};




#endif /*!gwtBUTTON_H*/

/*___oOo___*/
