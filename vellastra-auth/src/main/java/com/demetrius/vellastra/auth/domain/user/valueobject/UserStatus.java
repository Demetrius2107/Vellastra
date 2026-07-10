package com.demetrius.vellastra.auth.domain.user.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Title: UserStatus</p>
 * <p>Description: 用户状态枚举（正常/禁用）</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
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
