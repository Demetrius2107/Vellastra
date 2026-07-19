package com.demetrius.vellastra.auth.domain.role.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Role</p>
 * <p>Description: 角色领域实体，对应 t_role 表（一个角色可关联多个用户和多个菜单权限）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
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