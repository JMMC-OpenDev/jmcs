#ifndef gwtTABLE_H
#define gwtTABLE_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtTABLE.h,v 1.2 2005-02-07 14:36:20 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     16-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtTABLE class declaration file.
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
protected:
private:    
    gwtCELL **_cells;
    string *_columnHeaders;
    int _rows;
    int _columns;
};




#endif /*!gwtTABLE_H*/

/*___oOo___*/
