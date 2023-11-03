package com.security.gas.plant.config;


import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    private final Logger generalLog = LoggerFactory.getLogger(this.getClass());
    private final Logger endpointLog = LoggerFactory.getLogger("EndpointLogger");

    @Around("execution(* com.security.gas.plant.controller..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        String endpointCalled = request.getRequestURI();
        Object result = joinPoint.proceed();

        if (endpointCalled.equals("/api/v1/auth/login")) {
            String email = request.getHeader("email");
            endpointLog.info("User with email: {} at IP address: {} logged in", email, ipAddress);

        } else if (endpointCalled.equals("/api/v1/auth/register")) {
            String email = request.getHeader("email");
            endpointLog.info("User with email: {} at IP address: {} registered", email, ipAddress);

        } else {
            String username = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "anonymous";
            String response = (result != null) ? result.toString() : "null";
            endpointLog.info("User: {} from IP: {} called endpoint: {}. Response: {}", username, ipAddress, endpointCalled, response);
        }

        return result;
    }
}