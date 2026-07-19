package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.role.entity.Role;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: RoleConverter</p>
 * <p>Description: 角色转换器（PO <-> Domain），全字段映射</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class RoleConverter {

    public Role toDomain(RolePO po) {
        if (po == null) return null;
        return Role.builder()
                .id(po.getId())
                .roleName(po.getRoleName())
                .roleCode(po.getRoleCode())
                .description(po.getDescription())
                .sortOrder(po.getSortOrder())
                .status(po.getStatus())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    public RolePO toPO(Role domain) {
        if (domain == null) return null;
        RolePO po = new RolePO();
        po.setId(domain.getId());
        po.setRoleName(domain.getRoleName());
        po.setRoleCode(domain.getRoleCode());
        po.setDescription(domain.getDescription());
        po.setSortOrder(domain.getSortOrder());
        po.setStatus(domain.getStatus());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}