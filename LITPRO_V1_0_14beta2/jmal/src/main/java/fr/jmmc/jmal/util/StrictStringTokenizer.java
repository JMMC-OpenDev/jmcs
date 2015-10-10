/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.util;

import java.util.StringTokenizer;

/**
 * StringTokenizer Hack to return empty token if multiple delimiters found
 *
 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4140850
 */
public final class StrictStringTokenizer {

    /** delimiter */
    private final String delimiter;
    /** internal string tokenizer returning delimiter too */
    private final StringTokenizer st;
    /** last token reminder */
    private String lastToken;

    /**
     * Special StringTokenizer that returns empty token if multiple delimiters encountered
     * @param input input string
     * @param delimiter delimiter
     */
    public StrictStringTokenizer(final String input, final String delimiter) {
        this.delimiter = delimiter;
        this.st = new StringTokenizer(input, delimiter, true);
        this.lastToken = delimiter; // if first token is separator
    }

    /**
     * @return the next token from this string tokenizer.
     */
    public String nextToken() {
        String result = null;
        String token;
        while (result == null && this.st.hasMoreTokens()) {
            token = this.st.nextToken();
            if (token.equals(this.delimiter)) {
                if (this.lastToken.equals(this.delimiter)) {
                    // no value between 2 separators ?
                    result = "";
                }
            } else {
                result = token;
            }
            this.lastToken = token;
        } // next token
        if (result == null) {
            result = "";
        }
        return result;
    }

}
