/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtSEPARATOR.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtSEPARATOR class definition file.
 */

static char *rcsId="@(#) $Id: gwtSEPARATOR.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
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
#include "gwtSEPARATOR.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget.
 */
gwtSEPARATOR::gwtSEPARATOR()
{
    logExtDbg("gwtSEPARATOR::gwtSEPARATOR()");
}

/*
 * Class destructor
 */
gwtSEPARATOR::~gwtSEPARATOR()
{
    logExtDbg("gwtSEPARATOR::~gwtSEPARATOR()");
}

/*
 * Public methods
 */

string gwtSEPARATOR::GetXmlBlock()
{
    logExtDbg("gwtSEPARATOR::GetXmlBlock()");
    string s("<SEPARATOR />");
    return s;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
