package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.OperationLogMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.OperationLogPO;
import com.demetrius.vellastra.common.event.OperationLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>Title: OperationLogEventListener</p>
 * <p>Description: 操作日志事件监听器，异步将操作日志写入数据库</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogEventListener {

    private final OperationLogMapper operationLogMapper;

    /**
     * 监听操作日志事件，异步写入 t_operation_log 表
     */
    @Async
    @EventListener
    public void handleOperationLog(OperationLogEvent event) {
        try {
            OperationLogPO po = new OperationLogPO();
            po.setModule(event.getModule());
            po.setOperation(event.getOperation());
            po.setRequestMethod(event.getRequestMethod());
            po.setRequestUrl(event.getRequestUrl());
            po.setRequestParams(truncate(event.getRequestParams(), 1000));
            po.setResponseResult(truncate(event.getResponseResult(), 1000));
            po.setOperatorId(event.getOperatorId());
            po.setOperatorName(event.getOperatorName());
            po.setIpAddress(event.getIpAddress());
            po.setUserAgent(event.getUserAgent());
            po.setDuration(event.getDuration());
            po.setSuccess(event.isSuccess() ? 1 : 0);
            po.setErrorMsg(truncate(event.getErrorMsg(), 500));
            po.setCreateTime(LocalDateTime.now());

            operationLogMapper.insert(po);
        } catch (Exception e) {
            log.error("写入操作日志失败: {}", e.getMessage());
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}