/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtMENUITEM.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtMENUITEM class.
 */

static char *rcsId="@(#) $Id: gwtMENUITEM.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"; 
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
