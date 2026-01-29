package com.bank.channelbanking.global.aop;

import com.bank.channelbanking.global.util.MdcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String method = request.getMethod();
        String path = request.getRequestURI();
        String remoteIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        Object[] args = joinPoint.getArgs();

        logRequest(method, path, remoteIp, userAgent, args);

        Object result = null;
        String logLevel = "INFO";
        Integer statusCode = null;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();

            if (result instanceof ResponseEntity) {
                statusCode = ((ResponseEntity<?>) result).getStatusCode().value();
            } else {
                statusCode = 200;
            }

        } catch (Exception e) {
            logLevel = "ERROR";
            statusCode = 500;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logResponse(method, path, statusCode, duration, logLevel, errorMessage);
        }

        return result;
    }

    private void logRequest(String method, String path, String remoteIp,
                           String userAgent, Object[] args) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "api_request");
            logData.put("message", "API request received");

            Map<String, Object> httpInfo = new LinkedHashMap<>();
            httpInfo.put("method", method);
            httpInfo.put("path", path);
            httpInfo.put("remoteIp", remoteIp);
            httpInfo.put("userAgent", userAgent);
            logData.put("http", httpInfo);

            // Micrometer가 생성한 traceId, spanId
            logData.put("traceId", MdcUtil.getTraceId());
            logData.put("spanId", MdcUtil.getSpanId());

            log.info("{}", objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.warn("Failed to create request log: {}", e.getMessage());
        }
    }

    private void logResponse(String method, String path, Integer statusCode,
                            long duration, String logLevel, String errorMessage) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "api_response");
            logData.put("message", "API request completed");

            Map<String, Object> httpInfo = new LinkedHashMap<>();
            httpInfo.put("method", method);
            httpInfo.put("path", path);
            httpInfo.put("statusCode", statusCode);
            logData.put("http", httpInfo);

            logData.put("duration", duration);

            if (errorMessage != null) {
                Map<String, Object> errorInfo = new LinkedHashMap<>();
                errorInfo.put("message", errorMessage);
                logData.put("error", errorInfo);
            }

            // Micrometer가 생성한 traceId, spanId
            logData.put("traceId", MdcUtil.getTraceId());
            logData.put("spanId", MdcUtil.getSpanId());

            String jsonLog = objectMapper.writeValueAsString(logData);

            if ("ERROR".equals(logLevel)) {
                log.error("{}", jsonLog);
            } else {
                log.info("{}", jsonLog);
            }
        } catch (Exception e) {
            log.warn("Failed to create response log: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
