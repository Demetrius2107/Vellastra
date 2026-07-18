package com.demetrius.vellastra.common.context;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * <h3>用户请求上下文</h3>
 *
 * <p>基于 {@code ThreadLocal} 存储当前请求的用户信息，避免在方法调用链中反复传递 userId 参数。</p>
 *
 * <p><b>使用说明：</b>
 * <ul>
 *   <li>在 Filter / Interceptor 中解析 JWT 后调用 {@link #set(UserInfo)} 注入用户信息</li>
 *   <li>在 Service / Domain 层直接调用 {@link #getUserId()} 获取当前用户</li>
 *   <li>请求结束时在 {@code finally} 块中调用 {@link #clear()} 防止内存泄漏</li>
 * </ul>
 * </p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-05-17
 */
@Slf4j
public class UserContext {

    /**
     * 线程局部存储，持有当前请求的用户信息
     */
    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前请求的用户信息
     *
     * @param userInfo 用户信息（不允许为 null）
     */
    public static void set(UserInfo userInfo) {
        USER_HOLDER.set(userInfo);
        log.debug("UserContext set: userId={}, username={}",
                userInfo.getUserId(), userInfo.getUsername());
    }

    /**
     * 获取当前请求的用户信息
     *
     * @return 用户信息，未登录时返回 null
     */
    public static UserInfo get() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return userId，未登录时返回 null
     */
    public static Long getUserId() {
        UserInfo info = get();
        return info != null ? info.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录时返回 null
     */
    public static String getUsername() {
        UserInfo info = get();
        return info != null ? info.getUsername() : null;
    }

    /**
     * 获取当前登录用户的角色集合
     *
     * @return 角色集合，未登录时返回空集合
     */
    public static Set<String> getRoles() {
        UserInfo info = get();
        return info != null ? info.getRoles() : Set.of();
    }

    /**
     * 清除当前请求的用户信息（请求结束时调用，防止内存泄漏）
     */
    public static void clear() {
        UserInfo info = USER_HOLDER.get();
        if (info != null) {
            log.debug("UserContext cleared: userId={}, username={}",
                    info.getUserId(), info.getUsername());
        }
        USER_HOLDER.remove();
    }

    /**
     * <h3>用户信息内部类</h3>
     * <p>存储在 ThreadLocal 中的用户上下文数据载体。</p>
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class UserInfo {
        /**
         * 用户 ID
         */
        private Long userId;
        /**
         * 用户名
         */
        private String username;
        /**
         * 角色集合
         */
        private Set<String> roles;
    }
}
