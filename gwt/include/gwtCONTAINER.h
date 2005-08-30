#ifndef gwtCONTAINER_H
#define gwtCONTAINER_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCONTAINER.h,v 1.7 2005-08-30 07:34:24 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2005/08/26 13:00:52  mella
 * Add IsContainer that returns true
 * And add GetXmlBlock to make possible to add a Container to a Window
 *
 * Revision 1.5  2005/02/15 12:33:49  gzins
 * Updated file description
 *
 * Revision 1.4  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     15-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtCONTAINER class.
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
    virtual string GetXmlBlock();
    virtual string GetNewWidgetId(gwtWIDGET *widget);
    virtual mcsCOMPL_STAT Add(gwtWIDGET * widget);
    virtual mcsCOMPL_STAT AddContainer(gwtCONTAINER * container);

    virtual void DispatchGuiReturn(string widgetid, string data);
    virtual mcsLOGICAL IsContainer();
    /** 
     * typedef for map of widgets
     */
    typedef map<string, gwtWIDGET *> gwtMAP_STRING2WIDGET;
    typedef map<string, gwtCONTAINER *> gwtMAP_STRING2CONTAINER;
protected:
    /* list of contained widgets */
    gwtMAP_STRING2WIDGET _children;
    /* list of contained containers */
    gwtMAP_STRING2CONTAINER _containers;
private:
};

#endif /*!gwtCONTAINER_H*/

/*___oOo___*/
