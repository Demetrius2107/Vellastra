package com.demetrius.vellastra.auth.application;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;
import com.demetrius.vellastra.auth.domain.menu.repository.MenuRepository;
import com.demetrius.vellastra.auth.interfaces.dto.MenuVO;
import com.demetrius.vellastra.auth.interfaces.dto.CreateMenuRequest;
import com.demetrius.vellastra.auth.interfaces.dto.UpdateMenuRequest;
import com.demetrius.vellastra.common.exception.BizException;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: MenuApplicationService</p>
 * <p>Description: 菜单应用服务，负责菜单的 CRUD 和树形结构构建</p>
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
public class MenuApplicationService {

    private final MenuRepository menuRepository;

    public MenuApplicationService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * 获取菜单树（顶级菜单 → 递归子菜单）
     *
     * @return 树形菜单列表
     */
    public List<MenuVO> getMenuTree() {
        List<Menu> all = menuRepository.findAll();
        return buildTree(all.stream().map(this::toVO).collect(Collectors.toList()));
    }

    /**
     * 根据 ID 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单视图对象
     */
    public MenuVO getMenuById(Long id) {
        Menu menu = menuRepository.getById(id);
        if (menu == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();
        return toVO(menu);
    }

    /**
     * 创建菜单
     *
     * @param request 创建菜单请求
     * @return 新菜单 ID
     */
    @Transactional
    public Long createMenu(CreateMenuRequest request) {
        if (request.getParentId() != null && request.getParentId() > 0) {
            Menu parent = menuRepository.getById(request.getParentId());
            if (parent == null) {
                throw new BizException(400, "父菜单不存在");
            }
        }

        Menu menu = Menu.builder()
                .menuName(request.getMenuName())
                .menuType(request.getMenuType() != null ? request.getMenuType() : 2)
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .path(request.getPath())
                .component(request.getComponent())
                .perms(request.getPerms())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .visible(1)
                .status(1)
                .build();
        menu.initCreateTime();
        menuRepository.save(menu);
        return menu.getId();
    }

    /**
     * 更新菜单信息
     *
     * @param id      菜单ID
     * @param request 更新菜单请求
     */
    @Transactional
    public void updateMenu(Long id, UpdateMenuRequest request) {
        Menu menu = menuRepository.getById(id);
        if (menu == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();

        menu.setMenuName(request.getMenuName());
        menu.setMenuType(request.getMenuType());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setIcon(request.getIcon());
        if (request.getSortOrder() != null) menu.setSortOrder(request.getSortOrder());
        if (request.getVisible() != null) menu.setVisible(request.getVisible());
        if (request.getStatus() != null) menu.setStatus(request.getStatus());
        menu.updateTime();
        menuRepository.save(menu);
    }

    /**
     * 删除菜单（有子菜单时不允许删除）
     *
     * @param id 菜单ID
     */
    @Transactional
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.getById(id);
        if (menu == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();

        boolean hasChildren = menuRepository.existsByParentId(id);
        if (hasChildren) {
            throw new BizException(400, "该菜单下存在子菜单，无法删除");
        }

        menuRepository.delete(id);
    }

    /**
     * 构建树形菜单结构
     *
     * @param all 所有菜单列表
     * @return 树形结构（仅顶级节点）
     */
    private List<MenuVO> buildTree(List<MenuVO> all) {
        Map<Long, List<MenuVO>> groupByParent = all.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() > 0)
                .collect(Collectors.groupingBy(MenuVO::getParentId));

        List<MenuVO> roots = all.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .sorted((a, b) -> {
                    int sa = a.getSortOrder() != null ? a.getSortOrder() : 0;
                    int sb = b.getSortOrder() != null ? b.getSortOrder() : 0;
                    return sa - sb;
                })
                .toList();

        for (MenuVO root : roots) {
            root.setChildren(buildChildren(root.getId(), groupByParent));
        }
        return new ArrayList<>(roots);
    }

    /**
     * 递归构建子菜单
     *
     * @param parentId 父菜单ID
     * @param map      按父ID分组的菜单映射
     * @return 子菜单列表
     */
    private List<MenuVO> buildChildren(Long parentId, Map<Long, List<MenuVO>> map) {
        List<MenuVO> children = map.getOrDefault(parentId, new ArrayList<>());
        children.sort((a, b) -> {
            int sa = a.getSortOrder() != null ? a.getSortOrder() : 0;
            int sb = b.getSortOrder() != null ? b.getSortOrder() : 0;
            return sa - sb;
        });
        for (MenuVO child : children) {
            child.setChildren(buildChildren(child.getId(), map));
        }
        return children;
    }

    /**
     * 将菜单实体转换为视图对象
     *
     * @param menu 菜单实体
     * @return 菜单视图对象
     */
    private MenuVO toVO(Menu menu) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setParentId(menu.getParentId() != null && menu.getParentId() > 0 ? menu.getParentId() : null);
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        vo.setSortOrder(menu.getSortOrder());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setCreateTime(menu.getCreateTime());
        return vo;
    }
}