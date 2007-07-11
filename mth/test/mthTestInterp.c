/*******************************************************************************
 * LAOG project
 *
 * "@(#) $Id: mthTestInterp.c,v 1.3 2007-07-11 07:41:55 gluck Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/07/11 06:45:04  gluck
 * - Better declaration of yInterpolatedList
 * - Minor details
 *
 * Revision 1.1  2007/07/09 15:29:15  gluck
 * Added
 *
 ******************************************************************************/

/**
 * @file
 * Interpolation function tests.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthTestInterp.c,v 1.3 2007-07-11 07:41:55 gluck Exp $"; 


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>



/*
 * Local Headers 
 */
#include "mthInterp.h"
#include "mthPrivate.h"
#include "mcs.h"



/*
 * Local Variables
 */

 

/* 
 * Signal catching functions  
 */



/* 
 * Main
 */

int main (int argc, char *argv[])
{
    printf("*********************************************\n");
    printf("Begin Interp tests ...\n\n");
    printf("--> Testing mthLinInterp function ...\n\n");

    /* index */
    mcsINT32 i;

    
    /* Data set definition */
    /* Input x list */
    mcsDOUBLE xList[5] = {1, 2, 4, 5, 6};
    /* Input y list */
    mcsDOUBLE yList[5] = {1, 2, 1, 6, 3};
    /* number of points defining the segment curve */
    const mcsINT32 nbOfCurvePoints = sizeof(xList) / sizeof(xList[0]);
    /* Input x to interpolate list */
    const mcsDOUBLE xToInterpolateList[18] = {-0.3, 0.2, 0.5, 1, 1.5, 2, 2.5, 
                                              3, 3.5, 4, 4.25, 4.5, 4.75, 5, 
                                              5.5, 6, 8, 21};
    /* number of of points to interpolate */
    const mcsINT32 nbOfPointsToInterp = sizeof(xToInterpolateList) / 
                                        sizeof(xToInterpolateList[0]);
    /* Result y ordinate list */
    mcsDOUBLE yInterpolatedList[nbOfPointsToInterp];
    /* Blanking value */
    mcsDOUBLE * blankingVal = NULL;

    
    /* Print data set */    
    printf("-----------------------------------\n");
    printf("DATA SET\n\n");
    printf("Points contituting the linear curve : %d\n", nbOfCurvePoints);
    /* Print Pi(xi, yi) points  */
    printf("Pi : (xi, yi)\n");
    for (i = 0; i < nbOfCurvePoints; i++)
    {
        printf("P%i : (%f, %f)\n", i+1, xList[i], yList[i]);
    }
    /* Print x to interpolate list */
    printf("\nPoint abscissae to be interpolated : %d\n", nbOfPointsToInterp);
    for (i = 0; i < nbOfPointsToInterp; i++)
    {
        printf("(%f, ?)\n", xToInterpolateList[i]);
    }

    
    /* Test interpolation */
    printf("\n-----------------------------------\n");
    printf("INTERPOLATION with blanking value = NULL\n\n");
    
    /* Interpolate y list */ 
    if (mthLinInterp(nbOfCurvePoints, xList, yList, nbOfPointsToInterp,
                     xToInterpolateList, yInterpolatedList, blankingVal) == 
        mcsFAILURE)
    {
        printf("ERROR : mthInterp() failed\n");
        exit(EXIT_FAILURE);
    }
    /* Print interpolated y list */    
    printf("-----------------------------------\n");
    printf("RESULTS\n\n");
    for (i = 0; i < nbOfPointsToInterp; i++)
    {
        printf("P%d : (%f, %f)\n", i+1, xToInterpolateList[i], 
                                        yInterpolatedList[i]);
    }

    printf("\n-----------------------------------\n");
    printf("INTERPOLATION with blanking value different from NULL\n\n");
    blankingVal = malloc(sizeof(mcsDOUBLE));
    if (blankingVal == NULL)
    {
        printf("ERROR : malloc failed !");
        exit(EXIT_FAILURE);
    }
    *blankingVal = 0.00000;
    printf("blankingVal = %f\n", *blankingVal);

    /* Interpolate y list */ 
    if (mthLinInterp(nbOfCurvePoints, xList, yList, nbOfPointsToInterp,
                     xToInterpolateList, yInterpolatedList, blankingVal) == 
        mcsFAILURE)
    {
        printf("ERROR : mthInterp() failed\n");
        /* Free pointers */
        free(blankingVal);
        exit(EXIT_FAILURE);
    }
    /* Print interpolated y list */    
    printf("\n-----------------------------------\n");
    printf("RESULTS\n\n");
    for (i = 0; i < nbOfPointsToInterp; i++)
    {
        printf("P%d : (%f, %f)\n", i+1, xToInterpolateList[i], 
                                        yInterpolatedList[i]);
    }
    printf("\nInterp tests ended\n");
    printf("*********************************************\n");


    /* Free pointers */
    free(blankingVal);
    /* Exit from the application with SUCCESS */
    exit(EXIT_SUCCESS);
}


/*___oOo___*/
