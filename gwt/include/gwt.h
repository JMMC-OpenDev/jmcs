#ifndef gwt_H
#define gwt_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwt.h,v 1.2 2004-11-30 12:51:55 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * general include file for the gui module usage.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/* will be replaced by evh.h */
#include "evhCALLBACK.h"
#include "evhCMD_CALLBACK.h"
#include "evhIOSTREAM_CALLBACK.h"
#include "evhHANDLER.h"
#include "evhKEY.h"
#include "evhCMD_KEY.h"
#include "evhIOSTREAM_KEY.h"

#include "gwtGUI.h"
#include "gwtCOMMAND.h"
#include "gwtBUTTON.h"
#include "gwtCONTAINER.h"
#include "gwtTABLE.h"
#include "gwtTEXTFIELD.h"
#include "gwtWIDGET.h"
#include "gwtWINDOW.h"
#include "gwtSEPARATOR.h"
#include "gwtMENU.h"
#include "gwtXML_PRODUCER.h"
#include "gwtMENUITEM.h"
#include "gwtSUBPANEL.h"


typedef mcsCOMPL_STAT *gwtWIDGET_LISTENER(void* userdata); 


#endif /*!gwt_H*/

/*___oOo___*/
