#ifndef gwtCELL_H
#define gwtCELL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtCELL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"


/*
 * Class declaration
 */

/**
 * The gwtCELL is used by gwtTABLE to manage its cells.
 * \warning DO NOT USE THIS CLASS DIRECTLY.
 */
class gwtCELL
{

public:
    // Class constructor
    gwtCELL(string textContent="");

    // Class destructor
    virtual ~gwtCELL();
    
    void SetContent(string content);
    string GetContent();
    void SetBackgroundColor(string color);
    string GetXmlBlock();
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    gwtCELL(const gwtCELL&);
    gwtCELL& operator=(const gwtCELL&);
    string _textContent;
    string _backgroundColor;
};

#endif /*!gwtCELL_H*/

/*___oOo___*/
