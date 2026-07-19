package com.demetrius.vellastra.common.config;

import com.demetrius.vellastra.common.annotation.RequirePermission;
import com.demetrius.vellastra.common.exception.ErrorCode;
import com.demetrius.vellastra.common.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h3>权限校验 AOP 切面</h3>
 *
 * <p>拦截带有 {@link RequirePermission} 注解的方法，从请求头中获取用户角色，
 * 校验当前用户是否拥有指定权限。</p>
 *
 * <p><b>校验流程：</b>
 * <ol>
 *   <li>从请求头 {@code X-Roles} 获取用户角色 ID 列表（网关解析 JWT 后注入）</li>
 *   <li>从请求头 {@code X-User-Id} 获取用户 ID</li>
 *   <li>通过 {@link PermissionService} 查询角色对应的权限标识列表</li>
 *   <li>匹配注解的权限标识是否在权限列表中</li>
 *   <li>匹配成功 → 放行；匹配失败 → 抛出 FORBIDDEN 异常</li>
 * </ol>
 * </p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Slf4j
@Aspect
@Component
public class RequirePermissionAspect {

    private final PermissionService permissionService;

    public RequirePermissionAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 拦截所有带 @RequirePermission 注解的方法
     */
    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint pjp,
                                   RequirePermission requirePermission) throws Throwable {
        // 1. 获取当前请求
        ServletRequestAttributes attrs = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return pjp.proceed();
        }
        HttpServletRequest request = attrs.getRequest();

        // 2. 从请求头获取用户角色列表（网关解析 JWT 后注入）
        String rolesHeader = request.getHeader("X-Roles");
        List<Long> userRoleIds = parseRoleIds(rolesHeader);

        // 3. 获取需要的权限标识
        String requiredPerm = requirePermission.value();

        // 4. 校验：超级管理员（角色ID=1）拥有所有权限，直接放行
        if (userRoleIds.contains(1L)) {
            return pjp.proceed();
        }

        // 5. 通过 PermissionService 查询角色对应的权限列表
        List<String> userPerms = permissionService.getPermissionsByRoleIds(userRoleIds);

        // 6. 匹配权限
        if (userPerms.contains(requiredPerm)) {
            return pjp.proceed();
        }

        // 7. 无权限，抛出 403
        log.warn("权限校验失败: userId={}, requiredPerm={}, userPerms={}",
                request.getHeader("X-User-Id"), requiredPerm, userPerms);
        throw ErrorCode.FORBIDDEN.toException();
    }

    /**
     * 解析请求头中的角色 ID 列表（逗号分隔）
     */
    private List<Long> parseRoleIds(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            log.warn("解析 X-Roles 请求头失败: {}", rolesHeader);
            return Collections.emptyList();
        }
    }
}