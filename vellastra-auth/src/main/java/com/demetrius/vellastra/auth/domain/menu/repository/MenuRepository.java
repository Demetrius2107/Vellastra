package com.demetrius.vellastra.auth.domain.menu.repository;

import com.demetrius.vellastra.auth.domain.menu.entity.Menu;

import java.util.List;
import java.util.Optional;

/**
 * <h3>菜单仓储接口</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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