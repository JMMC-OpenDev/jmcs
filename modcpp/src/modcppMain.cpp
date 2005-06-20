/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: modcppMain.cpp,v 1.4 2005-06-20 13:17:52 swmgr Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/02/15 10:48:15  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.2  2005/02/15 10:40:17  gzins
 * Added CVS log as file modification history
 *
 * gluck     06-Jul-2004  Created
 *
 * IMPORTANT :
 * To make your own documentation, you have to substitute the general or
 * example comments, with your specific comments.
 * 
 * IMPORTANT:
 * To make AUTOMATIC DOCUMENTATION GENERATION by doxygen, you have to insert
 * your code documentation (about file, functions, define, enumeration, ...) as
 * shown below, in the special documentation blocks (beginning with 1 slash and
 * 2 stars), adding or deleting markers as needed.
 * Nevertheless, you also have to comment the code as usually.  For more
 * informations, you can report to Programming Standards (JRA4-PRO-2000-0001),
 * or doxygen documentation.
 *
 * IMPORTANT
 * Each time (except in certain case) there is a brief and a detailed
 * description, THE BRIEF DESCRIPTION IS A UNIQUE SENTENCE, WHICH ENDS AT THE
 * FIRST DOT FOLLOWED BY A SPACE OR A NEWLINE.
 * 
 * REMARKS
 * The documentation below, shows some possibilities of doxygen. The general
 * format of this documentation is recommended to make the documentation
 * easily. Some documentation lines are strongly recommended to get rapidly a
 * quite good documentation. Some others are optinonal, depending on the need.
 * They will be pointed out with the word OPTIONAL.
 *
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

static char *rcsId="@(#) $Id: modcppMain.cpp,v 1.4 2005-06-20 13:17:52 swmgr Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


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
