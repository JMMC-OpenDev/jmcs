/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * ByteArrayOutputStreamAppender is one singleton to append log events into a memory byte buffer
 * 
 * @author bourgesl
 */
public final class ByteArrayOutputStreamAppender extends OutputStreamAppender<ILoggingEvent> {

    /* members */
    /** ByteArrayOutputStream which keeps log (128K by default) */
    private final CopyableByteArrayOutputStream _byteArrayOutputStream = new CopyableByteArrayOutputStream(128 * 1024);

    /**
     * Public constructor used for Joran (xml configurator); does nothing
     */
    public ByteArrayOutputStreamAppender() {
        super();
    }

    /**
     * Checks that required parameters are set and if everything is in order,
     * activates this appender.
     */
    @Override
    public void start() {
        setOutputStream(_byteArrayOutputStream);
        super.start();
    }

    /**
     * <p>
     * Checks that the given stream corresponds to the internal ByteBuffer, otherwise
     * throws an IllegalStateException
     * 
     * @param outputStream The internal ByteArrayOutputStream instance
     */
    @Override
    public void setOutputStream(final OutputStream outputStream) {
        // Check
        if (outputStream != _byteArrayOutputStream) {
            throw new IllegalStateException("Invalid output stream (" + CopyableByteArrayOutputStream.class + " expected) !");
        }
        super.setOutputStream(outputStream);
    }

    /**
     * Return the partial log as string starting at the given argument from (THREAD SAFE)
     * @param from gives the position in the buffer to copy from
     * @return partial log output
     */
    public LogOutput getLogOutput(final int from) {
        final byte[] buffer;
        final int size;
        // the synchronization prevents the OutputStream from being closed or written while we
        // are getting its content.
        synchronized (lock) {
            size = _byteArrayOutputStream.size();
            buffer = (from < size) ? _byteArrayOutputStream.toByteArray(from) : null;
        }
        return new LogOutput(size, (buffer != null) ? new String(buffer, 0, buffer.length) : "");
    }

    /**
     * This CopyableByteArrayOutputStream extends ByteArrayOutputStream to allow partial buffer copy
     */
    private static class CopyableByteArrayOutputStream extends ByteArrayOutputStream {

        /**
         * Creates a new byte array output stream, with a buffer capacity of
         * the specified size, in bytes.
         *
         * @param   size   the initial size.
         * @exception  IllegalArgumentException if size is negative.
         */
        protected CopyableByteArrayOutputStream(final int size) {
            super(size);
        }

        /**
         * Creates a newly allocated byte array. Its size is the current
         * size of this output stream and the valid contents of the buffer
         * have been copied into it.
         * 
         * Note: if the argument from is given, it returns only the buffer content 
         * starting from this argument value
         *
         * @param from gives the position in the buffer to copy from
         * @return  the current contents of this output stream, as a byte array.
         */
        protected synchronized byte[] toByteArray(final int from) {
            final int pos = (from < 0 || from > count) ? 0 : from;
            return copyOfRange(this.buf, pos, this.count);
        }

        /**
         * Copies the specified range of the specified array into a new array.
         * The initial index of the range (<tt>from</tt>) must lie between zero
         * and <tt>original.length</tt>, inclusive.  The value at
         * <tt>original[from]</tt> is placed into the initial element of the copy
         * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
         * Values from subsequent elements in the original array are placed into
         * subsequent elements in the copy.  The final index of the range
         * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
         * may be greater than <tt>original.length</tt>, in which case
         * <tt>(byte)0</tt> is placed in all elements of the copy whose index is
         * greater than or equal to <tt>original.length - from</tt>.  The length
         * of the returned array will be <tt>to - from</tt>.
         *
         * @param original the array from which a range is to be copied
         * @param from the initial index of the range to be copied, inclusive
         * @param to the final index of the range to be copied, exclusive.
         *     (This index may lie outside the array.)
         * @return a new array containing the specified range from the original array,
         *     truncated or padded with zeros to obtain the required length
         * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
         *     or {@code from > original.length}
         * @throws IllegalArgumentException if <tt>from &gt; to</tt>
         * @throws NullPointerException if <tt>original</tt> is null
         * @since JDK 1.6
         */
        public static byte[] copyOfRange(final byte[] original, final int from, final int to) {
            final int newLength = to - from;
            if (newLength < 0) {
                throw new IllegalArgumentException(from + " > " + to);
            }
            final byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }
    }
}
