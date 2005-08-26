/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCONTAINER.cpp,v 1.3 2005-08-26 12:42:23 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/02/15 12:25:28  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
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
  "@(#) $Id: gwtCONTAINER.cpp,v 1.3 2005-08-26 12:42:23 mella Exp $";
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

string gwtCONTAINER::GetXmlBlock()
{
    logExtDbg("gwtCONTAINER::GetXmlBlock()");
    string s;
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
    return s;
}


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

  string wid(widget->GetWidgetId());
  // check if widget was previously added in one container
  if ( wid.compare(gwtUNINITIALIZED_WIDGET_NAME) == 0)
  {
    wid=GetNewWidgetId(widget);
    widget->SetWidgetId(wid);
  }
  else
  {
    logDebug("not inited widget added was %s",widget->GetXmlBlock().data());
    errAdd(gwtERR_WIDGET_ALREADY_ADDED);
    return mcsFAILURE;
  }
  logDebug ("add new widget referenced by: %s",wid.data());
  
  _children.insert ( make_pair(wid,widget));

  // If the widget is a container, add it to the container list
  if ( widget->IsContainer() == mcsTRUE )
  {
      logDebug ("add new container referenced by: %s", wid.data());
      _containers.insert ( make_pair(wid,(gwtCONTAINER *)widget));
  }

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


mcsLOGICAL gwtCONTAINER::IsContainer()
{
   return mcsTRUE;
}

/*
 * Protected methods
 */




/*
 * Private methods
 */



/*___oOo___*/
