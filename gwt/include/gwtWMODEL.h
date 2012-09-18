#ifndef gwtWMODEL_H
#define gwtWMODEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
