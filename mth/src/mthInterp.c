/*******************************************************************************
 * LAOG project
 * 
 * "@(#) $Id: mthInterp.c,v 1.2 2007-07-09 15:41:33 gluck Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Interpolation functions.
 * 
 * This file contains functions relative to:
 *  - linear interpolation
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mthInterp.c,v 1.2 2007-07-09 15:41:33 gluck Exp $"; 


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
 * @param nbOfCurvePoints number of points defining the segment curve
 * @param xList abscissae array of the points which constitute the segments
 * @param yList ordinates array of the points which constitute the segments
 * @param nbOfPointsToInterp number of points to interpolate
 * @param xToInterpList abscissae array of the points to interpolate
 * @param yInterpolatedList ordinate array of points that have been interpolated
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
                           mcsDOUBLE * const yInterpolatedList)
{
    
    /* index loop */
    mcsINT32 i, j;

    /* Check whether the x to interpolate are on the curve */
    /* Check whether the x to interpolate is not lower than the minimum
     * value of the curve */
    if (xToInterpList[0] < xList[0])
    {
        printf("ERROR : x1 = %f is out of x curve range [%f, %f]\n", 
               xToInterpList[0], xList[0], xList[nbOfCurvePoints - 1]);
        return mcsFAILURE;
    }
    /* Check if the x to interpolate is greater than the maximum value of the
     * curve */
    if (xToInterpList[nbOfPointsToInterp - 1] > xList[nbOfCurvePoints - 1])
    {
        printf("ERROR : x%d = %f is out of x curve range [%f, %f]\n", 
               nbOfPointsToInterp, 
               xToInterpList[nbOfPointsToInterp - 1], xList[0], 
               xList[nbOfCurvePoints - 1]);
        return mcsFAILURE;
    }


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
    for (i = 0; i < nbOfPointsToInterp; i++)
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
