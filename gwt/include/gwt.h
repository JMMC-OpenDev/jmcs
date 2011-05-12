#ifndef gwt_H
#define gwt_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * general include file for the gui module usage.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/* will be replaced by evh.h */
#include "evh.h"
#include "gwtErrors.h"

#include "gwtGUI.h"
#include "gwtCOMMAND.h"
#include "gwtBUTTON.h"
#include "gwtCONTAINER.h"
#include "gwtTABLE.h"
#include "gwtTEXTFIELD.h"
#include "gwtTEXTAREA.h"
#include "gwtWIDGET.h"
#include "gwtWINDOW.h"
#include "gwtSEPARATOR.h"
#include "gwtMENU.h"
#include "gwtXML_PRODUCER.h"
#include "gwtMENUITEM.h"
#include "gwtCHOICE.h"
#include "gwtCHECKBOX.h"
#include "gwtLABEL.h"
#include "gwtSUBPANEL.h"


typedef mcsCOMPL_STAT *gwtWIDGET_LISTENER(void* userdata); 


#endif /*!gwt_H*/

/*___oOo___*/
