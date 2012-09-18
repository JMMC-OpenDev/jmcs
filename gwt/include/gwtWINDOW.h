#ifndef gwtWindow_H
#define gwtWindow_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtWINDOW class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtCONTAINER.h"
#include "gwtXML_PRODUCER.h"
#include "gwtCOMMAND.h"

/*
 * Class declaration
 */
class gwtWINDOW:public gwtCONTAINER, public gwtXML_PRODUCER, public gwtCOMMAND
{
public:
   gwtWINDOW();
   gwtWINDOW(char *title);
   virtual ~gwtWINDOW();
   virtual string GetXmlBlock(mcsLOGICAL update = mcsFALSE);
   virtual void Show(void);   
   virtual void Update(void);   
   virtual void Hide(void);  
   virtual void SetWidgetId(string id);
   virtual void SetProducerId(string id);
   virtual void ReceiveFromGui(string widgetid, string data);
   virtual void SetTitle(string title);
   virtual void SetCloseCommand(string command);

protected:
private:
};


#endif /*!gwtWindow_H*/

/*___oOo___*/
