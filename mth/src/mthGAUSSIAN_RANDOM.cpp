/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthGAUSSIAN_RANDOM.cpp,v 1.1 2006-06-09 12:13:12 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 *  Definition of mthGAUSSIAN_RANDOM class.
 */

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
#include "mthGAUSSIAN_RANDOM.h"

/**
 * Class constructor
 */
mthGAUSSIAN_RANDOM::mthGAUSSIAN_RANDOM()
{
    _skipStep   = mcsFALSE ;
    _sigma      = 1.0;
    _mean         = 0.0;
    _keepInMind = 0.0;
    _seed       = this->SetSeedUsingClock();
}

mthGAUSSIAN_RANDOM::mthGAUSSIAN_RANDOM(mcsDOUBLE mean, mcsDOUBLE stdDev)
{
    _skipStep   = mcsFALSE ;
    _sigma      = stdDev ;
    _mean       = mean ;
    _keepInMind = 0.0;
    _seed       = this->SetSeedUsingClock();
}

/**
 * Class destructor
 */
mthGAUSSIAN_RANDOM::~mthGAUSSIAN_RANDOM()
{
}

/*
 * Public methods
 */

/**
 * Get the seed value
 * 
 * @return    seed value
 */
unsigned int mthGAUSSIAN_RANDOM::GetSeed()
{
    return _seed;
}

/**
 * Set mean value
 *
 * @param mean       mean value
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT mthGAUSSIAN_RANDOM::SetMean(mcsDOUBLE mean)
{
    _mean = mean;
    return mcsSUCCESS;
}

/**
 * Get the mean value
 *
 * @return mean value  
 */
mcsDOUBLE mthGAUSSIAN_RANDOM::GetMean()
{
    return _mean; 
}

/**
 * Set standard deviation value
 *
 * @param  stdDev   standard deviation value
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT mthGAUSSIAN_RANDOM::SetStdDev(mcsDOUBLE stdDev)
{
    _sigma = fabs(stdDev);
    return mcsSUCCESS;
}

/**
 * Get standard deviation value
 *
 * @return standard deviation value
 */
mcsDOUBLE mthGAUSSIAN_RANDOM::GetStdDev()
{
    return _sigma; 
}

/**
 * Get a normal variate with mean equal to 'mean' and standard deviation equal
 * to 'stdDev' previously set by user.
 *
 * @note If 'mean' or 'stdDev' are not set, the default values mean=0 and
 * stdDev=1 are used
 *
 * @return normal variate
 *
 * @sa GetRNorm(mcsDOUBLE mean, mcsDOUBLE stdDev)
 */
mcsDOUBLE mthGAUSSIAN_RANDOM::GetRNorm()
{
    if(_sigma == 0.0)
    {
        return _mean;
    }
    else
    {
        return (_mean + (_sigma * GetRStdNorm()));
    }

}

/**
 * Get a normal variate with mean equal to 'mean'
 * and standard deviation equal to 'stdDev'.
 *
 * @note If 'mean' or 'stdDev' are not specified they assume 
 * the default values
 *
 * @param  mean      mean value
 * @param  stdDev   std deviation value
 *
 * @return normal variate
 *
 * @sa GetRNorm()
 */
mcsDOUBLE mthGAUSSIAN_RANDOM::GetRNorm(mcsDOUBLE mean, mcsDOUBLE stdDev)
{
    if (stdDev == 0.0)
    {
        return mean;
    }
    else
    {
        return (mean + (stdDev * GetRStdNorm()));
    }
}


/*
 * Private methods
 */

/**
 * Set the seed of the unix random number generator
 *
 * @return value of the seed
 */
unsigned int mthGAUSSIAN_RANDOM::SetSeedUsingClock()
{
    unsigned int seed = (unsigned int)time(NULL) ;
    (void) srand(seed);  
    return seed;
}

/**
 * Compute normal standard deviate
 *
 * @return     Normal standard deviate 
 */
mcsDOUBLE mthGAUSSIAN_RANDOM::GetRStdNorm()
{
    if (_skipStep == mcsTRUE)
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

/*___oOo___*/
