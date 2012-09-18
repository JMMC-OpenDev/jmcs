/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtLABEL class.
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
#include "gwtLABEL.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs a gwtLABEL.
 */
gwtLABEL::gwtLABEL()
{
     logExtDbg("gwtLABEL::gwtLABEL()");
}

/** 
 * Constructs a gwtLABEL.
 * \param text the text of the widget
 * \param help the help of the widget
 */
gwtLABEL::gwtLABEL(string text, string help)
{
     logExtDbg("gwtLABEL::gwtLABEL()");
     SetText(text);
     SetHelp(help);
}

/*
 * Class destructor
 */
gwtLABEL::~gwtLABEL()
{
     logExtDbg("gwtLABEL::~gwtLABEL()");
}

/*
 * Public methods
 */

string gwtLABEL::GetXmlBlock()
{
    logExtDbg("gwtLABEL::GetXmlBlock()");
    string s;
    s.append("<SHOW");
    AppendXmlAttributes(s);
    s.append("/>");
    return s;
}

/**
 * Set the text value of the widget.
 *
 * \param text the text value of the widget.
 *
 * \todo try to make it dynamically changeable.
 */
void gwtLABEL::SetText(string text)
{
    logExtDbg("gwtLABEL::SetText()"); 
    SetXmlAttribute("text",text); 
}

/**
 * Get the text value of the widget.
 *
 * \return the text value of the widget.
 */
string gwtLABEL::GetText()
{
    logExtDbg("gwtLABEL::GetText()"); 
    return GetXmlAttribute("text"); 
}

/*
 * Protected methods
 */
void gwtLABEL::SetWidgetId(string id)
{
    logExtDbg("gwtLABEL::SetWidgetId()"); 
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}


/*
 * Private methods
 */

/*___oOo___*/
