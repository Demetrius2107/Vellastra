package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: OperationLogPO</p>
 * <p>Description: 操作日志持久化对象，与 t_operation_log 表 1:1 对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_operation_log")
public class OperationLogPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属模块 */
    private String module;

    /** 操作描述 */
    private String operation;

    /** HTTP方法 */
    private String requestMethod;

    /** 请求路径 */
    private String requestUrl;

    /** 请求参数（JSON） */
    private String requestParams;

    /** 返回结果 */
    private String responseResult;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作IP */
    private String ipAddress;

    /** 浏览器UA */
    private String userAgent;

    /** 耗时（毫秒） */
    private Long duration;

    /** 是否成功：0失败 1成功 */
    private Integer success;

    /** 错误信息 */
    private String errorMsg;

    /** 创建时间 */
    private LocalDateTime createTime;
}