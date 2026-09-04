package com.profile.api.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * HTTP request logging filter — runs BEFORE every request.
 *
 * WHAT IT DOES:
 *   1. Generates a unique requestId for each request
 *   2. Extracts the client's IP address
 *   3. Reads the request method and URI
 *   4. Maps the URI to a "bounded context" (e.g., /v1/personal-information → PROFILE)
 *   5. Puts all of this into MDC so every log line includes it
 *   6. Lets the request proceed (filterChain.doFilter)
 *   7. Logs the request with status code and duration
 *   8. Clears MDC in finally block to prevent memory leaks between threads
 *
 * THE RESULT:
 *   Every log line looks like:
 *     2024-01-15 10:30:45.123 [http-nio-8080-exec-1] [abc123] [192.168.1.1] [PROFILE] INFO ...
 *
 * WHY @Order(HIGHEST_PRECEDENCE):
 *   This filter must run FIRST so that all other filters and controllers
 *   have access to the MDC context (requestId, clientIp, etc.).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CentralizedRequestLoggingFilter extends OncePerRequestFilter {

    private static final Log log = Log.get(CentralizedRequestLoggingFilter.class);

    // MDC key names — these appear in logback-spring.xml as [%X{requestId:-}] etc.
    private static final String REQUEST_ID = "requestId";
    private static final String CLIENT_IP = "clientIp";
    private static final String BOUNDED_CONTEXT = "boundedContext";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String REQUEST_URI = "requestUri";

    // Maps URI prefixes to bounded context names.
    // When a request matches a URI, that context name is added to all logs.
    // To add a new context (e.g., education), just add a line here:
    //   "/v1/education", "EDUCATION"
    private static final Map<String, String> URI_CONTEXT_MAP = Map.of(
            "/v1/personal-information", "PROFILE"
    );

    // URIs that should NOT be logged (health checks, swagger docs, etc.)
    private static final String[] SKIP_LOG_URIS = {"/actuator", "/health", "/swagger", "/v3/api-docs"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // Skip logging for health checks, swagger, etc. — they clutter the logs
        if (shouldSkipLogging(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Generate a short unique ID for this request (e.g., "a1b2c3d4")
            String requestId = UUID.randomUUID().toString().substring(0, 8);

            // Get client IP — checks proxy headers first (X-Forwarded-For, X-Real-IP)
            String clientIp = getClientIp(request);
            String httpMethod = request.getMethod();

            // Map URI to bounded context (e.g., /v1/personal-information → "PROFILE")
            String boundedContext = resolveBoundedContext(requestUri);

            // Put everything into MDC — this makes it available in ALL log lines
            // until clearContext() is called in the finally block
            Log.setContext(REQUEST_ID, requestId);
            Log.setContext(CLIENT_IP, clientIp);
            Log.setContext(HTTP_METHOD, httpMethod);
            Log.setContext(REQUEST_URI, requestUri);
            if (boundedContext != null) {
                Log.setContext(BOUNDED_CONTEXT, boundedContext);
            }

            // Let the request proceed through the rest of the filter chain
            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long duration = System.currentTimeMillis() - startTime;

            // Log after the response is sent — we now know the status code and duration
            int statusCode = response.getStatus();
            log.info("{} {} {} - {} [{}ms]", httpMethod, requestUri, statusCode, getStatusCodeDescription(statusCode), duration);

        } finally {
            // ALWAYS clear MDC — prevents context from leaking to the next request
            // on the same thread (thread pools reuse threads)
            Log.clearContext();
        }
    }

    /**
     * Match the request URI to a bounded context.
     * Example: "/v1/personal-information/123" → "PROFILE"
     * Returns null if no match (context just won't appear in logs).
     */
    private String resolveBoundedContext(String uri) {
        for (Map.Entry<String, String> entry : URI_CONTEXT_MAP.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /** Check if this URI should skip logging (health, swagger, etc.) */
    private boolean shouldSkipLogging(String uri) {
        for (String skipUri : SKIP_LOG_URIS) {
            if (uri.startsWith(skipUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract client IP from the request.
     * Checks proxy headers first (for deployments behind load balancers):
     *   - X-Forwarded-For: standard proxy header (may have multiple IPs, we take the first)
     *   - X-Real-IP: nginx proxy header
     * Falls back to remoteAddr for direct connections.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    /** Convert HTTP status code to a human-readable description for logging */
    private String getStatusCodeDescription(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Unprocessable Entity";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
