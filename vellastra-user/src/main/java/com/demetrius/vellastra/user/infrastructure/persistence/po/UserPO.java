package com.demetrius.vellastra.user.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: 用户持久化对象</p>
 * <p>Description: sys_user 用户表数据库持久化PO对象，基础设施层持久映射</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
@TableName("sys_user")
public class UserPO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮件
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像（地址url）
     */
    private String avatar;

    /**
     * 状态 1禁用 0启用
     */
    private Integer status;

    /**
     * 角色 1超级管理员 2用户
     */
    private Integer role;

    /**
     * 最近一次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最近一次登录IP
     */
    private String lastLoginIp;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
