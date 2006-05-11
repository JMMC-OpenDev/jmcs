/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtWINDOW.cpp,v 1.7 2006-05-11 13:04:55 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2005/09/28 14:02:48  mella
 * Hide window inside destructor
 *
 * Revision 1.5  2005/03/08 14:19:38  mella
 * Add closing callback support
 *
 * Revision 1.4  2005/03/02 13:18:56  mella
 * Bug correction for to choose between update and creation
 *
 * Revision 1.3  2005/03/02 13:07:56  mella
 * Implement a basic update mechanism
 *
 * Revision 1.2  2005/02/15 12:25:28  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtWINDOW class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtWINDOW.cpp,v 1.7 2006-05-11 13:04:55 mella Exp $";

/* 
 * System Headers 
 */
#include <iostream>
#include <iterator>
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
#include "gwtWINDOW.h"
#include "gwtPrivate.h"

/*
 * Class constructor
 */
/** 
 * Creates a window.
 */
gwtWINDOW::gwtWINDOW()
{
    logExtDbg("gwtWINDOW::gwtWINDOW()");
    // Init a default empty closeCommand
    SetCloseCommand(""); 
}

/** 
 * Creates a window with a given title.
 * \param title the title of the window
 */
gwtWINDOW::gwtWINDOW(char *title)
{
    logExtDbg("gwtWINDOW::gwtWINDOW()");
    SetTitle(title);
}
/*
 * Class destructor
 */
gwtWINDOW::~gwtWINDOW()
{
    logExtDbg("gwtWINDOW::~gwtWINDOW()");
    Hide();
}


/*
 * Public methods
 */

/** 
 *  Get the description of the window for creation or update.
 *
 * \param update true returns a update document 
 *
 *  \returns the xml description
 */
string gwtWINDOW::GetXmlBlock(mcsLOGICAL update)
{
    logExtDbg("gwtWINDOW::GetXmlBlock()");
    string s;

    string rootTag;
    if( update == mcsTRUE )
    {
        rootTag.append("gui_update");
    }
    else
    {
        rootTag.append("gui_desc");
    }
    
    // append starting tag block
    s.append("<");
    s.append(rootTag);
    s.append(" ");
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
    s.append("</");
    s.append(rootTag);
    s.append(">\n");

    return s;
}

/**
 * Makes the window appear.
 * \todo replace attachedGui->send() by send().
 */
void gwtWINDOW::Show(void)
{
   logExtDbg("gwtWINDOW::Show()");
   SendXml(GetXmlBlock());
}  

/**
 * Makes the window appear.
 * \todo replace attachedGui->send() by send().
 */
void gwtWINDOW::Update(void)
{
   logExtDbg("gwtWINDOW::Update()");
   SendXml(GetXmlBlock(mcsTRUE));
}  

/**
 * Makes the window disappear.
 * \todo implement Hide method.
 */
void gwtWINDOW::Hide(void)   
{
   logExtDbg("gwtWINDOW::Hide()");
   string s;
   s.append("<gui_update>");
   s.append("<gui_desc variable=\"");
   s.append(GetXmlAttribute("widgetid"));
   s.append("\" variableValue=\"false\"></gui_desc>\n</gui_update>\n");
   SendXml(s);
}


void gwtWINDOW::SetWidgetId(string id)
{
    logExtDbg("gwtWINDOW::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}

/**
 * This method is called by the gwt during the registration.
 * \param id the given id associated to this object.
 */
void gwtWINDOW::SetProducerId(string id)
{
    logExtDbg("gwtWINDOW::SetProducerId()");
    SetWidgetId(id);
}

/**
 * This method is automatically called by the gwtGUI for each widget events.
 *
 * \param widgetid  the affected widget id
 * \param data  the new value or empty for a command widget
 *
 */
void gwtWINDOW::ReceiveFromGui(string widgetid, string data)
{
    logExtDbg("gwtWINDOW::ReceiveFromGui()");
    string closeCommand=GetXmlAttribute("cancelCommand");
    if(! closeCommand.empty()){
        // if widgetid equals the closeCommand value, execute callback
        if( widgetid.find(closeCommand) != string::npos ){
            ExecuteCB(&data);
        }
    }
    
    DispatchGuiReturn(widgetid, data);
}

/**
 * Set the title of the window.
 *
 * \param title The title of the window. 
 */
void gwtWINDOW::SetTitle(string title)
{
    logExtDbg ("gwtWINDOW::SetTitle()");
    SetXmlAttribute("title", title);
}

/** 
 *  Set the string that will have to return the gui if the window is closed by
 *  the user. Until next revision of this module, the command must be the id
 *  of one gwtCOMMAND object (eg: one button...)
 *
 * \param command  the command to return
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
void gwtWINDOW::SetCloseCommand(string command)
{
    logExtDbg ("gwtWINDOW::SetCloseCommand()");
    SetXmlAttribute("cancelCommand",command); 
}

/*
 * Protected methods
 */


/*
 * Private methods
 */



/*___oOo___*/
