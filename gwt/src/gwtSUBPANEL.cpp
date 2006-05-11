/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtSUBPANEL.cpp,v 1.2 2006-05-11 13:04:55 mella Exp $"
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
 * Definition of gwtSUBPANEL class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtSUBPANEL.cpp,v 1.2 2006-05-11 13:04:55 mella Exp $";

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
#include "gwtSUBPANEL.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget.
 * 
 */
gwtSUBPANEL::gwtSUBPANEL()
{
    logExtDbg("gwtSUBPANEL::gwtSUBPANEL()");
}

/** 
 * Constructs the widget.
 * \param text the text placed on the associated button.
 */
gwtSUBPANEL::gwtSUBPANEL(string text)
{
    logExtDbg("gwtSUBPANEL::gwtSUBPANEL()");
    SetText(text);
}


/** 
 * Constructs the widget.
 * \param text the text placed on the associated button.
 * \param help help of the widget.
 */
gwtSUBPANEL::gwtSUBPANEL(string text, string help)
{
    logExtDbg("gwtSUBPANEL::gwtSUBPANEL()");
    SetText(text);
    SetHelp(help);
}

/*
 * Class destructor
 */
gwtSUBPANEL::~gwtSUBPANEL()
{
    logExtDbg("gwtSUBPANEL::~gwtSUBPANEL()");
}

/*
 * Public methods
 */

string gwtSUBPANEL::GetXmlBlock()
{
    logExtDbg("gwtSUBPANEL::GetXmlBlock()");
    string s("<PANEL ");
    AppendXmlAttributes(s);
    s.append(">");
   
    // append children content
    gwtMAP_STRING2WIDGET::iterator i = _children.begin();
    while(i != _children.end())
    {
        gwtWIDGET * tmpWidget = i->second;
        string mystring = tmpWidget->GetXmlBlock();
        if ( ! mystring.empty() )
        {
            s.append(mystring);
        }
        i++;
    }

    // append closing tag
    s.append("</PANEL>");
    return s;
}


void gwtSUBPANEL::SetWidgetId(string id)
{
    logExtDbg("gwtSUBPANEL::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("command",id);
}



/**
 * Called automatically each time the command widget is triggered by the client.
 *
 * \param value not used.
 *
 */
void gwtSUBPANEL::Changed(string value){
    logExtDbg("gwtSUBPANEL::Changed()");
    ExecuteCB((void*)(&value));
}


/**
 * Set the text on the button.
 * \param text the text of the button
 */
void gwtSUBPANEL::SetText(string text)
{
    logExtDbg("gwtSUBPANEL::SetText()");
    SetXmlAttribute("title",text);
}


/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
