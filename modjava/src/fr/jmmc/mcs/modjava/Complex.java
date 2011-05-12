/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.modjava;


/**
 * Complex numbers.
 */
public class Complex
{
    /** real part of the complex number */
    private double real;

    /** imaginary part of the complex number */
    private double imag;

    /**
     * Default constructor.
     */
    public Complex()
    {
        real     = 0.0;
        imag     = 0.0;
    }

    /**
     * Parametred constructor.
     *
     * @param _real real part of the complex number.
     * @param _imag imaginary part of the complex number.
     */
    public Complex(double _real, double _imag)
    {
        real     = _real;
        imag     = _imag;
    }

    /**
     * Add a complex.
     *
     * @param z the complex to be added.
     *
     * @return the result of the addition.
     */
    public Complex add(Complex z)
    {
        Complex temp = new Complex();

        temp.real     = real + z.real;
        temp.imag     = imag + z.imag;

        return temp;
    }

    /**
     * Give back the string representation of a complex.
     *
     * @return the representation of the complex as a String object.
     */
    public String toString()
    {
        String temp = "(" + real + ", " + imag + ")";

        return temp;
    }
}
/*___oOo___*/
