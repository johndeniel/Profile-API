package com.profile.api.common.logging;

public final class BoundedContextTemplates {

    private BoundedContextTemplates() {}

    public static final String CONTROLLER_LOG_TEMPLATE = """
            [CONTROLLER] {} {} - {} - Input: {} - Status: {} - Duration: {}ms
            """;

    public static final String SERVICE_LOG_TEMPLATE = """
            [SERVICE] {} - {} - Input: {} - Output: {} - Duration: {}ms
            """;

    public static final String REPOSITORY_LOG_TEMPLATE = """
            [REPOSITORY] {} - Query: {} - Results: {} - Duration: {}ms
            """;

    public static final String ERROR_LOG_TEMPLATE = """
            [ERROR] {} - {} - Message: {} - StackTrace: {}
            """;

    public static final String SECURITY_LOG_TEMPLATE = """
            [SECURITY] {} - User: {} - Action: {} - IP: {} - Status: {}
            """;

    public static final String AUDIT_LOG_TEMPLATE = """
            [AUDIT] {} - Entity: {} - Action: {} - UserId: {} - Timestamp: {}
            """;
}
