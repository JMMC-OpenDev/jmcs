/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: TestFileUtils.java,v 1.1 2011-04-26 20:29:17 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 */
package fr.jmmc.jmcs.util;

import fr.jmmc.mcs.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Test some FileUtils methods.
 * @author mella
 */
public class TestFileUtils {

    /** logger */
    private final static Logger logger = Logger.getLogger(TestFileUtils.class.getName());

    public static void main(String[] args) {
        try {
            File f1 = FileUtils.getTempFile("toto", "txt");
            FileUtils.writeFile(f1, "ABCDEFGHABCDEFGHABCDEFGHABCDEFGHABCDEFGHABCDEFGHABCDEFGHABCDEFGH");
            File f2 = FileUtils.getTempFile("toto", ".txt.gz");
            FileUtils.zip(f1, f2);
            System.out.println("f1 = " + f1);
            System.out.println("f1.length() = " + f1.length());
            System.out.println("f2 = " + f2);
            System.out.println("f2.length() = " + f2.length());
            System.out.println("f2.read() = " + FileUtils.readFile(f2));

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "exception:", ex);
        }

    }
}
