/*******************************************************************************
 * JMMC project
 * 
 * "@(#) $Id: mthBesselK.c,v 1.1 2006-01-31 10:35:18 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 * Modified Bessel functions. 
 * 
 * This contains definition of the first and second (resp. K0 and K1) Modified
 * Bessel Functions (McDonald functions). 
 */

static char *rcsId="@(#) $Id: mthBesselK.c,v 1.1 2006-01-31 10:35:18 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers
 */
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
 * Public functions definition
 */
/**
 * First Modified Bessel function.
 *
 * This function returns Modified Bessel function of X of the first kind of
 * order 0.
 */
mcsDOUBLE mthBessK0(const mcsDOUBLE x)
{
    const mcsDOUBLE xBig       = 86.4;
    const mcsDOUBLE xVerySmall = 3.2e-8;
    const mcsDOUBLE egam       = .5772156649015329;

    /* Local variables */
    mcsDOUBLE g, t, y;

    /* Bessel value */
    mcsDOUBLE bessK0;

    /* If x is negative */
    if (x <= 0.0)
    {
        bessK0 = 0.0;
    }
    /* Else if x is so large than exp(-x)/sqrt(x) is greater than min real */
    else if (x > xBig)
    {
        bessK0 = 0.0;
    } 
    /* Else if x greater than 4.0 */
    else if (x > 4.0) 
    {
        /* Large X */
        t = 10. / (x + 1.) - 1.;

        /* EXPANSION (0042) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
		    t * (t * (t * 4.4374197988655104e-14 - 
		    1.28108310826991616e-13) + 2.0632889256255488e-13) - 
		    7.31344482663931904e-13) + 2.85481235167705907e-12) - 
		    1.11391758572647639e-11) + 3.49564293256545992e-11) - 
		    2.22829582288833265e-10) + 1.75359321273580603e-10) - 
		    9.41555321137176073e-9) - 4.16044811174114579e-8) - 
		    7.69177622529272933e-7) - 6.3169239833374647e-6) - 
		    9.02553345187404564e-5) - 9.25551464765637133e-4) - 
		    .0172683652385321641) + 1.23688664769425422;
        bessK0 = exp(-(x)) * y / sqrt(x);

    } 
    /* Else if x greater than 2.0 */
    else if (x > 2.) 
    {
        /* Upper middle X */
        t = x - 3.;

        /* EXPANSION (0041) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
		    t * (t * (t * (t * (t * (t * (t * (t * (t * 
		    2.43538242247537459e-12 - 7.39672783987933184e-12) + 
		    9.11109430833001267e-12) - 2.97787564633235128e-11) + 
		    1.28905587479980147e-10) - 4.03424607871960089e-10) + 
		    1.2242498277943297e-9) - 3.88349705250555658e-9) + 
		    1.23923137898346852e-8) - 3.9540325571351842e-8) + 
		    1.2667262941756736e-7) - 4.07851207862189007e-7) + 
		    1.32052261058932425e-6) - 4.30373871727268511e-6) + 
		    1.41376509343622727e-5) - 4.68936653814896712e-5) + 
		    1.57451516235860573e-4) - 5.37145622971910027e-4) + 
		    .00187292939725962385) - .00674459607940169198) + 
		    .0256253646031960321) - .108801882084935132) + 
		    .697761598043851776;
	    bessK0 = exp(-(x)) * y;

    }
    /* Else if x greater than 1.0 */
    else if (x > 1.) 
    {
        /* Lower middle X */
        t = x * 2. - 3.;

        /* EXPANSION (0040) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
		    t * (t * (t * (t * (t * (t * (t * (t * (t * 
		    2.57466288575820595e-12 - 7.83738609108569293e-12) + 
		    9.74410152270679245e-12) - 3.19241059198852137e-11) + 
		    1.37999268074442719e-10) - 4.33326665618780914e-10) + 
		    1.32069362385968867e-9) - 4.20597329258249948e-9) + 
		    1.34790467361340101e-8) - 4.32185089841834127e-8) + 
		    1.39217270224614153e-7) - 4.51017292375200017e-7) + 
		    1.47055796078231691e-6) - 4.83134250336922161e-6) + 
		    1.60185974149720562e-5) - 5.3710120889844176e-5) + 
		    1.82652460089342789e-4) - 6.32678357460594866e-4) + 
		    .00224709729617770471) - .00827780350351692662) + 
		    .0323582010649653009) - .142477910128828254) + 
		    .958210053294896496;
        bessK0 = exp(-(x)) * y;

    }
    /* Else if x greater than 'very small value' */
    else if (x > xVerySmall)
    {
        t = x * 2. * x - 1.;

        /* EXPANSION (0038) EVALUATED AS G(T)  --PRECISION 17E.18 */
        g = t * (t * (t * (t * (t * (t * (t * 1.9067419751456128e-14 + 
		    7.49110736894134794e-12) + 2.16382411824721532e-9) + 
		    4.3456267154615821e-7) + 5.59702338227915383e-5) + 
		    .00407157485171389048) + .132976966478338191) + 
		    1.12896092945412762;

        /* EXPANSION (0039) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * 1.0540771819136e-16 + 
		    5.1686788694633216e-14) + 1.92405264219706684e-11) + 
		    5.19906865800665633e-9) + 9.57878493265929443e-7) + 
		    1.09534292632401542e-4) + .00663513979313943827) + 
		    .152436921799395196) + .261841879258687055;
        bessK0 = -log(x) * g + y;

    } 
    /* Else very small X */
    else
    {
        bessK0 = -(log(x * .5) + egam);
    }
    
    return bessK0;
} 

/*___oOo___*/
