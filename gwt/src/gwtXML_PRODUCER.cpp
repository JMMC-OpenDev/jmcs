/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtXML_PRODUCER.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     09-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtXML_PRODUCER class.
 */

static char *rcsId="@(#) $Id: gwtXML_PRODUCER.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"; 
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
    _attachedGui=NULL;
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
  if(_attachedGui == NULL)
  {
      // \todo errAdd
      logWarning("No attached gui");
  }
  else
  {
      _attachedGui->Send(data);
  }
  
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
