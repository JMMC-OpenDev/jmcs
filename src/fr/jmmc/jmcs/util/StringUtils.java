/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.util.regex.Pattern;

/**
 * This class provides several helper methods related to String handling
 * @author Laurent BOURGES.
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
    /** RegExp expression to match white spaces (1..n) */
    private final static Pattern PATTERN_WHITE_SPACE_MULTIPLE = Pattern.compile("\\s+");
    /** regular expression used to match characters different than alpha/numeric/+/- (1..n) */
    private final static Pattern PATTERN_NON_ALPHA_NUM = Pattern.compile("[^a-zA-Z_\\+\\-0-9]+");
    /** regular expression used to match characters different than numeric (1..n) */
    private final static Pattern PATTERN_NON_NUM = Pattern.compile("[^0-9]+");
    /** RegExp expression to match carriage return */
    private final static Pattern PATTERN_CR = Pattern.compile("\n");
    /** RegExp expression to match tags */
    private final static Pattern PATTERN_TAGS = Pattern.compile("\\<.*?\\>");
    /** RegExp expression to SGML entities */
    private final static Pattern PATTERN_AMP = Pattern.compile("&");
    /** RegExp expression to start tag */
    private final static Pattern PATTERN_LT = Pattern.compile("<");
    /** RegExp expression to end tag */
    private final static Pattern PATTERN_GT = Pattern.compile(">");

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
        return PATTERN_WHITE_SPACE_MULTIPLE.matcher(value).replaceAll(replaceBy);
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
        return PATTERN_NON_ALPHA_NUM.matcher(value).replaceAll(replaceBy);
    }

    /**
     * Replace non numeric characters by the given replacement string
     * @param value input value
     * @param replaceBy replacement string
     * @return string value
     */
    public static String replaceNonNumericChars(final String value, final String replaceBy) {
        return PATTERN_NON_NUM.matcher(value).replaceAll(replaceBy);
    }

    /* --- common helper methods ------------------------------ */
    /**
     * Replace carriage return characters by the given replacement string
     * @param value input value
     * @param replaceBy replacement string
     * @return string value
     */
    public static String replaceCR(final String value, final String replaceBy) {
        return PATTERN_CR.matcher(value).replaceAll(replaceBy);
    }

    /**
     * Remove any tag
     * @param value input value
     * @return string value
     */
    public static String removeTags(final String value) {
        return PATTERN_TAGS.matcher(value).replaceAll(STRING_EMPTY);
    }

    /**
     * Encode special characters to entities
     * @param src input string
     * @return encoded value
     */
    public static String encodeTagContent(final String src) {
        String out = PATTERN_AMP.matcher(src).replaceAll("&amp;"); // Character [&] (xml restriction)
        out = PATTERN_LT.matcher(out).replaceAll("&lt;"); // Character [<] (xml restriction)
        out = PATTERN_GT.matcher(out).replaceAll("&gt;"); // Character [>] (xml restriction)
        return out;
    }
}
