/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWIDGET.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtWIDGET class definition file.
 *
 */

static char *rcsId="@(#) $Id: gwtWIDGET.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "gwt.h"
#include "gwtPrivate.h"

#include "gwtWIDGET.h"

/*
 * Class constructor
 */

/*
 * Class destructor
 */

gwtWIDGET::~gwtWIDGET(){
    logExtDbg("gwtWIDGET::~gwtWIDGET()");
}

/*
 * Public methods
 */

/** 
 * Set the label value for the widget.
 * In general, the lable is placed on the left and widgets are aligned from
 * the left border.
 * \param label the label string.
 */
void gwtWIDGET::SetLabel(string label){
   logExtDbg("gwtWIDGET::SetLabel()"); 
   SetXmlAttribute("label",label);
}

/** 
 * Associate one help string to the widget.
 * \param help the help string.
 */
void gwtWIDGET::SetHelp(string help){
   logExtDbg("gwtWIDGET::SetHelp()"); 
   SetXmlAttribute("help",help);
}

/**
 * Assign a new value to the widget.
 *
 * \param value the new value of the widget assigned by the user. 
 *
 */
void gwtWIDGET::Changed(string value){
    logExtDbg("gwtWIDGET::Changed()"); 
    
}

/**
 * Get the widget id string.
 *
 * \return the widget id.
 */
string gwtWIDGET::GetWidgetId()
{
    logExtDbg("gwtWIDGET::GetWidgetId()");
    return GetXmlAttribute("widgetid");
}

/**
 * Set the widget id associated to the widget object.
 * This method could be overloaded to use this widgetid for another use.
 *
 * \param id the widget id. 
 */
void gwtWIDGET::SetWidgetId(string id)
{
    logExtDbg("gwtWIDGET::SetWidgetId()");
    SetXmlAttribute("widgetid",id);
}



/*
 * Protected methods
 */

/**
 * Set a xml attribute pair for the widget.
 * This method erase the previous value if the attribute is already defined.
 * \param name  the attribute name.
 * \param value  the attribute value.
 * 
 */
void gwtWIDGET::SetXmlAttribute(string name, string value)
{
    logExtDbg("gwtWIDGET::SetXmlAttribute()");
    _xmlAttributes.erase(name);
    _xmlAttributes.insert(gwtNAME2VALUE::value_type(name, value));
}

/**
 * Get the xml attribute associated to the key name.
 *
 * \param name  the attribute name.
 * \todo implements the method 
 * \return the attribute value or NULL if no such attribute name is
 * registered.
 */
string gwtWIDGET::GetXmlAttribute(string name)
{
    logExtDbg("gwtWIDGET::GetXmlAttribute()");
    gwtNAME2VALUE::iterator i = _xmlAttributes.find(name);

    return i->second;
}

/** Append the xml attribute to the given string. Attributes are appended on
 * the following form:
 * \<space\>attr1="val1"\<space\>attr2="val2"\<space\>
 * \param s the target string where to be append attributes.
 */
void gwtWIDGET::AppendXmlAttributes(string &s)
{
    logExtDbg("gwtWIDGET::AppendXmlAttribute()");
    s.append(" ");
  
    gwtNAME2VALUE::iterator i = _xmlAttributes.begin();
    while( i != _xmlAttributes.end() ) 
    {
        s.append(i->first);
        s.append("=\"");
        s.append(i->second);
        s.append("\" ");
        i++;
    }
}

/*
 * Private methods
 */



/*___oOo___*/
