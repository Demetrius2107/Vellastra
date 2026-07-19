package com.demetrius.vellastra.user.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: UserVO</p>
 * <p>Description: 用户视图对象</p>
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
public class UserVO {
    /** 主键ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 邮箱 */
    private String email;
    /** 头像URL */
    private String avatar;
    /** 手机号 */
    private String phone;
    /** 性别:0未知 1男 2女 */
    private Integer gender;
    /** 个人简介 */
    private String bio;
    /** 状态:0禁用 1正常 */
    private Integer status;
    /** 角色名称 */
    private String roleName;
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    /** 创建时间 */
    private LocalDateTime createTime;
}