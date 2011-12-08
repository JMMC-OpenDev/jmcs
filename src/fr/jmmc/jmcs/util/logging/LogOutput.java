/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

/**
 * This simple class is a container for both String and byte count
 * @author bourgesl
 */
public final class LogOutput {

    /** byte count */
    private final int byteCount;
    /** log content */
    private final String content;

    /**
     * Public constructor
     * @param byteCount byte count
     * @param content log content
     */
    protected LogOutput(final int byteCount, final String content) {
        this.byteCount = byteCount;
        this.content = content;
    }

    /**
     * Return the byte count
     * @return byte count
     */
    public int getByteCount() {
        return byteCount;
    }

    /**
     * Return the log content
     * @return log content
     */
    public String getContent() {
        return content;
    }
}
