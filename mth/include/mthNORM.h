#ifndef RANDNORM_H
#define RANDNORM_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthNORM.h,v 1.1 2006-06-02 13:33:03 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/*!
 * @file
 * Declaration of mthNORM class.
 *
 **/

#ifndef __cplusplus
#warning This is a C++ include file and cannot be used from plain C
#else

/*
 * MCS header
 */
#include "mcs.h"
#include <math.h>

/*!
 * This class provides facilities to generate Normal (gaussian) random
 * numbers, using the Box-Muller algorithm. 
 * 
 * if \f$(u_1,u_2)\f$ is a set of two variates uniformly distributed between -1
 * and 1, then the two numbers \f$(g_1,g_2)\f$ computed from 
 *
 * \f[
 *     g_1 = u_1 \times \sqrt{2\ln r^2 \over r^2}  
 *     \quad{\rm and}\quad
 *     g_2 = u_2 \times \sqrt{2\ln r^2 \over r^2}  
 * \f]
 * where \f${r^2 = u_1^2+u_2^2}\f$ are normal distributed. 
 **/
class mthNORM
{

public:
    // Class constructor
    mthNORM();
    mthNORM(mcsDOUBLE,mcsDOUBLE);

    // Access methods to mean
    virtual mcsCOMPL_STAT   SetMean(mcsDOUBLE); 
    virtual mcsDOUBLE       GetMean();          
    
    // Access methods to standard deviation 
    virtual mcsCOMPL_STAT   SetStdDev(mcsDOUBLE);
    virtual mcsDOUBLE       GetStdDev();

    // Access method to normal variate
    virtual mcsDOUBLE       GetRNorm();
    virtual mcsDOUBLE       GetRNorm(mcsDOUBLE,mcsDOUBLE);
    
    // Acces method to the value of the seed, set during the instantiation of
    // the current object
    virtual unsigned int    GetSeed(); 
    
    // Class destructor
    virtual ~mthNORM();

protected:

    virtual unsigned int    SetSeedUsingClock();    // Set seed
    virtual mcsDOUBLE       GetRStdNorm();          // normal STANDARD variate   
    
    // private members 
    mcsLOGICAL              _skipStep ;
    mcsDOUBLE               _keepInMind ;
    mcsDOUBLE               _sigma ;
    mcsDOUBLE               _mu ;
    unsigned int            _seed ;

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mthNORM(const mthNORM&);
    mthNORM& operator=(const mthNORM&);
};

#endif
#endif /*!RANDNORM_H*/

/*___oOo___*/
