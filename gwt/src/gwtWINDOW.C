/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWINDOW.C,v 1.2 2004-11-29 14:43:43 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtWINDOW class definition file.
 */

static char *rcsId="@(#) $Id: gwtWINDOW.C,v 1.2 2004-11-29 14:43:43 mella Exp $"; 
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
#include "gwtWINDOW.h"
#include "gwtPrivate.h"

/*
 * Class constructor
 */
/** 
 * Creates a window.
 */
gwtWINDOW::gwtWINDOW()
{
    logExtDbg("gwtWINDOW::gwtWINDOW()");
}

/** 
 * Creates a window with a given title.
 * \param title the title of the window
 */
gwtWINDOW::gwtWINDOW(char *title)
{
    logExtDbg("gwtWINDOW::gwtWINDOW()");
    SetTitle(title);
}
/*
 * Class destructor
 */
gwtWINDOW::~gwtWINDOW()
{
    logExtDbg("gwtWINDOW::~gwtWINDOW()");
}


/*
 * Public methods
 */

string gwtWINDOW::GetXmlBlock()
{
    logExtDbg("gwtWINDOW::GetXmlBlock()");
    string s;

    // append starting tag block
    s.append("<gui_desc");
    AppendXmlAttributes(s);
    s.append(">");

    // append children content
    gwtMAP_STRING2WIDGET::iterator i = _children.begin();
    while(i != _children.end())
    {
        gwtWIDGET * tmpWidget = i->second;
        string mystring = tmpWidget->GetXmlBlock();
        if ( ! mystring.empty() )
        {
            s.append(mystring);
        }
        i++;
    }

    // append closing tag
    s.append("</gui_desc>\n");

    return s;
}

/**
 * Makes the window appear.
 * \todo replace attachedGui->send() by send().
 */
void gwtWINDOW::Show(void)
{
   logExtDbg("gwtWINDOW::Show()");
   SendXml(GetXmlBlock());
}  

/**
 * Makes the window disappear.
 * \todo implement Hide method.
 */
void gwtWINDOW::Hide(void)   
{
   logExtDbg("gwtWINDOW::Hide()");
   string s;
   s.append("<gui_update>");
   s.append("<gui_desc variable=\"");
   s.append(GetXmlAttribute("widgetid"));
   s.append("\" variableValue=\"false\"></gui_desc>\n</gui_update>\n");
   SendXml(s);
}


void gwtWINDOW::SetWidgetId(string id)
{
    logExtDbg("gwtWINDOW::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}

/**
 * This method is called by the gwt during the registration.
 * \param id the given id associated to this object.
 */
void gwtWINDOW::SetProducerId(string id)
{
    logExtDbg("gwtWINDOW::SetProducerId()");
    SetWidgetId(id);
}

/**
 * This method is automatically called by the gwtGUI for each widget events.
 *
 * \param widgetid  the affected widget id
 * \param data  the new value or empty for a command widget
 *
 */
void gwtWINDOW::ReceiveFromGui(string widgetid, string data)
{
    logExtDbg("gwtWINDOW::ReceiveFromGui()");
    DispatchGuiReturn(widgetid, data);
}



/*
 * Protected methods
 */


/*
 * Private methods
 */



/*___oOo___*/
