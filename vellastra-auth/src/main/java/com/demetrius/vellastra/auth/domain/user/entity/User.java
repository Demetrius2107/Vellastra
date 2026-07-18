package com.demetrius.vellastra.auth.domain.user.entity;

import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: User</p>
 * <p>Description: 用户领域实体（auth 模块），与 sys_user 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 主键ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 加密密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 角色：1超级管理员 2用户 */
    private Integer role;

    /** 邮箱 */
    private String email;

    /** 状态 */
    private UserStatus status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 判断是否启用 */
    public boolean isEnabled() {
        return this.status == UserStatus.ENABLED;
    }

    /** 禁用 */
    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    /** 启用 */
    public void enable() {
        this.status = UserStatus.ENABLED;
    }
}
