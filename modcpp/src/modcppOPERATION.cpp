/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Brief description of the class file, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class file follows here.
 *
 * \usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \filename fileName1 :  usage description of fileName1
 * \filename fileName2 :  usage description of fileName2
 *
 * \n
 * \env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * \envvar envVar1 :  usage description of envVar1
 * \envvar envVar2 :  usage description of envVar2
 * 
 * \n
 * \warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * \ex
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * \code
 * Insert your code example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa modcppMain.C
 * 
 * \bug OPTIONAL. Bugs list if it exists.
 * \bug For example, description of the first bug
 * \bug For example, description of the second bug
 * 
 * \todo OPTIONAL. Things to forsee list, if needed. For example, 
 * \todo add another method.
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: modcppOPERATION.cpp,v 1.7 2006-05-11 13:04:56 mella Exp $";

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
#include "modcppPrivate.h"
#include "modcppOPERATION.h"
#include "modcppErrors.h"


/*
 * Class constructor
 */

/**
 * Brief description of the constructor, which ends at this dot.
 *
 * OPTIONAL detailed description of the constructor follows here.
 *
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa For example modcppPERSON(char *name).
 * 
 */
modcppOPERATION::modcppOPERATION()
{
}


/**
 * Brief description of the constructor, which ends at this dot.
 *
 * OPTIONAL detailed description of the constructor follows here.
 *
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa For example modcppPERSON(char *name).
 * 
 */
modcppOPERATION::modcppOPERATION(char *name)
{
    SetName(name);
}


/*
 * Class destructor
 */

/**
 * Brief description of the destructor, which ends at this dot.
 *
 * OPTIONAL detailed description of the destructor follows here.
 *
 */
modcppOPERATION::~modcppOPERATION()
{
}


/*
 * Public methods
 */

/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param x description of parameter x. In the example, an integer.
 * \param y description of parameter y. In the example, an integer.
 *
 * \n
 * \return Description of the return value. In the example, mcsSUCCESS on
 * successful completion. Otherwise mcsFAILURE is returned. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n
 * \usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \filename fileName1 :  usage description of fileName1
 * \filename fileName2 :  usage description of fileName2
 *
 * \n
 * \env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * \envvar envVar1 :  usage description of envVar1
 * \envvar envVar2 :  usage description of envVar2
 * 
 * \n
 * \warning OPTIONAL. Warning if any (software requirements, ...).
 *
 * \n
 * \ex 
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * \code
 * Insert your code example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. For example,
 * modcppOPERATION::Divide
 * 
 * \bug OPTIONAL. Bugs list if it exists.
 * \bug For example, the method is valid only for INT8 integers.
 *
 * \todo OPTIONAL. Things to forsee list.
 * \todo For example, correct bugs.
 * \todo For example, extend the method with file1 and file 2.
 *
 */
mcsCOMPL_STAT modcppOPERATION::Add(mcsINT8 x, mcsINT8 y)
{
    logExtDbg("modcppOPERATION::Add()");

    mcsINT8 z;
    z = x + y;
    logTest("%d + %d = %d\n", x, y, z);
    
    return mcsSUCCESS;
}


/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param x description of parameter x. In the example, an integer.
 * \param y description of parameter y. In the example, an integer.
 * \param z description of parameter z. In the example, a float.
 *
 * \n
 * \return Description of the return value. In the example, mcsSUCCESS on
 * successful completion. Otherwise mcsFAILURE is returned. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. For example
 * \sa modcppOPERATION::Add
 * 
 */
mcsCOMPL_STAT modcppOPERATION::Divide(mcsINT8 x, mcsINT8 y, mcsFLOAT *z)
{
    logExtDbg("modcppOPERATION::Divide()");

    if (y == 0)
    {
        errAdd(modcppERR_DIVISION_BY_ZERO,x);
        return mcsFAILURE;
    }
    *z = ((float) x) / y;
    logTest("%d / %d = %.2f\n", x, y, *z);

    return mcsSUCCESS;
}


/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param x description of parameter x. In the example, an integer.
 * \param y description of parameter y. In the example, an integer.
 *
 * \n
 * \return Description of the return value. In the example, mcsSUCCESS on
 * successful completion. Otherwise mcsFAILURE is returned. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n 
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. For example
 * modcppOPERATION::Add, modcppOPERATION::Divide.
 * 
 */
mcsCOMPL_STAT modcppOPERATION::SubAndMultiply(mcsINT8 x, mcsINT8 y)
{
    logExtDbg("modcppOPERATION::SubAndMultiply()");

    //Sub method call
    if (Sub(x, y) == mcsFAILURE)
    {
        logTest("ERROR : modcppOPERATION::Sub method");
    }

    // Multiply method call
    if (Multiply(x, y) == mcsFAILURE)
    {
        logTest("ERROR : modcppOPERATION::Multiply method");
    }

    return mcsSUCCESS;
}

/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param name description of parameter name. In the example, a string
 * describing the operation name.
 *
 * \n
 * \return Description of the return value. In the example, nothing. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n 
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. 
 * 
 */
void modcppOPERATION::SetName(char *name)
{
    logExtDbg("modcppOPERATION::SetName()");
    
    // Initialise name member
    memset(_name, '\0', sizeof(_name));

    // Copy name into internal buffer
    strncpy(_name, name, sizeof(_name)-1);
}


char * modcppOPERATION::GetName()
{
    logExtDbg("modcppOPERATION::GetName()");

    return _name;
}


/*
 * Protected methods
 */

/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param x description of parameter x. In the example, an integer.
 * \param y description of parameter y. In the example, an integer.
 *
 * \n
 * \return Description of the return value. In the example, mcsSUCCESS on
 * successful completion. Otherwise mcsFAILURE is returned. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n 
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. For example
 * modcppOPERATION::Divide, modcppOPERATION::Add.
 * 
 */
mcsCOMPL_STAT modcppOPERATION::Sub(mcsINT8 x, mcsINT8 y)
{
    logExtDbg("modcppOPERATION::Sub()");

    mcsINT8 z;
    z = x - y;
    logTest("%d - %d = %d\n", x, y, z);
    
    return mcsSUCCESS;
}


/*
 * Private methods
 */

/**
 * Brief description of the method, which ends at this dot.
 *
 * OPTIONAL detailed description of the method follows here.
 *
 * \param x description of parameter x. In the example, an integer.
 * \param y description of parameter y. In the example, an integer.
 *
 * \n
 * \return Description of the return value. In the example, mcsSUCCESS on
 * successful completion. Otherwise mcsFAILURE is returned. 
 *
 * \n
 * \err
 * If error handling system is used.
 * The possible errors are :
 * \errname modcppERR_ERROR_1
 * \errname modcppERR_ERROR_2
 *
 * \n 
 * \sa OPTIONAL. See also section, in witch you can refer other documented
 * entities. Doxygen will create the link automatically. For example
 * Add, Sub, Divide.
 * 
 */
mcsCOMPL_STAT modcppOPERATION::Multiply(mcsINT8 x, mcsINT8 y)
{
    logExtDbg("modcppOPERATION::Multiply()");

    mcsINT8 z;
    z = x * y;
    logTest("%d * %d = %d\n", x, y, z);
    
    return mcsSUCCESS;
}


/*___oOo___*/
