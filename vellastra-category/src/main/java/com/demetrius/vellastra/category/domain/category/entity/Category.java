package com.demetrius.vellastra.category.domain.category.entity;

import com.demetrius.vellastra.category.domain.category.valueobject.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Category</p>
 * <p>Description: 分类领域实体</p>
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
    private Integer articleCount;
    private CategoryStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = CategoryStatus.ENABLED;
    }

    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}
