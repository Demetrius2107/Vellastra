package com.demetrius.vellastra.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.RoleMenuMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RoleMenuPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>Title: RoleMenuService</p>
 * <p>Description: 角色-菜单绑定应用服务</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class RoleMenuService {

    private final RoleMenuMapper roleMenuMapper;

    public RoleMenuService(RoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    /**
     * 获取角色已分配的菜单ID列表
     */
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuPO>().eq(RoleMenuPO::getRoleId, roleId)
        ).stream().map(RoleMenuPO::getMenuId).toList();
    }

    /**
     * 分配角色菜单权限（全量覆盖）
     */
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 删除旧关联
        roleMenuMapper.delete(
                new LambdaQueryWrapper<RoleMenuPO>().eq(RoleMenuPO::getRoleId, roleId)
        );
        // 插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenuPO po = new RoleMenuPO();
                po.setRoleId(roleId);
                po.setMenuId(menuId);
                roleMenuMapper.insert(po);
            }
        }
    }
}