/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Modified Bessel functions. 
 * 
 * This contains definition of the first and second (resp. K0 and K1) Modified
 * Bessel Functions (McDonald functions). 
 */

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
#include "mthBesselK.h"
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

/**
 * Second Modified Bessel function.
 *
 * This function returns Modified Bessel function of X of the first kind of
 * order 1.
 */
mcsDOUBLE mthBessK1(const mcsDOUBLE x)
{
    /* Initilized local data */
    const mcsDOUBLE xSmall = 7.9e-10;
    const mcsDOUBLE xBig   = 707.1;
    const mcsDOUBLE xsest  = 5.9e-39;

    /* Local variables */
    mcsDOUBLE g, t, y;
    
    /* Returned Bessel value */
    mcsDOUBLE bessK1;

    /* if x is negative */
    if (x <= 0.) 
    {
        bessK1 = 0.;
    }
    /* if x argument is smaller that 1/maxreal */
    else if (x <= xsest) 
    {
        bessK1 = 1. / xsest;
    } 
    /* else if x is larger than xBig where xBig is the largest rounded-up
     * real such that (exp(-x)/sqrt(x))>minreal.  
     * */
    else if (x >= xBig) 
    {
        bessK1 = 0.;
    } 
    /* else if x argument is too small */
    else if (x <= xSmall) 
    {
        bessK1 = 1. / x;
    }
    /* else if x<=1 */
    else if (x <= 1.) 
    {
        t = 2.0*x*x - 1.;
        /* EXPANSION (0046) EVALUATED AS G(T)  --PRECISION 17E.18 */
        g = t * (t * (t * (t * (t * (t * (t * 1.189649624399104e-15 + 
            5.33888268665658944e-13) + 1.79784792380155752e-10) + 
            4.32764823642997753e-8) + 6.95300274548206237e-6) + 
            6.71642805873498653e-4) + .0325725988137110495) + 
            .531907865913352762;
        /* EXPANSION (0047) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * 3.298810580198656e-15 + 
            1.40917103024514301e-12) + 4.46828628435618679e-10) + 
            9.96686689273781531e-8) + 1.44612432533006139e-5) + 
            .00120333585658219028) + .0450490442966943726) + 
            .351825828289325536;
        /* returned value */
        bessK1 = 1./x + x*(log(x)*g - y);
    }
    /* else if x<=2 (lower middle x) */
    else if (x <= 2.)
    {
        t = 2.0*x - 3.;
        /* EXPANSION (0048) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
            t * (t * (t * (t * (t * (t * (t * (t * (t * (t * 
            -1.46639291782948454e-11 + 4.27404330568767242e-11) - 
            4.02591066627023831e-11) + 1.28044023949946257e-10) - 
            6.15211416898895086e-10) + 1.82808381381205361e-9) - 
            5.13783508140332214e-9) + 1.54456653909012693e-8) - 
            4.66928912168020101e-8) + 1.40138351985185509e-7) - 
            4.20507152338934956e-7) + 1.26265578331941923e-6) - 
            3.79227698821142908e-6) + 1.13930169202553526e-5) - 
            3.42424912211942134e-5) + 1.0298274670006073e-4) - 
            3.10007681013626626e-4) + 9.3459415438764294e-4) - 
            .00282450787841655951) + .00857388087067410089) - 
            .0262545818729427417) + .0820250220860693888) - 
            .271910714388689413) + 1.24316587355255299;
        /* returned value */
        bessK1 = exp(-(x)) * y;
    }
    /* else if x<=4 (upper middle x) */
    else if (x <= 4.)
    {
        t = x - 3.;
        /* EXPANSION (0049) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
            t * (t * (t * (t * (t * (t * (t * (t * (t * (t * 
            -7.36478297050421658e-12 + 2.1473675106513322e-11) - 
            2.02680401514735862e-11) + 6.44913423545894175e-11) - 
            3.09667392343245062e-10) + 9.20781685906110546e-10) - 
            2.59039399308009059e-9) + 7.79421651144832709e-9) - 
            2.35855618461025265e-8) + 7.0872336669656988e-8) - 
            2.12969229346310343e-7) + 6.40581814037398274e-7) - 
            1.92794586996432593e-6) + 5.80692311842296724e-6) - 
            1.75089594354079944e-5) + 5.28712919123131781e-5) - 
            1.59994873621599146e-4) + 4.85707174778663652e-4) - 
            .00148185472032688523) + .00455865751206724687) - 
            .0142363136684423646) + .0458591528414023064) - 
            .160052611291327173) + .806563480128786903;
        /* returned value */
        bessK1 = exp(-(x)) * y;
    } 
    /* else, for large value of argument x */
    else
    {
        t = 10. / (x + 1.) - 1.;
        /* EXPANSION (0050) EVALUATED AS Y(T)  --PRECISION 17E.18 */
        y = t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (t * (
            t * (t * (t * -4.7785023811158016e-14 + 
            1.3932112294060032e-13) - 2.19287104441802752e-13) + 
            8.58211523713560576e-13) - 2.60774502020271104e-12) + 
            1.72026097285930936e-11) + 6.97075379117731379e-12) + 
            6.77688943857588882e-10) + 3.82717692121438315e-9) + 
            4.86651420008153956e-8) + 4.07563856931843484e-7) + 
            4.32776409784235211e-6) + 4.0472063152849502e-5) + 
            4.29973970898766831e-4) + .00431639434283445364) + 
            .0544845254318931612) + 1.30387573604230402;
        /* returned value */
        bessK1 = exp(-(x)) * y / sqrt(x);
    }
    
    /* Return value and exit */
    return bessK1;
}

/*___oOo___*/
