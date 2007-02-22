#ifndef gwtCELL_H
#define gwtCELL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCELL.h,v 1.3 2007-02-22 12:50:03 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/02/24 10:35:18  mella
 * Minor info
 *
 * Revision 1.1  2005/02/07 14:36:20  mella
 * Add Background color management for cells
 *
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
