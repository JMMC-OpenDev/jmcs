#ifndef gwtWIDGET_H
#define gwtWIDGET_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtWIDGET.h,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gwtWIDGET class declaration file.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include <map>


typedef map<string, string> gwtNAME2VALUE;

/*
 * Class declaration
 */
class gwtWIDGET{
public:
    gwtWIDGET(){}; 
    virtual ~gwtWIDGET(); 
    // Methods should be virtual pure because there is no sens for a gwtWIDGET
    // instanciation however the container's list doesn't accept virtual
    // classes...
    /**
     * Get the Xml block of the widget.
     * \return the xml block describing the widget.
     */
    virtual string GetXmlBlock(){ return string("");};

    virtual void SetLabel(string l);
    virtual void SetHelp(string h);

    virtual void Changed(string value);

    virtual string GetWidgetId();
    // \todo try to make it friend for gwtCONTAINER only 
    virtual void SetWidgetId(string id);
protected:
    virtual string GetXmlAttribute(string name);
    virtual void AppendXmlAttributes(string &s);
    virtual void SetXmlAttribute(string name, string value);
private:
    map<string, string> _xmlAttributes;

};

#endif /*!gwtWIDGET_H*/

/*___oOo___*/
