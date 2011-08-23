/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 *  This util class is dedicated to gather code associated to http domain.
 *
 *  It actually :
 * - returns a well configured apache commons HttpClient (legacy project)
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public final class Http
{

    /** class name */
    public final static String className_ = Http.class.getName();
    /** logger */
    private final static Logger logger_ = Logger.getLogger(className_);
    /** Jmmc web to detect proxies */
    private final static String JMMC_WEB = "http://www.jmmc.fr";
    /** Jmmc socks to detect proxies */
    private final static String JMMC_SOCKS = "socket://jmmc.fr";
    /** cached Jmmc web URL */
    private static URI JMMC_WEB_URI = null;
    /** cached Jmmc socks URL */
    private static URI JMMC_SOCKS_URI = null;

    /**
     * Forbidden constructor
     */
    private Http()
    {
        super();
    }

    /**
     * This class returns a multi threaded http client.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     *
     * @return httpClient instance
     */
    public static HttpClient getHttpClient()
    {
        return getHttpClient(getJmmcHttpURI(), true);
    }

    /**
     * This class returns an http client.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *
     * @param multiThreaded true indicates to create a multi threaded http client
     *
     * @return httpClient instance
     */
    public static HttpClient getHttpClient(final boolean multiThreaded)
    {
        return getHttpClient(getJmmcHttpURI(), multiThreaded);
    }

    /**
     * This class returns a multi threaded http client for the associated URI.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     * @param uri reference uri used to get the proper proxy
     * @param multiThreaded true indicates to create a multi threaded http client
     *
     * @todo remove the limit for support of the first proxy.
     *
     * @return httpClient instance
     */
    private static HttpClient getHttpClient(final URI uri, final boolean multiThreaded)
    {

        final HttpClient httpClient;
        if (multiThreaded) {
            // Create an HttpClient with the MultiThreadedHttpConnectionManager.
            // This connection manager must be used if more than one thread will
            // be using the HttpClient.
            httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        } else {
            httpClient = new HttpClient();
        }

        setConfiguration(httpClient);

        httpClient.setHostConfiguration(getProxyConfiguration(uri));

        return httpClient;
    }

    /**
     * Define client configuration
     * @param httpClient instance to configure
     */
    private static void setConfiguration(final HttpClient httpClient)
    {
        // define timeout value for allocation of connections from the pool
        httpClient.getParams().setConnectionManagerTimeout(NetworkSettings.DEFAULT_CONNECT_TIMEOUT);

        final HttpConnectionManagerParams httpParams = httpClient.getHttpConnectionManager().getParams();

        // define connect timeout:
        httpParams.setConnectionTimeout(NetworkSettings.DEFAULT_CONNECT_TIMEOUT);
        // define read timeout:
        httpParams.setSoTimeout(NetworkSettings.DEFAULT_SOCKET_READ_TIMEOUT);

        // define connection parameters:
        httpParams.setMaxTotalConnections(NetworkSettings.DEFAULT_MAX_TOTAL_CONNECTIONS);
        httpParams.setDefaultMaxConnectionsPerHost(NetworkSettings.DEFAULT_MAX_HOST_CONNECTIONS);

        // set content-encoding to UTF-8 instead of default ISO-8859
        final HttpClientParams httpClientParams = httpClient.getParams();
        httpClientParams.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET ,"UTF-8");
    }

    /**
     * Get JMMC Http URI
     * @return JMMC Http URI
     */
    private static URI getJmmcHttpURI()
    {
        if (JMMC_WEB_URI == null) {
            try {
                JMMC_WEB_URI = new URI(JMMC_WEB);
            } catch (URISyntaxException use) {
                logger_.log(Level.SEVERE, "invalid URL", use);
            }
        }
        return JMMC_WEB_URI;
    }

    /**
     * Get JMMC Socks URI
     * @return JMMC Socks URI
     */
    private static URI getJmmcSocksURI()
    {
        if (JMMC_SOCKS_URI == null) {
            try {
                JMMC_SOCKS_URI = new URI(JMMC_SOCKS);
            } catch (URISyntaxException use) {
                logger_.log(Level.SEVERE, "invalid URL", use);
            }
        }
        return JMMC_SOCKS_URI;
    }

    /**
     * This class returns the http proxy configuration (based on http://www.jmmc.fr).
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getHttpProxyConfiguration()
    {
        return getProxyConfiguration(getJmmcHttpURI());
    }

    /**
     * This class returns the socks proxy configuration (based on socks://jmmc.fr).
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getSocksProxyConfiguration()
    {
        return getProxyConfiguration(getJmmcSocksURI());
    }

    /**
     * This class returns the proxy configuration for the associated URI.
     * @param uri reference uri used to get the proper proxy
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getProxyConfiguration(final URI uri)
    {
        final HostConfiguration hostConfiguration = new HostConfiguration();

        if (uri != null) {
            final ProxySelector proxySelector = ProxySelector.getDefault();
            final List<Proxy> proxyList = proxySelector.select(uri);
            final Proxy proxy = proxyList.get(0);

            if (logger_.isLoggable(Level.FINE)) {
                logger_.log(Level.FINE, "using " + proxy + "in proxyList = " + proxyList);
            }
            if (proxy.type() != Proxy.Type.DIRECT) {
                final String host;
                final InetSocketAddress epoint = (InetSocketAddress) proxy.address();
                if (epoint.isUnresolved()) {
                    host = epoint.getHostName();
                } else {
                    host = epoint.getAddress().getHostName();
                }
                final int port = epoint.getPort();

                hostConfiguration.setProxy(host, port);
            }
        }
        return hostConfiguration;
    }
}
