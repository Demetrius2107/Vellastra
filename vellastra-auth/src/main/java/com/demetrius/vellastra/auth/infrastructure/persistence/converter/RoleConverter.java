package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.role.entity.Role;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Component;

/**
 * <h3>角色转换器（PO ↔ Domain）</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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