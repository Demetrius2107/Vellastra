package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.MenuPO;
import org.springframework.stereotype.Component;

/**
 * <h3>菜单转换器（PO ↔ Domain）</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Component
public class MenuConverter {

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