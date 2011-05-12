#ifndef gwtSEPARATOR_H
#define gwtSEPARATOR_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
