/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: NetworkSettings.java,v 1.5 2011-04-05 15:20:27 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2011/04/04 16:14:07  bourgesl
 * use Introspection to handle properly exception concerning introspection
 *
 * Revision 1.3  2011/03/30 15:01:48  bourgesl
 * fixed socks proxy detector
 *
 * Revision 1.2  2011/03/30 14:51:53  bourgesl
 * dump all known net properties to help us to debug proxy problems
 *
 * Revision 1.1  2011/03/30 09:31:06  bourgesl
 * define general network settings (timeouts / proxy)
 *
 */
package fr.jmmc.mcs.util;

import fr.jmmc.mcs.gui.Introspection;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HostConfiguration;

/**
 * This class gathers general network settings:
 * - socket and connect timeouts
 * - proxy (host / port)
 *
 * It uses Java System properties and also JMCS Preferences to get the proxy settings
 * 
 * @author bourgesl
 */
public final class NetworkSettings
{

    /** logger */
    private final static Logger logger = Logger.getLogger(NetworkSettings.class.getName());
    /* system properties */
    /** Timeout to establish connection in millis (sun classes) */
    public static final String PROPERTY_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
    /** Timeout "waiting for data" (read timeout) in millis (sun classes) */
    public static final String PROPERTY_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";
    /** Use System Proxies */
    public static final String PROPERTY_USE_SYSTEM_PROXIES = "java.net.useSystemProxies";
    /** Java plugin proxy list */
    public static final String PROPERTY_JAVA_PLUGIN_PROXY_LIST = "javaplugin.proxy.config.list";
    /** HTTP proxy host */
    public static final String PROPERTY_HTTP_PROXY_HOST = "http.proxyHost";
    /** HTTP proxy port */
    public static final String PROPERTY_HTTP_PROXY_PORT = "http.proxyPort";
    /** SOCKS proxy host */
    public static final String PROPERTY_SOCKS_PROXY_HOST = "socksProxyHost";
    /** SOCKS proxy port */
    public static final String PROPERTY_SOCKS_PROXY_PORT = "socksProxyPort";
    /* JMMC standard values */
    /** default value for the connection timeout in milliseconds (15 s) */
    public static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
    /** default value for the read timeout in milliseconds (10 minutes) */
    public static final int DEFAULT_SOCKET_READ_TIMEOUT = 10 * 60 * 1000;
    /** The default maximum number of connections allowed per host */
    public static final int DEFAULT_MAX_HOST_CONNECTIONS = 5;
    /** The default maximum number of connections allowed overall */
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 10;

    /**
     * Forbidden constructor
     */
    private NetworkSettings()
    {
        super();
    }

    /**
     * Main entry point : calls defineDefaults()
     * @param args unused
     */
    public static void main(final String[] args)
    {
        defineDefaults();
    }

    /**
     * Define default values (timeouts, proxy ...)
     */
    public static void defineDefaults()
    {
        defineTimeouts();

        defineProxy();
    }

    /**
     * Define timeouts (http / socket)
     */
    public static void defineTimeouts()
    {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("define default Connect timeout to " + DEFAULT_CONNECT_TIMEOUT + " ms.");
        }
        System.setProperty(PROPERTY_DEFAULT_CONNECT_TIMEOUT, Integer.toString(DEFAULT_CONNECT_TIMEOUT));

        if (logger.isLoggable(Level.INFO)) {
            logger.info("define default Read timeout to " + DEFAULT_SOCKET_READ_TIMEOUT + " ms.");
        }
        System.setProperty(PROPERTY_DEFAULT_READ_TIMEOUT, Integer.toString(DEFAULT_SOCKET_READ_TIMEOUT));

    }

    /**
     * Define the proxy settings for http protocol
     */
    public static void defineProxy()
    {
        // FIRST STEP: force JVM to use System proxies if System properties are not defined (or given by JNLP RE):

        // NOTE: can cause problems with SOCKS / HTTPS / Other protocols ?
        System.setProperty(PROPERTY_USE_SYSTEM_PROXIES, "true");




        // first, dump all known network properties:
        final Method netPropertiesGetMethod = getNetPropertiesGetMethod();
        if (netPropertiesGetMethod != null) {
            final Properties netProperties = new Properties();
            String value;

            // HTTP Proxy:
            value = getNetProperty(netPropertiesGetMethod, PROPERTY_HTTP_PROXY_HOST);
            if (value != null) {
                netProperties.put(PROPERTY_HTTP_PROXY_HOST, value);
            }
            value = getNetProperty(netPropertiesGetMethod, PROPERTY_HTTP_PROXY_PORT);
            if (value != null) {
                netProperties.put(PROPERTY_HTTP_PROXY_PORT, value);
            }

            // SOCKS Proxy:
            value = getNetProperty(netPropertiesGetMethod, PROPERTY_SOCKS_PROXY_HOST);
            if (value != null) {
                netProperties.put(PROPERTY_SOCKS_PROXY_HOST, value);
            }
            value = getNetProperty(netPropertiesGetMethod, PROPERTY_SOCKS_PROXY_PORT);
            if (value != null) {
                netProperties.put(PROPERTY_SOCKS_PROXY_PORT, value);
            }

            if (!netProperties.isEmpty() && logger.isLoggable(Level.INFO)) {
                logger.info("Java net properties:\n" + Preferences.dumpProperties(netProperties));
            }
        }

        final String proxyList = System.getProperty(PROPERTY_JAVA_PLUGIN_PROXY_LIST);
        if (proxyList != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Java plugin proxy list : " + proxyList);
            }
        }

        // Dump Http Proxy settings from ProxySelector:
        HostConfiguration hostConfiguration = Http.getHttpProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Found http proxy : " + hostConfiguration.getProxyHost() + ":" + hostConfiguration.getProxyPort());
            }
        }

        // Dump Socks Proxy settings from ProxySelector:
        hostConfiguration = Http.getSocksProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Found socks proxy : " + hostConfiguration.getProxyHost() + ":" + hostConfiguration.getProxyPort());
            }
        }

        // Get Proxy settings (available at least in JNLP runtime environement):
        hostConfiguration = Http.getHttpProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Get proxy settings from Java ProxySelector.");
            }

            defineProxy(hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort());
        } else {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Get proxy settings from CommonPreferences.");
            }

            final CommonPreferences prefs = CommonPreferences.getInstance();

            final String proxyHost = prefs.getPreference(CommonPreferences.HTTP_PROXY_HOST);
            final String proxyPort = prefs.getPreference(CommonPreferences.HTTP_PROXY_PORT);

            if (proxyHost != null && proxyHost.length() > 0) {
                if (proxyPort != null && proxyPort.length() > 0) {
                    try {
                        final int port = Integer.valueOf(proxyPort);
                        if (port != 0) {
                            defineProxy(proxyHost, port);
                            return;
                        }
                    } catch (NumberFormatException nfe) {
                        // invalid number
                    }
                }
            }
            if (logger.isLoggable(Level.INFO)) {
                logger.info("No http proxy defined.");
            }
        }
    }

    /**
     * Define the proxy settings for http protocol
     * @param proxyHost host name
     * @param proxyPort port
     */
    private static void defineProxy(final String proxyHost, final int proxyPort)
    {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("define http proxy to " + proxyHost + ":" + proxyPort);
        }
        // # http.proxyHost
        System.setProperty(PROPERTY_HTTP_PROXY_HOST, proxyHost);

        // # http.proxyPort
        System.setProperty(PROPERTY_HTTP_PROXY_PORT, Integer.toString(proxyPort));

        // TODO : support also advanced proxy settings (user, password ...)
        // # http.proxyUser
        // # http.proxyPassword
        // # http.nonProxyHosts
    }

    /**
     * Returns the sun.net.NetProperties specific property
     *
     * @param netPropertiesGetMethod sun.net.NetProperties.get(String)
     * @param key the property key
     * @return a networking system property. If no system property was defined
     * returns the default value, if it exists, otherwise returns <code>null</code>.
     */
    private static String getNetProperty(final Method netPropertiesGetMethod, final String key)
    {
        return (String) Introspection.getMethodValue(netPropertiesGetMethod, new Object[]{key});
    }

    /**
     * Return the sun.net.NetProperties.get(String) method
     * @return NetProperties.get(String) method or null if unavailable
     */
    private static Method getNetPropertiesGetMethod()
    {
        return Introspection.getMethod("sun.net.NetProperties", "get", new Class<?>[]{String.class});
    }
}
