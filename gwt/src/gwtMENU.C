/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtMENU.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtMENU class definition file.
 */

static char *rcsId="@(#) $Id: gwtMENU.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
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
#include "gwtMENU.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the menu.
 * \param label the lable of the menu
 *  
 */
gwtMENU::gwtMENU(string label)
{
    logExtDbg("gwtMENU::gwtMENU()");
    SetXmlAttribute("label",label);
}

/*
 * Class destructor
 */
gwtMENU::~gwtMENU()
{
    logExtDbg("gwtMENU::~gwtMENU()");
}

/*
 * Public methods
 */

string gwtMENU::GetXmlBlock()
{
    logExtDbg("gwtMENU::GetXmlBlock()");
    string s("<MENU ");
    AppendXmlAttributes(s);
    s.append(">");
    s.append("</MENU>");
    return s;
}

/**
 * Add a submenu entry.
 *
 * \param menu the submenu to add. 
 *
 */
void gwtMENU::AddMenu(gwtMENU * menu)
{
    logExtDbg("gwtMENU::AddMENU()");
}


/**
 * Add a menuitem into the menu.
 *
 * \param menuitem the menuitem to add. 
 *
 */
void gwtMENU::AddMenuItem(gwtMENUITEM * menuItem)
{
    logExtDbg("gwtMENU::AddMenuItem()");
}


/**
 * Makes the menu appear.
 *
 */
void gwtMENU::Show()
{
    logExtDbg("gwtMENU::Show()");

    string s;
    s.append("<gwt_menu>");
    s.append(GetXmlBlock());
    s.append("</gwt_menu>\n");
   
    SendXml(s);
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
