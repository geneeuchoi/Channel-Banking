package com.bank.channelbanking.config;

import brave.Tracing;
import brave.sampler.Sampler;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

@Configuration
public class TracingConfig {

    @Bean
    public io.micrometer.tracing.Tracer tracer() {
        // Zipkin Sender 설정
        URLConnectionSender sender = URLConnectionSender.create("http://localhost:9411/api/v2/spans");

        // Zipkin 리포터 설정
        AsyncZipkinSpanHandler zipkinSpanHandler = AsyncZipkinSpanHandler
                .create(sender);

        // Brave Tracing 설정
        Tracing tracing = Tracing.newBuilder()
                .localServiceName("channel-banking")
                .sampler(Sampler.ALWAYS_SAMPLE) // 모든 요청 샘플링
                .addSpanHandler(zipkinSpanHandler)
                .build();

        // Micrometer Tracer로 래핑
        return new BraveTracer(
                tracing.tracer(),
                new BraveCurrentTraceContext(tracing.currentTraceContext()),
                new BraveBaggageManager()
        );
    }
}
