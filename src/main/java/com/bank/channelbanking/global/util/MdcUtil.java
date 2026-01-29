package com.bank.channelbanking.global.util;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MDC 유틸리티
 * Micrometer Tracer에서 직접 traceId/spanId를 가져옴
 */
@Slf4j
@Component
public class MdcUtil {

    private static Tracer tracer;

    // Setter 주입으로 변경
    @Autowired
    public void setTracer(Tracer tracer) {
        MdcUtil.tracer = tracer;
        log.info("Tracer injected successfully: {}", tracer.getClass().getSimpleName());
    }

    /**
     * TraceId 가져오기
     * Micrometer Tracer의 현재 span에서 가져오기
     */
    public static String getTraceId() {
        if (tracer != null && tracer.currentSpan() != null) {
            String traceId = tracer.currentSpan().context().traceId();
            log.debug("Retrieved traceId from Micrometer Tracer: {}", traceId);
            return traceId;
        }

        log.debug("No active span found, traceId is null");
        return null;
    }

    /**
     * SpanId 가져오기
     * Micrometer Tracer의 현재 span에서 가져오기
     */
    public static String getSpanId() {
        if (tracer != null && tracer.currentSpan() != null) {
            String spanId = tracer.currentSpan().context().spanId();
            log.debug("Retrieved spanId from Micrometer Tracer: {}", spanId);
            return spanId;
        }

        log.debug("No active span found, spanId is null");
        return null;
    }
}
