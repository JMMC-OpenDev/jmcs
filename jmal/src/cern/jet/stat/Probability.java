/*
 Copyright (c) 1999 CERN - European Organization for Nuclear Research.
 Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
 is hereby granted without fee, provided that the above copyright notice appear in all copies and 
 that both that copyright notice and this permission notice appear in supporting documentation. 
 CERN makes no representations about the suitability of this software for any purpose. 
 It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.stat;

import cern.jet.math.Polynomial;
import net.jafama.FastMath;

/**
 * Custom tailored numerical integration of certain probability distributions.
 * <p>
 * <b>Implementation:</b>
 * <dt>
 * Some code taken and adapted from the <A HREF="http://www.sci.usq.edu.au/staff/leighb/graph/Top.html">Java 2D Graph Package 2.4</A>,
 * which in turn is a port from the <A HREF="http://people.ne.mediaone.net/moshier/index.html#Cephes">Cephes 2.2</A> Math Library (C).
 * Most Cephes code (missing from the 2D Graph Package) directly ported.
 *
 * @author peter.gedeck@pharma.Novartis.com
 * @author wolfgang.hoschek@cern.ch
 * @version 0.91, 08-Dec-99
 * 
 * BOURGES: only error...() and normal() functions kept BUT made array constants (avoid GC)
 */
public final class Probability extends cern.jet.math.Constants {

    /** T array used by errorFunction(double) */
    private final static double[] T = {
        9.60497373987051638749E0,
        9.00260197203842689217E1,
        2.23200534594684319226E3,
        7.00332514112805075473E3,
        5.55923013010394962768E4
    };
    /** U array used by errorFunction(double) */
    private final static double[] U = {
        //1.00000000000000000000E0,
        3.35617141647503099647E1,
        5.21357949780152679795E2,
        4.59432382970980127987E3,
        2.26290000613890934246E4,
        4.92673942608635921086E4
    };
    /** P array used by errorFunctionComplemented(double) */
    private final static double[] P = {
        2.46196981473530512524E-10,
        5.64189564831068821977E-1,
        7.46321056442269912687E0,
        4.86371970985681366614E1,
        1.96520832956077098242E2,
        5.26445194995477358631E2,
        9.34528527171957607540E2,
        1.02755188689515710272E3,
        5.57535335369399327526E2
    };
    /** Q array used by errorFunctionComplemented(double) */
    private final static double[] Q = {
        //1.0
        1.32281951154744992508E1,
        8.67072140885989742329E1,
        3.54937778887819891062E2,
        9.75708501743205489753E2,
        1.82390916687909736289E3,
        2.24633760818710981792E3,
        1.65666309194161350182E3,
        5.57535340817727675546E2
    };
    /** R array used by errorFunctionComplemented(double) */
    private final static double[] R = {
        5.64189583547755073984E-1,
        1.27536670759978104416E0,
        5.01905042251180477414E0,
        6.16021097993053585195E0,
        7.40974269950448939160E0,
        2.97886665372100240670E0
    };
    /** S array used by errorFunctionComplemented(double) */
    private final static double[] S = {
        //1.00000000000000000000E0, 
        2.26052863220117276590E0,
        9.39603524938001434673E0,
        1.20489539808096656605E1,
        1.70814450747565897222E1,
        9.60896809063285878198E0,
        3.36907645100081516050E0
    };

    /**
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected Probability() {
    }

    /**
     * Returns the error function of the normal distribution; formerly named <tt>erf</tt>.
     * The integral is
     * <pre>
     *                           x 
     *                            -
     *                 2         | |          2
     *   erf(x)  =  --------     |    exp( - t  ) dt.
     *              sqrt(pi)   | |
     *                          -
     *                           0
     * </pre>
     * <b>Implementation:</b>
     * For <tt>0 <= |x| < 1, erf(x) = x * P4(x**2)/Q5(x**2)</tt>; otherwise
     * <tt>erf(x) = 1 - erfc(x)</tt>.
     * <p>
     * Code adapted from the <A HREF="http://www.sci.usq.edu.au/staff/leighb/graph/Top.html">Java 2D Graph Package 2.4</A>,
     * which in turn is a port from the <A HREF="http://people.ne.mediaone.net/moshier/index.html#Cephes">Cephes 2.2</A> Math Library (C).
     *
     * @param x the argument to the function.
     */
    static public double errorFunction(final double x) throws ArithmeticException {
        if (Math.abs(x) > 1.0) {
            return (1.0 - errorFunctionComplemented(x));
        }
        double z = x * x;
        return x * Polynomial.polevl(z, T, 4) / Polynomial.p1evl(z, U, 5);
    }

    /**
     * Returns the complementary Error function of the normal distribution; formerly named <tt>erfc</tt>.
     * <pre>
     *  1 - erf(x) =
     *
     *                           inf. 
     *                             -
     *                  2         | |          2
     *   erfc(x)  =  --------     |    exp( - t  ) dt
     *               sqrt(pi)   | |
     *                           -
     *                            x
     * </pre>
     * <b>Implementation:</b>
     * For small x, <tt>erfc(x) = 1 - erf(x)</tt>; otherwise rational
     * approximations are computed.
     * <p>
     * Code adapted from the <A HREF="http://www.sci.usq.edu.au/staff/leighb/graph/Top.html">Java 2D Graph Package 2.4</A>,
     * which in turn is a port from the <A HREF="http://people.ne.mediaone.net/moshier/index.html#Cephes">Cephes 2.2</A> Math Library (C).
     *
     * @param a the argument to the function.
     */
    static public double errorFunctionComplemented(final double a) throws ArithmeticException {
        double x;

        if (a < 0.0) {
            x = -a;
        } else {
            x = a;
        }

        if (x < 1.0) {
            return 1.0 - errorFunction(a);
        }

        double z = -a * a;

        if (z < -MAXLOG) {
            if (a < 0) {
                return (2.0);
            } else {
                return (0.0);
            }
        }

        z = FastMath.exp(z);

        double p, q;
        if (x < 8.0) {
            p = Polynomial.polevl(x, P, 8);
            q = Polynomial.p1evl(x, Q, 8);
        } else {
            p = Polynomial.polevl(x, R, 5);
            q = Polynomial.p1evl(x, S, 6);
        }

        double y = (z * p) / q;

        if (a < 0) {
            y = 2.0 - y;
        }

        if (y == 0.0) {
            if (a < 0) {
                return 2.0;
            } else {
                return (0.0);
            }
        }

        return y;
    }

    /**
     * Returns the area under the Normal (Gaussian) probability density
     * function, integrated from minus infinity to <tt>x</tt>.
     * <pre>
     *                            x
     *                             -
     *                   1        | |                 2
     *  normal(x)  = ---------    |    exp( - (t-mean) / 2v ) dt
     *               sqrt(2pi*v)| |
     *                           -
     *                          -inf.
     *
     * </pre>
     * where <tt>v = variance</tt>.
     * Computation is via the functions <tt>errorFunction</tt>.
     *
     * @param mean the mean of the normal distribution.
     * @param variance the variance of the normal distribution.
     * @param x the integration limit.
     */
    static public double normal(final double mean, final double variance, final double x) throws ArithmeticException {
        if (x > 0) {
            return 0.5 + 0.5 * errorFunction((x - mean) / Math.sqrt(2.0 * variance));
        } else {
            return 0.5 - 0.5 * errorFunction((-(x - mean)) / Math.sqrt(2.0 * variance));
        }
    }
}
