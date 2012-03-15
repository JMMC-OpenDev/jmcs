package org.ivoa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

/**
 * File utility methods : Several utility methods : finds a file in the class
 * path (jar), open files for read or write operation and close file
 *
 * TODO: merge this class with jMCS FileUtils
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class FileUtils extends LogSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * default read buffer capacity : DEFAULT_READ_BUFFER_SIZE = 16K
     */
    private static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    /**
     * default write buffer capacity : DEFAULT_WRITE_BUFFER_SIZE = 16K
     */
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;
    /**
     * File encoding use UTF-8
     */
    public static final String FILE_ENCODING = "UTF-8";

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Forbidden FileUtils constructor
     */
    private FileUtils() {
        /*
         * no-op
         */
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Find a file in the current classloader (application class Loader)
     *
     * @param fileName file name only no path included
     * @return URL to the file or null
     */
    public static URL getResource(final String fileName) {
        // Find properties in the classpath
        final URL url = FileUtils.class.getClassLoader().getResource(fileName);

        if (url == null) {
            throw new RuntimeException("Unable to find the file in classpath : " + fileName);
        }

        logger.debug("FileUtils.getSystemFileInputStream: URL = {}", url);

        return url;
    }

    /**
     * Find a file in the current classloader (application class Loader)
     *
     * @param fileName file name only no path included
     * @return InputStream or RuntimeException if not found
     * @throws RuntimeException if not found
     */
    public static InputStream getSystemFileInputStream(final String fileName) {
        final URL url = getResource(fileName);

        try {
            return url.openStream();
        } catch (final IOException ioe) {
            throw new RuntimeException("Failure when loading file in classpath : " + fileName, ioe);
        }
    }

    /**
     * Close an inputStream
     *
     * @param in inputStream to close
     */
    public static void closeStream(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (final IOException ioe) {
                logger.error("FileUtils.closeStream : io close failure : ", ioe);
            }
        }
    }

    /**
     * Close an outputStream
     *
     * @param out outputStream to close
     */
    public static void closeStream(final OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (final IOException ioe) {
                logger.error("FileUtils.closeStream : io close failure : ", ioe);
            }
        }
    }

    /**
     * Returns an existing File for the given path
     *
     * @param path file path
     * @return File or null
     */
    private static File getExistingFile(final String path) {
        if (!JavaUtils.isEmpty(path)) {
            final File file = new File(path);

            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

    /**
     * Returns an existing directory for the given path
     *
     * @param path directory path
     * @return directory or null
     */
    public static File getDirectory(final String path) {
        final File dir = getExistingFile(path);

        if (dir != null && dir.isDirectory()) {
            return dir;
        }

        return null;
    }

    /**
     * Returns an existing File for the given path
     *
     * @param path file path
     * @return File or null
     */
    public static File getFile(final String path) {
        final File file = getExistingFile(path);

        if (file != null && file.isFile()) {
            return file;
        }

        return null;
    }

    /**
     * Returns a Writer for the given file path and use the default writer
     * buffer capacity
     *
     * @param absoluteFilePath absolute file path
     * @return Writer (buffered) or null
     */
    public static Writer openFile(final String absoluteFilePath) {
        return openFile(absoluteFilePath, DEFAULT_WRITE_BUFFER_SIZE);
    }

    /**
     * Returns a Writer for the given file path and use the given buffer
     * capacity
     *
     * @param absoluteFilePath absolute file path
     * @param bufferSize write buffer capacity
     * @return Writer (buffered) or null
     */
    public static Writer openFile(final String absoluteFilePath, final int bufferSize) {
        if (!JavaUtils.isEmpty(absoluteFilePath)) {
            return openFile(new File(absoluteFilePath), bufferSize);
        }

        return null;
    }

    /**
     * Returns a Writer for the given file and use the default writer buffer
     * capacity
     *
     * @param file file to write
     * @return Writer (buffered) or null
     */
    public static Writer openFile(final File file) {
        return openFile(file, DEFAULT_WRITE_BUFFER_SIZE);
    }

    /**
     * Returns a Writer for the given file and use the given buffer capacity
     *
     * @param file file to write
     * @param bufferSize write buffer capacity
     * @return Writer (buffered) or null
     */
    public static Writer openFile(final File file, final int bufferSize) {
        try {
            // Should define UTF-8 encoding for cross platform compatibility 
            // but we must stay compatible with existing files (windows vs unix)
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)), bufferSize);
            /*
             * return new BufferedWriter(new OutputStreamWriter(new
             * FileOutputStream(file), FILE_ENCODING), bufferSize);
             */
        } catch (final IOException ioe) {
            logger.error("FileUtils.openFile : io failure : ", ioe);
        }

        return null;
    }

    /**
     * Close the given writer
     *
     * @param w writer to close
     * @return null
     */
    public static Writer closeFile(final Writer w) {
        if (w != null) {
            try {
                w.close();
            } catch (final IOException ioe) {
                logger.error("FileUtils.closeFile : io close failure : ", ioe);
            }
        }

        return null;
    }

    /**
     * Returns a reader for the given file path and use the default read buffer
     * capacity
     *
     * @param absoluteFilePath absolute file path
     * @return Reader (buffered) or null
     */
    public static Reader readFile(final String absoluteFilePath) {
        return readFile(absoluteFilePath, DEFAULT_READ_BUFFER_SIZE);
    }

    /**
     * Returns a reader for the given file path and use the given read buffer
     * capacity
     *
     * @param absoluteFilePath absolute file path
     * @param bufferSize write buffer capacity
     * @return Reader (buffered) or null
     */
    public static Reader readFile(final String absoluteFilePath, final int bufferSize) {
        return readFile(getFile(absoluteFilePath), bufferSize);
    }

    /**
     * Returns a reader for the given file and use the default read buffer
     * capacity
     *
     * @param file file to read
     * @return Reader (buffered) or null
     */
    public static Reader readFile(final File file) {
        return readFile(file, DEFAULT_READ_BUFFER_SIZE);
    }

    /**
     * Returns a reader for the given file and use the given read buffer
     * capacity
     *
     * @param file file to read
     * @param bufferSize write buffer capacity
     * @return Reader (buffered) or null
     */
    public static Reader readFile(final File file, final int bufferSize) {
        try {
            // Should define UTF-8 encoding for cross platform compatibility 
            // but we must stay compatible with existing files (windows vs unix)
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)), bufferSize);
            /*
             * return new BufferedReader(new InputStreamReader(new
             * FileInputStream(file), FILE_ENCODING), bufferSize);
             */
        } catch (final IOException ioe) {
            logger.error("FileUtils.readFile : io failure : ", ioe);
        }

        return null;
    }

    /**
     * Close the given reader
     *
     * @param r reader to close
     * @return null
     */
    public static Reader closeFile(final Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (final IOException ioe) {
                logger.error("FileUtils.closeFile : io close failure : ", ioe);
            }
        }

        return null;
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------

