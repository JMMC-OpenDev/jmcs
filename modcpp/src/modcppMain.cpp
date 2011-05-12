/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * brief description of the program, which ends at this dot.
 *
 * \synopsis
 * \e \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * \param param1 : description of parameter 1
 * \param paramN : description of parameter N
 *
 * \n
 * \opt
 * \optname option1 : description of option 1
 * \optname optionN : description of option N
 * 
 * \n
 * \details
 * OPTIONAL detailed description of the c main file follows here.
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
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * \code
 * Insert your command example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa modcppOPERATION.C
 * 
 * \bug OPTIONAL. Known bugs list if it exists.
 * \bug Bug 1 : bug 1 description
 *
 * \todo OPTIONAL. Things to forsee list.
 * \todo Action 1 : action 1 description
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: modcppMain.cpp,v 1.5 2006-05-11 13:04:56 mella Exp $";

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

/**
 * \namespace std
 * Export standard iostream objects (cin, cout,...).
 */
using namespace std;


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"


/*
 * Local Headers 
 */
#include "modcppPrivate.h"
#include "modcppOPERATION.h"


/*
 * Local Variables
 */

 
/* 
 * Signal catching functions  
 */


/* 
 * Main
 */

int main(int argc, char *argv[])
{
    /* Initialize MCS services */
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary
        
        // Exit from the application with mcsFAILURE
        exit (EXIT_FAILURE);
    }

    // Set stdout log level
    logSetStdoutLogLevel(logTRACE);
    
    // operation object instanciation
    modcppOPERATION operation;
    
    // Set test variables
    mcsINT8 a = 2;
    mcsINT8 b = 4;

    // modcppOPERATION::Add method
    if (operation.Add(a, b) == mcsFAILURE)
    {
        logTest("ERROR : modcppOPERATION::Add method");
    }
    
    // modcppOPERATION::SubAndMultiply method
    if (operation.SubAndMultiply(a, b) == mcsFAILURE)
    {
        logTest("ERROR : modcppOPERATION::SubAndMultiply method");
    }
    
    // modcppOPERATION::Divide method
    mcsFLOAT c = 0.;
    if (operation.Divide(a, b, &c) == mcsFAILURE)
    {
        logTest("ERROR : modcppOPERATION::Divide method");
    }
    logTest("Division result = c = %.2f", c);

    // Another object instanciation with the second constructor
    modcppOPERATION operation2("Calculation_For_Astronomy");

    // modcppOPERATION::GetName method
    char * name;
    name = operation2.GetName();
    logTest("operation 2 name = %s", name);
      
    // Close MCS services
    mcsExit();
    
    // Exit from the application with mcsSUCCESS
    exit (EXIT_SUCCESS);

}


/*___oOo___*/
