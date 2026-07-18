package com.demetrius.vellastra.common.annotation;

import java.lang.annotation.*;

/**
 * <h3>权限校验注解</h3>
 *
 * <p>标记在 Controller 方法上，表示该接口需要指定的权限才能访问。</p>
 *
 * <p>配合 {@code RequirePermissionAspect} 使用，在 AOP 切面中完成权限校验。</p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * @RequirePermission("article:create")
 * @PostMapping
 * public Result<Long> createArticle(...) { ... }
 * }</pre>
 * </p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识，如 "article:create"
     */
    String value();
}