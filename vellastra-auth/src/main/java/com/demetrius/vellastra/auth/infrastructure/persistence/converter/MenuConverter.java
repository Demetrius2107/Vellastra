package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.MenuPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: MenuConverter</p>
 * <p>Description: 菜单转换器（PO <-> Domain），全字段映射</p>
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
public class MenuConverter {

    /**
     * 将持久化对象转换为领域实体
     *
     * @param po 菜单持久化对象
     * @return 菜单领域实体
     */
    public Menu toDomain(MenuPO po) {
        if (po == null) return null;
        return Menu.builder()
                .id(po.getId())
                .parentId(po.getParentId())
                .menuName(po.getMenuName())
                .menuType(po.getMenuType())
                .path(po.getPath())
                .component(po.getComponent())
                .perms(po.getPerms())
                .icon(po.getIcon())
                .sortOrder(po.getSortOrder())
                .visible(po.getVisible())
                .status(po.getStatus())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    /**
     * 将领域实体转换为持久化对象
     *
     * @param domain 菜单领域实体
     * @return 菜单持久化对象
     */
    public MenuPO toPO(Menu domain) {
        if (domain == null) return null;
        MenuPO po = new MenuPO();
        po.setId(domain.getId());
        po.setParentId(domain.getParentId());
        po.setMenuName(domain.getMenuName());
        po.setMenuType(domain.getMenuType());
        po.setPath(domain.getPath());
        po.setComponent(domain.getComponent());
        po.setPerms(domain.getPerms());
        po.setIcon(domain.getIcon());
        po.setSortOrder(domain.getSortOrder());
        po.setVisible(domain.getVisible());
        po.setStatus(domain.getStatus());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}