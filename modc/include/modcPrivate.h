#ifndef modcPrivate_H
#define modcPrivate_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: modcPrivate.h,v 1.4 2004-08-09 16:34:40 gluck Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* gluck     16-Jun-2004  Created
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
* 2 stars), adding or deleting markers
* as needed.
* Nevertheless, you also have to comment the body code as usually.  For more
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
 * Private header file; the brief description of the header file, which ends
 * at this dot.
 *
 * OPTIONAL detailed description of the header file follows here. 
 * 
 * IMPORTANT : This header file is made to share entities inside the module, ie
 * entities which are "local to the module". Therefore,
 * these entities are not desired to be read through the user standard doxygen
 * extracted documentation, ie user API.  It's why these entities are placed
 * in that "particular" private header file, since normally, these
 * entities should only be used by the module developers and not by module
 * users.
 * 
 * REMARK : It is possible to make these entities appear for the module
 * developper, creating or editing (if it already exists) the doxygen
 * configuration, changing the EXCLUDE_PATTRERNS marker, as shown below.
 * In the ../config/doxyfile file, add the line below
 * EXCLUDE_PATTRERNS =
 *
 */


/* The following piece of code alternates the linkage type to C for all
functions declared within the braces, which is necessary to use the
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

    
/* 
 * Module name
 */
#define MODULE_ID "modc"


/* 
 * Constants definition
 */

#define modcDEFAULT_CHOICE "nothing" /**< Brief description of the constant,
                                      ends at this dot. OPTIONAL detailed
                                      description of the constant follows
                                      here. */


/* 
 * Macro definition
 */

/**
 * Brief description of the macro, ends at this dot.
 *
 * OPTIONAL detailed description of the macro follows here.
 */
#define modcPrintChoice(choice) modcProc2(choice)




#ifdef __cplusplus
};
#endif
  

#endif /*!modcPrivate_H*/

/*___oOo___*/
