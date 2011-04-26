/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FileUtils.java,v 1.9 2011-04-26 20:29:18 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2011/04/06 15:42:36  bourgesl
 * removed class name
 *
 * Revision 1.7  2011/04/04 13:43:33  bourgesl
 * added closeStream()
 *
 * Revision 1.6  2011/02/14 11:08:52  mella
 * Closes fluxes in copy method even if one exception occurs
 *
 * Revision 1.5  2011/02/13 17:03:39  mella
 * javadoc
 *
 * Revision 1.4  2011/02/11 15:40:27  mella
 * Add a method to copy files
 *
 * Revision 1.3  2011/02/11 09:58:25  mella
 * Prevent some exceptions in getTempFile method
 *
 * Revision 1.2  2011/02/10 15:48:53  mella
 * ask to delete temp files on exit
 *
 * Revision 1.1  2011/02/10 15:44:49  mella
 * Imported from aspro with new methods getTempFile
 *
 * Revision 1.10  2010/12/15 13:30:38  bourgesl
 * removed to do
 *
 * Revision 1.9  2010/10/07 15:01:14  bourgesl
 * added readFile(file)
 *
 * Revision 1.8  2010/10/04 16:25:25  bourgesl
 * proper IO exception handling
 *
 * Revision 1.7  2010/09/24 15:51:09  bourgesl
 * better exception handling
 *
 * Revision 1.6  2010/07/07 09:29:13  bourgesl
 * javadoc
 *
 * Revision 1.5  2010/06/17 10:02:51  bourgesl
 * fixed warning hints - mainly not final static loggers
 *
 * Revision 1.4  2010/05/26 15:26:02  bourgesl
 * line separator is public
 *
 * Revision 1.3  2010/04/06 08:31:44  bourgesl
 * fixed classloader issue with JNLP
 *
 * Revision 1.2  2010/04/02 14:40:16  bourgesl
 * added writer methods for text files
 *
 * Revision 1.1  2010/01/13 16:12:31  bourgesl
 * added export to PDF button
 *
 */
package fr.jmmc.mcs.util;

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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Several File utility methods
 *
 * @author bourgesl, mella
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
     * Get the extension of a file in lower case
     * @param file file to use
     * @return the extension of the file (without the dot char) or null
     */
    public static String getExtension(final File file) {
        final String fileName = file.getName();
        final int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(i + 1).toLowerCase();
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
    public static void copy(final File src, final File dst) throws IOException, FileNotFoundException
    {
        final InputStream in = new BufferedInputStream(new FileInputStream(src));
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
