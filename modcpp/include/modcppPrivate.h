#ifndef modcppPrivate_H
#define modcppPrivate_H
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


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/* 
 * Module name
 */
#define MODULE_ID "modcpp"


#endif /*!modcppPrivate_H*/


/*___oOo___*/
