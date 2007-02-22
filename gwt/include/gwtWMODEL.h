#ifndef gwtWMODEL_H
#define gwtWMODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtWMODEL.h,v 1.3 2005-02-15 12:33:49 gzins Exp $"
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
 * Declaration of gwtWMODEL class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtWMODEL.
 */
class gwtWMODEL : public gwtWIDGET
{
public:
    gwtWMODEL();
    gwtWMODEL(string help);
    ~gwtWMODEL();
    virtual string GetXmlBlock();
protected:

private:    
    
};




#endif /*!gwtWMODEL_H*/

/*___oOo___*/
