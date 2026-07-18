package com.demetrius.vellastra.category.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: CreateCategoryRequest</p>
 * <p>Description: 创建分类请求 DTO</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
public class CreateCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Long parentId;
    private String description;
    private Integer sort;
}
