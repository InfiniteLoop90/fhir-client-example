package com.myorganization.interceptor;

import ca.uhn.fhir.rest.client.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This interceptor adds arbitrary header values to requests made by this client.
 */
public class AdditionalHttpHeadersInterceptor implements IClientInterceptor {
    private final Map<String, List<String>> additionalHttpHeaders = new HashMap<>();

    public AdditionalHttpHeadersInterceptor () {
        this(new HashMap<>());
    }

    public AdditionalHttpHeadersInterceptor (Map<String, List<String>> additionalHttpHeaders) {
        super();
        if (additionalHttpHeaders != null) {
            this.additionalHttpHeaders.putAll(additionalHttpHeaders);
        }
    }

    /**
     * Adds the given header value.
     * Note that {@code headerName} and {@code headerValue} cannot be null.
     * @param headerName the name of the header
     * @param headerValue the value to add for the header
     * @throws NullPointerException if either parameter is {@code null}
     */
    public void addHeaderValue (String headerName, String headerValue) throws NullPointerException {
        Objects.requireNonNull(headerName, "headerName cannot be null");
        Objects.requireNonNull(headerValue, "headerValue cannot be null");

        getHeaderValues(headerName).add(headerValue);
    }

    /**
     * Adds the list of header values for the given header.
     * Note that {@code headerName} and {@code headerValues} cannot be null.
     * @param headerName the name of the header
     * @param headerValues the list of values to add for the header
     * @throws NullPointerException if either parameter is {@code null}
     */
    public void addAllHeaderValues (String headerName, List<String> headerValues) throws NullPointerException {
        Objects.requireNonNull(headerName, "headerName cannot be null");
        Objects.requireNonNull(headerValues, "headerValues cannot be null");

        getHeaderValues(headerName).addAll(headerValues);
    }

    /**
     * Gets the header values list for a given header. If the header doesn't have any values, an empty list will be returned.
     * @param headerName the name of the header
     * @return the list of values for the header
     */
    private List<String> getHeaderValues (String headerName) {
        return additionalHttpHeaders.computeIfAbsent(headerName, val -> new ArrayList<>());
    }

    /**
     * Adds the additional header values to the HTTP request.
     * @param theRequest the HTTP request
     */
    @Override
    public void interceptRequest (IHttpRequest theRequest) {
        for (Map.Entry<String, List<String>> header : additionalHttpHeaders.entrySet()) {
            for (String headerValue : header.getValue()) {
                if (headerValue != null) {
                    theRequest.addHeader(header.getKey(), headerValue);
                }
            }
        }
    }

    @Override
    public void interceptResponse (IHttpResponse theResponse) throws IOException {
        // Do nothing. This interceptor doesn't care about the response.
    }
}
