/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCHOICE.cpp,v 1.2 2005-02-15 12:25:28 gzins Exp $"
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
 * Definition of gwtCHOICE class.
 */

static char *rcsId="@(#) $Id: gwtCHOICE.cpp,v 1.2 2005-02-15 12:25:28 gzins Exp $"; 
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

void gwtCHOICE::SetWidgetId(string id)
{
    logExtDbg("gwtCHOICE::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}



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
 * Assign a new value to the widget.
 *
 * \param value the new value of the widget assigned by the user. 
 *
 */
void gwtCHOICE::Changed(string value){
    logExtDbg("gwtCHOICE::Changed()"); 
    _selectedItem.assign(value); 
}

/** 
 *  Add an item to the CHOICE menu.
 *
 * \param item the item to be added. 
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT gwtCHOICE::Add(string item)
{
    logExtDbg("gwtCHOICE::Add()");
    _items.push_back(item);
    return mcsSUCCESS;
}

/** 
 *  Returns the index of the currently selected item.
 * If no value was modified by user, it returns the first element index (0).
 *
 *  \returns the index of the selected item.
 */
mcsINT32 gwtCHOICE::GetSelectedItem()
{
    logExtDbg("gwtCHOICE::GetSelectedItem()");

    if(_selectedItem.empty())
    {
        return 0;
    }
    else
    {
        unsigned int i;
        for (i=0; i<_items.size();i++)
        {
            if( _selectedItem.find(_items[i]) != string::npos )
            {
                return i;
            }
        }
        // if no value was affected return the first index.
        return 0;
    }
    
}

/** 
 *  Returns the value of the currently selected item.
 * If no value was modified by user, it returns the first element.
 *
 *  \returns the value of the selected item.
 */
string gwtCHOICE::GetSelectedItemValue()
{
    logExtDbg("gwtCHOICE::GetSelectedItemValue()");

    if(_selectedItem.empty())
    {
        return _items[0];
    }
    else
    {
        return _selectedItem;
    }
    
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
