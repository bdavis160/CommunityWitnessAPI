package org.communitywitness.api;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

// Based on https://stackoverflow.com/a/28067653
@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {
	// The headers that Cross-Origin clients are allowed to send
	private static final String ALLOWED_HEADERS = "X-API-KEY, X-Requested-With, Accept-Version, Content-MD5, CSRF-Token, Content-Type";
	// The methods that Cross-Origin clients are allowed to send
	private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	
	/**
	 * A filter that checks for CORS requests and handles them accordingly
	 */
    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
            return;
        }
    }

    /**
     * Checks if a request is a preflight CORS request.
     * @param request the request to check
     * @return true if the request is a preflight request, false if not
     */
    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    /**
     * A filter that attaches CORS data to responses as needed.
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {

        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        if (request.getHeaderString("Origin") == null) {
            return;
        }

        // If it is a preflight request, then we add all
        // the CORS headers here.
        if (isPreflightRequest(request)) {
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
            response.getHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        }

        // Cross origin requests can be either simple requests
        // or preflight request. We need to add this header
        // to both type of requests. Only preflight requests
        // need the previously added headers.
        response.getHeaders().add("Access-Control-Allow-Origin", Settings.getInstance().getAllowedCrossOrigin());
    }
}
