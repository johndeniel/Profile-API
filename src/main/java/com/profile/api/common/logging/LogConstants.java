package com.profile.api.common.logging;

public final class LogConstants {

    private LogConstants() {}

    public static final String REQUEST_ID = "requestId";
    public static final String CLIENT_IP = "clientIp";
    public static final String BOUNDED_CONTEXT = "boundedContext";
    public static final String USER_ID = "userId";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String REQUEST_URI = "requestUri";

    public static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId:-}] [%X{clientIp:-}] [%X{boundedContext:-}] %-5level %logger{36} - %msg%n";

    public static final String CONTROLLER_POINTCUT = "execution(* com.profile.api..controller..*(..))";
    public static final String SERVICE_POINTCUT = "execution(* com.profile.api..service..*(..))";
    public static final String REPOSITORY_POINTCUT = "execution(* com.profile.api..repository..*(..))";
}
