package com.demetrius.vellastra.auth.domain.role.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <h3>角色领域实体</h3>
 *
 * <p>对应 t_role 表，一个角色可关联多个用户和多个菜单权限。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /** 主键ID */
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码，如 SUPER_ADMIN */
    private String roleCode;

    /** 角色描述 */
    private String description;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态：0禁用 1正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}