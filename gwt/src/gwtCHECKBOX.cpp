/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtCHECKBOX class.
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
#include "gwtCHECKBOX.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget. By default it is true.
 * 
 */
gwtCHECKBOX::gwtCHECKBOX()
{
    logExtDbg("gwtCHECKBOX::gwtCHECKBOX()");
    SetValue( mcsTRUE );
    SetXmlAttribute("trueul","true");
    SetXmlAttribute("falseul","false");
    SetXmlAttribute("true","true");
    SetXmlAttribute("false","false");
}


/** 
 * Constructs the widget.
 * \param help help of the widget.
 */
gwtCHECKBOX::gwtCHECKBOX(string help)
{
    logExtDbg("gwtCHECKBOX::gwtCHECKBOX()");
    SetHelp(help);
}

/*
 * Class destructor
 */
gwtCHECKBOX::~gwtCHECKBOX()
{
    logExtDbg("gwtCHECKBOX::~gwtCHECKBOX()");
}

/*
 * Public methods
 */

void gwtCHECKBOX::SetWidgetId(string id)
{
    logExtDbg("gwtCHECKBOX::SetWidgetId()");
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}



string gwtCHECKBOX::GetXmlBlock()
{
    logExtDbg("gwtCHECKBOX::GetXmlBlock()");
    string s("<LOGIC ");
    AppendXmlAttributes(s);
    s.append(" />");
    return s;
}

/**
 * Assign a new value to the widget.
 *
 * \param value the new value of the widget assigned by the user. 
 *
 */
void gwtCHECKBOX::Changed(string value){
    logExtDbg("gwtCHECKBOX::Changed()"); 
    
    if(value.find("true") != string::npos)
    {
        _value=mcsTRUE; 
    }
    else
    {
        _value=mcsFALSE; 
    }
}

/** 
 *  Set the boolean value.
 *
 * \param flag the boolean value. 
 *
 *  \returns an MCS completion status code (mcsSUCCESS or mcsFAILURE)
 */
mcsCOMPL_STAT gwtCHECKBOX::SetValue(mcsLOGICAL flag)
{
    logExtDbg("gwtCHECKBOX::SetValue()");
    _value=flag;
    if(_value)
    {
        SetXmlAttribute("userlogic","true");
    }
    else
    {
        SetXmlAttribute("userlogic","false");
    }
    return mcsSUCCESS;
}

/** 
 *  Returns the boolean value.
 *
 *  \returns the value of the widget.
 */
mcsLOGICAL gwtCHECKBOX::GetValue()
{
    logExtDbg("gwtCHECKBOX::GetValue()");
    return _value;        
}
/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
