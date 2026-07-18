package com.demetrius.vellastra.category.domain.category.repository;

import com.demetrius.vellastra.category.domain.category.entity.Category;

import java.util.List;

/**
 * <p>Title: CategoryRepository</p>
 * <p>Description: 分类仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface CategoryRepository {

    Category findById(Long id);

    List<Category> findAll();

    boolean existsByParentId(Long parentId);

    void save(Category category);

    void delete(Long id);
}
