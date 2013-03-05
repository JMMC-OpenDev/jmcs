/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This program will remove the Class-Path attributes of the manifest contained
 * in the given jar files.
 *
 * TODO: move this class outside src folder to be not released
 */
public class CleanManifest {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(CleanManifest.class.getName());

    /**
     * Private constructor
     */
    private CleanManifest() {
        super();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: CleanManifest <jarFile1> ... <jarFileN>");
            System.out.println("  this program will remove the Class-Path attributes of the manifest contained in the given jar files.");
            System.out.println("  original files will be saved on disk with .old extension.");
            return;
        }
        for (String fname : args) {

            System.out.println("Trying to clean " + fname);
            File inFile = new File(fname);

            JarFile jf = null;
            Manifest manifest = null;
            try {
                // Get manifest from input jar file
                jf = new JarFile(inFile);
                manifest = jf.getManifest();

            } catch (IOException ioe) {
                logger.error("io exception: ", ioe);
            } finally {
                if (jf != null) {
                    try {
                        jf.close();
                    } catch (IOException ioe) {
                        logger.error("io exception: ", ioe);
                    }
                }
            }

            if (manifest != null) {
                // Try to clean 
                boolean modified = cleanManifest(manifest);

                // Backup and update given jar if manifest has been modified
                if (modified) {
                    final File tmpFile = new File(fname + ".tmp");

                    JarInputStream jis = null;
                    JarOutputStream jos = null;
                    try {
                        // Build input and output streams
                        jis = new JarInputStream(new FileInputStream(inFile));

                        jos = new JarOutputStream(new FileOutputStream(tmpFile), manifest);

                        // Copy jar files 
                        copyJarFile(jis, jos);

                    } catch (IOException ioe) {
                        logger.error("io exception: ", ioe);
                    } finally {
                        if (jis != null) {
                            try {
                                jis.close();
                            } catch (IOException ioe) {
                                logger.error("io exception: ", ioe);
                            }
                        }
                        if (jos != null) {
                            try {
                                jos.close();
                            } catch (IOException ioe) {
                                logger.error("io exception: ", ioe);
                            }
                        }
                    }

                    // backup and 
                    final File oldFile = new File(fname + ".old");

                    System.out.println("moving '" + inFile + "' to '" + oldFile + "'");

                    // display informations
                    if (!inFile.renameTo(oldFile)) {
                        System.out.print("Error moving file '" + inFile + "' to '" + oldFile + "'");
                    } else {
                        if (!tmpFile.renameTo(inFile)) {
                            System.out.println("Error moving '" + tmpFile + "' to '" + inFile + "'");
                        }

                        System.out.println("Jar file '" + inFile + "' done");
                    }
                }
            }
        }
    }

    /**
     * Copies all files from JarInputStream to JarOutputStream
     *
     * @param jin stream from which the files are copied from
     * @param jout output stream to store jar into
     * @throws IOException
     */
    private static void copyJarFile(JarInputStream jin, JarOutputStream jout)
            throws IOException {
        ZipEntry entry;
        byte[] chunk = new byte[32768];
        int bytesRead;

        while ((entry = jin.getNextEntry()) != null) {
            try {
                // Add file entry to output stream (meta data)
                jout.putNextEntry(entry);
                // Copy data to output stream (actual data)
                if (!entry.isDirectory()) {
                    while ((bytesRead = jin.read(chunk)) != -1) {
                        jout.write(chunk, 0, bytesRead);
                    }
                }
                jout.closeEntry();
            } catch (ZipException ex) {
                throw new IllegalStateException("Error during zip operation of " + entry.getName(), ex);
            }
        }
    }

    /**
     * This method clean the given manifest. Class-Path attributes are removed
     * if present
     *
     * @param manifest
     * @return true if manifest has been cleaned, else false.
     */
    private static boolean cleanManifest(Manifest manifest) {
        if (manifest == null) {
            return false;
        }

        // Get main attributes
        Attributes mainAttributes = manifest.getMainAttributes();

        // try to remove Class-Path attribute
        Object removedAttribute = mainAttributes.remove(new Attributes.Name("Class-Path"));
        if (removedAttribute != null) {
            System.out.println("Class-Path attribute removed from jar");
            return true;
        }
        return false;
    }
}
