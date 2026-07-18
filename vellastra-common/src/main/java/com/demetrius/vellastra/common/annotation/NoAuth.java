package com.demetrius.vellastra.common.annotation;

import java.lang.annotation.*;

/**
 * <h3>无需认证注解</h3>
 *
 * <p>标记在 Controller 方法或类上，表示该接口不需要登录即可访问。</p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>登录 / 注册接口</li>
 *   <li>健康检查 / 探活接口</li>
 *   <li>公开的文章浏览接口（如游客模式下查看文章列表）</li>
 * </ul>
 * </p>
 *
 * <p><b>配合 Filter / Interceptor 使用示例：</b>
 * <pre>{@code
 * // 在 JWT 过滤器中读取注解
 * HandlerMethod handlerMethod = (HandlerMethod) handler;
 * NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
 * if (noAuth != null) {
 *     chain.doFilter(request, response);
 *     return;
 * }
 * }</pre>
 * </p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuth {

    /**
     * 可选说明（如 "注册接口，无需登录"）
     */
    String value() default "";
}