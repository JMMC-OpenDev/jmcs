#ifndef gwtWindow_H
#define gwtWindow_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtWINDOW.h,v 1.6 2005-02-15 12:33:49 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     14-Sep-2004  Created
 *
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

/*
 * Class declaration
 */
class gwtWINDOW:public gwtCONTAINER, public gwtXML_PRODUCER
{
public:
   gwtWINDOW();
   gwtWINDOW(char *title);
   virtual ~gwtWINDOW();
   virtual string GetXmlBlock();
   virtual void Show(void);   
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
