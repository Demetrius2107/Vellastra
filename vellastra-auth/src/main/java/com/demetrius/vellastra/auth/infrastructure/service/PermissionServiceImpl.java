package com.demetrius.vellastra.auth.infrastructure.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.MenuMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.RoleMenuMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.MenuPO;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RoleMenuPO;
import com.demetrius.vellastra.common.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: PermissionServiceImpl</p>
 * <p>Description: 权限查询服务实现，根据角色ID查询菜单权限标识</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    public PermissionServiceImpl(RoleMenuMapper roleMenuMapper, MenuMapper menuMapper) {
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
    }

    @Override
    public List<String> getPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 查询角色关联的所有菜单ID
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuPO>()
                        .in(RoleMenuPO::getRoleId, roleIds)
                        .select(RoleMenuPO::getMenuId)
        ).stream().map(RoleMenuPO::getMenuId).distinct().collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查询菜单的权限标识（perms 字段）
        return menuMapper.selectList(
                new LambdaQueryWrapper<MenuPO>()
                        .in(MenuPO::getId, menuIds)
                        .isNotNull(MenuPO::getPerms)
                        .ne(MenuPO::getPerms, "")
        ).stream().map(MenuPO::getPerms).distinct().collect(Collectors.toList());
    }
}