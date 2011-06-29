/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Definition of gwtMENUITEM class.
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
#include "gwtMENUITEM.h"
#include "gwtPrivate.h"


/*
 * Class constructor
 */

/** 
 * Constructs the menuitem.
 * \param label name of the menuitem.
 */
gwtMENUITEM::gwtMENUITEM(string label)
{
    logExtDbg("gwtMENUITEM::gwtMENUITEM()");
    SetXmlAttribute("label",label);
}

/*
 * Class destructor
 */
gwtMENUITEM::~gwtMENUITEM()
{
    logExtDbg("gwtMENUITEM::~gwtMENUITEM()");
}

/*
 * Public methods
 */

string gwtMENUITEM::GetXmlBlock()
{
    logExtDbg("gwtMENUITEM::GetXmlBlock()");
    string s("<MENUITEM ");
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
