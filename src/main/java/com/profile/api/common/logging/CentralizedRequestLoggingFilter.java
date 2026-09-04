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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CentralizedRequestLoggingFilter extends OncePerRequestFilter {

    private static final Log log = Log.get(CentralizedRequestLoggingFilter.class);

    private static final Map<String, String> URI_CONTEXT_MAP = Map.of(
            "/v1/personal-information", "PROFILE"
    );

    private static final String[] SKIP_LOG_URIS = {"/actuator", "/health", "/swagger", "/v3/api-docs"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (shouldSkipLogging(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String requestId = UUID.randomUUID().toString().substring(0, 8);
            String clientIp = getClientIp(request);
            String httpMethod = request.getMethod();
            String boundedContext = resolveBoundedContext(requestUri);

            Log.setContext(Log.REQUEST_ID, requestId);
            Log.setContext(Log.CLIENT_IP, clientIp);
            Log.setContext(Log.HTTP_METHOD, httpMethod);
            Log.setContext(Log.REQUEST_URI, requestUri);
            if (boundedContext != null) {
                Log.setContext(Log.BOUNDED_CONTEXT, boundedContext);
            }

            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long duration = System.currentTimeMillis() - startTime;

            int statusCode = response.getStatus();
            log.info("{} {} {} - {} [{}ms]", httpMethod, requestUri, statusCode, getStatusCodeDescription(statusCode), duration);

        } finally {
            Log.clearContext();
        }
    }

    private String resolveBoundedContext(String uri) {
        for (Map.Entry<String, String> entry : URI_CONTEXT_MAP.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean shouldSkipLogging(String uri) {
        for (String skipUri : SKIP_LOG_URIS) {
            if (uri.startsWith(skipUri)) {
                return true;
            }
        }
        return false;
    }

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
