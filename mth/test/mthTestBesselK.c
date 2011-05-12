/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Test program in order to check the mth library (McDonald funxtions K1 and K0)
 *
 * @synopsis
 * Code could help the user to represent sampled mthBessK0 and mthBessK1 - 
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mthTestBesselK.c,v 1.4 2006-06-09 12:18:35 gzins Exp $";

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>
#include <math.h>


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "mthBesselK.h"
#include "mthPrivate.h"

/*
 * Function definition
 * */
/* 
 * Evaluating function for test of the implementation of BessK0 and BessK1
 * McDonald function (modified Bessel function of the first kind).
 *
 * Functions could be evaluated on a standard dec-dec scale or on a log-log one
 * and sampled on a equally spaced set of values in their respective
 * representation. 
 * Note that in the log-log case, the lower/upper bounds refer to their value 
 * in the log-log scale.
 *
 * RETURN VALUE : The function return EXIT_SUCCES upon successful completion or 
 *                EXIT_FAILURE otherwise. 
 * */
static mcsINT16 mthEvalFunction(const mcsDOUBLE  xMin,
                                const mcsDOUBLE  xMax,
                                const mcsINT16   nbOfData, 
                                const mcsLOGICAL logScale)
{
    /* Local variables */
    const mcsDOUBLE dTiny = 1.0e-99; 
    mcsDOUBLE xIncr = (xMax-xMin)/((mcsDOUBLE)(nbOfData-1));

    mcsDOUBLE *x;
    mcsDOUBLE *yK0;
    mcsDOUBLE *yK1;


    /* Allocated memory */
    x   = (mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    yK0 = (mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    yK1 = (mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));

    mcsINT16 i; /* loop index */
    for( i=0 ; i<nbOfData ; i++)
    {
        *( x +i) = xMin + ((mcsDOUBLE)i)*xIncr ;
        /* user require evaluation on a geometrical ladder */
        if(logScale==mcsTRUE)
        {
            mcsDOUBLE xDec = pow(10.0,*(x+i));
            *(yK0+i) = log10(mthBessK0( xDec )+dTiny) ;
            *(yK1+i) = log10(mthBessK1( xDec )+dTiny) ;
        }
        /* otherwise, evaluation are made on an 
         * equally spaced decimal ladder */
        else
        {
            *(yK0+i) = mthBessK0(*(x+i)) ;
            *(yK1+i) = mthBessK1(*(x+i)) ;
        }
        printf("% 5.2e  % 5.2e  % 5.2e\n",
               *( x +i),
               *(yK0+i),
               *(yK1+i));
    } /* End of loop */

    /* Free memory before exiting */
    free( x );
    free(yK0);
    free(yK1);

    return(EXIT_SUCCESS);
}


/* 
 * Main
 */

int main (int argc, char *argv[])
{
    /* Initializes MCS services */
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        /* Exit from the application with FAILURE */
        exit (EXIT_FAILURE);
    }

    /* =================================================================== */ 
    
    /* Local variables */
    mcsINT16 status; /*  returned status from function evaluation */ 
   
    /* Functions are firstly evaluated on a standard equally spaced decimal
     * ladder */
    printf ("\n ** dec-dec scale\n");
    status = mthEvalFunction(0.0 , 4.0 , 1000, mcsFALSE);
    if (status == EXIT_FAILURE)
    {
        errCloseStack();
        exit(EXIT_FAILURE);
    }
    /* and then, secondly functions are now evaluated on a log-log scale */
    printf ("\n ** log-log scale\n");
    status = mthEvalFunction(-10.0 , 3.0 , 1000, mcsTRUE);
    if (status == EXIT_FAILURE)
    {
        errCloseStack();
        exit(EXIT_FAILURE);
    }
    
    /* =================================================================== */ 

    /* Close MCS services */
    mcsExit();
    
    /* Exit from the application with SUCCESS */
    exit (EXIT_SUCCESS);
}





/*___oOo___*/
