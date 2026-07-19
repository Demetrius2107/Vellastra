package com.demetrius.vellastra.auth.domain.role.valueobject;

/**
 * <p>Title: RoleStatus</p>
 * <p>Description: 角色状态枚举（正常/禁用）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public enum RoleStatus {

    /** 正常 */
    ENABLED(1),
    /** 禁用 */
    DISABLED(0);

    private final int code;

    RoleStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RoleStatus fromCode(int code) {
        for (RoleStatus s : values()) {
            if (s.code == code) return s;
        }
        return DISABLED;
    }
}