/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtBUTTON.C,v 1.3 2004-12-01 12:06:19 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtBUTTON class definition file.
 */

static char *rcsId="@(#) $Id: gwtBUTTON.C,v 1.3 2004-12-01 12:06:19 mella Exp $"; 
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
#include "gwtBUTTON.h"
#include "gwtPrivate.h"

/*
 * Class constructor
 */

/** 
 * Constructs a gwtBUTTON
 */
gwtBUTTON::gwtBUTTON()
{
    logExtDbg("gwtBUTTON::gwtBUTTON()");
}

/** 
 * Constructs a gwtBUTTON with text.
 * \param text the text of the button
 */
gwtBUTTON::gwtBUTTON(string text)
{
    logExtDbg("gwtBUTTON::gwtBUTTON()");
    SetText(text);
}

/** 
 * Constructs a gwtBUTTON with text and help
 * \param text the text of the button
 * \param help the help of the button
 */
gwtBUTTON::gwtBUTTON(string text, string help)
{
    logExtDbg("gwtBUTTON::gwtBUTTON()");
    SetText(text);
    SetHelp(help);
}

/**
 * Class destructor
 */
gwtBUTTON::~gwtBUTTON()
{
    logExtDbg("gwtBUTTON::~gwtBUTTON()");
}

/*
 * Public methods
 */

/**
 * Set the text on the button.
 * \param text the text of the button
 */
void gwtBUTTON::SetText(string text)
{
    logExtDbg("gwtBUTTON::SetText()");
    SetXmlAttribute("title",text);
}

string gwtBUTTON::GetXmlBlock()
{
    logExtDbg("gwtBUTTON::GetXmlBlock()");
    string s("<BUTTON ");
    AppendXmlAttributes(s);
    s.append("/>");
    return s;
}


void gwtBUTTON::SetWidgetId(string id)
{
    logExtDbg("gwtBUTTON::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("command",id);
}


/**
 * Called automatically each time the command widget is triggered by the client.
 *
 * \param widgetId the value of the widgetid.
 *
 */
void gwtBUTTON::Changed(string widgetId){
    logExtDbg("gwtBUTTON::Changed()");
    ExecuteCB((void*)(&widgetId));
}

/** 
 * Place the button on the window top or leave in place.
 *
 * \param flag indicates if the button must be grouped at top. 
 *
 */
void gwtBUTTON::PlaceAtTop(mcsLOGICAL flag)
{
    logExtDbg("gwtBUTTON::PlaceAtTop()");
    if(flag)
    {
        SetXmlAttribute("group","true");
    }else{
        SetXmlAttribute("group","false");
    }
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
