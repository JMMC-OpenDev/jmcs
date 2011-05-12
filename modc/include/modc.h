#ifndef modc_H
#define modc_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

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
    modcYELLOW = 1,     /**< enumeration value description */
    modcRED,            /**< enumeration value description */
    modcBLUE = 5        /**< enumeration value description */
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
