package com.demetrius.vellastra.category.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: CreateCategoryRequest</p>
 * <p>Description: 创建分类请求 DTO</p>
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
public class CreateCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Long parentId;
    private String slug;
    private String description;
    private String icon;
    private Integer sortOrder;
}
