#ifndef modc_H
#define modc_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: modc.h,v 1.3 2004-08-09 09:42:49 gluck Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* gluck     09-Jun-2004  Created
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
 * Brief description of the header file, which ends at this dot.
 */


/* The following piece of code alternates the linkage type to C for all
functions declared within the braces, which is necessary to use the
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

    
/* 
 * Constants definition
 */

#define modcPROCNAME_LENGHT 16   /**< Brief description of the constant, ends
                                  at this dot. OPTIONAL detailed description
                                  of the constant follows here. */

#define modcPROCNAME_ID 4        /**< Brief description of the constant, ends
                                  at this dot. OPTIONAL detailed description
                                  of the constant follows here. */


/* 
 * Macro definition
 */
    
/**
 * Brief description of the macro, ends at this dot.
 *
 * OPTIONAL detailed description of the macro follows here.
 */
#define modcPrint(word, number) modcProc1(word, number)


/*
 * Enumeration type definition
 */

/**
 * Brief description of the enumeration, ends at this dot.
 *
 * OPTIONAL detailed description of the enumeration follows here.
 */
typedef enum 
{
    modcYellow = 1,     /**< enumeration value description */
    modcRed,            /**< enumeration value description */
    modcBlue = 5        /**< enumeration value description */
} modcCOLOR;


/*
 * Structure type definition
 */

/**
 * Brief description of the structure, ends at this dot.
 *
 * OPTIONAL detailed description of the structure follows here.
 */
typedef struct
{
    mcsINT8 modcNo;         /**< Brief field description, ends at this dot.
                              OPTIONAL detailed field description  follows
                              here. */
    mcsINT8 modcQte;        /**< Brief field description, ends at this dot.
                              OPTIONAL detailed field description follows
                              here. */
    mcsFLOAT modcPrice;     /**< Brief field description, ends at this dot.
                              OPTIONAL detailed field description follows
                              here. */
} modcARTICLE;


/*
 * Unions type definition
 */

/**
 * Brief description of the union, ends at this dot.
 *
 * OPTIONAL detailed description of the union follows here.
 */
typedef union 
{
    mcsDOUBLE modcn;    /**< Brief field description, ends at this dot.
                          OPTIONAL detailed field description follows here. */
    mcsFLOAT modcx;     /**< Brief field description, ends at this dot. 
                          OPTIONAL detailed field description follows here. */
} modcENTFLOT;


/* 
 * Global variables declaration
 */

extern mcsINT8 modcNumber;     /**< Brief description of the variable, ends at
                                 this dot. OPTIONAL detailed description of
                                 the variable follows here. */

extern mcsFLOAT modcReal;      /**< Brief description of the variable, ends at
                                 this dot. OPTIONAL detailed description of
                                 the variable follows here. */


/*
 * Pubic functions declaration
 */

/* IMPORTANT : doxygen extracted documentation for public functions is located
 * in the .c file and not in this header file. It's why a normal documentation
 * block (beginning with 1 slash and 1 star) is used here with a brief
 * description (just to know a little about the function) and NOT A DOXYGEN
 * DOCUMENTATION BLOCK (beginning with 1 slash and 2 stars).
 */

/* Brief description of the procedure */
mcsCOMPL_STAT modcProc1(mcsBYTES32 a, mcsINT8 b);

/* Brief description of the procedure */
mcsCOMPL_STAT modcProc2(mcsBYTES8 c);


#ifdef __cplusplus
};
#endif
  
#endif /*!modc_H*/


/*___oOo___*/
