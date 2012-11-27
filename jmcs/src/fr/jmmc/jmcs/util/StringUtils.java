/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

/**
 * This class provides several helper methods related to String handling
 * @author bourgesl
 */
public final class StringUtils {

    // TODO: use Pattern.compile to compile regexp once for all:
    /** empty String constant '' */
    public final static String STRING_EMPTY = "";
    /** String constant containing 1 space character ' ' */
    public final static String STRING_SPACE = " ";
    /** String constant containing 1 underscore character '_' */
    public final static String STRING_UNDERSCORE = "_";
    /** String constant containing 1 minus sign character '-' */
    public final static String STRING_MINUS_SIGN = "-";
    /** regexp expression to match white spaces (1..n) */
    public final static String REGEXP_WHITE_SPACE_MULTIPLE = "\\s+";
    /** regular expression used to match characters different than alpha/numeric/+/- (1..n) */
    public static final String REGEXP_NON_ALPHA_NUM = "[^a-zA-Z_\\+\\-0-9]+";
    /** regexp expression to match carriage return */
    public final static String REGEXP_CR = "\n";
    /** regexp expression to match tags */
    public final static String REGEXP_TAGS = "\\<.*?\\>";

    /**
     * Forbidden constructor
     */
    private StringUtils() {
        super();
    }

    /**
     * Test if value is set ie not empty
     *
     * @param value string value
     * @return true if value is NOT empty
     */
    public static boolean isSet(final String value) {
        return !isEmpty(value);
    }

    /**
     * Test if value is empty (null or no chars)
     * 
     * @param value string value
     * @return true if value is empty (null or no chars)
     */
    public static boolean isEmpty(final String value) {
        return value == null || value.length() == 0;
    }

    /**
     * Test if value is empty (null or no chars after trim)
     * 
     * @param value string value
     * @return true if value is empty (null or no chars after trim)
     */
    public static boolean isTrimmedEmpty(final String value) {
        return value == null || value.trim().length() == 0;
    }

    /* --- common white space helper methods -------------------------------- */
    /**
     * Remove any white space character
     * @param value input value
     * @return string value
     */
    public static String removeWhiteSpaces(final String value) {
        return replaceWhiteSpaces(value, STRING_EMPTY);
    }

    /**
     * Remove redundant white space characters
     * @param value input value
     * @return string value
     */
    public static String removeRedundantWhiteSpaces(final String value) {
        return replaceWhiteSpaces(value, STRING_SPACE);
    }

    /**
     * Replace white space characters (1..n) by the underscore character
     * @param value input value
     * @return string value
     */
    public static String replaceWhiteSpacesByUnderscore(final String value) {
        return replaceWhiteSpaces(value, STRING_UNDERSCORE);
    }

    /**
     * Replace white space characters (1..n) by the minus sign character
     * @param value input value
     * @return string value
     */
    public static String replaceWhiteSpacesByMinusSign(final String value) {
        return replaceWhiteSpaces(value, STRING_MINUS_SIGN);
    }

    /**
     * Replace white space characters (1..n) by the given replacement string
     * @param value input value
     * @param replaceBy replacement string
     * @return string value
     */
    public static String replaceWhiteSpaces(final String value, final String replaceBy) {
        return value.replaceAll(REGEXP_WHITE_SPACE_MULTIPLE, replaceBy);
    }

    /* --- common alpha numeric helper methods ------------------------------ */
    /**
     * Remove any non alpha numeric character
     * @param value input value
     * @return string value
     */
    public static String removeNonAlphaNumericChars(final String value) {
        return replaceNonAlphaNumericChars(value, STRING_EMPTY);
    }

    /**
     * Replace non alpha numeric characters (1..n) by the underscore character
     * @param value input value
     * @return string value
     */
    public static String replaceNonAlphaNumericCharsByUnderscore(final String value) {
        return replaceNonAlphaNumericChars(value, STRING_UNDERSCORE);
    }

    /**
     * Replace non alpha numeric characters by the given replacement string
     * @param value input value
     * @param replaceBy replacement string
     * @return string value
     */
    public static String replaceNonAlphaNumericChars(final String value, final String replaceBy) {
        return value.replaceAll(REGEXP_NON_ALPHA_NUM, replaceBy);
    }
    /* --- common helper methods ------------------------------ */

    /**
     * Replace carriage return characters by the given replacement string
     * @param value input value
     * @param replaceBy replacement string
     * @return string value
     */
    public static String replaceCR(final String value, final String replaceBy) {
        return value.replaceAll(REGEXP_CR, replaceBy);
    }

    /**
     * Remove any tag
     * @param value input value
     * @return string value
     */
    public static String removeTags(final String value) {
        return value.replaceAll(REGEXP_TAGS, STRING_EMPTY);
    }
}
