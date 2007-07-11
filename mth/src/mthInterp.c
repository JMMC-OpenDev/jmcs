/*******************************************************************************
 * LAOG project
 * 
 * "@(#) $Id: mthInterp.c,v 1.4 2007-07-11 07:41:10 gluck Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/07/11 06:47:33  gluck
 * - Changed prototype: array length transmission
 * - implementation at the limits: out of range x are set to y of the curve extremities
 *
 * Revision 1.2  2007/07/09 15:41:33  gluck
 * segment list (based on segment structure) not used anymore for optimisation/performance reasons
 *
 ******************************************************************************/

/**
 * @file
 * Interpolation functions.
 * 
 * This file contains functions relative to:
 *  - linear interpolation
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthInterp.c,v 1.4 2007-07-11 07:41:10 gluck Exp $"; 


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
    /* Set real number of points to interpolate, that is all points except those
     * out of the segment curve */
    
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

/*___oOo___*/
