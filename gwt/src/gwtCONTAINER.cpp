/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCONTAINER.cpp,v 1.2 2005-02-15 12:25:28 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     15-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtCONTAINER class.
 * 
 */

static char *rcsId =
  "@(#) $Id: gwtCONTAINER.cpp,v 1.2 2005-02-15 12:25:28 gzins Exp $";
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
 *  Return a widgetId for the given widget.
 *
 * \param widget the widget that request a widgetId. 
 *
 *  \returns the widgetId.
 */
string gwtCONTAINER::GetNewWidgetId(gwtWIDGET *widget)
{
    /* give an id to the wigdet */
  string wid;
  ostringstream osstring;
  // append ancestor at the end of the widget id to make it different in every
  // conatiners
  osstring << "widget_" <<_children.size()<<"@"<<this;
  wid = osstring.str();
  return wid;
}

/**
 * Add the given widget into the container. The widget will get a widget id
 * to make retrieval possible and identification. 
 *
 * \param widget  The widget to add to the container
 *
 * \return mcsSUCCESS or mcsFAILURE in case of error.
 */
mcsCOMPL_STAT gwtCONTAINER::Add (gwtWIDGET * widget)
{
  logExtDbg ("gwtCONTAINER::Add()");

  string wid(GetNewWidgetId(widget));
   widget->SetWidgetId(wid);
  
  logDebug ("add new widget referenced by: %s",wid.data());
  
  _children.insert ( make_pair(wid,widget));

  return mcsSUCCESS;
}

/**
 * Add the given container into the container map. This method must be used
 * instead of Add one for every containers to make retrieval possible and identification. 
 *
 * \param container The container to add to the list of containers.
 *
 * \return mcsSUCCESS or mcsFAILURE in case of error.
 */
mcsCOMPL_STAT gwtCONTAINER::AddContainer(gwtCONTAINER * container)
{
  logExtDbg ("gwtCONTAINER::AddContainer()");
  Add(container);
  // the container should now have a widget id
  string wid = container->GetWidgetId();
  logDebug ("add new container referenced by: %s", wid.data());
  _containers.insert ( make_pair(wid,container));
  return mcsSUCCESS;
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
    if( iter != _children.end() ) 
    {
        // change
        logDebug(" widget [%s] will be updated ", widgetid.data());
        (iter->second)->Changed(data);
    }
    else
    {
        logDebug(" widget [%s] not found at this level", widgetid.data());
        gwtMAP_STRING2CONTAINER::iterator jter = _containers.begin();
        while (jter != _containers.end())
        {
            (jter->second)->DispatchGuiReturn(widgetid, data);
            jter++;
        }
    }
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
