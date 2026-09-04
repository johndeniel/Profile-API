package com.profile.api.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized logging utility.
 *
 * Usage in any class:
 *   private static final Log log = Log.get(MyClass.class);
 *
 * Then call:
 *   log.info("Something happened with id={}", id);
 *   log.error("Failed to process", exception);
 *
 * The log pattern (requestId, clientIp, boundedContext, etc.)
 * is configured in logback-spring.xml and applied automatically.
 */
public final class Log {

    public static final String REQUEST_ID = "requestId";
    public static final String CLIENT_IP = "clientIp";
    public static final String BOUNDED_CONTEXT = "boundedContext";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String REQUEST_URI = "requestUri";

    private static final Map<Class<?>, Log> CACHE = new ConcurrentHashMap<>();

    private final Logger logger;

    private Log(Logger logger) {
        this.logger = logger;
    }

    public static Log get(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, c -> new Log(LoggerFactory.getLogger(c)));
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

    // --- Standard log methods ---

    public void debug(String msg, Object... args) {
        logger.debug(msg, args);
    }

    public void info(String msg, Object... args) {
        logger.info(msg, args);
    }

    public void warn(String msg, Object... args) {
        logger.warn(msg, args);
    }

    public void error(String msg, Object... args) {
        logger.error(msg, args);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
