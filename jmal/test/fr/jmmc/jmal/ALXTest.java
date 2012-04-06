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
            double expResult = 0.0;
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
            double expResult = 0.0;
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
            double expResult = 0.0;
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
}
