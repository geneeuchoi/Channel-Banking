package com.bank.channelbanking.global.aop;

import com.bank.channelbanking.global.util.MdcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ClientLoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("execution(* com.bank.channelbanking.*.controller.*Client.*(..))")
    public Object logExternalCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String targetSystem = joinPoint.getTarget().getClass().getSimpleName()
                .replace("$Proxy", "")
                .replace("$$", "");
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logCallStart(targetSystem, method, args);

        Object result = null;
        String logLevel = "INFO";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();

        } catch (Exception e) {
            logLevel = "ERROR";
            errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logCallEnd(targetSystem, method, duration, logLevel, errorMessage);
        }

        return result;
    }

    private void logCallStart(String targetSystem, String method, Object[] args) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "external_call_start");
            logData.put("message", "Calling external system - " + targetSystem);

            // 외부 호출 정보
            Map<String, Object> externalCall = new LinkedHashMap<>();
            externalCall.put("targetSystem", targetSystem);
            externalCall.put("method", method);
            logData.put("externalCall", externalCall);

            // 요청 파라미터
            if (args != null && args.length > 0) {
                logData.put("requestParams", args);
            }

            // Micrometer가 생성한 traceId, spanId
            // Micrometer가 생성한 traceId, spanId
            logData.put("traceId", MdcUtil.getTraceId());
            logData.put("spanId", MdcUtil.getSpanId());

            log.info("{}", objectMapper.writeValueAsString(logData));
        } catch (Exception e) {
            log.warn("Failed to create external call start log: {}", e.getMessage());
        }
    }

    private void logCallEnd(String targetSystem, String method, long duration,
                           String logLevel, String errorMessage) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("logType", "external_call_end");
            logData.put("message", "External system call completed - " + targetSystem);

            Map<String, Object> externalCall = new LinkedHashMap<>();
            externalCall.put("targetSystem", targetSystem);
            externalCall.put("method", method);
            externalCall.put("duration", duration);
            externalCall.put("status", errorMessage == null ? "SUCCESS" : "FAILED");
            logData.put("externalCall", externalCall);

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
            } else if (duration > 3000) {
                log.warn("{}", jsonLog);
            } else {
                log.info("{}", jsonLog);
            }
        } catch (Exception e) {
            log.warn("Failed to create external call end log: {}", e.getMessage());
        }
    }
}
