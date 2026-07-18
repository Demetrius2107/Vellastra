package com.demetrius.vellastra.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h3>全局业务错误码枚举</h3>
 *
 * <p>集中管理所有业务错误码，按模块分组，确保全局唯一。</p>
 *
 * <p><b>编码规则：</b>
 * <ul>
 *   <li><b>2xx / 4xx / 5xx</b> — 兼容 HTTP 状态码的通用错误</li>
 *   <li><b>1xxx</b> — 用户 / 认证模块</li>
 *   <li><b>2xxx</b> — Token 相关</li>
 *   <li><b>3xxx</b> — 文章模块</li>
 *   <li><b>4xxx</b> — 分类模块</li>
 *   <li><b>5xxx</b> — 评论模块</li>
 * </ul>
 * </p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-05-17
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ====================== 通用（兼容 HTTP 语义） ======================

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 系统内部错误，未预期的异常
     */
    SYSTEM_ERROR(500, "系统内部错误"),
    /**
     * 请求参数校验失败
     */
    PARAM_ERROR(400, "参数错误"),
    /**
     * 未认证，需要登录
     */
    UNAUTHORIZED(401, "未认证"),
    /**
     * 无权限访问
     */
    FORBIDDEN(403, "无权限"),
    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    // ====================== 用户 / 认证模块（1xxx） ======================

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    /**
     * 密码错误
     */
    USER_PASSWORD_ERROR(1002, "密码错误"),
    /**
     * 用户已被禁用
     */
    USER_DISABLED(1003, "用户已被禁用"),
    /**
     * 用户已存在（注册冲突）
     */
    USER_ALREADY_EXISTS(1004, "用户已存在"),

    // ====================== Token 模块（2xxx） ======================

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(2001, "Token已过期"),
    /**
     * Token 无效（签名错误 / 被篡改）
     */
    TOKEN_INVALID(2002, "Token无效"),

    // ====================== 文章模块（3xxx） ======================

    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND(3001, "文章不存在"),
    /**
     * 文章已发布，无法删除
     */
    ARTICLE_PUBLISHED(3002, "文章已发布，无法删除"),

    // ====================== 分类模块（4xxx） ======================

    /**
     * 分类不存在
     */
    CATEGORY_NOT_FOUND(4001, "分类不存在"),
    /**
     * 分类下存在文章，无法删除
     */
    CATEGORY_HAS_ARTICLE(4002, "分类下存在文章，无法删除"),

    // ====================== 评论模块（5xxx） ======================

    /**
     * 评论不存在
     */
    COMMENT_NOT_FOUND(5001, "评论不存在");

    private final int code;
    private final String message;

    /**
     * 快速将当前错误码转为业务异常对象
     *
     * @return BizException 实例
     */
    public BizException toException() {
        return new BizException(this.code, this.message);
    }
}
