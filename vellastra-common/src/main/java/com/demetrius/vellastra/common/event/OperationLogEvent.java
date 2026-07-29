package com.demetrius.vellastra.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * <p>Title: OperationLogEvent</p>
 * <p>Description: 操作日志事件，由 AOP 切面发布，由监听器异步写入数据库</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Getter
public class OperationLogEvent extends ApplicationEvent {

    /** 所属模块 */
    private final String module;
    /** 操作描述 */
    private final String operation;
    /** HTTP方法 */
    private final String requestMethod;
    /** 请求路径 */
    private final String requestUrl;
    /** 请求参数（JSON） */
    private final String requestParams;
    /** 返回结果 */
    private final String responseResult;
    /** 操作人ID */
    private final Long operatorId;
    /** 操作人姓名 */
    private final String operatorName;
    /** 操作IP */
    private final String ipAddress;
    /** 浏览器UA */
    private final String userAgent;
    /** 耗时（毫秒） */
    private final Long duration;
    /** 是否成功 */
    private final boolean success;
    /** 错误信息 */
    private final String errorMsg;

    public OperationLogEvent(Object source, String module, String operation,
                             String requestMethod, String requestUrl,
                             String requestParams, String responseResult,
                             Long operatorId, String operatorName,
                             String ipAddress, String userAgent,
                             Long duration, boolean success, String errorMsg) {
        super(source);
        this.module = module;
        this.operation = operation;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestParams = requestParams;
        this.responseResult = responseResult;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.duration = duration;
        this.success = success;
        this.errorMsg = errorMsg;
    }
}