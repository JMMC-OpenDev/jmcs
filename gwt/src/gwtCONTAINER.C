/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtCONTAINER.C,v 1.2 2004-11-30 12:51:57 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     15-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtCONTAINER class definition file.
 * 
 */

static char *rcsId =
  "@(#) $Id: gwtCONTAINER.C,v 1.2 2004-11-30 12:51:57 mella Exp $";
static void *use_rcsId = ((void) &use_rcsId, (void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
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
#include "gwtCONTAINER.h"
#include "gwt.h"
#include "gwtPrivate.h"

/*
 * Class constructor
 */

/** 
 * Constructs a new gwtCONTAINER
 */
gwtCONTAINER::gwtCONTAINER ()
{
  logExtDbg ("gwtCONTAINER::gwtCONTAINER()");
}


/*
 * Class destructor
 */



/*
 * Public methods
 */

/**
 * Add the given widget into the container. The widget will get a widget id
 * to make retrieval possible and identification. 
 *
 * \param widget  The widget to add to the container
 *
 * \return SUCCESS or FAILURE in case of error.
 */
    
mcsCOMPL_STAT gwtCONTAINER::Add (gwtWIDGET * widget)
{
  logExtDbg ("gwtCONTAINER::Add()");

  /* give an id to the wigdet */
  string wid;
  ostringstream osstring;
  osstring << "widget_" <<_children.size();
  wid = osstring.str();
  // append ancestor at the end of the widget id
  wid.append("@");
  wid.append(this->GetWidgetId());
  widget->SetWidgetId(wid);
  
  logDebug ("add new widget referenced by: %s",wid.data());
  
  _children.insert ( make_pair(wid,widget));

  return SUCCESS;
}


/**
 * Set the title of the container. Actually it is always used on the window
 * objects.
 *
 * \param title The title of the container. 
 */
void gwtCONTAINER::SetTitle(string title)
{
    logExtDbg ("gwtCONTAINER::SetTitle()");
    SetXmlAttribute("title", title);
}


/**
 * Dispach the Gui return to the widgets. 
 * 
 * \param widgetid  Widget id to transmit.
 * \param data  Associated data to transmit.
 *
 */
void gwtCONTAINER::DispatchGuiReturn(string widgetid, string data)
{
    logExtDbg ("gwtCONTAINER::DispatchGuiReturn()");

    gwtMAP_STRING2WIDGET::iterator iter = _children.find(widgetid);
    if( iter != _children.end() ) {
        // change
        logDebug(" widget [%s] will be updated ", widgetid.data());
        (iter->second)->Changed(data);
    }else{
        logDebug(" widget [%s] not found ", widgetid.data());
    }

}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
