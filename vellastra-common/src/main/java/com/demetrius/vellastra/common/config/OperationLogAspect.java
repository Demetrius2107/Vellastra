package com.demetrius.vellastra.common.config;

import com.demetrius.vellastra.common.annotation.OperationLog;
import com.demetrius.vellastra.common.event.OperationLogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: OperationLogAspect</p>
 * <p>Description: 操作日志 AOP 切面，拦截 @OperationLog 注解，发布操作日志事件</p>
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
public class OperationLogAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean success = true;
        String errorMsg = null;
        Object result = null;

        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable e) {
            success = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                publishEvent(pjp, operationLog, result, duration, success, errorMsg);
            } catch (Exception e) {
                log.warn("记录操作日志异常: {}", e.getMessage());
            }
        }
    }

    private void publishEvent(ProceedingJoinPoint pjp, OperationLog operationLog,
                               Object result, long duration, boolean success, String errorMsg) {
        ServletRequestAttributes attrs = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        String params = extractParams(pjp);
        String responseResult = success ? toJsonString(result) : null;

        OperationLogEvent event = new OperationLogEvent(
                this,
                operationLog.module(),
                operationLog.operation(),
                request.getMethod(),
                request.getRequestURI(),
                params,
                responseResult,
                getUserId(request),
                request.getHeader("X-Username"),
                getClientIp(request),
                request.getHeader("User-Agent"),
                duration,
                success,
                errorMsg
        );
        eventPublisher.publishEvent(event);
    }

    /** 提取方法参数为 JSON */
    private String extractParams(ProceedingJoinPoint pjp) {
        try {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = pjp.getArgs();
            Map<String, Object> paramsMap = new HashMap<>();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    // 过滤掉 HttpServletRequest 等框架参数
                    if (args[i] != null && !(args[i] instanceof HttpServletRequest)) {
                        paramsMap.put(paramNames[i], args[i]);
                    }
                }
            }
            return paramsMap.isEmpty() ? null : toJsonString(paramsMap);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getUserId(HttpServletRequest request) {
        String headerUserId = request.getHeader("X-User-Id");
        if (headerUserId != null && !headerUserId.isEmpty()) {
            try {
                return Long.parseLong(headerUserId);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String toJsonString(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}