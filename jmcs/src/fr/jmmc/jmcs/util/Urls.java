/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.security.Permission;
import java.util.logging.Logger;

/**
 * This class contains URL related utility methods.
 * 
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class Urls {

    /** Class logger */
    private static final Logger logger = Logger.getLogger(Urls.class.getName());
    /** URL encoding use UTF-8 */
    private static final String URL_ENCODING = "UTF-8";

    /**
     * Private constructor
     */
    private Urls() {
        super();
    }

    /**
     * http://forums.sun.com/thread.jspa?messageID=10522645
     *
     * @param url the URL to fix
     *
     * @return the fixed URL
     */
    public static URL fixJarURL(URL url) {
        if (url == null) {
            return null;
        }

        // final String method = _module + ".fixJarURL";
        String originalURLProtocol = url.getProtocol();

        // if (log.isDebugEnabled()) { log.debug(method + " examining '" + originalURLProtocol + "' protocol url: " + url); }
        if (!"jar".equalsIgnoreCase(originalURLProtocol)) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: URL is not 'jar' protocol: " + url); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " URL is jar protocol, continuing"); }
        String originalURLString = url.toString();

        // if (log.isDebugEnabled()) { log.debug(method + " using originalURLString: " + originalURLString); }
        int bangSlashIndex = originalURLString.indexOf("!/");

        if (bangSlashIndex > -1) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: originalURLString already has bang-slash: " + originalURLString); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " originalURLString needs fixing (it has no bang-slash)"); }
        String originalURLPath = url.getPath();

        // if (log.isDebugEnabled()) { log.debug(method + " using originalURLPath: " + originalURLPath); }
        URLConnection urlConnection;

        try {
            urlConnection = url.openConnection();

            if (urlConnection == null) {
                throw new Exception("urlConnection is null");
            }
        } catch (Exception e) // skip complex case
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: openConnection() exception", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnection: " + urlConnection); }
        Permission urlConnectionPermission;

        try {
            urlConnectionPermission = urlConnection.getPermission();

            if (urlConnectionPermission == null) {
                throw new Exception("urlConnectionPermission is null");
            }
        } catch (Exception e) // skip complex case
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: getPermission() exception", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnectionPermission: " + urlConnectionPermission); }
        String urlConnectionPermissionName = urlConnectionPermission.getName();

        if (urlConnectionPermissionName == null) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: urlConnectionPermissionName is null"); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnectionPermissionName: " + urlConnectionPermissionName); }
        File file = new File(urlConnectionPermissionName);

        if (!file.exists()) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: file does not exist: " + file); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using file: " + file); }
        String newURLStr;

        try {
            newURLStr = "jar:" + file.toURL().toExternalForm() + "!/"
                    + originalURLPath;
        } catch (MalformedURLException mue) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: exception creating newURLStr", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using newURLStr: " + newURLStr); }
        try {
            url = new URL(newURLStr);
        } catch (MalformedURLException mue) {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: exception creating new URL", e); }
            return url;
        }

        return url;
    }

    /**
     * Parse the given url
     * @param url url as string
     * @return URL object
     * @throws IllegalStateException if the url is malformed
     */
    public static URL parseURL(final String url) throws IllegalStateException {
        try {
            return new URL(url);
        } catch (MalformedURLException mue) {
            throw new IllegalStateException("Cannot parse url " + url, mue);
        }
    }

    /**
     * Encode the given query string into <code>application/x-www-form-urlencoded</code>
     * @param queryString query string to encode
     * @return encoded query string
     * @throws IllegalStateException if the UTF-8 encoding is not supported
     */
    public static String encode(final String queryString) throws IllegalStateException {
        try {
            return URLEncoder.encode(queryString, URL_ENCODING);
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("Unsupported encoding : " + URL_ENCODING, uee);
        }
    }
}
