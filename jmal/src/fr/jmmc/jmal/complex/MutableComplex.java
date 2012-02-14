/**
 * *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 * ****************************************************************************
 */
/*
 * MutableComplex class inspired from apache commons-math-2.2:
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.jmmc.jmal.complex;

/**
 * Representation of a mutable Complex number - a number which has both a real and imaginary part. <p> Implementations of
 * arithmetic operations handle
 * <code>NaN</code> and infinite values according to the rules for {@link java.lang.Double} arithmetic, applying
 * definitional formulas and returning
 * <code>NaN</code> or infinite values in real or imaginary parts as these arise in computation. See individual method
 * javadocs for details.</p> <p>
 * {@link #equals} identifies all values with
 * <code>NaN</code> in either real or imaginary part - e.g.,
 * <pre>
 * <code>1 + NaNi  == NaN + i == NaN + NaNi.</code></pre></p>
 *
 * implements Serializable since 2.0
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public final class MutableComplex implements Complex {

    /** mutable flag to indicate if this class is mutable or not */
    private static final boolean MUTABLE = true;

    /* members */
    /** The real part. */
    private double real;
    /** The imaginary part. */
    private double imaginary;
    /** Record whether this complex number is equal to NaN. */
    private boolean isNaN;
    /** Record whether this complex number is infinite. */
    private boolean isInfinite;

    /**
     * Create a mutable complex number (zero).
     */
    public MutableComplex() {
        this(0d, 0d);
    }

    /**
     * Create a mutable complex number given the real and imaginary parts.
     *
     * @param real the real part
     * @param imaginary the imaginary part
     */
    public MutableComplex(final double real, final double imaginary) {
        /* same code in updateComplex(real, imaginary); */
        this.real = real;
        this.imaginary = imaginary;

        if (CHECK_NAN_INF) {
            this.isNaN = Double.isNaN(real) || Double.isNaN(imaginary);
            this.isInfinite = !this.isNaN && (Double.isInfinite(real) || Double.isInfinite(imaginary));
        } else {
            this.isNaN = false;
            this.isInfinite = false;
        }
    }

    /**
     * Return the absolute value of this complex number. <p> Returns
     * <code>NaN</code> if either real or imaginary part is
     * <code>NaN</code> and
     * <code>Double.POSITIVE_INFINITY</code> if neither part is
     * <code>NaN</code>, but at least one part takes an infinite value.</p>
     *
     * @return the absolute value
     */
    @Override
    public double abs() {
        if (CHECK_NAN_INF) {
            if (this.isNaN) {
                return Double.NaN;
            }

            if (this.isInfinite) {
                return Double.POSITIVE_INFINITY;
            }
        }
        return ImmutableComplex.abs(real, imaginary);
    }

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
    @Override
    public double getArgument() {
        return ImmutableComplex.getArgument(real, imaginary);
    }

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
    @Override
    public Complex add(final Complex rhs) {
        return updateOrCreateComplex(real + rhs.getReal(), imaginary + rhs.getImaginary());
    }

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
    @Override
    public Complex conjugate() {
        if (CHECK_NAN_INF) {
            if (this.isNaN) {
                return updateOrCreateComplex(NaN);
            }
        }
        return updateOrCreateComplex(real, -imaginary);
    }

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
    @Override
    public Complex divide(final Complex rhs) {
        if (CHECK_NAN_INF) {
            if (this.isNaN || rhs.isNaN()) {
                return updateOrCreateComplex(NaN);
            }
        }

        final double c = rhs.getReal();
        final double d = rhs.getImaginary();
        if (c == 0.0d && d == 0.0d) {
            return updateOrCreateComplex(NaN);
        }

        if (CHECK_NAN_INF) {
            if (rhs.isInfinite() && !this.isInfinite) {
                return updateOrCreateComplex(ZERO);
            }
        }

        if (abs(c) < abs(d)) {
            final double q = c / d;
            final double denominator = c * q + d;
            return updateOrCreateComplex((real * q + imaginary) / denominator, (imaginary * q - real) / denominator);
        } else {
            final double q = d / c;
            final double denominator = d * q + c;
            return updateOrCreateComplex((imaginary * q + real) / denominator, (imaginary - real * q) / denominator);
        }
    }

    /**
     * Test for the equality of two Complex objects. <p> If both the real and imaginary parts of two
     * Complex numbers are exactly the same, and neither is
     * <code>Double.NaN</code>, the two Complex objects are considered to be equal.</p> <p> All
     * <code>NaN</code> values are considered to be equal - i.e, if either (or both) real and imaginary parts of the
     * complex number are equal to
     * <code>Double.NaN</code>, the complex number is equal to
     * <code>Complex.NaN</code>.</p>
     *
     * @param other Object to test for equality to this
     * @return true if two Complex objects are equal, false if object is null, not an instance of Complex,
     * or not equal to this Complex instance
     *
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex) {
            final Complex rhs = (Complex) other;
            if (CHECK_NAN_INF && rhs.isNaN()) {
                return this.isNaN;
            } else {
                return (this.real == rhs.getReal()) && (this.imaginary == rhs.getImaginary());
            }
        }
        return false;
    }

    /**
     * Get a hashCode for the complex number. <p> All NaN values have the same hash code.</p>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        if (CHECK_NAN_INF) {
            if (this.isNaN) {
                return 7;
            }
        }
        return 37 * (17 * hashcodeDouble(imaginary) + hashcodeDouble(real));
    }

    /**
     * Access the imaginary part.
     *
     * @return the imaginary part
     */
    @Override
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Access the real part.
     *
     * @return the real part
     */
    @Override
    public double getReal() {
        return real;
    }

    /**
     * Returns true if either or both parts of this complex number is NaN; false otherwise
     *
     * @return true if either or both parts of this complex number is NaN; false otherwise
     */
    @Override
    public boolean isNaN() {
        return this.isNaN;
    }

    /**
     * Returns true if either the real or imaginary part of this complex number takes an infinite value (either
     * <code>Double.POSITIVE_INFINITY</code> or
     * <code>Double.NEGATIVE_INFINITY</code>) and neither part is
     * <code>NaN</code>.
     *
     * @return true if one or both parts of this complex number are infinite and neither part is
     * <code>NaN</code>
     */
    @Override
    public boolean isInfinite() {
        return this.isInfinite;
    }

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
    @Override
    public Complex multiply(final Complex rhs) {
        if (CHECK_NAN_INF) {
            if (this.isNaN || rhs.isNaN()) {
                return updateOrCreateComplex(NaN);
            }
            if (this.isInfinite || rhs.isInfinite()) {
                return updateOrCreateComplex(INF);
            }
        }
        return updateOrCreateComplex(real * rhs.getReal() - imaginary * rhs.getImaginary(), real * rhs.getImaginary() + imaginary * rhs.getReal());
    }

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
    @Override
    public Complex multiply(final double rhs) {
        if (CHECK_NAN_INF) {
            if (this.isNaN || Double.isNaN(rhs)) {
                return updateOrCreateComplex(NaN);
            }
            if (this.isInfinite || Double.isInfinite(rhs)) {
                return updateOrCreateComplex(INF);
            }
        }
        return updateOrCreateComplex(real * rhs, imaginary * rhs);
    }

    /**
     * Return the additive inverse of this complex number. <p> Returns
     * <code>Complex.NaN</code> if either real or imaginary part of this Complex number equals
     * <code>Double.NaN</code>.</p>
     *
     * @return the negation of this complex number
     */
    @Override
    public Complex negate() {
        if (CHECK_NAN_INF) {
            if (this.isNaN) {
                return updateOrCreateComplex(NaN);
            }
        }
        return updateOrCreateComplex(-real, -imaginary);
    }

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
    @Override
    public Complex subtract(final Complex rhs) {
        if (CHECK_NAN_INF) {
            if (this.isNaN || rhs.isNaN()) {
                return updateOrCreateComplex(NaN);
            }
        }
        return updateOrCreateComplex(real - rhs.getReal(), imaginary - rhs.getImaginary());
    }

    /**
     * Create a complex number (immutable) or update this instance given the real and imaginary parts.
     *
     * @param other the other complex instance
     * @return a new complex number instance if immutable or this updated instance
     */
    private Complex updateOrCreateComplex(final Complex other) {
        if (MUTABLE) {
            updateComplex(other);
            return this;
        }
        return other;
    }

    /**
     * Update the complex number given the real and imaginary parts.
     *
     * @param other the other complex instance
     */
    private void updateComplex(final Complex other) {
        // does nothing in ImmutableComplex
        // does following in MutableComplex
        this.real = other.getReal();
        this.imaginary = other.getImaginary();

        this.isNaN = other.isNaN();
        this.isInfinite = other.isInfinite();
    }

    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param realPart the real part
     * @param imaginaryPart the imaginary part
     * @return a new complex number instance
     */
    private Complex updateOrCreateComplex(final double realPart, final double imaginaryPart) {
        if (MUTABLE) {
            updateComplex(realPart, imaginaryPart);
            return this;
        }
        return new ImmutableComplex(realPart, imaginaryPart);
    }

    /**
     * Update the complex number given the real and imaginary parts.
     *
     * @param real the real part
     * @param imaginary the imaginary part
     */
    @Override
    public void updateComplex(final double real, final double imaginary) {
        // does nothing in ImmutableComplex
        // does following in MutableComplex
        this.real = real;
        this.imaginary = imaginary;

        if (CHECK_NAN_INF) {
            this.isNaN = Double.isNaN(real) || Double.isNaN(imaginary);
            this.isInfinite = !this.isNaN && (Double.isInfinite(real) || Double.isInfinite(imaginary));
        } else {
            this.isNaN = false;
            this.isInfinite = false;
        }
    }

    // FastMath utils:
    /**
     * Absolute value.
     *
     * @param x number from which absolute value is requested
     * @return abs(x)
     */
    private static double abs(final double x) {
        return (x < 0.0d) ? -x : (x == 0.0d) ? 0.0d : x; // -0.0 => +0.0
    }

    /**
     * Returns a hash code for this {@code Double} object. The result is the exclusive OR of the two halves of the
     * {@code long} integer bit representation, exactly as produced by the method {@link #doubleToLongBits(double)}, of
     * the primitive {@code double} value represented by this
     * {@code Double} object. That is, the hash code is the value of the expression:
     *
     * <blockquote>
     *  {@code (int)(v^(v>>>32))} </blockquote>
     *
     * where {@code v} is defined by:
     *
     * <blockquote>
     *  {@code long v = Double.doubleToLongBits(this.doubleValue());} </blockquote>
     *
     * @param value double value
     * @return a {@code hash code} value for this object.
     */
    private static int hashcodeDouble(final double value) {
        final long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }
}
