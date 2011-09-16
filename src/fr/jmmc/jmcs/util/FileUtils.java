/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Several File utility methods
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public final class FileUtils {

    /** Class logger */
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());
    /** platform dependent line separator */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Forbidden constructor
     */
    private FileUtils() {
        // no-op
    }

    /**
     * Get the file name part without extension
     * @param file file as File
     * @return the file name part without extension or null
     */
    public static String getFileNameWithoutExtension(final File file) {
        if (file != null) {
            return getFileNameWithoutExtension(file.getName());
        }
        return null;
    }

    /**
     * Get the file name part without extension
     * @param fileName file name as String
     * @return the file name part without extension or null
     */
    public static String getFileNameWithoutExtension(final String fileName) {
        if (fileName != null) {
            final int pos = fileName.lastIndexOf('.');
            if (pos == -1) {
                return fileName;
            }
            if (pos > 0) {
                return fileName.substring(0, pos);
            }
        }
        return null;
    }

    /**
     * Get the extension of a file in lower case
     * @param file file as File
     * @return the extension of the file (without the dot char) or null
     */
    public static String getExtension(final File file) {
        if (file != null) {
            return getExtension(file.getName());
        }
        return null;
    }

    /**
     * Get the extension of a file in lower case
     * @param fileName file name as String
     * @return the extension of the file (without the dot char) or null
     */
    public static String getExtension(final String fileName) {
        if (fileName != null) {
            final int i = fileName.lastIndexOf('.');

            if (i > 0 && i < fileName.length() - 1) {
                return fileName.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Find a file in the current classloader (application class Loader)
     *
     * Accepts filename like fr/jmmc/aspro/fileName.ext
     *
     * @param classpathLocation file name like fr/jmmc/aspro/fileName.ext
     * @return URL to the file or null
     *
     * @throws IllegalStateException if the file is not found
     */
    public static URL getResource(final String classpathLocation) throws IllegalStateException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("getResource : " + classpathLocation);
        }
        // use the class loader resource resolver
        final URL url = FileUtils.class.getClassLoader().getResource(classpathLocation);

        if (url == null) {
            throw new IllegalStateException("Unable to find the file in the classpath : " + classpathLocation);
        }

        return url;
    }

    /**
     * Read a text file from the current classloader into a string
     *
     * @param classpathLocation file name like fr/jmmc/aspro/fileName.ext
     * @return text file content
     *
     * @throws IllegalStateException if the file is not found or an I/O exception occured
     */
    public static String readFile(final String classpathLocation) throws IllegalStateException {
        final URL url = getResource(classpathLocation);

        try {
            return readFile(url.openStream(), 2048);
        } catch (IOException ioe) {
            // Unexpected exception :
            throw new IllegalStateException("unable to read file : " + classpathLocation, ioe);
        }
    }

    /**
     * Read a text file from the given file
     *
     * @param file local file
     * @return text file content
     *
     * @throws IOException if an I/O exception occured
     */
    public static String readFile(final File file) throws IOException {
        return readFile(new FileInputStream(file), (int) file.length());
    }

    /**
     * Read a text file from the given input stream into a string
     *
     * @param inputStream stream to load
     * @param bufferCapacity initial buffer capacity (chars)
     * @return text file content
     *
     * @throws IOException if an I/O exception occured
     */
    private static String readFile(final InputStream inputStream, final int bufferCapacity) throws IOException {

        String result = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final StringBuilder sb = new StringBuilder(bufferCapacity);

            // Read incoming data line by line
            String currentLine = null;

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append(LINE_SEPARATOR);
                }
                sb.append(currentLine);
            }

            result = sb.toString();

        } finally {
            closeFile(bufferedReader);
        }
        return result;
    }

    /**
     * Write the given string into the given file
     * @param file file to write
     * @param content content to write
     *
     * @throws IOException if an I/O exception occured
     */
    public static void writeFile(final File file, final String content) throws IOException {
        final Writer w = openFile(file);
        try {
            w.write(content);
        } finally {
            closeFile(w);
        }
    }

    /**
     * Returns a Writer for the given file
     *
     * @param file file to write
     * @return Writer (buffered)
     *
     * @throws IOException if an I/O exception occured
     */
    public static Writer openFile(final File file) throws IOException {
        return new BufferedWriter(new FileWriter(file));
    }

    /**
     * Close the given reader
     *
     * @param r reader to close
     */
    public static void closeFile(final Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (IOException ioe) {
                logger.log(Level.FINE, "IO close failure.", ioe);
            }
        }
    }

    /**
     * Close the given writer
     *
     * @param w writer to close
     */
    public static void closeFile(final Writer w) {
        if (w != null) {
            try {
                w.close();
            } catch (IOException ioe) {
                logger.log(Level.FINE, "IO close failure.", ioe);
            }
        }
    }

    /**
     * Close the given input stream
     *
     * @param in input stream to close
     */
    public static void closeStream(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ioe) {
                logger.log(Level.FINE, "IO close failure.", ioe);
            }
        }
    }

    /**
     * Close the given output stream
     *
     * @param out output stream to close
     */
    public static void closeStream(final OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ioe) {
                logger.log(Level.FINE, "IO close failure.", ioe);
            }
        }
    }

    /**
     * Copy file
     * @param src source file
     * @param dst destination file
     * @throws IOException if io problem occurs
     * @throws FileNotFoundException if input file is not found
     */
    public static void copy(final File src, final File dst) throws IOException, FileNotFoundException {
        final InputStream in = new BufferedInputStream(new FileInputStream(src));

        saveStream(in, dst);
    }

    /**
     * Save the given input stream as file
     * @param in input stream to save as file
     * @param dst destination file
     * @throws IOException if io problem occurs
     * @throws FileNotFoundException if input file is not found
     */
    public static void saveStream(final InputStream in, final File dst) throws IOException, FileNotFoundException {
        final OutputStream out = new BufferedOutputStream(new FileOutputStream(dst));

        // Transfer bytes from in to out
        try {
            final byte[] buf = new byte[65536];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    /**
     * Zip source file into destination one.
     * @param src source file to be zipped
     * @param dst destination file corresponding to the zipped source file
     * @throws IOException if io problem occurs
     * @throws FileNotFoundException if input file is not found
     */
    public static void zip(final File src, final File dst) throws IOException, FileNotFoundException {
        final InputStream in = new BufferedInputStream(new FileInputStream(src));
        final OutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(dst)));

        // Transfer bytes from in to out
        try {
            final byte[] buf = new byte[65536];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    /**
     * Creates an empty file in the default temporary-file directory, using
     * the given prefix and suffix to generate its name.      
     * The file will be deleted on program exit.
     * 
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @throws  IllegalStateException
     *         If a file could not be created
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not allow a file to be created
     */
    public static File getTempFile(final String prefix, final String suffix) {
        // Prevent exception thrown by createTempFile that requires one prefix and
        // suffix longer than 3 chars.
        final String p;
        if (prefix.length() < 3) {
            p = prefix + "___";
        } else {
            p = prefix;
        }
        final String s;
        if (suffix.length() < 3) {
            s = "___" + suffix;
        } else {
            s = suffix;
        }

        File file = null;
        try {
            file = File.createTempFile(p, s);
            file.deleteOnExit();
        } catch (IOException ioe) {
            throw new IllegalStateException("unable to create a temporary file", ioe);
        }
        return file;
    }

    /**
     * Return an tmp filename using temp directory and given filename.
     * The caller must consider that this file may already be present.
     * The file will be deleted on program exit.
     * @param filename the short name to use in the computation of the temporary filename
     * @return the temporary filename
     */
    public static File getTempFile(final String filename) {
        final File file = new File(getTempDir(), filename);
        file.deleteOnExit();
        return file;
    }

    /**
     * Return the tmp dir where temporary file can be saved into.
     * 
     * @return the tmp directory name
     */
    private static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
