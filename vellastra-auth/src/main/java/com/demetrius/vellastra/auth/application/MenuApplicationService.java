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
 * <h3>菜单应用服务</h3>
 *
 * <p>负责菜单的 CRUD 和树形结构构建。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Service
public class MenuApplicationService {

    private final MenuRepository menuRepository;

    public MenuApplicationService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuVO> getMenuTree() {
        List<Menu> all = menuRepository.findAll();
        return buildTree(all.stream().map(this::toVO).collect(Collectors.toList()));
    }

    public MenuVO getMenuById(Long id) {
        Menu menu = menuRepository.getById(id);
        if (menu == null) throw ErrorCode.COMMENT_NOT_FOUND.toException();
        return toVO(menu);
    }

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