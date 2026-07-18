package com.demetrius.vellastra.common.exception;

import com.demetrius.vellastra.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * <h3>全局异常处理器</h3>
 *
 * <p>使用 {@code @RestControllerAdvice} 统一拦截所有 Controller 层抛出的异常，转换为统一的 {@link Result} 响应格式。</p>
 *
 * <p><b>处理优先级（Handler 顺序 = 异常类型精确度优先级）：</b>
 * <ol>
 *   <li>{@link BizException} — 业务主动抛出的可预期异常，记录 warn 级别</li>
 *   <li>{@link MethodArgumentNotValidException} — {@code @Valid} 参数校验失败</li>
 *   <li>{@link MissingServletRequestParameterException} / {@link MissingRequestHeaderException} — 缺少必传参数/请求头</li>
 *   <li>{@link HttpMessageNotReadableException} — 请求体格式错误</li>
 *   <li>{@link NoResourceFoundException} — 静态资源/路径 404</li>
 *   <li>{@link Exception} — 未预期的系统异常兜底，记录 error 级别 + 完整堆栈</li>
 * </ol>
 * </p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-05-17
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常（由 Service / Domain 层主动抛出）
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 非法参数（{@code IllegalArgumentException}）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * {@code @Valid} 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: field={}, message={}",
                fieldError != null ? fieldError.getField() : "unknown", message);
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * {@code @RequestParam(required=true)} 缺少必传参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: name={}, type={}", e.getParameterName(), e.getParameterType());
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "缺少必要参数: " + e.getParameterName());
    }

    /**
     * {@code @RequestHeader} 缺少必传请求头
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingHeader(MissingRequestHeaderException e) {
        log.warn("缺少请求头: name={}", e.getHeaderName());
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "缺少必要请求头: " + e.getHeaderName());
    }

    /**
     * 请求体格式错误或不可读
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "请求体格式错误");
    }

    /**
     * 静态资源 / 无效路径 404（不打印堆栈，避免日志污染）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoResource(NoResourceFoundException e) {
        log.warn("资源不存在: method={}, resource={}", e.getHttpMethod(), e.getResourcePath());
        return Result.fail(ErrorCode.NOT_FOUND.getCode(), "请求的资源不存在");
    }

    /**
     * 未预期的系统异常兜底处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
