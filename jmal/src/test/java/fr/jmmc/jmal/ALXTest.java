/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class performs few tests on ALX class
 * @author bourgesl
 */
public class ALXTest {

    /**
     * Test of parseHMS method, of class ALX.
     */
    @Test
    public void testParseHMS() {
        System.out.println("parseHMS");

        if (true) {
            String raHms = "";
            double expResult = Double.NaN;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        // HH:MM:SS.TT or HH MM SS.TT
        if (true) {
            String raHms = "01:23:45.67";
            double expResult = 20.940291666666667d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "01 23 45.67";
            double expResult = 20.940291666666667d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "12 00 00.0";
            double expResult = 180.0d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "23 59 59.999";
            double expResult = 359.9999958333333d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        // degenerated:
        if (true) {
            String raHms = "01:23.45";
            double expResult = 20.8625d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "01.5";
            double expResult = 22.5d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
    }

    /**
     * Test of parseRA method, of class ALX.
     */
    @Test
    public void testParseRA() {
        System.out.println("parseRA");

        if (true) {
            String raHms = "";
            double expResult = Double.NaN;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
        // HH:MM:SS.TT or HH MM SS.TT
        if (true) {
            String raHms = "01:23:45.67";
            double expResult = 20.940291666666667d;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "01 23 45.67";
            double expResult = 20.940291666666667d;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "12 00 00.0";
            double expResult = 180.0d;
            double result = ALX.parseHMS(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "23 59 59.999";
            double expResult = -4.1666667129902635E-6d;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
        // degenerated:
        if (true) {
            String raHms = "01:23.45";
            double expResult = 20.8625d;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String raHms = "01.5";
            double expResult = 22.5d;
            double result = ALX.parseRA(raHms);
            assertEquals(expResult, result, 0.0);
        }
    }

    /**
     * Test of parseDEC method, of class ALX.
     */
    @Test
    public void testParseDEC() {
        System.out.println("parseDEC");

        if (true) {
            String dec = "";
            double expResult = Double.NaN;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        // DD:MM:SS.TT or DD MM SS.TT
        if (true) {
            String dec = "20:01:23.456";
            double expResult = 20.02318222222222d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "+20:01:23.456";
            double expResult = 20.02318222222222d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "-20:01:23.456";
            double expResult = -20.02318222222222d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "20 01 23.456";
            double expResult = 20.02318222222222d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "90:00:00.0";
            double expResult = 90.0d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "-90:00:00.0";
            double expResult = -90.0d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        // degenerated:
        if (true) {
            String dec = "20:01.23456";
            double expResult = 20.020576d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
        if (true) {
            String dec = "20.01";
            double expResult = 20.01d;
            double result = ALX.parseDEC(dec);
            assertEquals(expResult, result, 0.0);
        }
    }

    /**
     * Test of toHMS method, of class ALX.
     */
    @Test
    public void testToHMS() {
        System.out.println("toHMS");

        if (true) {
            double raDeg = -24.0 * ALX.HOUR_IN_DEGREES;
            String expResult = "-24:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = -12.0 * ALX.HOUR_IN_DEGREES;
            String expResult = "-12:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 0.0;
            String expResult = "00:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 4.0;
            String expResult = "00:16:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 4.123456789123456789;
            String expResult = "00:16:29.62963";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 12.0;
            String expResult = "00:48:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 1.0 * ALX.HOUR_IN_DEGREES;
            String expResult = "01:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 12.0 * ALX.HOUR_IN_DEGREES;
            String expResult = "12:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 24.0 * ALX.HOUR_IN_DEGREES;
            String expResult = "24:00:00";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 10.0 * ALX.MILLI_ARCSEC_IN_DEGREES * ALX.HOUR_IN_DEGREES;
            String expResult = "00:00:00.01";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 1.0 * ALX.MILLI_ARCSEC_IN_DEGREES * ALX.HOUR_IN_DEGREES;
            String expResult = "00:00:00.001";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = 0.5 * ALX.MILLI_ARCSEC_IN_DEGREES * ALX.HOUR_IN_DEGREES;
            String expResult = "00:00:00.0005";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double raDeg = (0.5 - 3.0 * Math.ulp(0.5)) * ALX.MILLI_ARCSEC_IN_DEGREES * ALX.HOUR_IN_DEGREES;
            String expResult = "00:00:00.0005";
            String result = ALX.toHMS(raDeg);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of toDMS method, of class ALX.
     */
    @Test
    public void testToDMS() {
        System.out.println("toDMS");

        if (true) {
            double decDeg = -90.0;
            String expResult = "-90:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = -30.0;
            String expResult = "-30:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = -5.523456789123456789;
            String expResult = "-05:31:24.4444";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = -5.0;
            String expResult = "-05:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 0.0;
            String expResult = "+00:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 5.0;
            String expResult = "+05:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 5.523456789123456789;
            String expResult = "+05:31:24.4444";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 30.0;
            String expResult = "+30:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 90.0;
            String expResult = "+90:00:00";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 10.0 * ALX.MILLI_ARCSEC_IN_DEGREES;
            String expResult = "+00:00:00.01";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 1.0 * ALX.MILLI_ARCSEC_IN_DEGREES;
            String expResult = "+00:00:00.001";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = 0.5 * ALX.MILLI_ARCSEC_IN_DEGREES;
            String expResult = "+00:00:00.0005";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
        if (true) {
            double decDeg = (0.5 - 3.0 * Math.ulp(0.5)) * ALX.MILLI_ARCSEC_IN_DEGREES;
            String expResult = "+00:00:00.0005";
            String result = ALX.toDMS(decDeg);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of parse + toHMS method, of class ALX.
     */
    @Test
    public void testFixRA() {
        System.out.println("parseRA");

        if (true) {
            String raHms = "";
            String expResult = "";
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
        // HH:MM:SS.TT or HH MM SS.TT
        if (true) {
            String raHms = "01:23:45.67";
            String expResult = raHms;
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
        if (true) {
            String raHms = "12:00:00.0";
            String expResult = "12:00:00";
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
        if (true) {
            String raHms = "23:59:59.999";
            String expResult = raHms;
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
        // degenerated:
        if (true) {
            String raHms = "01:23.45";
            String expResult = "01:23:27";
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
        if (true) {
            String raHms = "01.5";
            String expResult = "01:30:00";
            String result = fixRA(raHms);
            assertEquals(expResult, result);
        }
    }

    /**
     * Fix RA: parse given value as HMS and re-format to HMS (normalization)
     * @param ra right ascension as HMS
     * @return right ascension as HMS
     */
    public static String fixRA(final String ra) {
        return ALX.toHMS(ALX.parseHMS(ra));
    }

    /**
     * Fix DEC: parse given value as DMS and re-format to DMS (normalization)
     * @param dec declination as DMS
     * @return declination as DMS
     */
    public static String fixDEC(final String dec) {
        return ALX.toDMS(ALX.parseDEC(dec));
    }
}
