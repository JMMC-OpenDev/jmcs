/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWINDOW.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
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

static char *rcsId="@(#) $Id: gwtWINDOW.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
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
    s.append("<gwt_desc");
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
    s.append("</gwt_desc>\n");

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
   s.append("<gwt_update>");
   s.append("<gwt_desc variable=\"");
   s.append(GetXmlAttribute("widgetid"));
   s.append("\" variableValue=\"false\"></gwt_desc>\n</gwt_update>\n");
   SendXml(s);
}



void gwtWINDOW::SetProducerId(string id)
{
    logExtDbg("gwtWINDOW::SetProducerId()");
    SetWidgetId(id);
}

void gwtWINDOW::SetWidgetId(string id)
{
    logExtDbg("gwtWINDOW::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}

/*
 * Protected methods
 */


/*
 * Private methods
 */



/*___oOo___*/
