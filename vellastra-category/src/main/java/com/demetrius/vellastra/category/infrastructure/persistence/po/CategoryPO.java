package com.demetrius.vellastra.category.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: CategoryPO</p>
 * <p>Description: 分类持久化对象，与 blog_category 表 1:1 对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@TableName("blog_category")
public class CategoryPO {

    /** 分类ID */
    @TableId(type = IdType.AUTO)
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
}
