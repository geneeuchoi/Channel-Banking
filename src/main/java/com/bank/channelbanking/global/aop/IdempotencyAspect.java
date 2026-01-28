package com.bank.channelbanking.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;

    // 포인트컷 지시자(PCD)
    @Around("@annotation(com.bank.channelbanking.global.annotation.Idempotent)")
    public Object idempotencyAop(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key 헤더가 없습니다.");
        }

        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent("idempotency:" + idempotencyKey, "USED", 24, TimeUnit.HOURS);

        if (!isFirstRequest) {
            throw new IllegalStateException("이미 처리 중이거나 처리된 요청입니다.");
        }

        return joinPoint.proceed();
    }
}