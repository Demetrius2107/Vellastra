package com.demetrius.vellastra.user.domain.user.entity;

import com.demetrius.vellastra.user.domain.user.valueobject.UserRole;
import com.demetrius.vellastra.user.domain.user.valueobject.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <p>Title: 用户实体</p>
 * <p>Description: 用户领域聚合根实体，包含用户核心属性与值对象</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 主键ID
     */
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
     * 头像(地址url)
     */
    private String avatar;

    /**
     * 角色
     */
    private UserRole role;

    /**
     * 状态: 1禁用 0启用
     */
    private UserStatus status;

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
