/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtMENUITEM.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtMENUITEM class definition file.
 */

static char *rcsId="@(#) $Id: gwtMENUITEM.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
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
#include "gwtMENUITEM.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the menuitem.
 * \param label name of the menuitem.
 */
gwtMENUITEM::gwtMENUITEM(string label)
{
    logExtDbg("gwtMENUITEM::gwtMENUITEM()");
    SetXmlAttribute("label",label);
}

/*
 * Class destructor
 */
gwtMENUITEM::~gwtMENUITEM()
{
    logExtDbg("gwtMENUITEM::~gwtMENUITEM()");
}

/*
 * Public methods
 */

string gwtMENUITEM::GetXmlBlock()
{
    logExtDbg("gwtMENUITEM::GetXmlBlock()");
    string s("<MENUITEM ");
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
