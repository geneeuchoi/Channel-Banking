package com.bank.channelbanking.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
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

        // 요청 정보 수집
        String method = request.getMethod();
        String path = request.getRequestURI();
        String remoteIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        Object[] args = joinPoint.getArgs();

        // 요청 로그 출력
        logRequest(method, path, remoteIp, userAgent, args);

        Object result = null;
        String logLevel = "INFO";
        Integer statusCode = null;
        String errorMessage = null;

        try {
            // 실제 메서드 실행
            result = joinPoint.proceed();

            // 응답 상태코드 추출
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

            // 응답 로그 출력
            logResponse(method, path, statusCode, duration, logLevel, errorMessage);
        }

        return result;
    }

    /**
     * API 요청 로그 (JSON 형식)
     */
    private void logRequest(String method, String path, String remoteIp,
                           String userAgent, Object[] args) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "api_request");
            logData.put("message", "API request received");

            // HTTP 정보
            Map<String, Object> httpInfo = new LinkedHashMap<>();
            httpInfo.put("method", method);
            httpInfo.put("path", path);
            httpInfo.put("remoteIp", remoteIp);
            httpInfo.put("userAgent", userAgent);
            logData.put("http", httpInfo);


            // MDC에서 traceId, spanId 가져오기
            logData.put("traceId", MDC.get("traceId"));
            logData.put("spanId", MDC.get("spanId"));

            log.info("{}", objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.warn("Failed to create request log: {}", e.getMessage());
        }
    }

    /**
     * API 응답 로그 (JSON 형식)
     */
    private void logResponse(String method, String path, Integer statusCode,
                            long duration, String logLevel, String errorMessage) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "api_response");
            logData.put("message", "API request completed");

            // HTTP 정보
            Map<String, Object> httpInfo = new LinkedHashMap<>();
            httpInfo.put("method", method);
            httpInfo.put("path", path);
            httpInfo.put("statusCode", statusCode);
            logData.put("http", httpInfo);

            // 성능 정보
            logData.put("duration", duration);

            // 에러 정보 (있는 경우)
            if (errorMessage != null) {
                Map<String, Object> errorInfo = new LinkedHashMap<>();
                errorInfo.put("message", errorMessage);
                logData.put("error", errorInfo);
            }

            // MDC에서 traceId, spanId 가져오기
            logData.put("traceId", MDC.get("traceId"));
            logData.put("spanId", MDC.get("spanId"));

            String jsonLog = objectMapper.writeValueAsString(logData);

            // 로그 레벨에 따라 출력
            if ("ERROR".equals(logLevel)) {
                log.error("{}", jsonLog);
            } else {
                log.info("{}", jsonLog);
            }
        } catch (Exception e) {
            log.warn("Failed to create response log: {}", e.getMessage());
        }
    }

    /**
     * 클라이언트 IP 추출 (프록시 고려)
     */
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
