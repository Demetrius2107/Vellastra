package com.demetrius.vellastra.category.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: CategoryVO</p>
 * <p>Description: 分类视图对象，与 blog_category 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
public class CategoryVO {

    private Long id;
    private String name;
    private String description;
    private Integer sort;
    private Long parentId;
    private List<CategoryVO> children;
    private LocalDateTime createTime;
}
