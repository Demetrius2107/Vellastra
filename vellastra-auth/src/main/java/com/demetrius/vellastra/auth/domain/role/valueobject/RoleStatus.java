package com.demetrius.vellastra.auth.domain.role.valueobject;

/**
 * <h3>角色状态枚举</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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