package com.demetrius.vellastra.category.domain.category.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Category</p>
 * <p>Description: 分类领域实体，与 blog_category 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /** 分类ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 父分类ID（0为一级分类） */
    private Long parentId;

    /** 排序权重，越小越靠前 */
    private Integer sort;

    /** 分类描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}
