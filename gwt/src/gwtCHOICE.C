/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtCHOICE.C,v 1.1 2004-12-01 08:57:06 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtCHOICE class definition file.
 */

static char *rcsId="@(#) $Id: gwtCHOICE.C,v 1.1 2004-12-01 08:57:06 mella Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
#include <vector>
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
#include "gwtCHOICE.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget.
 * 
 */
gwtCHOICE::gwtCHOICE()
{
    logExtDbg("gwtCHOICE::gwtCHOICE()");
}


/** 
 * Constructs the widget.
 * \param help help of the widget.
 */
gwtCHOICE::gwtCHOICE(string help)
{
    logExtDbg("gwtCHOICE::gwtCHOICE()");
    SetHelp(help);
}

/*
 * Class destructor
 */
gwtCHOICE::~gwtCHOICE()
{
    logExtDbg("gwtCHOICE::~gwtCHOICE()");
}

/*
 * Public methods
 */

string gwtCHOICE::GetXmlBlock()
{
    logExtDbg("gwtCHOICE::GetXmlBlock()");
    string s("<CHOICE ");
    AppendXmlAttributes(s);
    s.append(">");
    unsigned int i;
    for (i=0; i<_items.size();i++)
    {
        s.append("<ITEM value=\"");
        s.append(_items[i]);
        s.append("\"/>");
    }
    
    s.append("</CHOICE>");
    return s;
}

/** 
 *  Add an item to the CHOICE menu.
 *
 * \param item the item to be added. 
 *
 *  \returns an MCS completion status code (SUCCESS or FAILURE)
 */
mcsCOMPL_STAT gwtCHOICE::Add(string item)
{
    logExtDbg("gwtCHOICE::Add()");
    _items.push_back(item);
    return SUCCESS;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
