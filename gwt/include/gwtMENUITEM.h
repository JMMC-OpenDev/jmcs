#ifndef gwtMENUITEM_H
#define gwtMENUITEM_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
