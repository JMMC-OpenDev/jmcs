/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data.preference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class provides several tests on Preferences abstract class (
 * @author bourgesl
 */
public class PreferencesTest {

    /** Logger - get from given class name */
    private static final Logger _logger = LoggerFactory.getLogger(PreferencesTest.class.getName());
    public final static String PREF_BOOLEAN = "myBoolean";
    public final static boolean VAL_BOOLEAN = true;
    public final static String PREF_INTEGER = "myInteger";
    public final static int VAL_INTEGER = 13;
    public final static String PREF_DOUBLE = "myDouble";
    public final static double VAL_DOUBLE = Math.PI;
    public final static String PREF_STR = "myString";
    public final static String VAL_STR = getValString();
    public final static String PREF_STR_LIST = "myStrList";
    public final static List<String> VAL_STR_LIST = Arrays.asList(new String[]{"jmmc", "", "guillaume", "sylvain", "laurent"});

    public static String getValString() {
        String str = "";
        for (int i = 32; i < 127; i++) {
            str += (char) i;
        }
        return str;
    }

    /**
     * Test of getPreference method, of class Preferences.
     */
    @Test
    public void testGetPreference() throws Exception {
        System.out.println("getPreference");
        final Preferences instance = new PreferencesImpl();

        if (true) {
            Object preferenceName = "Undefined";
            try {
                instance.getPreference(preferenceName);

                fail("MissingPreferenceException expected.");

            } catch (MissingPreferenceException mpe) {
                _logger.info("MissingPreferenceException: expected: ", mpe);
            }
            String expResult = null;
            String result = instance.getPreference(preferenceName, true);
            assertEquals(expResult, result);
        }
        if (true) {
            Object preferenceName = PREF_STR;
            String expResult = VAL_STR;
            String result = instance.getPreference(preferenceName);
            assertEquals(expResult, result);
        }
        if (true) {
            Object preferenceName = PREF_STR;
            String expResult = "laurent";
            instance.setPreference(preferenceName, expResult);
            String result = instance.getPreference(preferenceName);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getPreferenceAsBoolean method, of class Preferences.
     */
    @Test
    public void testGetPreferenceAsBoolean() throws Exception {
        System.out.println("getPreferenceAsBoolean");
        final Preferences instance = new PreferencesImpl();

        if (true) {
            Object preferenceName = PREF_BOOLEAN;
            boolean expResult = VAL_BOOLEAN;
            boolean result = instance.getPreferenceAsBoolean(preferenceName);
            assertEquals(expResult, result);
        }
        if (true) {
            Object preferenceName = PREF_BOOLEAN;
            boolean expResult = false;
            instance.setPreference(preferenceName, expResult);
            boolean result = instance.getPreferenceAsBoolean(preferenceName);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getPreferenceAsInt method, of class Preferences.
     */
    @Test
    public void testGetPreferenceAsInt() throws Exception {
        System.out.println("getPreferenceAsInt");
        final Preferences instance = new PreferencesImpl();

        if (true) {
            Object preferenceName = PREF_INTEGER;
            int expResult = VAL_INTEGER;
            int result = instance.getPreferenceAsInt(preferenceName);
            assertEquals(expResult, result, 1e-10d);
        }
        if (true) {
            Object preferenceName = PREF_INTEGER;
            int expResult = 666;
            instance.setPreference(preferenceName, expResult);
            int result = instance.getPreferenceAsInt(preferenceName);
            assertEquals(expResult, result, 1e-10d);
        }
    }

    /**
     * Test of getPreferenceAsDouble method, of class Preferences.
     */
    @Test
    public void testGetPreferenceAsDouble() throws Exception {
        System.out.println("getPreferenceAsDouble");
        final Preferences instance = new PreferencesImpl();

        if (true) {
            Object preferenceName = PREF_DOUBLE;
            double expResult = VAL_DOUBLE;
            double result = instance.getPreferenceAsDouble(preferenceName);
            assertEquals(expResult, result, 1e-10d);
        }
        if (true) {
            Object preferenceName = PREF_DOUBLE;
            double expResult = 123456.789d;
            instance.setPreference(preferenceName, expResult);
            double result = instance.getPreferenceAsDouble(preferenceName);
            assertEquals(expResult, result, 1e-10d);
        }
    }

    /**
     * Test of getPreferenceAsStringList method, of class Preferences.
     */
    @Test
    public void testGetPreferenceAsStringList() throws Exception {
        System.out.println("getPreferenceAsStringList");
        final Preferences instance = new PreferencesImpl();

        if (true) {
            Object preferenceName = PREF_STR_LIST;
            List<String> expResult = VAL_STR_LIST;
            List<String> result = instance.getPreferenceAsStringList(preferenceName);
            assertEquals(expResult, result);
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            List<String> expResult = Arrays.asList(new String[]{"guillaume", "sylvain", "laurent"});
            instance.setPreference(preferenceName, expResult);
            List<String> result = instance.getPreferenceAsStringList(preferenceName);
            assertEquals(expResult, result);
        }

        // test list types:
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            try {
                instance.setPreference(preferenceName, new String[]{"bad", "value"});

                fail("PreferencesException expected.");

            } catch (PreferencesException pe) {
                _logger.info("PreferencesException: expected: ", pe);
            }
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            List<String> expResult = new ArrayList<String>(Arrays.asList(new String[]{"guillaume", "sylvain", "laurent"}));
            instance.setPreference(preferenceName, expResult);
            List<String> result = instance.getPreferenceAsStringList(preferenceName);
            assertEquals(expResult, result);
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            List<String> expResult = new LinkedList<String>(Arrays.asList(new String[]{"guillaume", "sylvain", "laurent"}));
            instance.setPreference(preferenceName, expResult);
            List<String> result = instance.getPreferenceAsStringList(preferenceName);
            assertEquals(expResult, result);
        }

        // test invalid values in list:
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            try {
                instance.setPreference(preferenceName, Arrays.asList(new String[]{"guillaume", null}));

                fail("PreferencesException expected.");

            } catch (PreferencesException pe) {
                _logger.info("PreferencesException: expected: ", pe);
            }
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            try {
                instance.setPreference(preferenceName, Arrays.asList(new String[]{"guillaume", "value|with|separator"}));

                fail("PreferencesException expected.");

            } catch (PreferencesException pe) {
                _logger.info("PreferencesException: expected: ", pe);
            }
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            try {
                instance.setPreference(preferenceName, Arrays.asList(new Object[]{"guillaume", new Object()}));

                fail("PreferencesException expected.");

            } catch (PreferencesException pe) {
                _logger.info("PreferencesException: expected: ", pe);
            }
        }
        if (true) {
            Object preferenceName = PREF_STR_LIST;
            try {
                instance.setPreference(preferenceName, Arrays.asList(new Object[]{"guillaume", 3.14d}));

                fail("PreferencesException expected.");

            } catch (PreferencesException pe) {
                _logger.info("PreferencesException: expected: ", pe);
            }
        }
    }

    private class PreferencesImpl extends Preferences {

        public void setDefaultPreferences() throws PreferencesException {
            setDefaultPreference(PREF_BOOLEAN, VAL_BOOLEAN);
            setDefaultPreference(PREF_INTEGER, VAL_INTEGER);
            setDefaultPreference(PREF_DOUBLE, VAL_DOUBLE);
            setDefaultPreference(PREF_STR, VAL_STR);
            setDefaultPreference(PREF_STR_LIST, VAL_STR_LIST);
        }

        public String getPreferenceFilename() {
            return "fr.jmmc.jmcs.test.properties";
        }

        public int getPreferencesVersionNumber() {
            return 1;
        }
    }
}
