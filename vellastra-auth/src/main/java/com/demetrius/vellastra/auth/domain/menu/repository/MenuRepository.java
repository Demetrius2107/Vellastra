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

    /** 根据 ID 查询菜单（返回 Optional） */
    Optional<Menu> findById(Long id);

    /** 根据 ID 获取菜单（不存在返回 null） */
    Menu getById(Long id);

    /** 查询所有菜单 */
    List<Menu> findAll();

    /** 根据父菜单 ID 查询子菜单列表 */
    List<Menu> findByParentId(Long parentId);

    /** 判断是否存在子菜单 */
    boolean existsByParentId(Long parentId);

    /** 保存菜单（新增或更新） */
    void save(Menu menu);

    /** 根据 ID 删除菜单 */
    void delete(Long id);
}