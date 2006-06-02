/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthNORM.cpp,v 1.1 2006-06-02 13:14:19 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 *  Definition of mthNORM class.
 *
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthNORM.cpp,v 1.1 2006-06-02 13:14:19 lsauge Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "mthNORM.h"

/**
 * Class constructor
 */
mthNORM::mthNORM()
{
    _skipStep   = mcsFALSE ;
    _sigma      = 1.0;
    _mu         = 0.0;
    _keepInMind = 0.0;
    _seed       = this->SetSeedUsingClock();
}

mthNORM::mthNORM(mcsDOUBLE mu, mcsDOUBLE sigma)
{
    _skipStep   = mcsFALSE ;
    _sigma      = sigma ;
    _mu         = mu ;
    _keepInMind = 0.0;
    _seed       = this->SetSeedUsingClock();
}

/**
 * Class destructor
 */
mthNORM::~mthNORM()
{
}

/*
 * Public methods
 */

/*!
  Get the seed value
  return    seed value
  */
unsigned int
mthNORM::GetSeed()
{
    return _seed;
}

/*!
    Set mean value
    @param mu       mean value
    @return always mcsSUCCESS
*/
mcsCOMPL_STAT mthNORM::SetMean(mcsDOUBLE mu)
{
    _mu = mu;
    return mcsSUCCESS;
}

/*!
    Get the mean value
    @return mean value  
*/
mcsDOUBLE mthNORM::GetMean()
{
    return _mu; 
}

/*!
    Set standard deviation value
    @param  sigma   standard deviation value
    @return always mcsSUCCESS
*/
mcsCOMPL_STAT mthNORM::SetStdDev(mcsDOUBLE sigma)
{
    _sigma = fabs(sigma);
    return mcsSUCCESS;
}

/*!
    Get standard deviation value
    @return standard deviation value
*/
mcsDOUBLE mthNORM::GetStdDev()
{
    return _sigma; 
}

/*!
    Get a normal variate with mean equal to 'mu'
    and standard deviation equal to 'sigma' previously set
    by user.
    @note If 'mu' or 'sigma' are not set they use
    the default values mu=0 and sigma=1

    @return normal variate

    @sa GetRNorm(mcsDOUBLE mu, mcsDOUBLE sigma)
*/

mcsDOUBLE mthNORM::GetRNorm()
{
    if(_sigma == 0.0)
    {
        return _mu;
    }
    else
    {
        return _mu+ _sigma*GetRStdNorm();
    }

}

/*!
    Get a normal variate with mean equal to 'mu'
    and standard deviation equal to 'sigma'.
   
    @note If 'mu' or 'sigma' are not specified they assume 
    the default values

    @param  mu      mean value
    @param  sigma   std deviation value
    
    @return normal variate

    @sa GetRNorm()
*/
mcsDOUBLE mthNORM::GetRNorm(mcsDOUBLE mu, mcsDOUBLE sigma)
{
    if(sigma == 0.0)
    {
        return mu;
    }
    else
    {
        return mu+ sigma*GetRStdNorm();
    }

}


/*
 * Protected methods
 */

/*!
    Set the seed of the unix random number generator
    @return value of the seed
*/
unsigned int 
mthNORM::SetSeedUsingClock()
{
    unsigned int seed = (unsigned int)time(NULL) ;
    (void) srand(seed);  
    return seed;
}

/*!
    Compute normal standard deviate
    @return     Normal standard deviate 
*/
mcsDOUBLE mthNORM::GetRStdNorm()
{
   if(_skipStep)
   {
       _skipStep = mcsFALSE;
       return _keepInMind; 
   }
   else
   {
        _skipStep = mcsTRUE ;
        mcsDOUBLE unif1 = 0.0;
        mcsDOUBLE unif2 = 0.0;
        mcsDOUBLE rsqr  = 0.0;

        do
        {
            unif1 = 2.0*(mcsDOUBLE)rand()/(mcsDOUBLE)RAND_MAX-1.0;
            unif2 = 2.0*(mcsDOUBLE)rand()/(mcsDOUBLE)RAND_MAX-1.0;
            rsqr  = unif1*unif1 + unif2*unif2 ;
        } while ((rsqr==0)||(rsqr>1.0)) ;
    
        mcsDOUBLE cFact = sqrt(-2.0*log(rsqr)/rsqr) ;
        
        _keepInMind = unif2*cFact;
        return unif1*cFact;
   }
}





/*
 * Private methods
 */


/*___oOo___*/
