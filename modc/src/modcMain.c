/*******************************************************************************
* JMMC project
*
* "@(#) $Id: modcMain.c,v 1.8 2004-08-05 12:21:56 gluck Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* gluck     11-Jun-2004  Created
*
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
*******************************************************************************/


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
 * \sa modcProc.c
 * 
 * \bug OPTIONAL. Known bugs list if it exists.
 * \bug Bug 1 : bug 1 description
 *
 * \todo OPTIONAL. Things to forsee list.
 * \todo Action 1 : action 1 description
 * 
 */

static char *rcsId="@(#) $Id: modcMain.c,v 1.8 2004-08-05 12:21:56 gluck Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"


/*
 * Local Headers 
 */
#include "modcPrivate.h"
#include "modc.h"


/* 
 * Local variables
 */
static mcsINT8 modcId;    /**< Brief description of the variable, ends at
                             this dot. OPTIONAL detailed description of the
                             variable follows here. */


/* 
 * Signal catching functions  
 */


/* 
 * Main
 */

int main (int argc, char *argv[])
{
    /* Initializes MCS services */
    mcsInit(argv[0]);
    
    /* Set verbosity level */
    logSetStdoutLogLevel(logEXTDBG);
    
    /* global variable modcNumber */
    mcsINT8 modcNumber = 34;
    logTest("modc.h : global variable modcNumber = %i\n", modcNumber);
    
    /* global variable modcReal */
    mcsFLOAT modcReal = 78.23;
    logTest("modc.h : global variable modcReal = %g\n", modcReal);
    
    /* constante modcPROCNAME_LENGHT */
    logTest("modc.h : constante modcPROCNAME_LENGHT = %i\n", \
            modcPROCNAME_LENGHT);
    
    /* constante modcPROCNAME_ID */
    logTest("modc.h : constante modcPROCNAME_ID = %i\n", \
            modcPROCNAME_ID);
    
    /* constante modcDEFAULT_CHOICE */
    logTest("modcPrivate.h : constante modcDEFAULT_CHOICE = %s\n", \
            modcDEFAULT_CHOICE);
    
    /* modcPrintChoice macro */
    logTest("modcPrivate.h : modcPrintChoice macro\n");
    logTest("=> call public function modcProc2 :\n");
    if (modcPrintChoice(modcDEFAULT_CHOICE) == FAILURE)
    {
       logTest("ERROR modcPrintChoice\n");
    }

    /* local variable modcId */
    modcId = 5;
    logTest("modcMain.c : local variable modcId = %i\n", modcId);
    
    /* public function modcProc1 */
    logTest("modcProc.c : public function modcProc1 : \n");
    mcsBYTES32 w;
    strcpy (w, "test 1");
    mcsINT8 i = 7;
    if (modcProc1(w, i) == FAILURE)
    {
        logTest("ERROR modcProc1\n");
    }
    
    /* modcPrint macro */
    logTest("modc.h : modcPrint macro\n");
    logTest("=> call public function modcProc1 : \n");
    if (modcPrint(w, i) == FAILURE)
    {
        logTest("ERROR modcPrint");
    }
    
    /* public function modcProc2 */
    logTest("modcProc.c : public function modcProc2 : \n");
    mcsBYTES32 word;
    strcpy (word, "test 2");
    if (modcProc2(word) == FAILURE)
    {
         logTest("ERROR modcProc2\n");
    }

    /** \todo test stuct, enum and union type */


    exit (EXIT_SUCCESS);
}


/*___oOo___*/
