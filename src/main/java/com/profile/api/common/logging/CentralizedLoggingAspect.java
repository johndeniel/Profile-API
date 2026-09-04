package com.profile.api.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class CentralizedLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(CentralizedLoggingAspect.class);

    @Pointcut(LogConstants.CONTROLLER_POINTCUT)
    public void controllerMethods() {}

    @Pointcut(LogConstants.SERVICE_POINTCUT)
    public void serviceMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[CONTROLLER] {}.{}() - Input: {}", className, methodName, formatArgs(args));

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[CONTROLLER] {}.{}() - Completed in {}ms", className, methodName, duration);

        return result;
    }

    @Around("serviceMethods()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("[SERVICE] {}.{}() - Processing", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        log.debug("[SERVICE] {}.{}() - Completed in {}ms", className, methodName, duration);

        return result;
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "none";
        }
        return Arrays.toString(args);
    }
}
