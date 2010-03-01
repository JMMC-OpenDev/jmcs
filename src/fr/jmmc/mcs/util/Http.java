/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Http.java,v 1.2 2010-03-01 14:51:57 mella Exp $"
 *
 */
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

/**
 *  This util class is dedicated to gather code associated to http domain.
 *
 *  It actually :
 * - returns a well configured HttpClient (jakarta project)
 * @author mella
 */
public class Http {

    public final static String className_ = Http.class.getName();
    static Logger logger_ = Logger.getLogger(className_);

    /**
     * This class returns one httpclient.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     * 
     * @todo remove the limit for support of the first proxy.
     */
    public static HttpClient getHttpClient() {
        try {
            return getHttpClient(new URI("http://www.jmmc.fr"));
        } catch (URISyntaxException ex) {
            logger_.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This class returns one httpclient for the associated URI.
     * This client:
     *  * uses the default proxy configuration (based on http://www.jmmc.fr).
     *  * is thread safe.
     * @param uri reference uri used to get the proper proxy
     * @todo remove the limit for support of the first proxy.
     */
    public static HttpClient getHttpClient(URI uri) {
        HttpClient httpClient = null;
        if (httpClient == null) {
            MultiThreadedHttpConnectionManager connectionManager =
                    new MultiThreadedHttpConnectionManager();
            httpClient = new HttpClient(connectionManager);

            HostConfiguration hostConfiguration = new HostConfiguration();
            ProxySelector proxySelector = ProxySelector.getDefault();
            List<Proxy> list = proxySelector.select(uri);
            Proxy p = list.get(0);
            logger_.log(Level.FINE, "using " + p + "in proxyList = " + list);
            if (p.type() != Proxy.Type.DIRECT) {
                String host;
                int port;
                InetSocketAddress epoint = (InetSocketAddress) p.address();
                if (epoint.isUnresolved()) {
                    host = epoint.getHostName();
                } else {
                    host = epoint.getAddress().getHostName();
                }
                port = epoint.getPort();
                hostConfiguration.setProxy(host, port);
                logger_.log(Level.FINE, "setting proxy " + host + ":" + port);
                System.setProperty(className_+".proxy", host+":"+port);
                httpClient.setHostConfiguration(hostConfiguration);
            }
        }

        return httpClient;
    }
}
