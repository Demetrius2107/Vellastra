package com.demetrius.vellastra.auth.domain.user.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Title: UserStatus</p>
 * <p>Description: 用户状态枚举（正常/禁用）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    ENABLED(1, "正常"),
    DISABLED(0, "禁用");

    private final int code;
    private final String desc;

    public static UserStatus of(int code) {
        for (UserStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return DISABLED;
    }
}
