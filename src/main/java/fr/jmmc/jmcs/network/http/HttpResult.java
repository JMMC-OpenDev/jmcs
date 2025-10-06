/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.http;

import org.apache.commons.httpclient.HttpStatus;

/**
 * HTTP Query result
 * @author bourgesl
 */
public final class HttpResult {

    public final static int HTTP_OK = HttpStatus.SC_OK;

    // HTTP URL:
    private final String url;
    // HTTP resulkt code:
    private final int httpResultCode;
    // optional http response:
    private String response = null;

    public HttpResult(final String url, final int httpResultCode) {
        this.url = url;
        this.httpResultCode = httpResultCode;
    }

    public HttpResult(final String response) {
        this(null, HTTP_OK);
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHttpResultOK() {
        return (httpResultCode == HTTP_OK);
    }

    public int getHttpResultCode() {
        return httpResultCode;
    }

    public String getResponse() {
        return response;
    }

    void setResponse(final String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "HttpResult[" + httpResultCode + "]"
                + ((url != null) ? "(url=" + url + ")" : "")
                + " = " + response;
    }

}
