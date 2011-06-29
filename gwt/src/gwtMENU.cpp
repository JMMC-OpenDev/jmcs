/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtMENU class.
 */


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
 * \param menuItem the menuitem to add. 
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
    s.append("<gui_menu>");
    s.append(GetXmlBlock());
    s.append("</gui_menu>\n");
   
    SendXml(s);
}


/**
 * This method is called by the gwt during the registration.
 * \param id the given id associated to this object.
 */
void gwtMENU::SetProducerId(string id)
{
    logExtDbg("gwtMENU::SetProducerId()");
    SetWidgetId(id);
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
