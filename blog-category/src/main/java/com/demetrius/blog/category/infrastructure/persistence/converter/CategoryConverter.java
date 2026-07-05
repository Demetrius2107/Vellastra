package com.demetrius.blog.category.infrastructure.persistence.converter;

import com.demetrius.blog.category.domain.category.entity.Category;
import com.demetrius.blog.category.domain.category.valueobject.CategoryStatus;
import com.demetrius.blog.category.infrastructure.persistence.po.CategoryPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: CategoryConverter</p>
 * <p>Description: 分类对象转换器（PO <-> Domain）</p>
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
@Component
public class CategoryConverter {

    public Category toDomain(CategoryPO po) {
        if (po == null) {
            return null;
        }
        return Category.builder()
                .id(po.getId())
                .name(po.getName())
                .slug(po.getSlug())
                .description(po.getDescription())
                .icon(po.getIcon())
                .parentId(po.getParentId())
                .sortOrder(po.getSortOrder())
                .articleCount(po.getArticleCount())
                .status(CategoryStatus.of(po.getStatus()))
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
        po.setSlug(domain.getSlug());
        po.setDescription(domain.getDescription());
        po.setIcon(domain.getIcon());
        po.setParentId(domain.getParentId());
        po.setSortOrder(domain.getSortOrder());
        po.setArticleCount(domain.getArticleCount());
        po.setStatus(domain.getStatus().getCode());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
