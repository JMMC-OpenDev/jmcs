#ifndef gwtTEXTAREA_H
#define gwtTEXTAREA_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtTEXTAREA.h,v 1.1 2004-11-30 14:36:16 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtTEXTAREA class declaration file.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtTEXTAREA.
 */
class gwtTEXTAREA : public gwtWIDGET
{
public:
    gwtTEXTAREA();
    gwtTEXTAREA(string text, int rows, int columns, string help);
    ~gwtTEXTAREA();
    virtual string GetXmlBlock();
    virtual void SetRows(int rows);
    virtual void SetColumns(int columns);
    virtual void SetText(string text);
    virtual string GetText();
    virtual void Changed(string value);
protected:
    virtual void SetWidgetId(string id);

private:    
    
};




#endif /*!gwtTEXTAREA_H*/

/*___oOo___*/
