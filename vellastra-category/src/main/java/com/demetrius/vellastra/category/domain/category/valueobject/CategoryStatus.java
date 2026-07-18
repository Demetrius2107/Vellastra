package com.demetrius.vellastra.category.domain.category.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Title: CategoryStatus</p>
 * <p>Description: 分类状态枚举（正常/禁用）</p>
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
@NoArgsConstructor(force = true)
public enum CategoryStatus {

    ENABLED(1, "正常"),
    DISABLED(0, "禁用");

    private final int code;
    private final String desc;

    public static CategoryStatus of(int code) {
        for (CategoryStatus status : values()) {
            if (status.code == code) return status;
        }
        return DISABLED;
    }
}
