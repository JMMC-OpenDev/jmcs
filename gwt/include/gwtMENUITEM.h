#ifndef gwtMENUITEM_H
#define gwtMENUITEM_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtMENUITEM.h,v 1.3 2005-02-15 12:33:49 gzins Exp $"
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
 * Declaration of gwtMENUITEM class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtMENUITEM.
 */
class gwtMENUITEM : public gwtWIDGET
{
public:
    gwtMENUITEM(string label);
    ~gwtMENUITEM();
    virtual string GetXmlBlock();
protected:

private:    
    
};




#endif /*!gwtMENUITEM_H*/

/*___oOo___*/
