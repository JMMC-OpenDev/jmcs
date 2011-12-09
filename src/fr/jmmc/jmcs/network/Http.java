/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network;

import fr.jmmc.jmcs.util.FileUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 *  This utility class is dedicated to gather code associated to HTTP domain.
 *
 *  It actually :
 * - returns a well configured apache commons HttpClient (legacy project)
 * 
 * @author Guillaume MELLA, Laurent BOURGES.
 */
public final class Http {

    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(Http.class.getName());
    /** JMMC web to detect proxies */
    private final static String JMMC_WEB = "http://www.jmmc.fr";
    /** JMMC socks to detect proxies */
    private final static String JMMC_SOCKS = "socket://jmmc.fr";
    /** cached JMMC web URL */
    private static URI JMMC_WEB_URI = null;
    /** cached JMMC socks URL */
    private static URI JMMC_SOCKS_URI = null;

    /**
     * Forbidden constructor
     */
    private Http() {
        super();
    }

    /**
     * This class returns a multi threaded HTTP client.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     *
     * @return httpClient instance
     */
    public static HttpClient getHttpClient() {
        return getHttpClient(getJmmcHttpURI(), true);
    }

    /**
     * This class returns an HTTP client.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *
     * @param multiThreaded true indicates to create a multi threaded HTTP client
     *
     * @return httpClient instance
     */
    public static HttpClient getHttpClient(final boolean multiThreaded) {
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
    private static HttpClient getHttpClient(final URI uri, final boolean multiThreaded) {

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
    private static void setConfiguration(final HttpClient httpClient) {
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
        httpClientParams.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, "UTF-8");
    }

    /**
     * Get JMMC HTTP URI
     * @return JMMC HTTP URI
     */
    private static URI getJmmcHttpURI() {
        if (JMMC_WEB_URI == null) {
            try {
                JMMC_WEB_URI = new URI(JMMC_WEB);
            } catch (URISyntaxException use) {
                logger.error("invalid URL", use);
            }
        }
        return JMMC_WEB_URI;
    }

    /**
     * Get JMMC Socks URI
     * @return JMMC Socks URI
     */
    private static URI getJmmcSocksURI() {
        if (JMMC_SOCKS_URI == null) {
            try {
                JMMC_SOCKS_URI = new URI(JMMC_SOCKS);
            } catch (URISyntaxException use) {
                logger.error("invalid URL", use);
            }
        }
        return JMMC_SOCKS_URI;
    }

    /**
     * This class returns the HTTP proxy configuration (based on http://www.jmmc.fr).
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getHttpProxyConfiguration() {
        return getProxyConfiguration(getJmmcHttpURI());
    }

    /**
     * This class returns the socks proxy configuration (based on socks://jmmc.fr).
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getSocksProxyConfiguration() {
        return getProxyConfiguration(getJmmcSocksURI());
    }

    /**
     * This class returns the proxy configuration for the associated URI.
     * @param uri reference URI used to get the proper proxy
     * @return HostConfiguration instance (proxy host and port only are defined)
     */
    public static HostConfiguration getProxyConfiguration(final URI uri) {
        final HostConfiguration hostConfiguration = new HostConfiguration();

        if (uri != null) {
            final ProxySelector proxySelector = ProxySelector.getDefault();
            final List<Proxy> proxyList = proxySelector.select(uri);
            final Proxy proxy = proxyList.get(0);

            logger.debug("using {} in proxyList = {}", proxy, proxyList);

            if (proxy.type() != Proxy.Type.DIRECT) {
                final String host;
                final InetSocketAddress epoint = (InetSocketAddress) proxy.address();
                if (epoint.isUnresolved()) {
                    host = epoint.getHostName();
                } else {
                    host = epoint.getAddress().getHostName();
                }
                final int port = epoint.getPort();

                if (!host.trim().isEmpty() && port > 0) {
                    hostConfiguration.setProxy(host, port);
                }
            }
        }
        return hostConfiguration;
    }

    /**
     * Save the document located at the given URI in the given file. 
     * Requests with dedicatedClient will instance one new client with proxies compatible with given uri. 
     * Other requests will use the common multithreaded httpclient.
     * 
     * @param uri URI to download
     * @param outputFile file to save into
     * @param useDedicatedClient use one dedicated httpclient if true or the common multithreaded one else
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    public static boolean download(final URI uri, final File outputFile, final boolean useDedicatedClient) throws IOException {

        return download(uri, useDedicatedClient, new StreamProcessor() {

            /**
             * Process the given input stream and CLOSE it anyway (try/finally)
             * @param in input stream to process
             * @throws IOException if any IO error occurs
             */
            @Override
            public void process(final InputStream in) throws IOException {
                FileUtils.saveStream(in, outputFile);
                logger.debug("File '{}' saved ({} bytes).", outputFile, outputFile.length());
            }
        });
    }

    /**
     * Read a text file from the given uri into a string
     *
     * @param uri uri to load
     * @param useDedicatedClient use one dedicated httpclient if true or the common multithreaded one else
     * @return text file content
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String download(final URI uri, final boolean useDedicatedClient) throws IOException {

        final StringStreamProcessor stringProcessor = new StringStreamProcessor();

        if (download(uri, useDedicatedClient, stringProcessor)) {
            return stringProcessor.getResult();
        }

        return null;
    }

    /**
     * Save the document located at the given URI in the given file. 
     * Requests with dedicatedClient will instance one new client with proxies compatible with given uri. 
     * Other requests will use the common multithreaded httpclient.
     * 
     * @param uri URI to download
     * @param processor stream processor to use to consume http response
     * @param useDedicatedClient use one dedicated httpclient if true or the common multithreaded one else
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    private static boolean download(final URI uri, final boolean useDedicatedClient, final StreamProcessor processor) throws IOException {
        // Create an HTTP client for the given URI to detect proxies for this host or use common one depending of given flag
        final HttpClient client = (useDedicatedClient) ? Http.getHttpClient(uri, false) : Http.getHttpClient();

        final GetMethod method = new GetMethod(uri.toString());

        try {
            logger.debug("Http client and get method have been created");

            // Send HTTP GET query:
            int resultCode = client.executeMethod(method);

            logger.debug("The query has been sent. Status code: {}", resultCode);

            if (resultCode == 200) {
                // Get response
                final InputStream in = new BufferedInputStream(method.getResponseBodyAsStream());

                processor.process(in);

                return true;
            }

        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return false;
    }

    /**
     * Generic Stream processors
     */
    private interface StreamProcessor {

        /**
         * Process the given input stream and CLOSE it anyway (try/finally)
         * @param in input stream to process
         * @throws IOException if any IO error occurs
         */
        public void process(final InputStream in) throws IOException;
    }

    /**
     * Custom StreamProcessor that copy the input stream to one String
     */
    private static final class StringStreamProcessor implements StreamProcessor {

        /** result as String */
        private String result = null;

        /**
         * Process the given input stream and CLOSE it anyway (try/finally)
         * @param in input stream to process
         * @throws IOException if any IO error occurs
         */
        @Override
        public void process(final InputStream in) throws IOException {

            // TODO check if we can get response size from http headers
            result = FileUtils.readStream(in);
            logger.debug("String stored in memory ({} chars).", result.length());
        }

        /**
         * Return the result as String
         * @return result as String or null
         */
        String getResult() {
            return result;
        }
    }
}
