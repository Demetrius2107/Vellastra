package com.demetrius.blog.category.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: CategoryVO</p>
 * <p>Description: 分类视图对象</p>
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
public class CategoryVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Integer articleCount;
    private Long parentId;
    private List<CategoryVO> children;
    private LocalDateTime createTime;
}
