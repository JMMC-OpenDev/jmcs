/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtXML_PRODUCER.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     09-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtXML_PRODUCER class definition.
 */

static char *rcsId="@(#) $Id: gwtXML_PRODUCER.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
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
#include "gwtXML_PRODUCER.h"
#include "gwtGUI.h"
#include "gwtPrivate.h"

/*
 * Class constructor
 */



/*
 * Class destructor
 */



/*
 * Public methods
 */

/**
 * Attach a GUI class that will relay events. The gui will also register the
 * window as a new active window to get back widget events.
 * 
 * \param g the gui to attach.
 */
void gwtXML_PRODUCER::AttachAGui(gwtGUI * g)
{
    logExtDbg("gwtXML_PRODUCER::AttachAGui()");
    _attachedGui = g;
    g->RegisterXmlProducer(this);
}

/**
 * Send xml data to the real gwt.
 *
 * \param data the data to send.
 *
 */
void gwtXML_PRODUCER::SendXml(string data){
  logExtDbg("gwtXML_PRODUCER::SendXml()");

}


/**
 * This method must be overloaded.
 *
 * \param widgetid  
 * \param data  
 *
 */
void gwtXML_PRODUCER::DispatchGuiReturn(string widgetid, string data)
{
  logExtDbg("gwtXML_PRODUCER::DispatchGuiReturn()");

}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
