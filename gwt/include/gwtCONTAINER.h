#ifndef gwtCONTAINER_H
#define gwtCONTAINER_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtCONTAINER.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     15-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtCONTAINER class declaration file.
 *
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


#include "gwtWIDGET.h"

#include <map>


/*
 * Class declaration
 */

/**
 * A gwtCONTAINER is a component that can contain other gui objects.
 * Widgets added to the container are stored in a list.
 */
class gwtCONTAINER: public gwtWIDGET
{
public:
    gwtCONTAINER();
    virtual ~gwtCONTAINER(){};
    virtual mcsCOMPL_STAT Add(gwtWIDGET * widget);
    virtual void SetTitle(string title);
    virtual void DispatchGuiReturn(string widgetid, string data);
    /** 
     * typedef for map of widgets
     */
    typedef map<string, gwtWIDGET *> gwtMAP_STRING2WIDGET;
protected:
    gwtMAP_STRING2WIDGET _children;
private:
};

#endif /*!gwtCONTAINER_H*/

/*___oOo___*/
