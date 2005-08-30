/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtSEPARATOR.cpp,v 1.2 2005-08-30 06:55:45 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtSEPARATOR class.
 */

static char *rcsId="@(#) $Id: gwtSEPARATOR.cpp,v 1.2 2005-08-30 06:55:45 mella Exp $"; 
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
    string s("<SEPARATOR ");
    AppendXmlAttributes(s);
    s.append("/>");
    return s;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
