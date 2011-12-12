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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains URL related utility methods.
 * 
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class Urls {

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(Urls.class.getName());
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
    public static URL fixJarURL(final URL url) {
        if (url == null) {
            return null;
        }

        // final String method = _module + ".fixJarURL";
        final String originalURLProtocol = url.getProtocol();

        _logger.debug("examining '{}' protocol url: {}", originalURLProtocol, url);

        if (!"jar".equalsIgnoreCase(originalURLProtocol)) {
            _logger.debug("skipping fix: URL is not 'jar' protocol: {}", url);
            return url;
        }

        _logger.debug("URL is jar protocol, continuing");

        final String originalURLString = url.toString();

        _logger.debug("using originalURLString: {}", originalURLString);

        final int bangSlashIndex = originalURLString.indexOf("!/");

        if (bangSlashIndex > -1) {
            _logger.debug("skipping fix: originalURLString already has bang-slash: {}", originalURLString);
            return url;
        }

        _logger.debug("originalURLString needs fixing (it has no bang-slash)");

        final String originalURLPath = url.getPath();

        _logger.debug("using originalURLPath: {}", originalURLPath);

        URLConnection urlConnection;

        try {
            urlConnection = url.openConnection();

            if (urlConnection == null) {
                throw new Exception("urlConnection is null");
            }
        } catch (Exception e) {
            // skip complex case
            _logger.debug("skipping fix: openConnection() exception: ", e);
            return url;
        }

        _logger.debug("using urlConnection: {}", urlConnection);

        Permission urlConnectionPermission;

        try {
            urlConnectionPermission = urlConnection.getPermission();

            if (urlConnectionPermission == null) {
                throw new Exception("urlConnectionPermission is null");
            }
        } catch (Exception e) {
            // skip complex case
            _logger.debug("skipping fix: getPermission() exception:", e);
            return url;
        }

        _logger.debug("using urlConnectionPermission: {}", urlConnectionPermission);

        final String urlConnectionPermissionName = urlConnectionPermission.getName();

        if (urlConnectionPermissionName == null) {
            _logger.debug("skipping fix: urlConnectionPermissionName is null");
            return url;
        }

        _logger.debug("using urlConnectionPermissionName: {}", urlConnectionPermissionName);

        final File file = new File(urlConnectionPermissionName);

        if (!file.exists()) {
            _logger.debug("skipping fix: file does not exist: {}", file);
            return url;
        }

        _logger.debug("using file: {}", file);

        String newURLStr;

        try {
            newURLStr = "jar:" + file.toURL().toExternalForm() + "!/" + originalURLPath;

        } catch (MalformedURLException mue) {
            _logger.debug("skipping fix: exception creating newURLStr", mue);
            return url;
        }

        _logger.debug("using newURLStr: {}", newURLStr);
        try {
            return new URL(newURLStr);
        } catch (MalformedURLException mue) {
            _logger.debug("skipping fix: exception creating new URL", mue);
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
