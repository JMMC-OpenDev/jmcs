/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtTABLE.cpp,v 1.5 2005-03-02 14:53:42 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/03/02 13:54:34  mella
 * Make table updatable using the xml variable attribute
 *
 * Revision 1.3  2005/02/28 12:48:31  mella
 * Implement SetWidgth and SetHeight
 *
 * Revision 1.2  2005/02/07 14:36:24  mella
 * Add Background color management for cells
 *
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     16-Sep-2004  Created
 * gzins     12-Dec-2004  Fixed gcc 3.4 error 
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtTABLE class.
 */

static char *rcsId="@(#) $Id: gwtTABLE.cpp,v 1.5 2005-03-02 14:53:42 mella Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
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
#include "gwtTABLE.h"
#include "gwtPrivate.h"
#include "gwt.h"


/*
 * Class constructor
 */

/** 
 * Constructs a gwtTABLE. The inner table vector is initialized using
 * given dimensions.
 * \param rows number of rows
 * \param columns number of columns
 */
gwtTABLE::gwtTABLE(int rows, int columns)
{
    logExtDbg("gwtTABLE::gwtTABLE()");
    _rows=0;
    _columns=0;
    SetDimension(rows, columns);    
}

/*
 * Class destructor
 */

gwtTABLE::~gwtTABLE()
{
    logExtDbg("gwtTABLE::~gwtTABLE()");
    // delete the two dimension cell array
    int i;
    for (i=0;i<_rows;i++)
    {
        delete []_cells[i];
    }
    delete [] _cells;

    // delete the columnHeaders array
    delete _columnHeaders;
}

/*
 * Public methods
 */

/** 
 *  Reinitialize the table with new dimensions.
 *
 * \param rows  number of rows
 * \param columns  number of columns
 * 
 * \warning All data are removed after each change of dimension.
 */
void gwtTABLE::SetDimension(int rows, int columns)
{
    logExtDbg("gwtTABLE::SetDimension()");
    int i;
        
    // clean previous data if not empty
    if ( (_rows+_columns)  > 0 )
    {
        for (i=0;i<_rows;i++)
        {
            delete []_cells[i];
        }
        if(_columns!=0)
        {
            delete [] _cells;
            // delete the columnHeaders array
            delete _columnHeaders;
        }

    }
    
    // prepare data two dimension array
    _rows=rows;
    _columns=columns;
    _cells = new  gwtCELL *[rows];
 
    // build colums arrays
    for (i=0;i<_rows;i++)
    {
        _cells[i] = new gwtCELL[columns]("");
    }

    // prepare columnHeader array
    _columnHeaders = new string[columns];
}

string gwtTABLE::GetXmlBlock()
{
    logExtDbg("gwtTABLE::GetXmlBlock()");
    string s;
    s.append("<TABLE");
    AppendXmlAttributes(s);
    s.append(">\n");

    int r,c;
    // append Column headers 
    s.append("<CHEADER>");
    for (c=0; c<_columns ; c++)
    {
        s.append("<FIELD ");
        // append the value if the header colum does exist
        if( ! _columnHeaders[c].empty())
        {
           s.append("name=\"");
           s.append(_columnHeaders[c]);
           s.append("\"");
        }
        s.append("\"/>\n");
    }  
    s.append("</CHEADER>");

    // append data
    s.append("<DATA>");
    for (r=0; r<_rows ; r++)
    {
        s.append("<TR>\n\t");
        for (c=0; c<_columns ; c++)
        {
            s.append(_cells[r][c].GetXmlBlock());
        }  
        s.append("\n</TR>\n");
    }    
    s.append("</DATA>");
    s.append("</TABLE>");
    return s;
}

/**
 * Set the value of a given cell.
 * \param row the row index.
 * \param column the column index.
 * \param value the content value.
 */
void gwtTABLE::SetCell(int row, int column, string value)
{
    // logExtDbg("gwtTABLE::SetCell()");
    _cells[row][column].SetContent(value);
}

/**
 * Set the background color of a given cell.
 * \param row the row index.
 * \param column the column index.
 * \param color the background color.
 * the color is interpreted as a decimal, octal, or hexidecimal integer into the
 * RGB system. Example "#0FF3D1" .
 */
void gwtTABLE::SetCellBackground(int row, int column, string color)
{
    // logExtDbg("gwtTABLE::SetCellBackground()");
     _cells[row][column].SetBackgroundColor(color);
}

/**
 * Get the value of a requested cell.
 * \param row
 * \param column
 * \return the value of the requested cell.
 */
string gwtTABLE::GetCell(int row, int column)
{
    // logExtDbg("gwtTABLE::GetCell()");
    return _cells[row][column].GetContent();
}

/**
 * Set the title of a given column.
 * \param column the column number (first one corresponds to 0)
 * \param title the title of the column
 */
void gwtTABLE::SetColumnHeader(int column, string title)
{
    logExtDbg("gwtTABLE::SetColumnHeader()");
    _columnHeaders[column]=title;    
}

/** 
 *  Set width of table in pixels. 
 * \param nbPixels number of pixels for the width 
 */
void gwtTABLE::SetWidth(int nbPixels)
{
    logExtDbg("gwtTABLE::SetWidth()");
    ostringstream osstring;
    osstring << nbPixels;
    SetXmlAttribute("width",osstring.str());
}


/** 
 *  Set height of table in pixels. 
 * \param nbPixels number of pixels for the height 
 */
void gwtTABLE::SetHeight(int nbPixels)
{
    logExtDbg("gwtTABLE::SetHeight()");
    ostringstream osstring;
    osstring << nbPixels;
    SetXmlAttribute("height",osstring.str());
}

/*
 * Protected methods
 */
void gwtTABLE::SetWidgetId(string id)
{
    logExtDbg("gwtTABLE::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}

/*
 * Private methods
 */



/*___oOo___*/
