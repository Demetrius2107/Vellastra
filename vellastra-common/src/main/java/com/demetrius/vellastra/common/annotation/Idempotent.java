package com.demetrius.vellastra.common.annotation;

import java.lang.annotation.*;

/**
 * <p>Title: Idempotent</p>
 * <p>Description: 幂等性注解，标记在需要幂等保护的接口方法上</p>
 * <p>项目名称: Vellastra</p>
 *
 * <p>配合 {@code IdempotentAspect} 使用，通过幂等 Token 机制防止重复提交。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /** Token 来源：请求头名称，默认 X-Idempotent-Token */
    String tokenHeader() default "X-Idempotent-Token";

    /** Token 有效期（秒），超过此时间允许重放（防止客户端未收到响应时重试被拒） */
    long ttlSeconds() default 600;
}