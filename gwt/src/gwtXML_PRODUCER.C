/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtXML_PRODUCER.C,v 1.2 2004-11-29 14:43:43 mella Exp $"
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

static char *rcsId="@(#) $Id: gwtXML_PRODUCER.C,v 1.2 2004-11-29 14:43:43 mella Exp $"; 
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

/** 
 * Interface class that define communication with the wgtGUI.
 */
gwtXML_PRODUCER::gwtXML_PRODUCER()
{
    logExtDbg("gwtXML_PRODUCER::gwtXML_PRODUCER()");
}

/*
 * Class destructor
 */

gwtXML_PRODUCER::~gwtXML_PRODUCER()
{
    logExtDbg("gwtXML_PRODUCER::~gwtXML_PRODUCER()");
}

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
  _attachedGui->Send(data);
}


/**
 * This method must be overloaded to handle properly the events from the gui.
 *
 * \param widgetid  
 * \param data  
 *
 */
void gwtXML_PRODUCER::ReceiveFromGui(string widgetid, string data)
{
  logExtDbg("gwtXML_PRODUCER::ReceiveFromGui()");

  logWarning("The xml_producer '%s' has received a not handled message '%s'",widgetid.data(),data.data());

}

/**
 * This method is called by the gwt during the registration.
 * \param id the given id associated to this object.
 */
void gwtXML_PRODUCER::SetProducerId(string id)
{
    logExtDbg("gwtXML_PRODUCER::SetProducerId()");
}


/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
