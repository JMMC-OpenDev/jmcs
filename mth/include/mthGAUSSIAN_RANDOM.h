#ifndef mthGAUSSIAN_RANDOM_H
#define mthGAUSSIAN_RANDOM_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of mthGAUSSIAN_RANDOM class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * MCS header
 */
#include "mcs.h"
#include <math.h>

/**
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
class mthGAUSSIAN_RANDOM
{

public:
    // Class constructor
    mthGAUSSIAN_RANDOM();
    mthGAUSSIAN_RANDOM(mcsDOUBLE,mcsDOUBLE);

    // Access methods to mean
    virtual mcsCOMPL_STAT   SetMean(mcsDOUBLE mean); 
    virtual mcsDOUBLE       GetMean();          
    
    // Access methods to standard deviation 
    virtual mcsCOMPL_STAT   SetStdDev(mcsDOUBLE stdDev);
    virtual mcsDOUBLE       GetStdDev();

    // Access method to normal variate
    virtual mcsDOUBLE       GetRNorm();
    virtual mcsDOUBLE       GetRNorm(mcsDOUBLE mean, mcsDOUBLE stdDev);
    
    // Acces method to the value of the seed, set during the instantiation of
    // the current object
    virtual unsigned int    GetSeed(); 
    
    // Class destructor
    virtual ~mthGAUSSIAN_RANDOM();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    mthGAUSSIAN_RANDOM(const mthGAUSSIAN_RANDOM&);
    mthGAUSSIAN_RANDOM& operator=(const mthGAUSSIAN_RANDOM&);

    virtual unsigned int    SetSeedUsingClock();    // Set seed
    virtual mcsDOUBLE       GetRStdNorm();          // normal STANDARD variate   
    mcsLOGICAL              _skipStep ;
    mcsDOUBLE               _keepInMind ;
    mcsDOUBLE               _sigma ;
    mcsDOUBLE               _mean ;
    unsigned int            _seed ;
};

#endif /*!mthGAUSSIAN_RANDOM_H*/

/*___oOo___*/
