#ifndef gwtTABLE_H
#define gwtTABLE_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
    virtual void SetDimension(int rows, int columns);
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
