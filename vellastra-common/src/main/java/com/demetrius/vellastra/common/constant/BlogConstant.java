package com.demetrius.vellastra.common.constant;

/**
 * <h3>博客系统全局常量</h3>
 *
 * <p>集中管理系统中使用的常量值，避免 magic string 散落在各个模块中。</p>
 *
 * <p><b>分类说明：</b>
 * <ul>
 *   <li><b>TOKEN_*</b> — JWT 令牌相关的请求头与前缀</li>
 *   <li><b>USER_*</b> — 线程上下文 / JWT Payload 中用户信息的键名</li>
 * </ul>
 * </p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-05-17
 */
public final class BlogConstant {

    // ====================== JWT 令牌 ======================

    /**
     * HTTP 请求头中携带令牌的字段名
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Bearer 令牌前缀（含尾部空格）
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    // ====================== 用户上下文 ======================

    /**
     * 用户 ID 在上下文 / JWT Payload 中的键名
     */
    public static final String USER_ID = "userId";

    /**
     * 用户名在上下文 / JWT Payload 中的键名
     */
    public static final String USERNAME = "username";

    /**
     * 角色集合在上下文 / JWT Payload 中的键名
     */
    public static final String ROLES = "roles";

    private BlogConstant() {
        // 工具类，禁止实例化
    }
}
