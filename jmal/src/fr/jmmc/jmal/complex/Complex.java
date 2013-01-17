/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.complex;

/**
 * This interface defines a (Im)mutable Complex type
 * @author bourgesl
 */
public interface Complex {

    /** flag to enable or disable NaN / Infinity checks */
    public boolean CHECK_NAN_INF = false;

    /** The square root of -1. A number representing "0.0 + 1.0i" */
    public Complex I = new ImmutableComplex(0.0d, 1.0d); // immutable complex for safety
    /** A complex number representing "NaN + NaNi" */
    public Complex NaN = new ImmutableComplex(Double.NaN, Double.NaN); // immutable complex for safety
    /** A complex number representing "+INF + INFi" */
    public Complex INF = new ImmutableComplex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY); // immutable complex for safety
    /** A complex number representing "1.0 + 0.0i" */
    public Complex ONE = new ImmutableComplex(1.0d, 0.0d); // immutable complex for safety
    /** A complex number representing "0.0 + 0.0i" */
    public Complex ZERO = new ImmutableComplex(0.0d, 0.0d); // immutable complex for safety

    /**
     * Return the absolute value of this complex number. <p> Returns
     * <code>NaN</code> if either real or imaginary part is
     * <code>NaN</code> and
     * <code>Double.POSITIVE_INFINITY</code> if neither part is
     * <code>NaN</code>, but at least one part takes an infinite value.</p>
     *
     * @return the absolute value
     */
    public double abs();

    /**
     * Return the sum of this complex number and the given complex number. <p> Uses the definitional formula
     * <pre>
     * (a + bi) + (c + di) = (a+c) + (b+d)i
     * </pre></p> <p> If either this or
     * <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise Inifinite and NaN values are returned in the parts of the result according to
     * the rules for
     * {@link java.lang.Double} arithmetic.</p>
     *
     * @param rhs the other complex number
     * @return the complex number sum
     * @throws NullPointerException if
     * <code>rhs</code> is null
     */
    public Complex add(final Complex rhs);

    /**
     * Return the conjugate of this complex number. The conjugate of "A + Bi" is "A - Bi". <p>
     * {@link #NaN} is returned if either the real or imaginary part of this Complex number equals
     * <code>Double.NaN</code>.</p> <p> If the imaginary part is infinite, and the real part is not NaN, the returned
     * value has infinite imaginary part of the opposite sign - e.g. the conjugate of
     * <code>1 + POSITIVE_INFINITY i</code> is
     * <code>1 - NEGATIVE_INFINITY i</code></p>
     *
     * @return the conjugate of this Complex object
     */
    public Complex conjugate();

    /**
     * Return the quotient of this complex number and the given complex number. <p> Implements the definitional formula
     * <pre><code>
     *    a + bi          ac + bd + (bc - ad)i
     *    ----------- = -------------------------
     *    c + di         c<sup>2</sup> + d<sup>2</sup>
     * </code></pre> but uses <a href="http://doi.acm.org/10.1145/1039813.1039814"> prescaling of operands</a> to limit
     * the effects of overflows and underflows in the computation.</p> <p> Infinite and NaN values are handled /
     * returned according to the following rules, applied in the order presented: <ul> <li>If either this or
     * <code>rhs</code> has a NaN value in either part,
     *  {@link #NaN} is returned.</li> <li>If
     * <code>rhs</code> equals {@link #ZERO}, {@link #NaN} is returned. </li> <li>If this and
     * <code>rhs</code> are both infinite,
     * {@link #NaN} is returned.</li> <li>If this is finite (i.e., has no infinite or NaN parts) and
     * <code>rhs</code> is infinite (one or both parts infinite),
     * {@link #ZERO} is returned.</li> <li>If this is infinite and
     * <code>rhs</code> is finite, NaN values are returned in the parts of the result if the {@link java.lang.Double}
     * rules applied to the definitional formula force NaN results.</li> </ul></p>
     *
     * @param rhs the other complex number
     * @return the complex number quotient
     * @throws NullPointerException if
     * <code>rhs</code> is null
     */
    public Complex divide(final Complex rhs);

    /**
     * Access the imaginary part.
     *
     * @return the imaginary part
     */
    public double getImaginary();

    /**
     * Access the real part.
     *
     * @return the real part
     */
    public double getReal();

    /**
     * Returns true if either or both parts of this complex number is NaN; false otherwise
     *
     * @return true if either or both parts of this complex number is NaN; false otherwise
     */
    public boolean isNaN();

    /**
     * Returns true if either the real or imaginary part of this complex number takes an infinite value (either
     * <code>Double.POSITIVE_INFINITY</code> or
     * <code>Double.NEGATIVE_INFINITY</code>) and neither part is
     * <code>NaN</code>.
     *
     * @return true if one or both parts of this complex number are infinite and neither part is
     * <code>NaN</code>
     */
    public boolean isInfinite();

    /**
     * Return the product of this complex number and the given complex number. <p> Implements preliminary checks for NaN
     * and infinity followed by the definitional formula:
     * <pre><code>
     * (a + bi)(c + di) = (ac - bd) + (ad + bc)i
     * </code></pre> </p> <p> Returns {@link #NaN} if either this or
     * <code>rhs</code> has one or more NaN parts. </p> Returns {@link #INF} if neither this nor
     * <code>rhs</code> has one or more NaN parts and if either this or
     * <code>rhs</code> has one or more infinite parts (same result is returned regardless of the sign of the
     * components). </p> <p> Returns finite values in components of the result per the definitional formula in all
     * remaining cases. </p>
     *
     * @param rhs the other complex number
     * @return the complex number product
     * @throws NullPointerException if
     * <code>rhs</code> is null
     */
    public Complex multiply(final Complex rhs);

    /**
     * Return the product of this complex number and the given scalar number. <p> Implements preliminary checks for NaN
     * and infinity followed by the definitional formula:
     * <pre><code>
     * c(a + bi) = (ca) + (cb)i
     * </code></pre> </p> <p> Returns {@link #NaN} if either this or
     * <code>rhs</code> has one or more NaN parts. </p> Returns {@link #INF} if neither this nor
     * <code>rhs</code> has one or more NaN parts and if either this or
     * <code>rhs</code> has one or more infinite parts (same result is returned regardless of the sign of the
     * components). </p> <p> Returns finite values in components of the result per the definitional formula in all
     * remaining cases. </p>
     *
     * @param rhs the scalar number
     * @return the complex number product
     */
    public Complex multiply(final double rhs);

    /**
     * Return the additive inverse of this complex number. <p> Returns
     * <code>Complex.NaN</code> if either real or imaginary part of this Complex number equals
     * <code>Double.NaN</code>.</p>
     *
     * @return the negation of this complex number
     */
    public Complex negate();

    /**
     * Return the difference between this complex number and the given complex number. <p> Uses the definitional formula
     * <pre>
     * (a + bi) - (c + di) = (a-c) + (b-d)i
     * </pre></p> <p> If either this or
     * <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise inifinite and NaN values are returned in the parts of the result according to
     * the rules for
     * {@link java.lang.Double} arithmetic. </p>
     *
     * @param rhs the other complex number
     * @return the complex number difference
     * @throws NullPointerException if
     * <code>rhs</code> is null
     */
    public Complex subtract(final Complex rhs);

    /**
     * <p>Compute the argument of this complex number. </p> <p>The argument is the angle phi between the positive real
     * axis and the point representing this number in the complex plane. The value returned is between -PI (not
     * inclusive) and PI (inclusive), with negative values returned for numbers with negative imaginary parts. </p>
     * <p>If either real or imaginary part (or both) is NaN, NaN is returned. Infinite parts are handled as
     * java.Math.atan2 handles them, essentially treating finite parts as zero in the presence of an infinite coordinate
     * and returning a multiple of pi/4 depending on the signs of the infinite parts. See the javadoc for
     * java.Math.atan2 for full details.</p>
     *
     * @return the argument of this complex number
     */
    public double getArgument();

    /**
     * Update the complex number given the real and imaginary parts.
     *
     * @param real the real part
     * @param imaginary the imaginary part
     */
    public void updateComplex(final double real, final double imaginary);
}
