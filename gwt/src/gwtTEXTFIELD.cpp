/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtTEXTFIELD class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtTEXTFIELD.cpp,v 1.3 2006-05-11 13:04:55 mella Exp $";

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
#include "gwtTEXTFIELD.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs a gwtTEXTFIELD.
 */
gwtTEXTFIELD::gwtTEXTFIELD()
{
     logExtDbg("gwtTEXTFIELD::gwtTEXTFIELD()");
}

/** 
 * Constructs a gwtTEXTFIELD.
 * \param text the text of the widget
 * \param help the help of the widget
 */
gwtTEXTFIELD::gwtTEXTFIELD(string text, string help)
{
     logExtDbg("gwtTEXTFIELD::gwtTEXTFIELD()");
     SetText(text);
     SetHelp(help);
}

/*
 * Class destructor
 */
gwtTEXTFIELD::~gwtTEXTFIELD()
{
     logExtDbg("gwtTEXTFIELD::~gwtTEXTFIELD()");
}

/*
 * Public methods
 */

string gwtTEXTFIELD::GetXmlBlock()
{
    logExtDbg("gwtTEXTFIELD::GetXmlBlock()");
    string s;
    s.append("<CHAIN");
    AppendXmlAttributes(s);
    s.append("/>");
    return s;
}

/**
 * Set the text value of the widget.
 *
 * \param text the text value of the widget.
 *
 * \todo try to make it dynamically changeable.
 */
void gwtTEXTFIELD::SetText(string text)
{
    logExtDbg("gwtTEXTFIELD::SetText()"); 
    SetXmlAttribute("userchain",text); 
}

/**
 * Get the text value of the widget.
 *
 * \return the text value of the widget.
 */
string gwtTEXTFIELD::GetText()
{
    logExtDbg("gwtTEXTFIELD::GetText()"); 

    return GetXmlAttribute("userchain"); 
}

/**
 * Assign a new value to the widget.
 *
 * \param value the new value of the widget assigned by the user. 
 *
 */
void gwtTEXTFIELD::Changed(string value){
    logExtDbg("gwtTEXTFIELD::Changed()"); 
    SetXmlAttribute("userchain",value); 
}

/*
 * Protected methods
 */
void gwtTEXTFIELD::SetWidgetId(string id)
{
    logExtDbg("gwtTEXTFIELD::SetWidgetId()"); 
    gwtWIDGET::SetWidgetId(id);
    SetXmlAttribute("variable",id);
}



/*
 * Private methods
 */



/*___oOo___*/
