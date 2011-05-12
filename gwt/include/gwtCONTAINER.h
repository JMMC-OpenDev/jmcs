#ifndef gwtCONTAINER_H
#define gwtCONTAINER_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
