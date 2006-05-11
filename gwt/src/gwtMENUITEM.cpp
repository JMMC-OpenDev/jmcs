/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtMENUITEM.cpp,v 1.2 2006-05-11 13:04:55 mella Exp $"
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
 * Definition of gwtMENUITEM class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtMENUITEM.cpp,v 1.2 2006-05-11 13:04:55 mella Exp $";

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
