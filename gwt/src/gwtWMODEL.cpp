/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtWMODEL.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtWMODEL class.
 */

static char *rcsId="@(#) $Id: gwtWMODEL.cpp,v 1.1 2005-01-27 18:09:35 gzins Exp $"; 
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
#include "gwtWMODEL.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget.
 * 
 */
gwtWMODEL::gwtWMODEL()
{
    logExtDbg("gwtWMODEL::gwtWMODEL()");
}


/** 
 * Constructs the widget.
 * \param help help of the widget.
 */
gwtWMODEL::gwtWMODEL(string help)
{
    logExtDbg("gwtWMODEL::gwtWMODEL()");
    SetHelp(help);
}

/*
 * Class destructor
 */
gwtWMODEL::~gwtWMODEL()
{
    logExtDbg("gwtWMODEL::~gwtWMODEL()");
}

/*
 * Public methods
 */

string gwtWMODEL::GetXmlBlock()
{
    logExtDbg("gwtWMODEL::GetXmlBlock()");
    string s("<guiWMODEL ");
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
