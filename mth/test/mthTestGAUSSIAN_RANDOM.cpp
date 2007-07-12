/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthTestGAUSSIAN_RANDOM.cpp,v 1.1 2006-06-09 12:19:24 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Providing facilities to generate Normal (gaussian) distributed random
 * numbers
 *
 **/

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthTestGAUSSIAN_RANDOM.cpp,v 1.1 2006-06-09 12:19:24 gzins Exp $"; 

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

/**
 * @namespace std
 * Export standard iostream objects (cin, cout,...).
 */
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
#include "mth.h"
#include "mthPrivate.h"

#include <math.h>

/*
 * Local Variables
 */

 

/* 
 * Signal catching functions  
 */



/* 
 * Main
 */


typedef struct 
{
    mcsDOUBLE mean;
    mcsDOUBLE var;
    mcsDOUBLE skweness;
    mcsDOUBLE kurtosis;
    mcsUINT16 size;
    mcsDOUBLE *vec;
} mthSTAT;

static mcsDOUBLE mthGetMean(mcsDOUBLE*vec,mcsUINT16 size)
{
    mcsDOUBLE sum=0.0;
    for(mcsUINT16 idx=0 ; idx<size ; idx++)
    {
        sum += *(vec+idx);
    }
    return sum/(mcsDOUBLE)size;
}

static void mthPerformStat(mthSTAT *stat)
{
    if(stat->size>1)
    {
        mcsDOUBLE mean = mthGetMean(stat->vec,stat->size);
        mcsDOUBLE s2  = 0.0;
        mcsDOUBLE s3  = 0.0;
        mcsDOUBLE s4  = 0.0;

        for(mcsUINT16 idx=0 ; idx<stat->size ; idx++)
        {
            mcsDOUBLE xiC = *(stat->vec+idx)-mean;
            mcsDOUBLE t2  = xiC*xiC; 
            mcsDOUBLE t3  =  t2*xiC; 
            mcsDOUBLE t4  =  t3*xiC; 
            s2 += t2 ;
            s3 += t3 ;
            s4 += t4 ;
        }
        // Standardized moments
        s2 = s2/(mcsDOUBLE)(stat->size-1);
        s3 = s3*pow(s2,-1.5)/(mcsDOUBLE)(stat->size-1);
        s4 = s4/(s2*s2)/(mcsDOUBLE)(stat->size-1);

        stat->mean     = mean       ;
        stat->var      = s2         ;
        stat->skweness = s3         ;
        stat->kurtosis = s4 - 3.0   ;
    }
    else
    {
        // NAN is defined in math.h
        if(stat->size == 0)
            stat->mean = NAN ;
        else
            stat->mean = *(stat->vec) ;

        stat->var      = NAN ; 
        stat->skweness = NAN ;
        stat->kurtosis = NAN ;
    }
}


static mcsDOUBLE mthSignif(mcsDOUBLE x,mcsUINT16 digits)
{

    mcsINT32  INT  = (mcsINT32)(x*pow(10.0,(mcsDOUBLE)digits)) ;
    mcsDOUBLE DBLE = ((mcsDOUBLE)INT)*pow(10.0,-(mcsDOUBLE)digits); 

    return DBLE;
}



int main(int argc, char *argv[])
{
    double mean   = 5.0;
    double stdDev = 2.0;
    double error;

    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary

        // Exit from the application with FAILURE
        exit (EXIT_FAILURE);
    }

    // set size of the sample
    const mcsUINT16 size= 1000000 ;
    // allocate memory for the random vector 
    mcsDOUBLE *vec = (mcsDOUBLE*)malloc(size*sizeof(mcsDOUBLE));
   
    // Set stat structure
    mthSTAT stat;
    stat.vec  = vec;
    stat.size = size;

    // instantiate new random number generator (rng)
    mthGAUSSIAN_RANDOM *rg = new mthGAUSSIAN_RANDOM();
    // Set mean and std deviation
    rg->SetMean  (mean);
    rg->SetStdDev(stdDev);
    
    // fill the random vector
    for(mcsUINT32 idx = 0 ; idx<size ; idx++)
    {
        *(vec+idx) = rg->GetRNorm();
    }

    // delete rng
    delete(rg);
    
    // perform stat and display it
    (void) mthPerformStat(&stat);
    
    // check result 
    error = fabs ((mean - mthSignif(stat.mean,2)) / mean * 100);
    if (error < 2.0)
    {
        printf("Mean value      OK\n");
    }
    else
    {
        printf("Mean           : %7.3f\n", mthSignif(stat.mean,2));
        printf("Mean value      NOK\n");
    }
    error = fabs ((stdDev - mthSignif(sqrt(stat.var),2)) / stdDev * 100);
    if (error < 2.0)
    {
        printf("Variance value  OK\n");
    }
    else
    {
        printf("Standard dev   : %7.3f\n", mthSignif(sqrt(stat.var),2));
        printf("Variance value  NOK\n");
    }
    if (fabs(mthSignif(stat.skweness,2)) < 0.1)
    {
        printf("Sknewness value OK\n");
    }
    else
    {
        printf("Sknewness      : %7.3f\n", mthSignif(stat.skweness,2));
        printf("Sknewness value NOK\n");
    }
    if (fabs(mthSignif(stat.kurtosis,2)) < 0.1)
    {
        printf("Kurtosis value  OK\n");
    }
    else
    {
        printf("Kurtosis       : %7.3f\n", mthSignif(stat.kurtosis,2));
        printf("Kurtosis value  NOK\n");
    }


    // free vector 
    free(vec);

    // Close MCS services
    mcsExit();
    
    // Exit from the application with SUCCESS
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
