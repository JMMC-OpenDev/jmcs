/*******************************************************************************
* JMMC project
*
* "@(#) $Id: modcMain.c,v 1.1 2004-06-29 15:21:19 gluck Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* gluck     11-Jun-2004  Created
*
*
* IMPORTANT:
* To make AUTOMATIC DOCUMENTATION GENERATION by doxygen, you have to insert
* your code documentation (about file, functions, define, enumeration, ...) as
* shown below, in the special documentation blocks, adding or deleting markers
* as needed.
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
 * \e \<Command Name\> - brief description of the program, which ends at
 * this dot.
 *
 * \b Synopsis:
 * \n
 * \e \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * \b Details:
 * \n
 * OPTIONAL detailed description of the c main file follows here.
 * 
 * \b Files:
 * \n
 * OPTIONAL. If files are used, for each one, name, and usage
 * description.
 * \li \e \<fileName1\> :  usage description of fileName1
 * \li \e \<fileName2\> :  usage description of fileName2
 *
 * \b Environment:
 * \n
 * OPTIONAL. If needed, environmental variables accessed by the program. For each
 * variable, name, and usage description, as below.
 * \li \e \<envVar1\> :  usage description of envVar1
 * \li \e \<envVar2\> :  usage description of envVar2
 * 
 * \warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * \b Code \b Example:
 * \n
 * OPTIONAL. Command example if needed
 *
 * \code
 * Insert your command example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * 
 * \bug OPTIONAL. Known bugs list if it exists. You can make a list with the
 * \li marker, like in the Files section above.
 * 
 * \todo OPTIONAL. Things to forsee list, if needed. You can make a list with
 * the \li marker, like in the Files section above.
 * 
 * \n
 */

static char *rcsId="@(#) $Id: modcMain.c,v 1.1 2004-06-29 15:21:19 gluck Exp $"; 
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
 * Local functions declaration 
 */

/* IMPORTANT : doxygen extracted documentation for local functions is located
 * just below in this file. It's why a normal documentation block is used here
 * with a brief description (just to know a little about the function) and NOT
 * A DOXYGEN DOCUMENTATION BLOCK 
 */

/* Brief description of the procedure */
static mcsCOMPL_STAT modcAdd(mcsINT8 x, mcsINT8 y);


/* 
 * Local functions definition
 */

/**
 * Brief description of the function, which ends at this dot.
 *
 * OPTIONAL detailed description of the function follows here.
 *
 * \param x description of parameter x. In the example, a number.
 * \param y description of parameter y. In the example, a number.
 * 
 * \n
 * \return Description of the return value. In the example, SUCCESS or FAILURE. 
 *
 * \n
 */
static mcsCOMPL_STAT modcAdd(mcsINT8 x, mcsINT8 y)
{
    mcsINT8 z;
    z=x+y;
    printf ("%d + %d = %d\n", x, y, z);

    return SUCCESS;
}


/* 
 * Main
 */

int main (int argc, char *argv[])
{
    /* global variable modcNumber */
    mcsINT8 modcNumber = 34;
    printf ("\n* modc.h : global variable modcNumber = %i\n", modcNumber);
    
    /* global variable modcReal */
    mcsFLOAT modcReal = 78.23;
    printf ("\n* modc.h : global variable modcReal = %g\n", modcReal);
    
    /* constante modcPROCNAME_LENGHT */
    printf ("\n* modc.h : constante modcPROCNAME_LENGHT = %i\n", \
            modcPROCNAME_LENGHT);
    
    /* constante modcPROCNAME_ID */
    printf ("\n* modc.h : constante modcPROCNAME_ID = %i\n", \
            modcPROCNAME_ID);
    
    /* constante modcDEFAULT_CHOICE */
    printf ("\n* modcPrivate.h : constante modcDEFAULT_CHOICE = %s\n", \
            modcDEFAULT_CHOICE);
    
    /* modcPrintChoice macro */
    printf ("\n* modcPrivate.h : modcPrintChoice macro\n");
    printf ("\t=> call public function modcProc2 :\n");
    if (modcPrintChoice(modcDEFAULT_CHOICE) == FAILURE)
    {
       printf ("ERROR modcPrintChoice\n");
    }

    /* local variable modcId */
    modcId = 5;
    printf ("\n* modcMain.c : local variable modcId = %i\n", modcId);
    
    /* local function modcAdd */
    printf ("\n* modcMain.c : local function modcAdd : ");
    mcsINT8 Id2 = 3;
    if (modcAdd(modcId, Id2) == FAILURE)
    {
        printf ("\tERROR modcAdd\n");
    }
    
    /* public function modcProc1 */
    printf ("\n* modcProc.c : public function modcProc1 : ");
    mcsBYTES32 w;
    strcpy (w, "test 1");
    mcsINT8 i = 7;
    if (modcProc1(w, i) == FAILURE)
    {
        printf ("ERROR modcProc1\n");
    }
    
    /* modcPrint macro */
    printf ("\n* modc.h : modcPrint macro\n");
    printf ("\t=> call public function modcProc1 : ");
    if (modcPrint(w, i) == FAILURE)
    {
        printf ("ERROR modcPrint\n");
    }
    
    /* public function modcProc2 */
    printf ("\n* modcProc.c : public function modcProc2 : \n");
    mcsBYTES32 word;
    strcpy (word, "test 2");
    if (modcProc2(word) == FAILURE)
    {
         printf ("ERROR modcProc2\n");
    }

        
    printf ("\n");
    
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
