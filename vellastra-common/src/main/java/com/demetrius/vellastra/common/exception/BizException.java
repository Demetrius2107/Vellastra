package com.demetrius.vellastra.common.exception;

import lombok.Getter;
import com.demetrius.vellastra.common.response.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>业务异常</h3>
 *
 * <p>在 Service / Domain 层抛出，由 {@link GlobalExceptionHandler} 统一捕获并转换为 {@link Result} 返回。</p>
 *
 * <p>推荐配合 {@link ErrorCode#toException()} 快速创建，或直接 {@code throw new BizException(ErrorCode.ARTICLE_NOT_FOUND)}。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-05-17
 */
@Slf4j
@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {

    /**
     * 业务错误码
     */
    private int code;

    /**
     * 错误描述
     */
    private String message;

    /**
     * 快速构造业务异常（默认错误码 500）
     *
     * @param message 错误描述
     */
    public BizException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
        log.debug("BizException created: code={}, message={}", this.code, this.message);
    }

    /**
     * 将业务异常转为统一响应体
     *
     * @param e 业务异常
     * @return 失败响应
     */
    public static Result<Void> toResult(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }
}
