package com.demetrius.vellastra.category.infrastructure.persistence.converter;

import com.demetrius.vellastra.category.domain.category.entity.Category;
import com.demetrius.vellastra.category.infrastructure.persistence.po.CategoryPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: CategoryConverter</p>
 * <p>Description: 分类对象转换器（PO <-> Domain），与 blog_category 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Component
public class CategoryConverter {

    public Category toDomain(CategoryPO po) {
        if (po == null) {
            return null;
        }
        return Category.builder()
                .id(po.getId())
                .name(po.getName())
                .description(po.getDescription())
                .parentId(po.getParentId())
                .sort(po.getSort())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    public CategoryPO toPO(Category domain) {
        if (domain == null) {
            return null;
        }
        CategoryPO po = new CategoryPO();
        po.setId(domain.getId());
        po.setName(domain.getName());
        po.setDescription(domain.getDescription());
        po.setParentId(domain.getParentId());
        po.setSort(domain.getSort());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
