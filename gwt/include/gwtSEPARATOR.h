#ifndef gwtSEPARATOR_H
#define gwtSEPARATOR_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtSEPARATOR.h,v 1.3 2005-02-15 12:33:49 gzins Exp $"
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
 * Declaration of gwtSEPARATOR class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtSEPARATOR.
 */
class gwtSEPARATOR : public gwtWIDGET
{
public:
    gwtSEPARATOR();
    ~gwtSEPARATOR();
    virtual string GetXmlBlock();
protected:

private:    
    
};

#endif /*!gwtSEPARATOR_H*/

/*___oOo___*/
