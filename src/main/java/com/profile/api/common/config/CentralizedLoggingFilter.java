package com.profile.api.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CentralizedLoggingFilter extends OncePerRequestFilter {

    private static final Map<Class<?>, Logger> CACHE = new ConcurrentHashMap<>();

    private static final String REQUEST_ID = "requestId";
    private static final String CLIENT_IP = "clientIp";
    private static final String BOUNDED_CONTEXT = "boundedContext";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String REQUEST_URI = "requestUri";

    private static final Map<String, String> URI_CONTEXT_MAP = Map.of(
            "/v1/personal-information", "PROFILE",
            "/v1/file-store", "FILE"
    );

    private static final String[] SKIP_LOG_URIS = {"/actuator", "/health", "/swagger", "/v3/api-docs"};

    // --- Logger factory (replaces Log.java) ---

    public static Logger getLogger(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }

    // --- MDC context helpers ---

    public static void setContext(String key, String value) {
        MDC.put(key, value);
    }

    public static void removeContext(String key) {
        MDC.remove(key);
    }

    public static void clearContext() {
        MDC.clear();
    }

    public static void withContext(String key, String value, Runnable action) {
        MDC.put(key, value);
        try {
            action.run();
        } finally {
            MDC.remove(key);
        }
    }

    // --- Filter logic ---

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

            setContext(REQUEST_ID, requestId);
            setContext(CLIENT_IP, clientIp);
            setContext(HTTP_METHOD, httpMethod);
            setContext(REQUEST_URI, requestUri);
            if (boundedContext != null) {
                setContext(BOUNDED_CONTEXT, boundedContext);
            }

            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long duration = System.currentTimeMillis() - startTime;

            int statusCode = response.getStatus();
            Logger log = getLogger(CentralizedLoggingFilter.class);
            log.info("{} {} {} - {} [{}ms]", httpMethod, requestUri, statusCode, getStatusCodeDescription(statusCode), duration);

        } finally {
            clearContext();
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
