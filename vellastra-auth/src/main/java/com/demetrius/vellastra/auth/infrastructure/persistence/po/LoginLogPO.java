package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: LoginLogPO</p>
 * <p>Description: 登录日志持久化对象，与 t_login_log 表 1:1 对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_login_log")
public class LoginLogPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 登录方式:1账号密码 2手机号 3第三方 */
    private Integer loginType;

    /** 登录IP */
    private String ipAddress;

    /** 登录地点 */
    private String location;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 状态:0失败 1成功 */
    private Integer status;

    /** 提示消息 */
    private String message;

    /** 创建时间 */
    private LocalDateTime createTime;
}