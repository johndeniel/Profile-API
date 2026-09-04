package com.profile.api.common.filter;

import com.profile.api.common.logging.Log;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Log log = Log.get(RateLimitFilter.class);

    private static final int MAX_REQUESTS = 60;
    private static final int WINDOW_SECONDS = 60;

    private final ConcurrentHashMap<String, RequestWindow> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        if (shouldSkip(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestWindow window = requestCounts.compute(clientIp, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new RequestWindow();
            }
            return existing;
        });

        if (window.incrementAndGet() > MAX_REQUESTS) {
            long retryAfter = window.secondsUntilReset();
            log.warn("Rate limit exceeded for IP={} on {}", clientIp, requestUri);
            send429(response, retryAfter);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void send429(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        String body = """
                {
                  "timestamp": "%s",
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Rate limit exceeded. Try again in %d seconds."
                }
                """.formatted(Instant.now(), retryAfterSeconds);
        response.getWriter().write(body);
    }

    private boolean shouldSkip(String uri) {
        return uri.startsWith("/actuator")
                || uri.startsWith("/health")
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs");
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

    private static class RequestWindow {
        private final Instant windowStart = Instant.now();
        private long count = 0;

        long incrementAndGet() {
            return ++count;
        }

        boolean isExpired() {
            return Instant.now().isAfter(windowStart.plusSeconds(WINDOW_SECONDS));
        }

        long secondsUntilReset() {
            return Math.max(1, WINDOW_SECONDS - (Instant.now().getEpochSecond() - windowStart.getEpochSecond()));
        }
    }
}
