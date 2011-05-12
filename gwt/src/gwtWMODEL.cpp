/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtWMODEL class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtWMODEL.cpp,v 1.2 2006-05-11 13:04:55 mella Exp $";

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
#include "gwtWMODEL.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the widget.
 * 
 */
gwtWMODEL::gwtWMODEL()
{
    logExtDbg("gwtWMODEL::gwtWMODEL()");
}


/** 
 * Constructs the widget.
 * \param help help of the widget.
 */
gwtWMODEL::gwtWMODEL(string help)
{
    logExtDbg("gwtWMODEL::gwtWMODEL()");
    SetHelp(help);
}

/*
 * Class destructor
 */
gwtWMODEL::~gwtWMODEL()
{
    logExtDbg("gwtWMODEL::~gwtWMODEL()");
}

/*
 * Public methods
 */

string gwtWMODEL::GetXmlBlock()
{
    logExtDbg("gwtWMODEL::GetXmlBlock()");
    string s("<guiWMODEL ");
    AppendXmlAttributes(s);
    s.append("/>");
    return s;
}

/*
 * Protected methods
 */



/*
 * Private methods
 */



/*___oOo___*/
