package com.demetrius.blog.category.domain.category.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Title: CategoryStatus</p>
 * <p>Description: 分类状态枚举（正常/禁用）</p>
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
