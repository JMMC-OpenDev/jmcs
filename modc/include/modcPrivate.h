#ifndef modcPrivate_H
#define modcPrivate_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

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
