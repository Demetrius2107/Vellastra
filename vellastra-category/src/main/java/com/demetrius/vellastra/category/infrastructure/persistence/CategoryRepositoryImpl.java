package com.demetrius.vellastra.category.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.domain.category.repository.CategoryRepository;
import com.demetrius.vellastra.category.infrastructure.persistence.converter.CategoryConverter;
import com.demetrius.vellastra.category.infrastructure.persistence.mapper.CategoryMapper;
import com.demetrius.vellastra.category.infrastructure.persistence.po.CategoryPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Title: CategoryRepositoryImpl</p>
 * <p>Description: 分类仓储实现（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;
    private final CategoryConverter categoryConverter;

    public CategoryRepositoryImpl(CategoryMapper categoryMapper, CategoryConverter categoryConverter) {
        this.categoryMapper = categoryMapper;
        this.categoryConverter = categoryConverter;
    }

    @Override
    public Category findById(Long id) {
        CategoryPO po = categoryMapper.selectById(id);
        return po != null ? categoryConverter.toDomain(po) : null;
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<CategoryPO>().orderByAsc(CategoryPO::getSort)
        ).stream().map(categoryConverter::toDomain).toList();
    }

    @Override
    public boolean existsByParentId(Long parentId) {
        return categoryMapper.exists(
                new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getParentId, parentId)
        );
    }

    @Override
    public void save(Category category) {
        CategoryPO po = categoryConverter.toPO(category);
        if (po.getId() == null) {
            categoryMapper.insert(po);
            category.setId(po.getId());
        } else {
            categoryMapper.updateById(po);
        }
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
