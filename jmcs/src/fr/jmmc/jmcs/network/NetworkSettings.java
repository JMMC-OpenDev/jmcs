/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network;

import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.util.Introspection;
import java.lang.reflect.Method;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.httpclient.HostConfiguration;

/**
 * This class gathers general network settings:
 * - socket and connect timeouts
 * - proxy (host / port)
 *
 * It uses Java System properties and also JMCS Preferences to get the proxy settings
 * 
 * @author Laurent BOURGES, Guillaume MELLA.
 */
public final class NetworkSettings {

    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(NetworkSettings.class.getName());
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
    /** HTTP non proxy hosts */
    public static final String PROPERTY_HTTP_NO_PROXY_HOSTS = "http.nonProxyHosts";
    /** SOCKS proxy host */
    public static final String PROPERTY_SOCKS_PROXY_HOST = "socksProxyHost";
    /** SOCKS proxy port */
    public static final String PROPERTY_SOCKS_PROXY_PORT = "socksProxyPort";
    /* JMMC standard values */
    /** Use system proxies (false by default) */
    public static final String USE_SYSTEM_PROXIES = "false";
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
    private NetworkSettings() {
        super();
    }

    /**
     * Main entry point : calls defineDefaults()
     * @param args unused
     */
    public static void main(final String[] args) {
        defineDefaults();
    }

    /**
     * Define default values (timeouts, proxy ...)
     */
    public static void defineDefaults() {
        defineTimeouts();

        defineProxy();
    }

    /**
     * Define timeouts (http / socket)
     */
    public static void defineTimeouts() {
        logger.info("define default Connect timeout to {} ms.", DEFAULT_CONNECT_TIMEOUT);
        System.setProperty(PROPERTY_DEFAULT_CONNECT_TIMEOUT, Integer.toString(DEFAULT_CONNECT_TIMEOUT));

        logger.info("define default Read timeout to {} ms.", DEFAULT_SOCKET_READ_TIMEOUT);
        System.setProperty(PROPERTY_DEFAULT_READ_TIMEOUT, Integer.toString(DEFAULT_SOCKET_READ_TIMEOUT));
    }

    /**
     * Define the proxy settings for http protocol
     */
    public static void defineProxy() {
        // FIRST STEP: force JVM to use System proxies if System properties are not defined (or given by JNLP RE):

        // NOTE: USE of SYSTEM_PROXIES can cause problems with SOCKS / HTTPS / Other protocols ?
        // unset env var all_proxy=socks://w and ALL_PROXY
        System.setProperty(PROPERTY_USE_SYSTEM_PROXIES, USE_SYSTEM_PROXIES);

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

            if (!netProperties.isEmpty() && logger.isInfoEnabled()) {
                logger.info("Java net properties:\n{}", Preferences.dumpProperties(netProperties));
            }
        }

        final String proxyList = System.getProperty(PROPERTY_JAVA_PLUGIN_PROXY_LIST);
        if (proxyList != null) {
            logger.info("Java plugin proxy list: {}", proxyList);
        }

        // Dump Http Proxy settings from ProxySelector:
        HostConfiguration hostConfiguration = Http.getHttpProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            logger.info("Found http proxy: {}:{}", hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort());
        }

        // Dump Socks Proxy settings from ProxySelector:
        hostConfiguration = Http.getSocksProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            logger.info("Found socks proxy: {}:{}", hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort());
        }

        // Get Proxy settings (available at least in JNLP runtime environement):
        hostConfiguration = Http.getHttpProxyConfiguration();

        if (hostConfiguration.getProxyHost() != null) {
            logger.info("Get proxy settings from Java ProxySelector.");

            defineProxy(hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort());
        } else {
            logger.info("Get proxy settings from CommonPreferences.");

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
            logger.info("No http proxy defined.");
        }
    }

    /**
     * Define the proxy settings for http protocol
     * @param proxyHost host name
     * @param proxyPort port
     */
    private static void defineProxy(final String proxyHost, final int proxyPort) {
        logger.info("define http proxy to {}:{}", proxyHost, proxyPort);

        // # http.proxyHost
        System.setProperty(PROPERTY_HTTP_PROXY_HOST, proxyHost);

        // # http.proxyPort
        System.setProperty(PROPERTY_HTTP_PROXY_PORT, Integer.toString(proxyPort));

        // # http.nonProxyHosts
//        System.setProperty(PROPERTY_HTTP_NO_PROXY_HOSTS, "localhost|127.0.0.1");

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
    private static String getNetProperty(final Method netPropertiesGetMethod, final String key) {
        return (String) Introspection.getMethodValue(netPropertiesGetMethod, new Object[]{key});
    }

    /**
     * Return the sun.net.NetProperties.get(String) method
     * @return NetProperties.get(String) method or null if unavailable
     */
    private static Method getNetPropertiesGetMethod() {
        return Introspection.getMethod("sun.net.NetProperties", "get", new Class<?>[]{String.class});
    }
}
