package com.demetrius.vellastra.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h3>统一 API 响应体</h3>
 *
 * <p>所有 REST 接口统一使用此结构返回，确保前后端契约一致。</p>
 *
 * <p><b>响应格式：</b>
 * <pre>
 * {
 *   "code": 200,        // 业务状态码（200=成功，其余为错误码）
 *   "message": "success", // 提示信息
 *   "data": null         // 业务数据（泛型）
 * }
 * </pre>
 * </p>
 *
 * <p>成功时使用 {@link #success()} / {@link #success(Object)} 创建；
 * 失败时使用 {@link #fail(int, String)} 并配合 {@link com.demetrius.vellastra.common.exception.ErrorCode} 传递错误码。</p>
 *
 * @param <T> 业务数据类型
 * @author wanqiu
 * @version 1.0
 * @since 2026-05-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 业务状态码（200 成功，其余为错误码，与 ErrorCode 定义一致）
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据（成功时携带，失败时为 null）
     */
    private T data;

    // ====================== 成功工厂方法 ======================

    /**
     * 操作成功，无返回数据
     *
     * @param <T> 泛型占位
     * @return code=200, message="success", data=null
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 操作成功，携带返回数据
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return code=200, message="success"
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 操作成功，自定义提示信息与返回数据
     *
     * @param message 自定义提示
     * @param data    业务数据
     * @param <T>     数据类型
     * @return code=200
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ====================== 失败工厂方法 ======================

    /**
     * 操作失败，指定错误码与提示
     *
     * @param code    业务错误码（参见 {@link com.demetrius.vellastra.common.exception.ErrorCode}）
     * @param message 错误描述
     * @param <T>     泛型占位
     * @return data=null
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 操作失败，使用默认 500 错误码
     *
     * @param message 错误描述
     * @param <T>     泛型占位
     * @return code=500
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }
}
