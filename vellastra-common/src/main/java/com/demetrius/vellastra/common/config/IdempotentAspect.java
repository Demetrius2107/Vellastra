package com.demetrius.vellastra.common.config;

import com.demetrius.vellastra.common.annotation.Idempotent;
import com.demetrius.vellastra.common.service.IdempotentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <p>Title: IdempotentAspect</p>
 * <p>Description: 幂等性校验 AOP 切面，拦截 @Idempotent 注解防止重复提交</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    private final IdempotentService idempotentService;

    public IdempotentAspect(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint pjp, Idempotent idempotent) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return pjp.proceed();
        }
        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader(idempotent.tokenHeader());
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("缺少幂等 Token，请求头: " + idempotent.tokenHeader());
        }
        boolean valid = idempotentService.tryConsume(token, idempotent.ttlSeconds());
        if (!valid) {
            log.warn("重复请求被拦截: token={}, uri={}", token, request.getRequestURI());
            throw new RuntimeException("重复请求，请勿重复提交");
        }
        return pjp.proceed();
    }
}