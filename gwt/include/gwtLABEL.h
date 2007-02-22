#ifndef gwtLABEL_H
#define gwtLABEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtLABEL.h,v 1.1 2005-02-24 10:35:04 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
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
