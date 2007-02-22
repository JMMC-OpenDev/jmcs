/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtCELL.cpp,v 1.4 2006-05-11 13:04:55 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/02/15 12:33:49  gzins
 * Updated file description
 *
 * Revision 1.2  2005/02/07 14:45:31  mella
 * Correct minor doxygen problem
 *
 * Revision 1.1  2005/02/07 14:36:24  mella
 * Add Background color management for cells
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtCELL class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtCELL.cpp,v 1.4 2006-05-11 13:04:55 mella Exp $";
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
#include "gwtCELL.h"
#include "gwtPrivate.h"

/**
 * User should not use this to build a gwtTABLE. Initialize a new Cell content.
 * \param textContent The text content of the cell.
 */
gwtCELL::gwtCELL(string textContent)
{
    logExtDbg("gwtCELL::gwtCELL()");
    _textContent = textContent;
}

/**
 * Class destructor
 */
gwtCELL::~gwtCELL()
{
    logExtDbg("gwtCELL::~gwtCELL()");
}

/*
 * Public methods
 */

/** 
 * Set the content of the cell.
 * \param content the content to be assigned.
 */
void gwtCELL::SetContent(string content)
{
    logExtDbg("gwtCELL::SetContent()");
    _textContent=content;
}

/** 
 * Get the content of the cell.
 * \return the content of the cell
 */
string gwtCELL::GetContent()
{
    logExtDbg("gwtCELL::GetContent()");
    return _textContent;
}

/** 
 * Set the background color of the cell.
 * \param bgcolor the background color to be assigned.
 */
void gwtCELL::SetBackgroundColor(string bgcolor)
{
    logExtDbg("gwtCELL::SetBackgroundColor()");
    _backgroundColor=bgcolor;
}

/** 
 * Returns the corresponding xml block of this cell.
 * \return the xml string representation.
 */
string gwtCELL::GetXmlBlock()
{
    logExtDbg("gwtCELL::GetXmlBlock()");
    string xmlStr;
    if(_backgroundColor.empty())
    {
        xmlStr.append("<TD>");
    }else{
        xmlStr.append("<TD bgcolor=\"");
        xmlStr.append(_backgroundColor);
        xmlStr.append("\">");
    }
    xmlStr.append(_textContent);
    xmlStr.append("</TD>");
  
    return xmlStr;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
