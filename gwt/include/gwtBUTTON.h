#ifndef gwtBUTTON_H
#define gwtBUTTON_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtBUTTON.h,v 1.4 2005-02-15 12:33:49 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     16-Sep-2004  Created
 *
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
