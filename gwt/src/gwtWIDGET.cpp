/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtWIDGET.cpp,v 1.5 2005-03-08 14:19:01 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/02/28 12:48:31  mella
 * Implement SetWidgth and SetHeight
 *
 * Revision 1.3  2005/02/15 12:33:49  gzins
 * Updated file description
 *
 * Revision 1.2  2005/01/28 09:31:40  mella
 * Add vertical axis capability to place the widget on the full window width.
 *
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtWIDGET class.
 */

static char *rcsId="@(#) $Id: gwtWIDGET.cpp,v 1.5 2005-03-08 14:19:01 mella Exp $"; 
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
gwtWIDGET::gwtWIDGET()
{
    logExtDbg("gwtWIDGET::gwtWIDGET()");
    SetWidgetId("default");
}


/*
 * Class destructor
 */

gwtWIDGET::~gwtWIDGET()
{
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
void gwtWIDGET::SetLabel(string label)
{
   logExtDbg("gwtWIDGET::SetLabel()"); 
   SetXmlAttribute("label",label);
}

/** 
 * Associate one help string to the widget.
 * \param help the help string.
 */
void gwtWIDGET::SetHelp(string help)
{
   logExtDbg("gwtWIDGET::SetHelp()"); 
   SetXmlAttribute("help",help);
}

/**
 * Set an attribute to the widget. This method must be used only if you know
 * what are the consequences of attribute modifying. This method acts on the
 * internal object model.
 * \param name name of the attribute.
 * \param value value of the attribute.
 */
void gwtWIDGET::SetAttribute(string name, string value)
{
    logExtDbg("gwtWIDGET::SetAttribute()"); 
    SetXmlAttribute(name, value);
}


/** 
 *  Set Orientation between the real widget and its associated label.
 *
 * \param flag true indicates Vertical orientation, else Horizontal. 
 *
 */
void gwtWIDGET::SetVerticalOrientation(mcsLOGICAL flag)
{
    logExtDbg("gwtWIDGET::SetVerticalOrientation()"); 
    if ( flag==mcsTRUE )
    {
        SetXmlAttribute("axis","vertical");   
    }else{
        SetXmlAttribute("axis","horizontal");   
    }    
}


/**
 * Assign a new value to the widget.
 *
 * \param value the new value of the widget assigned by the user. 
 *
 */
void gwtWIDGET::Changed(string value)
{
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

    if( _xmlAttributes.count(name) == 0 )
    {
        logWarning("Can't get '%s' xml attribute",name.data());
        return NULL;
    }

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
        // append only if value is not empty
        if(! i->second.empty() )
        {
            s.append(i->first);
            s.append("=\"");
            s.append(i->second);
            s.append("\" ");
        }
        i++;
    }
}

/*
 * Private methods
 */



/*___oOo___*/
