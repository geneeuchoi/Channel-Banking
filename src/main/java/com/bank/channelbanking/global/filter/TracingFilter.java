package com.bank.channelbanking.global.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class TracingFilter implements Filter {

    private final Tracer tracer;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 새로운 Span 시작
        Span span = tracer.nextSpan().name("http." + httpRequest.getMethod())
                .tag("http.method", httpRequest.getMethod())
                .tag("http.path", httpRequest.getRequestURI())
                .start();

        log.debug("Started span: traceId={}, spanId={}",
                span.context().traceId(), span.context().spanId());

        // Span을 현재 컨텍스트로 설정
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            chain.doFilter(request, response);

            // HTTP 상태 코드 태그 추가
            span.tag("http.status_code", String.valueOf(httpResponse.getStatus()));

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            // Span 종료
            span.end();
            log.debug("Ended span: traceId={}, spanId={}",
                    span.context().traceId(), span.context().spanId());
        }
    }
}
