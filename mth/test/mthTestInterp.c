/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Interpolation function tests.
 */



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
    mcsINT32 i, j;
    /* Number of points defining the segment curve */
    const mcsINT32 nbOfCurvePoints = 362268;
    /* Number of points to interpolate */
    const mcsINT32 nbOfPointsToInterp = nbOfCurvePoints;
    /* wavelength */
    mcsDOUBLE lbdaMin = 380;
    mcsDOUBLE lbdaMax = 690;
    mcsDOUBLE deltaLbda = (lbdaMax - lbdaMin) / nbOfPointsToInterp;
    mcsDOUBLE newLbdaMin = 400;
    mcsDOUBLE newLbdaMax = 700;
    mcsDOUBLE curveDeltaLbda = (newLbdaMax - newLbdaMin) / nbOfCurvePoints;
    /* Input x list */
    mcsDOUBLE * xList = malloc(nbOfPointsToInterp * sizeof(mcsDOUBLE));
    if (xList == NULL)
    {
        printf("ERROR : malloc() for xList failled !\n");
        exit(EXIT_FAILURE);
    }
    /* Shifted x list */
    /*mcsDOUBLE *shiftedLbdaList=malloc (nbOfPointsToInterp * sizeof(mcsDOUBLE));*/
    /* Input y list */
    mcsDOUBLE * yList = malloc(nbOfPointsToInterp * sizeof(mcsDOUBLE));
    if (yList == NULL)
    {
        printf("ERROR : malloc() for yList failled !\n");
        exit(EXIT_FAILURE);
    }
    /* x to interpolate list */
    mcsDOUBLE * xToInterpolateList = 
        malloc(nbOfPointsToInterp * sizeof(mcsDOUBLE));
    if (yList == NULL)
    {
        printf("ERROR : malloc() for xToInterpolateList failled !\n");
        exit(EXIT_FAILURE);
    }
    /* Result y ordinate list */
    /*mcsDOUBLE yInterpolatedList[nbOfPointsToInterp];*/
    /* Blanking value */
    mcsDOUBLE blankingVal = 0.0;
    /* Radial velocity */
    mcsDOUBLE radVeloc = 1265;

    mcsDOUBLE radVelocForVisibleCell = 2691; 
    mcsINT32 nbOflbda = nbOfCurvePoints; 
    mcsDOUBLE rForVisibleCell = 0.56;
    mcsDOUBLE projAreaForVisibleCell = 333544;
    mcsINT32 tacheForVisibleCell = 0;
    mcsDOUBLE tPh = 789;
    mcsDOUBLE tSp = 7444;
    mcsDOUBLE *starSpectrum = calloc(nbOflbda, sizeof(mcsDOUBLE));
    mcsDOUBLE *cellFlxSpectrum = calloc(nbOflbda, sizeof(mcsDOUBLE));
    if (cellFlxSpectrum == NULL)
    {
        printf("ERROR : malloc() for cellFlxSpectrum failled !\n");
        exit(EXIT_FAILURE);
    }
    
    /* Data set definition */
    /* Input x list */
    for (i = 0; i < nbOflbda; i++)
    {
        xList[i] = newLbdaMin + (i * curveDeltaLbda);
    }
    /* Input y list */
    for (i = 0; i < nbOflbda; i++)
    {
        yList[i] = rand() / 100;
    }
    /* x to interpolate list */
    printf("nbOfPointsToInterp = %d\n", nbOfPointsToInterp);
    for (i = 0; i < nbOfPointsToInterp; i++)
    {
        xToInterpolateList[i] = lbdaMin + (i * deltaLbda);
    }

#if 0
    /* Print data set */    
    printf("-----------------------------------\n");
    printf("DATA SET\n\n");
    printf("Points contituting the linear curve : %d\n", nbOfCurvePoints);
    /* Print Pi(xi, yi) points  */
    printf("Pi : (xi, yi)\n");
/*     for (i = 0; i < nbOfCurvePoints; i++) */
/*     { */
/*         printf("P%i : (%f, %f)\n", i+1, xList[i], yList[i]); */
/*     } */
    /* Print x to interpolate list */
    printf("\nPoint abscissae to be interpolated : %d\n", nbOfPointsToInterp);
    printf("(xToInterpolateList[0] = %f, ?)\n", xToInterpolateList[0]);
    printf("(xToInterpolateList[1] = %f, ?)\n", xToInterpolateList[1]);
    printf("(xToInterpolateList[362266] = %f, ?)\n", 
           xToInterpolateList[362266]);
    printf("(xToInterpolateList[362267] = %f, ?)\n", 
           xToInterpolateList[362267]);

#endif    
    /* Interpolation test */
#if 0
    /* test 1 */ 
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
    printf("P1 : (%f, %f)\n", xToInterpolateList[0], yInterpolatedList[0]);
    printf("P2 : (%f, %f)\n", xToInterpolateList[1], yInterpolatedList[1]);
    printf("P362267 : (%f, %f)\n", xToInterpolateList[362266], 
           yInterpolatedList[362266]);
    printf("P362268 : (%f, %f)\n", xToInterpolateList[362267], 
           yInterpolatedList[362267]);
#endif
#if 0    
    /* test 2 */ 
    printf("\n-----------------------------------\n");
    printf("DOPPLER SHIFT + INTERPOLATION\n\n");
    system("date");
    for (i = 0; i < 1024; i++)
    {
        /*printf("DOPPLER SHIFT\n\n");*/
        if (mthDopplerShift(radVeloc, nbOfPointsToInterp, xList, 
                            shiftedLbdaList) == mcsFAILURE)
        {
            printf("ERROR : mthDopplerShift() failed\n");
            exit(EXIT_FAILURE);
        }
        /*printf("INTERPOLATION with blanking value different from NULL\n\n");*/
        /*printf("blankingVal = %f\n", blankingVal);*/

        /* Interpolate y list */ 
        if (mthLinInterp(nbOfCurvePoints, shiftedLbdaList, yList, 
                         nbOfPointsToInterp, xToInterpolateList, 
                         yInterpolatedList, &blankingVal) == mcsFAILURE)
        {
            printf("ERROR : mthInterp() failed\n");
            exit(EXIT_FAILURE);
        }
    }
    system("date");
    /* Print interpolated y list */    
    printf("\n-----------------------------------\n");
    printf("RESULTS\n\n");
    printf("P1 : (%f, %f)\n", xToInterpolateList[0], yInterpolatedList[0]);
    printf("P2 : (%f, %f)\n", xToInterpolateList[1], yInterpolatedList[1]);
    printf("P362267 : (%f, %f)\n", xToInterpolateList[362266], 
           yInterpolatedList[362266]);
    printf("P362268 : (%f, %f)\n", xToInterpolateList[362267], 
           yInterpolatedList[362267]);
#endif    

    /* test 3 */ 
    printf("\n-----------------------------------\n");
    printf("COMPUTE CELL SPECTRUM\n\n");
    system("date");
    for (i = 0; i < 1024; i++)
    {
        tacheForVisibleCell = !(i % 8);
        if (mthComputeCellSpectrum(radVelocForVisibleCell, 
                                   nbOflbda, 
                                   xList, 
                                   yList, 
                                   xToInterpolateList, 
                                   rForVisibleCell, 
                                   projAreaForVisibleCell, 
                                   tacheForVisibleCell, 
                                   tPh, 
                                   tSp, 
                                   cellFlxSpectrum) == mcsFAILURE)
        {
            printf("ERROR : mthComputeCellSpectrum() !\n");
        }
#if 1
        for (j = 0; j < nbOflbda; j++)
        {
            starSpectrum[j] += cellFlxSpectrum[j];
        }
#endif
    }
    system("date");

    /* Free pointers; */
    free(xList);
    free(yList);
    free(xToInterpolateList);
    free(cellFlxSpectrum);
    
    printf("\nInterp tests ended\n");
    printf("*********************************************\n");


    /* Exit from the application with SUCCESS */
    exit(EXIT_SUCCESS);
}


/*___oOo___*/
