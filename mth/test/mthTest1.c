/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mthTest1.c,v 1.1 2006-02-01 07:45:57 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Test program in order to check the mth library (McDonald funxtions K1 and K0)
 *
 * @synopsis
 * This piece of code 
 * 
 */

static char *rcsId="@(#) $Id: mthTest1.c,v 1.1 2006-02-01 07:45:57 lsauge Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>


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
 * Main
 */

int main (int argc, char *argv[])
{
    /* Initializes MCS services */
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        /* Error handling if necessary */
        
        /* Exit from the application with FAILURE */
        exit (EXIT_FAILURE);
    }

    /* Local variables */
    const mcsINT16  nbOfData = 1000 ;
    const mcsDOUBLE xMax     =    4.0 ;
    const mcsDOUBLE xMin     =    0.0 ;
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
    fprintf(stdout,"Creating file ..."); 
    fflush(stdout);
    fDesc=fopen("besselK-func.dat","w");
    if(fDesc==NULL)
    {
        fprintf(stderr,"Error creating/opening file.\n");
        fflush(stderr);
        errCloseStack();
        exit(EXIT_FAILURE);
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
       *(yK0+i) = mthBessK0(*(x+i)) ;
       *(yK1+i) = mthBessK1(*(x+i)) ;
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

    /* Close MCS services */
    mcsExit();
    
    /* Exit from the application with SUCCESS */
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
