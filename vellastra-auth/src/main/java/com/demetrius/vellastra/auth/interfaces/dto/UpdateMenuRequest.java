package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新菜单请求
 */
@Data
public class UpdateMenuRequest {
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private Integer menuType;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private Integer status;
}