package com.profile.api.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized logging utility — the ONLY place that touches SLF4J.
 *
 * WHY THIS EXISTS:
 *   Instead of every file doing this:
 *     import org.slf4j.Logger;
 *     import org.slf4j.LoggerFactory;
 *     private static final Logger log = LoggerFactory.getLogger(MyClass.class);
 *
 *   Every file now does this:
 *     import com.profile.api.common.logging.Log;
 *     private static final Log log = Log.get(MyClass.class);
 *
 *   One import, same result, and all loggers are cached in memory.
 *
 * HOW IT WORKS:
 *   1. Log.get(Class) returns a cached Log wrapper for that class
 *   2. log.info("msg {}", value) delegates to SLF4J under the hood
 *   3. The log FORMAT is configured in logback-spring.xml, NOT here
 *   4. MDC context (requestId, clientIp, boundedContext) is set by the
 *      CentralizedRequestLoggingFilter and appears in every log line
 *      automatically via the logback pattern:
 *        [%X{requestId:-}] [%X{clientIp:-}] [%X{boundedContext:-}]
 *
 * USAGE:
 *   private static final Log log = Log.get(MyClass.class);
 *
 *   log.debug("Detail: {}", detail);       // only logged if DEBUG level enabled
 *   log.info("Created user id={}", id);    // logged at INFO level
 *   log.warn("Invalid input: {}", input);  // logged at WARN level
 *   log.error("Something failed", ex);     // logged at ERROR level with stack trace
 */
public final class Log {

    // Cache so we don't create a new Log wrapper for every class.
    // Thread-safe via ConcurrentHashMap.
    private static final Map<Class<?>, Log> CACHE = new ConcurrentHashMap<>();

    private final Logger logger;

    private Log(Logger logger) {
        this.logger = logger;
    }

    /**
     * Get a cached logger for the given class.
     * First call creates it, subsequent calls return the same instance.
     */
    public static Log get(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, c -> new Log(LoggerFactory.getLogger(c)));
    }

    // --- MDC context helpers ---
    // MDC (Mapped Diagnostic Context) lets you attach key-value data to the
    // current thread. Logback reads these values and inserts them into every
    // log line. Example: [abc123] [192.168.1.1] [PROFILE] INFO ...
    // The filter sets these before each request and clears them after.

    /** Add a key-value to the current thread's log context. */
    public static void setContext(String key, String value) {
        MDC.put(key, value);
    }

    /** Remove a key from the current thread's log context. */
    public static void removeContext(String key) {
        MDC.remove(key);
    }

    /** Clear all context from the current thread. Called in filter's finally block. */
    public static void clearContext() {
        MDC.clear();
    }

    /** Set a context value only for the duration of a code block, then auto-remove. */
    public static void withContext(String key, String value, Runnable action) {
        MDC.put(key, value);
        try {
            action.run();
        } finally {
            MDC.remove(key);
        }
    }

    // --- Standard log methods ---
    // These delegate to SLF4J Logger. The logback-spring.xml pattern
    // handles the format (timestamp, thread, MDC fields, level, message).

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

    /** Log an error with the full exception stack trace. */
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
