#ifndef gwt_H
#define gwt_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwt.h,v 1.8 2005-08-26 12:59:37 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2005/02/24 13:26:59  mella
 * Add gwtLABEL
 *
 * Revision 1.6  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     14-Sep-2004  Created
 *
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