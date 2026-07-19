package com.demetrius.vellastra.auth.domain.menu.repository;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;

import java.util.List;
import java.util.Optional;

/**
 * <p>Title: MenuRepository</p>
 * <p>Description: 菜单仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface MenuRepository {

    Optional<Menu> findById(Long id);

    Menu getById(Long id);

    List<Menu> findAll();

    List<Menu> findByParentId(Long parentId);

    boolean existsByParentId(Long parentId);

    void save(Menu menu);

    void delete(Long id);
}