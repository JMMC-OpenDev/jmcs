#ifndef gwtWindow_H
#define gwtWindow_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWINDOW.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtWINDOW class declaration file.
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
protected:
private:
};


#endif /*!gwtWindow_H*/

/*___oOo___*/
