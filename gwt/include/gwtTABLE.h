#ifndef gwtTABLE_H
#define gwtTABLE_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtTABLE.h,v 1.6 2005-03-02 13:54:33 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2005/02/28 12:59:53  mella
 * Implement SetWidgth and SetHeight
 *
 * Revision 1.4  2005/02/15 12:33:49  gzins
 * Updated file description
 *
 * Revision 1.3  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     16-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtTABLE class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include "gwtWIDGET.h"
#include "gwtCELL.h"

/*
 * Class declaration
 */

/**
 * Constructs a new gwtTABLE.
 */
class gwtTABLE : public gwtWIDGET
{
public:
    gwtTABLE(int rows, int columns);
    ~gwtTABLE();
    virtual string GetXmlBlock();
    virtual void SetCell(int row, int column, string value);
    virtual string GetCell(int row, int column);
    virtual void SetCellBackground(int row, int column, string color);
    virtual void SetColumnHeader(int column, string title);
    virtual void SetWidth(int nbPixels);
    virtual void SetHeight(int nbPixels);    
protected:
    virtual void SetWidgetId(string id);
private:    
    gwtCELL **_cells;
    string *_columnHeaders;
    int _rows;
    int _columns;
};




#endif /*!gwtTABLE_H*/

/*___oOo___*/
