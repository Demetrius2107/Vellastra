package com.demetrius.blog.category.domain.category.repository;

import com.demetrius.blog.category.domain.category.entity.Category;

import java.util.List;

/**
 * <p>Title: CategoryRepository</p>
 * <p>Description: 分类仓储接口</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
public interface CategoryRepository {

    Category findById(Long id);

    List<Category> findAll();

    boolean existsByParentId(Long parentId);

    void save(Category category);

    void delete(Long id);
}
