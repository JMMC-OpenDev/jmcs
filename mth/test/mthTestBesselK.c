/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthTestBesselK.c,v 1.3 2006-05-11 13:04:56 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/02/01 13:31:07  lsauge
 * Add dTiny value in order to avoid infinity in the log calculation of BesselK1
 * and BesselK0 for large argument value.
 *
 * Revision 1.1  2006/02/01 13:08:40  lsauge
 * Rename mthTestBesselK-1.c to mthTestBesselK.c
 *
 * Revision 1.2  2006/02/01 11:57:23  lsauge
 * Minor Changes
 *
 * Revision 1.1  2006/02/01 11:46:04  lsauge
 * Rename file mthTest1.c to mthTestBesselK-1.c
 *
 * Revision 1.2  2006/02/01 08:41:44  lsauge
 * Test code now permit evaluation of the function on a log-log scale.
 *
 * Revision 1.1  2006/02/01 07:45:57  lsauge
 * Add first relaese of test file
 *
 ******************************************************************************/

/**
 * @file
 * Test program in order to check the mth library (McDonald funxtions K1 and K0)
 *
 * @synopsis
 * Code could help the user to represent sampled mthBessK0 and mthBessK1 - 
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mthTestBesselK.c,v 1.3 2006-05-11 13:04:56 mella Exp $";

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
#include "mth.h"
#include "mthPrivate.h"


/*
 * Function prototypes 
 * */
mcsINT16
EvalFunctionAndSave(const char*, 
                    const mcsDOUBLE, 
                    const mcsDOUBLE, 
                    const mcsINT16, 
                    const mcsLOGICAL); 

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
    status = EvalFunctionAndSave("mthTestBesselK-dec.dat",
                                 0.0 , 4.0 , 1000, 
                                 mcsFALSE);
    if(status==EXIT_FAILURE)
    {
        errCloseStack();
        exit(EXIT_FAILURE);
    }
    /* and then, secondly functions are now evaluated on a log-log scale */
    status = EvalFunctionAndSave("mthTestBesselK-log.dat",
                                 -10.0 , 3.0 , 1000, 
                                 mcsTRUE);
    if(status==EXIT_FAILURE)
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


/*
 * Function definition
 * */
mcsINT16
EvalFunctionAndSave(filename,xMin,xMax,nbOfData,logScale)
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
    const char      *filename ; /* filename */
    const mcsDOUBLE  xMin     ; /* value of the lower bound*/
    const mcsDOUBLE  xMax     ; /* value of the upper bound*/
    const mcsINT16   nbOfData ; /* number of data */
    const mcsLOGICAL logScale ; /* logarithm scale (geometrical ladder) */
{
    /* Local variables */
    const mcsDOUBLE dTiny = 1.0e-99; 
    mcsDOUBLE xIncr = (xMax-xMin)/((mcsDOUBLE)(nbOfData-1));

    mcsDOUBLE *x;
    mcsDOUBLE *yK0;
    mcsDOUBLE *yK1;

    FILE *fDesc ; /* Files descriptor for output */

    /* Allocated memory */
    x  =(mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    yK0=(mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    yK1=(mcsDOUBLE*)malloc(nbOfData*sizeof(mcsDOUBLE));
    
    /* Open file for output */
    fprintf(stdout,"Creating file %s ...",filename); 
    fflush(stdout);
    fDesc=fopen(filename,"w");
    if(fDesc==NULL)
    {
        fprintf(stderr,"Error creating/opening file.\n");
        fflush(stderr);
        return(EXIT_FAILURE);
    }
    fprintf(stdout," done\n"); 
    fflush(stdout);

    /* Main loop over x values */
     
    fprintf(stdout,"Writing file ..."); 
    fflush(stdout);
    
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
        fprintf(fDesc,"% 5.2e  % 5.2e  % 5.2e\n",
               *( x +i),
               *(yK0+i),
               *(yK1+i));
    } /* End of loop */
    fprintf(stdout," done\n"); 
    fflush(stdout);
    
    /* Close file */
    fclose(fDesc);
    /* Free memory before exiting */
    free( x );
    free(yK0);
    free(yK1);

    return(EXIT_SUCCESS);
}



/*___oOo___*/
