/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Interpolation functions.
 * 
 * This file contains functions relative to:
 *  - linear interpolation
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthInterp.c,v 1.6 2007-07-12 15:14:05 gluck Exp $"; 


/* 
 * System Headers
 */
#include <stdlib.h>
#include <stdio.h>
#include <math.h>



/* 
 * Local Headers
 */
#include "mthInterp.h"
#include "mthPrivate.h"


/*
 * MCS header
 */
#include "mcs.h"



/*
 * Local Variables
 */



/*
 * Local Functions declaration
 */



/* 
 * Local functions definition
 */



/*
 * Public functions definition
 */

/**
 * Linear interpolation function.
 *
 * This interpolation function is a segment linear interpolation function. An
 * interpolation curve is built, which is a segment succession. Between 2 points
 * Pi and Pi+1, with respective coordinates (xi, yi) and (xi+1, yi+1), the
 * interpolation is given by the following formula:
 *
 * For a point P(x, y) which belong to segment i, named Si
 * Si(x) = y = ai (x - xi) + bi
 * where
 *  - ai is the slope: ai = (yi+1 - yi) / (xi+1 - xi)
 *  - bi is the ordinate at the origin: bi = yi
 * 
 * Behaviour at the limit: point to interpolate which are out of the segment
 * curve range are set to 
 * - the blanking value if this one is provided (the blanking value pointer is
 * then different from NULL)
 * - y value of the first and last segment points for the x value lower and
 * greater than the first and last segment points respectively
 * 
 * @param nbOfCurvePoints number of points defining the segment curve
 * @param xList abscissae array of the points which constitute the segments
 * @param yList ordinates array of the points which constitute the segments
 * @param nbOfPointsToInterp number of points to interpolate
 * @param xToInterpList abscissae array of the points to interpolate
 * @param yInterpolatedList ordinate array of points that have been interpolated
 * @param blankingVal pointer on a blanking value
 *
 * @warning
 * For optimisation reason, hypothesis or prerequisites has been taken for the
 * implementation. Then, they should be verified by the calling function before
 * this function call.
 * - curve points (xList, yList) are given in increasing order. If this is not
 * the case, results will be inconsistent
 * - points to interpolate (xToInterpList) are given in increasing order. If
 * this is not the case, results will be inconsistent
 * - abscissae of the point to interpolate belong to the segment curve.
 * Otherwise, the function will raise an error.
 * 
 * 
 * @return  mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT mthLinInterp(const mcsINT32 nbOfCurvePoints,
                           const mcsDOUBLE * xList, 
                           const mcsDOUBLE * yList, 
                           const mcsINT32 nbOfPointsToInterp, 
                           const mcsDOUBLE * xToInterpList, 
                           mcsDOUBLE * const yInterpolatedList,
                           mcsDOUBLE * blankingVal)
{
    
    /* index loop */
    mcsINT32 i, j;
    /* start and stop index for segment search loop */  
    mcsINT32 startSearchIdx = 0;
    mcsINT32 stopSearchIdx = nbOfPointsToInterp;
    
    
    /* Check whether the x to interpolate are on the curve */
    /* For all points to interpolate which are out of segment curve range, set
     * them to
     * - either y value of the first point of the first segment, for x lower
     * than x value of the first point of the first segment
     * - either y value of the last point of the last segment, for x greater
     * than x value of the last point of the last segment
     */
    i = 0;
    while ((xToInterpList[i] < xList[0]) && (i < nbOfPointsToInterp))
    {
        if (blankingVal == NULL)
        {
            /* Set yi to y of the first curve point */
            yInterpolatedList[i] = yList[0];
        }
        else
        {
            yInterpolatedList[i]  = *blankingVal;
        }
        /* Set real number of points to interpolate, that is all points except
         * those before the segment curve */
        i++;
    }
    /* Set index from which the following segment search will start */
    startSearchIdx = i;

    /* Check if the x to interpolate is greater than the maximum value of the
     * curve */
    i= nbOfPointsToInterp - 1;
    while ((xToInterpList[i] > xList[nbOfCurvePoints - 1]) && (i >= 0))
    {
        if (blankingVal == NULL)
        {
            /* Set yi to y of the last curve point */
            yInterpolatedList[i] = yList[nbOfCurvePoints - 1];
        }
        else
        {
            yInterpolatedList[i]  = *blankingVal;
        }
        /* Set real number of points to interpolate, that is all points except
         * those after the segment curve */
        i--;
    }
    /* Set index at which the following segment search will stop */
    stopSearchIdx = i;


    /* Segment search */
    /* For each x to interpolate
     * - get the corresponding segment to which it belongs
     * - interpolate x with the segment parameters */
    /* Index from which the segment search is started : assuming that the list
     * of x to interpolate is in an increasing order, the index is initialised
     * here and not near the while loop as usual, to remind of the last found
     * segment and to restart the search from there rather than from the
     * beginning of the list, and so to gain time
     */
    j = 0;
    for (i = startSearchIdx; i <= stopSearchIdx; i++)
    {
        /* While the x to interpolate does not belong to a segment, go to the
         * following segment */
        /* For optimisation reason, the while loop continuation condition (not
         * ended and not found) has been simplified, assuming that, 
         *  - point list (xList) and x to interpolate list (xToInterpList) are
         *  in increasing order
         *  - it has been checked before that all x to interpolate belong to the
         *  segment curve 
         */
        while (xToInterpList[i] > xList[j+1])
        {
            j++;
        }
        /* a segment has been found */
        /* interpolate x */
        yInterpolatedList[i] = (yList[j+1] - yList[j]) / 
                               (xList[j+1] - xList[j]) * 
                               (xToInterpList[i] - xList[j]) + yList[j];
    }
    
    return mcsSUCCESS;
}


mcsCOMPL_STAT mthDopplerShift(mcsDOUBLE radVeloc,
                              mcsDOUBLE nbOflbda,
                              mcsDOUBLE * lbdaList,
                              mcsDOUBLE * shiftedLbdaList)
{
    /* index loop */
    mcsINT32 i;
    /* Light speed */
    const mcsDOUBLE C = 299735961;

    for (i = 0; i < nbOflbda; i++)
    {
        shiftedLbdaList[i] = lbdaList[i] * ((radVeloc / C) + 1);
    }

    return mcsSUCCESS;
}


mcsCOMPL_STAT mthComputeCellSpectrum(mcsDOUBLE radVelocForVisibleCell, 
                                     mcsINT32 nbOflbda,
                                     mcsDOUBLE * lbdaList, 
                                     mcsDOUBLE * flxList, 
                                     mcsDOUBLE * lbdaToInterpList, 
                                     mcsDOUBLE rForVisibleCell, 
                                     mcsDOUBLE projAreaForVisibleCell,
                                     mcsINT32 tacheForVisibleCell,
                                     mcsDOUBLE tPh,
                                     mcsDOUBLE tSp,
                                     mcsDOUBLE * cellFlxSpectrum)
{
    /* Constants */
    /* Light speed */
    const mcsDOUBLE C = 299735961;
    /* Planck constant */
    const mcsDOUBLE h = 6.63e-34;
    /* Boltzmann constant */
    const mcsDOUBLE kb = 1.38e-23;
    /* eps */
    const mcsDOUBLE eps = 0.6;

    /* Local variables */
    /* index loop */
    mcsINT32 i, j;
    /* Shifted wavelength list */
    mcsDOUBLE * shiftedLbdaList = malloc(nbOflbda * sizeof(mcsDOUBLE));
    if (shiftedLbdaList == NULL)
    {
        printf("ERROR : malloc() for shiftedLbdaList failled !\n");
        return mcsFAILURE;
    }
    /* start and stop index for segment search loop */  
    mcsINT32 startSearchIdx = 0;
    mcsINT32 stopSearchIdx = nbOflbda;
    /* Interpolated flux */
    mcsDOUBLE * interpolatedFlxList = malloc(nbOflbda * sizeof(mcsDOUBLE));
    if (interpolatedFlxList == NULL)
    {
        printf("ERROR : malloc() for interpolatedFlxList failled !\n"); 
        return mcsFAILURE;
    }
    /* Blanking value */
    mcsDOUBLE blankingVal = 0.00;
    /* Angle between the line of sight and the normal vector to the area at the
     * considered point*/
    mcsDOUBLE angle = 0.0;
    /* limb-darkening correction factor */
    mcsDOUBLE ldCorrFactor = 0.0;
    /* Temporary variables */
    mcsDOUBLE tmpCste = 0.0;
    mcsDOUBLE hCTmpCste = 0.0;
    mcsDOUBLE kbtPh = 0.0;
    mcsDOUBLE kbtSp = 0.0;
    /* Transmission coefficient */
    mcsDOUBLE transmissionCoeff = 0.0;


    /*********************************************************************/
    /* Wavelength doppler shift */
    /*********************************************************************/
#if 1
    /* Use temporary variable to decrease number of operations inside the
     * following loop */
    tmpCste = (radVelocForVisibleCell / C) + 1;
    
    for (i = 0; i < nbOflbda; i++)
    {
        /*shiftedLbdaList[i] = lbdaList[i] * ((radVelocForVisibleCell / C) +
         * 1);*/
        shiftedLbdaList[i] = lbdaList[i] * tmpCste;
    }
#endif 
    
    /*********************************************************************/
    /* Wavelength interpolation */
    /*********************************************************************/
    
    /* Check whether the x to interpolate are on the curve */
    /* For all points to interpolate which are out of segment curve range, set
     * them to
     * - either y value of the first point of the first segment, for x lower
     * than x value of the first point of the first segment
     * - either y value of the last point of the last segment, for x greater
     * than x value of the last point of the last segment
     */
#if 1
    i = 0;
    while ((lbdaToInterpList[i] < shiftedLbdaList[0]) && (i < nbOflbda))
    {

        interpolatedFlxList[i]  = blankingVal;
        i++;
    }
    /* Set index from which the following segment search will start */
    startSearchIdx = i;

    /* Check if the x to interpolate is greater than the maximum value of the
     * curve */
    i= nbOflbda - 1;
    while ((lbdaToInterpList[i] > shiftedLbdaList[nbOflbda - 1]) && (i >= 0))
    {
        interpolatedFlxList[i]  = blankingVal;
        i--;
    }
#endif
    /* Set index at which the following segment search will stop */
    stopSearchIdx = i;


    /* Segment search */
    /* For each x to interpolate
     * - get the corresponding segment to which it belongs
     * - interpolate x with the segment parameters */
    /* Index from which the segment search is started : assuming that the list
     * of x to interpolate is in an increasing order, the index is initialised
     * here and not near the while loop as usual, to remind of the last found
     * segment and to restart the search from there rather than from the
     * beginning of the list, and so to gain time
     */
#if 1
    j = 0;
    for (i = startSearchIdx; i <= stopSearchIdx; i++)
    {
        /* While the x to interpolate does not belong to a segment, go to the
         * following segment */
        /* For optimisation reason, the while loop continuation condition (not
         * ended and not found) has been simplified, assuming that, 
         *  - point list (lbdaList) and x to interpolate list (lbdaToInterpList) are
         *  in increasing order
         *  - it has been checked before that all x to interpolate belong to the
         *  segment curve 
         */
        while (lbdaToInterpList[i] > shiftedLbdaList[j+1])
        {
            j++;
        }
        /* a segment has been found */
        /* interpolate x */
        interpolatedFlxList[i] = (flxList[j+1] - flxList[j]) / 
                                 (shiftedLbdaList[j+1] - shiftedLbdaList[j]) * 
                                 (lbdaToInterpList[i] - shiftedLbdaList[j]) + 
                                 flxList[j];
    }
#endif
    /* Free pointer */ 
    free(shiftedLbdaList);

    
    /*********************************************************************/
    /* projection effect + limb-darkening */
    /*********************************************************************/
#if 1
    angle = asin(rForVisibleCell);
    ldCorrFactor = 1 - eps + eps * cos(angle);

    /* Use temporary variable to decrease number of operations inside the
     * following loop */
    tmpCste = ldCorrFactor * projAreaForVisibleCell;
    
    /* For each wavelength */
    for (i = 0; i < nbOflbda; i++)
    {
        cellFlxSpectrum[i] = tmpCste * interpolatedFlxList[i];
    }
#endif   
    /* Free pointer */ 
    free(interpolatedFlxList);

#if 1
    
    /*********************************************************************/
    /* flux attenuation if spot existence */
    /*********************************************************************/
    
    if (tacheForVisibleCell == 1)
    {
        /* Use temporary variable to decrease number of operations inside the
         * following loop */
        hCTmpCste = h * C;
        kbtPh = kb * tPh;
        kbtSp = kb * tSp;
        for (i = 0; i < nbOflbda; i++)
        {
            /* Compute transmission coefficient */
            /*transmissionCoeff = (exp(h * C / (lbdaToInterpList[i] * kb * tPh))                                    - 1) /
                                  (exp(h * C / (lbdaToInterpList[i] * kb * tSp))                                    - 1);*/
            transmissionCoeff = (exp(hCTmpCste / (lbdaToInterpList[i] * kbtPh)) 
                                 - 1) /
                                (exp(hCTmpCste / (lbdaToInterpList[i] * kbtSp)) 
                                 - 1);

            cellFlxSpectrum[i] *= transmissionCoeff;
        }
    }
#endif
    return mcsSUCCESS;
}

/*___oOo___*/
