#ifndef gwtTEXTAREA_H
#define gwtTEXTAREA_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtTEXTAREA.h,v 1.3 2005-02-15 12:33:49 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtTEXTAREA class.
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
