/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import fr.jmmc.jmcs.App;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to get resources from inside JAR files.
 * 
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class ResourceUtils {

    /** the logger facility */
    protected static final Logger _logger = LoggerFactory.getLogger(ResourceUtils.class.getName());

    /**
     * Return the filename from a resource path (assumed delimiter is '/').
     *
     * @param resourcePath a '/' delimited path, such as Java resource path.
     * @return the last element of a '/' delimited path, or null otherwise.
     */
    public static String filenameFromResourcePath(final String resourcePath) {
        String[] pathTokens = resourcePath.split("/");
        if (pathTokens.length > 0) {
            return pathTokens[pathTokens.length - 1];
        }
        return null;
    }

    /**
     * Get Path from resource filename located in the class loader using the following path:
     * $package(appClass)$/resource/fileName
     *
     * For example: getPathFromResourceFilename(App.class, fileName) uses the path:
     * fr/jmmc/jmcs/resource/$fileName$
     *
     * @param appClass any App class or subclass
     * @param fileName name of searched file.
     *
     * @return resource path, or null.
     */
    static String getPathFromResourceFilename(final Class<? extends App> appClass, final String fileName) {
        if (appClass == null) {
            return null;
        }
        final String packageName = appClass.getPackage().getName();
        final String packagePath = packageName.replace(".", "/");
        final String filePath = packagePath + "/resource/" + fileName;
        _logger.debug("filePath = '{}'.", filePath);
        return filePath;
    }

    /**
     * Get path from resource filename located in the following path:
     * $package(this App class)$/resource/fileName
     *
     * @param fileName name of searched file.
     *
     * @return resource path
     */
    public static String getPathFromResourceFilename(final String fileName) {
        return getPathFromResourceFilename(App.getInstance().getClass(), fileName);
    }

    /**
     * Get URL from resource filename located in the class loader using the following path:
     * $package(appClass)$/resource/fileName
     *
     * For example: getURLFromResourceFilename(App.class, fileName) uses the path:
     * fr/jmmc/jmcs/resource/$fileName$
     *
     * @param appClass any App class or subclass
     * @param fileName name of searched file.
     *
     * @return resource file URL, or null.
     */
    public static URL getUrlFromResourceFilename(final Class<? extends App> appClass, final String fileName) {
        final String filePath = getPathFromResourceFilename(appClass, fileName);
        if (filePath == null) {
            return null;
        }
        _logger.debug("filePath = '{}'.", filePath);
        final URL fileURL = appClass.getClassLoader().getResource(filePath);
        if (fileURL == null) {
            _logger.warn("Cannot find resource from '{}' file.", filePath);
            return null;
        }
        _logger.debug("fileURL = '{}'.", fileURL);
        return UrlUtils.fixJarURL(fileURL);
    }

    /**
     * Get URL from resource filename located in the following path:
     * $package(this App class)$/resource/fileName
     *
     * @param fileName name of searched file.
     *
     * @return resource URL
     */
    public static URL getUrlFromResourceFilename(final String fileName) {
        return getUrlFromResourceFilename(App.getInstance().getClass(), fileName);
    }

    /**
     * Read a text file from the current class loader into a string
     *
     * @param classpathLocation file name like fr/jmmc/aspro/fileName.ext
     * @return text file content
     *
     * @throws IllegalStateException if the file is not found or an I/O
     * exception occurred
     */
    public static String readResource(final String classpathLocation) throws IllegalStateException {
        final URL url = getResource(classpathLocation);
        try {
            return FileUtils.readStream(new BufferedInputStream(url.openStream()), FileUtils.DEFAULT_BUFFER_CAPACITY);
        } catch (IOException ioe) {
            throw new IllegalStateException("unable to read file : " + classpathLocation, ioe);
        }
    }

    /**
     * Extract the given resource given its file name in the JAR archive and save it as one temporary file.
     *
     * @param fullResourceFilePath complete path to the resource name to
     * extract.
     * @return file URL
     * @throws IllegalStateException if the given resource does not exist
     */
    public static String extractResource(final String fullResourceFilePath) throws IllegalStateException {
        final URL url = getResource(fullResourceFilePath);
        final File tmpFile = FileUtils.getTempFile(filenameFromResourcePath(fullResourceFilePath));
        try {
            FileUtils.saveStream(new BufferedInputStream(url.openStream()), tmpFile);
            return tmpFile.toURI().toString();
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to save file '" + tmpFile + "' for URL '" + url + "'.", ioe);
        }
    }

    /**
     * Find a file in the current classloader (application class Loader)
     * Accepts filename like fr/jmmc/aspro/fileName.ext
     *
     * @param classpathLocation file name like fr/jmmc/aspro/fileName.ext
     * @return URL to the file or null
     *
     * @throws IllegalStateException if the file is not found
     */
    public static URL getResource(final String classpathLocation) throws IllegalStateException {
        _logger.debug("getResource : {}", classpathLocation);
        if (classpathLocation == null) {
            throw new IllegalStateException("Invalid 'null' value for classpathLocation.");
        }
        final String fixedPath;
        if (classpathLocation.startsWith("/")) {
            fixedPath = classpathLocation.substring(1);
            _logger.warn("Given classpath had to be fixed : {}", classpathLocation);
        } else {
            fixedPath = classpathLocation;
        }
        final URL url = ResourceUtils.class.getClassLoader().getResource(fixedPath);
        if (url == null) {
            throw new IllegalStateException("Unable to find the file in the classpath : " + fixedPath);
        }
        return url;
    }

    /**
     * Private constructor
     */
    private ResourceUtils() {
        super();
    }
}
/*___oOo___*/
