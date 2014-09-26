/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.network.http;

import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.network.NetworkSettings;
import static fr.jmmc.jmcs.network.NetworkSettings.getJmmcHttpURI;
import fr.jmmc.jmcs.network.ProxyConfig;
import fr.jmmc.jmcs.util.FileUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger _logger = LoggerFactory.getLogger(Http.class.getName());

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
     * This class returns a multi-threaded HTTP client for the associated URI.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     * @param uri reference URI used to get the proper proxy
     * @param multiThreaded true indicates to create a multi threaded HTTP client
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

        // Get Proxy settings for the given URI:
        final ProxyConfig config = NetworkSettings.getProxyConfiguration(uri);
        if (config.getHostname() != null) {
            final HostConfiguration hostConfig = new HostConfiguration();
            hostConfig.setProxy(config.getHostname(), config.getPort());
            httpClient.setHostConfiguration(hostConfig);
        }

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
        // avoid retries (3 by default):
        httpClientParams.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
    }

    /**
     * Transform the given URL in URI if valid.
     * @param url URL as string
     * @return URI instance
     * @throws IllegalArgumentException if the URI is malformed
     */
    public static URI validateURL(final String url) throws IllegalArgumentException {
        try {
            return new URI(url);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Invalid URL:" + url, use);
        }
    }

    /**
     * Save the document located at the given URI in the given file. 
     * Requests with dedicatedClient will instance one new client with proxies compatible with given URI. 
     * Other requests will use the common multi-threaded HTTP client .
     * 
     * @param uri URI to download
     * @param outputFile file to save into
     * @param useDedicatedClient use one dedicated HttpClient if true or the common multi-threaded one else
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    public static boolean download(final URI uri, final File outputFile, final boolean useDedicatedClient) throws IOException {
        // Create an HTTP client for the given URI to detect proxies for this host or use common one depending of given flag
        final HttpClient client = (useDedicatedClient) ? Http.getHttpClient(uri, false) : Http.getHttpClient();

        return download(uri, client, null, new StreamProcessor() {
            /**
             * Process the given input stream and CLOSE it anyway (try/finally)
             * @param in input stream to process
             * @throws IOException if any IO error occurs
             */
            @Override
            public void process(final InputStream in) throws IOException {
                FileUtils.saveStream(in, outputFile);
                _logger.debug("File '{}' saved ({} bytes).", outputFile, outputFile.length());
            }
        });
    }

    /**
     * Read a text file from the given URI into a string
     *
     * @param uri URI to load
     * @param useDedicatedClient use one dedicated HttpClient if true or the common multi-threaded one else
     * @return text file content or null if no result
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String download(final URI uri, final boolean useDedicatedClient) throws IOException {
        // Create an HTTP client for the given URI to detect proxies for this host or use common one depending of given flag
        final HttpClient client = (useDedicatedClient) ? Http.getHttpClient(uri, false) : Http.getHttpClient();

        return download(uri, client);
    }

    /**
     * Read a text file from the given URI into a string
     *
     * @param uri URI to load
     * @param client http client to use
     * @return text file content or null if no result
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String download(final URI uri, final HttpClient client) throws IOException {

        final StringStreamProcessor stringProcessor = new StringStreamProcessor();

        if (download(uri, client, null, stringProcessor)) {
            return stringProcessor.getResult();
        }

        return null;
    }

    /**
     * Post a request to the given URI and get a string as result.
     *
     * @param uri URI to load
     * @param useDedicatedClient use one dedicated HttpClient if true or the common multi-threaded one else
     * @param queryProcessor post query processor to define query parameters
     * @return result as string or null if no result
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String post(final URI uri, final boolean useDedicatedClient,
                              final PostQueryProcessor queryProcessor) throws IOException {

        // Create an HTTP client for the given URI to detect proxies for this host or use common one depending of given flag
        final HttpClient client = (useDedicatedClient) ? Http.getHttpClient(uri, false) : Http.getHttpClient();

        return post(uri, client, queryProcessor);
    }

    /**
     * Post a request to the given URI and get a string as result.
     *
     * @param uri URI to load
     * @param client http client to use
     * @param queryProcessor post query processor to define query parameters
     * @return result as string or null if no result
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String post(final URI uri, final HttpClient client,
                              final PostQueryProcessor queryProcessor) throws IOException {

        final StringStreamProcessor stringProcessor = new StringStreamProcessor();

        if (post(uri, client, queryProcessor, stringProcessor)) {
            return stringProcessor.getResult();
        }

        return null;
    }

    /**
     * Execute a request to the given URI and get a string as result.
     *
     * @param client HttpClient to use
     * @param method http method to execute
     * @return result as string or null if no result
     *
     * @throws IOException if an I/O exception occurred
     */
    public static String execute(final HttpClient client, final HttpMethod method) throws IOException {

        final StringStreamProcessor stringProcessor = new StringStreamProcessor();

        if (execute(client, method, stringProcessor)) {
            return stringProcessor.getResult();
        }

        return null;
    }

    /**
     * Save the document located at the given URI and use the given processor to get the result.
     * Requests with dedicatedClient will instance one new client with proxies compatible with given URI.
     * Other requests will use the common multi-threaded HTTP client.
     * 
     * @param uri URI to download
     * @param resultProcessor stream processor to use to consume HTTP response
     * @param client http client to use
     * @param credentials 
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    private static boolean download(final URI uri, final HttpClient client, final Credentials credentials,
                                    final StreamProcessor resultProcessor) throws IOException {

        final GetMethod method = new GetMethod(uri.toString());

        // when present, add the credential to the client 
        if (credentials != null) {
            HttpState state = client.getState();
            state.setCredentials(AuthScope.ANY, credentials);
        }

        _logger.info("HTTP client and GET method have been created. doAuthentication = {}", method.getDoAuthentication());

        try {
            // Send HTTP GET query:
            final int resultCode = client.executeMethod(method);
            _logger.debug("The query has been sent. Status code: {}", resultCode);

            // If everything went fine
            if (resultCode == 200) {

                // Get response
                final InputStream in = new BufferedInputStream(method.getResponseBodyAsStream());
                resultProcessor.process(in);

                return true;
            }
            
            if (resultCode == 401) {
                // TODO: ensure EDT (Swing):
                // Memorize the credentials into a session ... reuse login per host name ? or query part ?
                
                // Request user/login password and try again with given credential
                final HttpCredentialForm credentialForm = new HttpCredentialForm(method);
                credentialForm.setVisible(true);
                final Credentials c = credentialForm.getCredentials();

                // if user gives one login/password, try again with the new credential
                if (c != null) {
                    return download(uri, client, credentialForm.getCredentials(), resultProcessor);
                }
                MessagePane.showWarning("Sorry, your file '" + uri + "' can't be retrieved properly\nresult code :" + resultCode + "\n status :" + method.getStatusText(), "Remote file can't be dowloaded");
            }

            _logger.warn("download didn't succeed, result code: {}, status: {}", resultCode, method.getStatusText());

        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        return false;
    }

    /**
     * Push the post form to the given URI and use the given processor to get the result.
     * Requests with dedicatedClient will instance one new client (with automatic proxies compatible with given URI). 
     * Other requests will use the common multi-threaded HttpClient.
     * 
     * @param uri URI to download
     * @param queryProcessor post query processor to define query parameters
     * @param resultProcessor stream processor to use to consume HTTP response
     * @param client HttpClient to use
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    private static boolean post(final URI uri, final HttpClient client,
                                final PostQueryProcessor queryProcessor, final StreamProcessor resultProcessor) throws IOException {

        final PostMethod method = new PostMethod(uri.toString());
        _logger.debug("HTTP client and POST method have been created");

        try {
            // Define HTTP POST parameters
            queryProcessor.process(method);

            // Send HTTP query
            final int resultCode = client.executeMethod(method);
            _logger.debug("The query has been sent. Status code: {}", resultCode);

            // If everything went fine
            if (resultCode == 200) {
                // Get response
                final InputStream in = new BufferedInputStream(method.getResponseBodyAsStream());
                resultProcessor.process(in);

                return true;
            }
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        return false;
    }

    /**
     * Execute the given Http method (GET, POST...) to the given URI and use the given processor to get the result.
     * 
     * @param client HttpClient to use
     * @param method http method to execute
     * @param resultProcessor stream processor to use to consume HTTP response
     * @return true if successful
     * @throws IOException if any I/O operation fails (HTTP or file) 
     */
    private static boolean execute(final HttpClient client,
                                   final HttpMethod method, final StreamProcessor resultProcessor) throws IOException {
        try {
            // Send HTTP query
            final int resultCode = client.executeMethod(method);
            _logger.debug("The query has been sent. Status code: {}", resultCode);

            // If everything went fine
            if (resultCode == 200) {
                // Get response
                final InputStream in = new BufferedInputStream(method.getResponseBodyAsStream());
                resultProcessor.process(in);

                return true;
            }
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        return false;
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
            // TODO check if we can get response size from HTTP headers
            result = FileUtils.readStream(in);
            _logger.debug("String stored in memory ({} chars).", result.length());
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
