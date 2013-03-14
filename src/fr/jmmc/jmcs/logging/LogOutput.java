/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.logging;

/**
 * This simple class is a container for both String and byte count.
 * 
 * @author Laurent BOURGES.
 */
public final class LogOutput {

    /** byte count */
    private final int _byteCount;
    /** log content */
    private final String _content;

    /**
     * Public constructor
     * @param byteCount byte count
     * @param content log content
     */
    protected LogOutput(final int byteCount, final String content) {
        _byteCount = byteCount;
        _content = content;
    }

    /**
     * Return the byte count
     * @return byte count
     */
    public int getByteCount() {
        return _byteCount;
    }

    /**
     * Return the log content
     * @return log content
     */
    public String getContent() {
        return _content;
    }
}
