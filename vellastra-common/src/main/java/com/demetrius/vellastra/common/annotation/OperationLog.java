package com.demetrius.vellastra.common.annotation;

import java.lang.annotation.*;

/**
 * <p>Title: OperationLog</p>
 * <p>Description: 操作日志注解，标记在 Controller 方法上自动记录操作日志</p>
 * <p>项目名称: Vellastra</p>
 *
 * <p>配合 {@code OperationLogAspect} 使用，自动记录接口调用的模块、操作、耗时、
 * 请求参数、返回结果、操作人等信息到 t_operation_log 表。</p>
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
public @interface OperationLog {

    /** 所属模块，如 "用户管理"、"文章管理" */
    String module();

    /** 操作描述，如 "新增用户"、"删除文章" */
    String operation();
}