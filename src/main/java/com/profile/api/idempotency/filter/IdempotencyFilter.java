package com.profile.api.idempotency.filter;

import com.profile.api.common.config.CentralizedLoggingFilter;
import com.profile.api.idempotency.model.IdempotencyKey;
import com.profile.api.idempotency.service.IdempotencyKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Logger log = CentralizedLoggingFilter.getLogger(IdempotencyFilter.class);
    private static final String HEADER = "Idempotency-Key";
    private static final long DEFAULT_TTL = 86400;
    private static final int MAX_KEY = 64;

    private static final Set<String> PATHS = Set.of(
            "/v1/personal-information",
            "/v1/social-links",
            "/v1/license-certificate",
            "/v1/file-store",
            "/v1/curriculum-vitae",
            "/v1/professional-experience",
            "/v1/educational-attainment"
    );

    private final IdempotencyKeyService service;

    public IdempotencyFilter(IdempotencyKeyService service) {
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!"POST".equals(request.getMethod()) || !PATHS.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(HEADER);

        if (key == null || key.isBlank()) {
            sendError(response, HttpStatus.BAD_REQUEST, "Missing required header: " + HEADER);
            return;
        }

        if (key.length() > MAX_KEY) {
            sendError(response, HttpStatus.BAD_REQUEST, HEADER + " must not exceed " + MAX_KEY + " characters");
            return;
        }

        Optional<IdempotencyKey> result;
        try {
            result = service.findOrCreate(key, DEFAULT_TTL);
        } catch (DataIntegrityViolationException e) {
            result = service.findOrCreate(key, DEFAULT_TTL);
        }

        if (result.isEmpty()) {
            sendError(response, HttpStatus.CONFLICT, "Request with this " + HEADER + " is already processing");
            return;
        }

        IdempotencyKey idempotencyKey = result.get();

        if (idempotencyKey.isCompleted()) {
            log.info("Replaying cached response for key={}", key);
            replay(response, idempotencyKey);
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(request, wrappedResponse);
            int status = wrappedResponse.getStatus();
            byte[] body = wrappedResponse.getContentAsByteArray();
            String contentType = wrappedResponse.getContentType();

            if (status >= 200 && status < 300) {
                service.markCompleted(idempotencyKey, status, body, contentType);
            } else {
                service.delete(idempotencyKey);
            }
            wrappedResponse.setHeader(HEADER, key);
            wrappedResponse.copyBodyToResponse();
        } catch (Exception e) {
            service.delete(idempotencyKey);
            throw e;
        }
    }

    private void replay(HttpServletResponse response, IdempotencyKey key) throws IOException {
        response.setStatus(key.getResponseStatus());
        if (key.getResponseContentType() != null) {
            response.setContentType(key.getResponseContentType());
        }
        response.setHeader(HEADER, key.getIdempotencyKey());
        if (key.getResponseBody() != null) {
            byte[] body = key.getResponseBody().getBytes(StandardCharsets.UTF_8);
            response.setContentLength(body.length);
            response.getOutputStream().write(body);
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String safe = message.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
        String json = "{\"status\":" + status.value()
                + ",\"error\":\"" + status.getReasonPhrase() + "\""
                + ",\"message\":\"" + safe + "\"}";
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }
}
