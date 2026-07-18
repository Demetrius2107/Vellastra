package com.demetrius.vellastra.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h3>逻辑删除枚举</h3>
 *
 * <p>配合 MyBatis-Plus 逻辑删除功能使用，所有实体类的 {@code deleted} 字段统一使用此枚举。</p>
 *
 * <p><b>配置示例（application.yml）：</b>
 * <pre>{@code
 * mybatis-plus:
 *   global-config:
 *     db-config:
 *       logic-delete-field: deleted
 *       logic-delete-value: 1
 *       logic-not-delete-value: 0
 * }</pre>
 * </p>
 *
 * @author wanqiu
 * @version 1.0
 * @since 2026-07-18
 */
@Getter
@AllArgsConstructor
public enum DeleteEnum {

    /** 正常（未删除） */
    NORMAL(0, "正常"),

    /** 已删除（逻辑删除） */
    DELETED(1, "已删除");

    /** 数据库存储值，标记 {@code @EnumValue} 使 MyBatis-Plus 自动识别 */
    @EnumValue
    private final int value;

    /** 描述 */
    private final String description;
}