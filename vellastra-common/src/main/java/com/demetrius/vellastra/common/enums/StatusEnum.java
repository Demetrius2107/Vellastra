package com.demetrius.vellastra.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h3>通用启用/禁用状态枚举</h3>
 *
 * <p>用于用户状态、配置开关等需要启用/禁用二值状态的场景。</p>
 *
 * @author wanqiu
 * @since 1.1
 * @since 2026-07-18
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /** 启用 */
    @EnumValue
    ENABLED(0, "启用"),

    /** 禁用 */
    @EnumValue
    DISABLED(1, "禁用");

    private final int value;
    private final String description;
}