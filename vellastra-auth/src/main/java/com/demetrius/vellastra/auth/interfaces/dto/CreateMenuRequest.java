package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建菜单请求
 */
@Data
public class CreateMenuRequest {
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private Integer menuType;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sortOrder;
}