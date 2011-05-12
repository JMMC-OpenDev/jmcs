/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

local package_mthPlugin;
/* DOCUMENT package_mthPlugin -- mthPlugin.i
  Low level mth functions
*/

// Wrapper to C sub-routines 
#include "mthWrapper.i"

/********************* AMBER Data Reduction routines *********************
*
* A complete mth interface has been generated automatically from the library
* header file and is named mthWrapper.i 
* Here is the routines which have to be used to call the mth interface 
*/

/************************************************************************/

func mthPlugin(void)
/* DOCUMENT mthPlugin(void)
    
  DESCRIPTION
    mth plugin low level routines

  VERSION
    $Revision: 1.2 $
    
  FUNCTIONS
    - _mthLinInterp : Performs linear interpolation 

  SEE ALSO
    mth
*/
{
    version = strpart(strtok("$Revision: 1.2 $",":")(2),2:-2);
    if (am_subroutine())
    {
        help, mthPlugin;
    }   
    return version;
}

/************************************************************************/


/************************************************************************/

func mthInterp(y, x, xp)
/* DOCUMENT mthInterp(y, x, xp)

  DESCRIPTION
    Returns the list of interpolated values corresponding to xp abscissae, and
    which lie on the piecewise linear curve; i.e. it performs the same treatment
    than interp yorick function, excepted that it has been optimized for one
    dimensional array.

  CAUTION
    The array y and y must be one dimensional, have numberof(x)>=2, and be
    monotonically increasing

  PARAMETERS
    - y  : ordinates of curve 
    - x  : abscissae of curve
    - xp : abscissae to be interpolated

  RETURN VALUES
    List of interpolated values 

  EXAMPLES
    > nbPoints = 1000
    > x = double(indgen(nbPoints));
    > y = random(nbPoints) * 100.0;
    > xrand = random(nbPoints) * 0.5;
    > xp = x - xrand;
    > xp(1) = 1.0;
    > xp(nbPoints) = nbPoints;
    > yp = mthInterp(y, x, xp);
    > ..
    > plg, y, x
    > plp, yp, xp

  SEE ALSO
    interp
*/
{
    // Check dimension of input arrays 
    // Must be one dimensional array
    if ((dimsof(x)(1) != 1) || (dimsof(y)(1) != 1) || (dimsof(xp)(1) != 1))
    {
        exit("mthInterp - x, y and xp must be one dimensional");
    }
    // Must have at least 2 points 
    if (dimsof(x)(2) < 2) 
    {
        exit("mthInterp - x must contain at least 2 points");
    }
    // Must have same dimension 
    if ((dimsof(y)(2) != dimsof(x)(2)))
    {
        exit("mthInterp - x and y must have same dimension");
    }
    
    // Check array type
    if ((typeof(x) != "double") || 
        (typeof(y) != "double") || 
        (typeof(xp) != "double") )
    {
        exit("mthInterp - x, y and xp must be double");
    }

    // Allocate arry for yp
    yp = array(double, dimsof(xp)(2));

    // Call C-function
    status = __mthLinInterp(dimsof(x)(2), &x, &y, dimsof(xp)(2), &xp, &yp);
    if (status == 1)
    {
        error("mthInterp - could not perform linear interpolation");
    }

    return yp;
}

