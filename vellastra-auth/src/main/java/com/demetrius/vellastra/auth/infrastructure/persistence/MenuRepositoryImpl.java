package com.demetrius.vellastra.auth.infrastructure.persistence;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;
import com.demetrius.vellastra.auth.domain.menu.repository.MenuRepository;
import com.demetrius.vellastra.auth.infrastructure.persistence.converter.MenuConverter;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.MenuMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.MenuPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>Title: MenuRepositoryImpl</p>
 * <p>Description: 菜单仓储实现（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Repository
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuMapper menuMapper;
    private final MenuConverter menuConverter;

    public MenuRepositoryImpl(MenuMapper menuMapper, MenuConverter menuConverter) {
        this.menuMapper = menuMapper;
        this.menuConverter = menuConverter;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(menuMapper.selectById(id)).map(menuConverter::toDomain);
    }

    @Override
    public Menu getById(Long id) {
        return findById(id).orElse(null);
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectList(null).stream()
                .map(menuConverter::toDomain).toList();
    }

    @Override
    public List<Menu> findByParentId(Long parentId) {
        return menuMapper.selectList(
                new LambdaQueryWrapper<MenuPO>().eq(MenuPO::getParentId, parentId)
        ).stream().map(menuConverter::toDomain).toList();
    }

    @Override
    public boolean existsByParentId(Long parentId) {
        return menuMapper.exists(
                new LambdaQueryWrapper<MenuPO>().eq(MenuPO::getParentId, parentId)
        );
    }

    @Override
    public void save(Menu menu) {
        MenuPO po = menuConverter.toPO(menu);
        if (po.getId() == null) {
            menuMapper.insert(po);
            menu.setId(po.getId());
        } else {
            menuMapper.updateById(po);
        }
    }

    @Override
    public void delete(Long id) {
        menuMapper.deleteById(id);
    }
}