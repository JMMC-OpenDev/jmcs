#ifndef modcppOPERATION_H
#define modcppOPERATION_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: modcppOPERATION.h,v 1.2 2004-07-22 14:19:33 gluck Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* gluck     08-Jul-2004  Created
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
 *
 * OPTIONAL detailed description of the header file follows here.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * Class declaration
 */

/**
 * Brief description of the class, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class follows here.
 *
 * \b Files:\n
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \li \e \<fileName1\> :  usage description of fileName1
 * \li \e \<fileName2\> :  usage description of fileName2
 *
 * \b Environment:\n
 * OPTIONAL. If needed, environmental variables accessed by the class. For
 * each variable, name, and usage description, as below.
 * \li \e \<envVar1\> :  usage description of envVar1
 * \li \e \<envVar2\> :  usage description of envVar2
 * 
 * \warning OPTIONAL. Warning if any (software requirements, ...)
 *
 *  \n \b Code \b Example:\n
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
 * \todo OPTIONAL. Things to forsee list, if needed. For example, add
 * other methods, dealing with.
 * 
 */
class modcppOPERATION 
{

    // IMPORTANT : doxygen extracted documentation for public functions is
    // located in the .C file and not in this header file. It's why a normal
    // documentation block (beginning with 2 slashes) is used here with a
    // brief description (just to know a little about the function) and NOT A
    // DOXYGEN DOCUMENTATION BLOCK (beginning with 1 slash and 2 stars).
    
public:
     // Brief description of the constructor
     modcppOPERATION();
     
     // Brief description of the constructor
     modcppOPERATION(char *name);
     
     // Brief description of the destructor
     virtual ~modcppOPERATION();

     // Brief description of the method
     mcsCOMPL_STAT Add(mcsINT8 x, mcsINT8 y);

     // Brief description of the method
     mcsCOMPL_STAT Divide(mcsINT8 x, mcsINT8 y, mcsFLOAT *z);

     // Brief description of the method
     mcsCOMPL_STAT SubAndMultiply(mcsINT8 x, mcsINT8 y);

     // Brief description of the method
     void SetName(char *name);
     
     // Brief description of the method
     char * GetName();

protected:
     // Brief description of the method
     mcsCOMPL_STAT Sub(mcsINT8 x, mcsINT8 y);

private:    
     /** Brief member description */
     char _name[64];
     
     // Brief description of the method
     mcsCOMPL_STAT Multiply(mcsINT8 x, mcsINT8 y);
};


#endif /*!modcppOPERATION_H*/


/*___oOo___*/
