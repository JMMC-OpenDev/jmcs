/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Urls.java,v 1.3 2011-04-05 15:18:09 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2009/04/16 15:44:51  lafrasse
 * Jalopization.
 *
 * Revision 1.1  2009/02/26 13:31:17  mella
 * First revision (moved from HelpView)
 *
 *
 */
package fr.jmmc.mcs.util;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.security.Permission;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class Urls
{
    /**
     * http://forums.sun.com/thread.jspa?messageID=10522645
     *
     * @param url the URL to fix
     *
     * @return the fixed URL
     */
    public static URL fixJarURL(URL url)
    {
        if (url == null)
        {
            return null;
        }

        // final String method = _module + ".fixJarURL";
        String originalURLProtocol = url.getProtocol();

        // if (log.isDebugEnabled()) { log.debug(method + " examining '" + originalURLProtocol + "' protocol url: " + url); }
        if ("jar".equalsIgnoreCase(originalURLProtocol) == false)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: URL is not 'jar' protocol: " + url); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " URL is jar protocol, continuing"); }
        String originalURLString = url.toString();

        // if (log.isDebugEnabled()) { log.debug(method + " using originalURLString: " + originalURLString); }
        int bangSlashIndex = originalURLString.indexOf("!/");

        if (bangSlashIndex > -1)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: originalURLString already has bang-slash: " + originalURLString); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " originalURLString needs fixing (it has no bang-slash)"); }
        String originalURLPath = url.getPath();

        // if (log.isDebugEnabled()) { log.debug(method + " using originalURLPath: " + originalURLPath); }
        URLConnection urlConnection;

        try
        {
            urlConnection      = url.openConnection();

            if (urlConnection == null)
            {
                throw new Exception("urlConnection is null");
            }
        }
        catch (Exception e) // skip complex case
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: openConnection() exception", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnection: " + urlConnection); }
        Permission urlConnectionPermission;

        try
        {
            urlConnectionPermission = urlConnection.getPermission();

            if (urlConnectionPermission == null)
            {
                throw new Exception("urlConnectionPermission is null");
            }
        }
        catch (Exception e) // skip complex case
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: getPermission() exception", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnectionPermission: " + urlConnectionPermission); }
        String urlConnectionPermissionName = urlConnectionPermission.getName();

        if (urlConnectionPermissionName == null)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: urlConnectionPermissionName is null"); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using urlConnectionPermissionName: " + urlConnectionPermissionName); }
        File file = new File(urlConnectionPermissionName);

        if (file.exists() == false)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: file does not exist: " + file); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using file: " + file); }
        String newURLStr;

        try
        {
            newURLStr = "jar:" + file.toURL().toExternalForm() + "!/" +
                originalURLPath;
        }
        catch (MalformedURLException e)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: exception creating newURLStr", e); }
            return url;
        }

        // if (log.isDebugEnabled()) { log.debug(method + " using newURLStr: " + newURLStr); }
        try
        {
            url = new URL(newURLStr);
        }
        catch (MalformedURLException e)
        {
            // if (log.isDebugEnabled()) { log.debug(method + " skipping fix: exception creating new URL", e); }
            return url;
        }

        return url;
    }
}
