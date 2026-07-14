package com.demetrius.vellastra.user.domain.user.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Title: 用户角色枚举</p>
 * <p>Description: 用户角色枚举，定义超级管理员、普通用户等角色</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public enum UserRole {

    ADMIN(1, "超级管理员"),
    USER(2, "用户");

    private final int code;

    private final String desc;


    public static UserRole of(int code) {
        for (UserRole userRole : values()) {
            if (userRole.code == code) return userRole;
        }
        return USER;
    }

}
